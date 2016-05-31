/*
Copyright 2015 Red Hat, Inc. and/or its affiliates.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package uk.ac.ncl.cs.csc8101.weblogcoursework;

import com.clearspring.analytics.stream.cardinality.CardinalityMergeException;
import com.clearspring.analytics.stream.cardinality.HyperLogLog;
import com.datastax.driver.core.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Integration point for application specific processing logic.
 *
 * @author Jonathan Halliday (jonathan.halliday@redhat.com)
 * @since 2015-01
 */
public class MessageHandler {

    private final static Cluster cluster;
    private final static Session session;

    private final static PreparedStatement query1;
    private final static PreparedStatement query2;
    private final static PreparedStatement query3;
    private final static PreparedStatement queryGetHyperLog;

    private final static int MAX_OUTSTANDING_FUTURES=100 ;
    private final static int ACCESS_COUNTER_SIZE=50;

    private final BlockingQueue<ResultSetFuture> outstandingFutures = new LinkedBlockingQueue<>(MAX_OUTSTANDING_FUTURES);

    private final HashMap accessCounters = new LinkedHashMap<String, UrlHourCounter>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            if (accessCounters.size() >= ACCESS_COUNTER_SIZE) {
                UrlHourCounter urlHourCounter = (UrlHourCounter) eldest.getValue();
                BoundStatement statement = new BoundStatement(query1).bind(urlHourCounter.getCounter(), urlHourCounter.getUrl(), urlHourCounter.getHour());
                executeAsyncQuery(statement);
            }
            return accessCounters.size() >= ACCESS_COUNTER_SIZE;
        }
    };
    // Stores currently processing user session. removeEldestEntry allows to use memory more efficiently.
    private final HashMap userSessions = new LinkedHashMap<String, SiteSession>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            SiteSession siteSession = (SiteSession)eldest.getValue();
            boolean shouldExpire = siteSession.isExpired();
            //If Session expired push to persistent layer (Cassandra)
            if(shouldExpire) {
                try {
                    //Format dates
                    Date startDate = new Date(siteSession.getFirstHitMillis());
                    Date endDate = new Date(siteSession.getLastHitMillis());

                    String id = siteSession.getId();
                    long access = siteSession.getHitCount();
                    long url = siteSession.getHyperLogLog().cardinality();

                    BoundStatement statement = new BoundStatement(query2).bind(id, startDate, endDate, access, url);
                    executeAsyncQuery(statement);
                    handleQuery3(siteSession);
                    //executeSynchQuery(statement);
                } catch (Exception e) {
                    System.err.println(e);
                }
            }
            return siteSession.isExpired();
        }
    };
    static {

        cluster = new Cluster.Builder()
                .addContactPoint("127.0.0.1")
                .build();

        //create keySpace
        final Session bootstrapSession = cluster.connect();
        bootstrapSession.execute("CREATE KEYSPACE IF NOT EXISTS csc8101 WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1 }");
        bootstrapSession.close();

        session = cluster.connect("csc8101");

        //Create Table for query1
        session.execute("CREATE TABLE IF NOT EXISTS query1 " +
                "(counter_value counter, " +
                "url varchar," +
                "hour int," +
                "PRIMARY KEY (url, hour) )");
        //Create Table for query2
        session.execute("CREATE TABLE IF NOT EXISTS query2 (user_id varchar, " +
                "session_start timestamp, " +
                "session_end timestamp, " +
                "no_access bigint, " +
                "distinct_url_accessed bigint, " +
                "PRIMARY KEY (user_id, session_end) )");
        //Create Table for query3
        session.execute("CREATE TABLE IF NOT EXISTS query3 (user_id varchar, " +
                "distinct_url_accessed bigint, " +
                "hyper_log blob, " +
                "PRIMARY KEY (user_id) )");

        //Prepare queries
        query1 = session.prepare("UPDATE query1 SET counter_value=counter_value+? WHERE url=? AND hour=?");
        query2 = session.prepare("INSERT INTO query2 (user_id, session_start, session_end, no_access, distinct_url_accessed) VALUES (?, ?, ?, ?, ?)");
        query3 = session.prepare("INSERT INTO query3 (user_id, distinct_url_accessed, hyper_log) VALUES (?, ?, ?)");
        queryGetHyperLog = session.prepare("SELECT hyper_log FROM query3 WHERE user_id=?");
    }

    public static void close() {
        session.close();
        cluster.close();
    }

    public void flush() {

        try {
            //Push the sessions that are still in memory
            for(Object o: userSessions.values()) {
                SiteSession session = (SiteSession) o;

                Date startDate = new Date(session.getFirstHitMillis());
                Date endDate = new Date(session.getLastHitMillis());
                String id = session.getId();
                long access = session.getHitCount();
                long urls = session.getHyperLogLog().cardinality();

                BoundStatement statement = new BoundStatement(query2).bind(id, startDate, endDate, access, urls);
                executeAsyncQuery(statement);
                handleQuery3(session);
                //executeSynchQuery(statement);
            }
            for(Object o: accessCounters.values()) {
                UrlHourCounter urlHourCounter = (UrlHourCounter) o;
                BoundStatement statement = new BoundStatement(query1).bind(urlHourCounter.getCounter(), urlHourCounter.getUrl(), urlHourCounter.getHour());
                executeAsyncQuery(statement);
            }
            //Finish the outstanding futures
            while(!outstandingFutures.isEmpty()) {
                ResultSetFuture resultSetFuture = outstandingFutures.take();
                resultSetFuture.getUninterruptibly();
            }
        } catch (Exception e) {
            System.err.println("ERROR(flush)");
            e.printStackTrace();
        }

    }

    public void handle(String message) {
        handleQuery1(message);
        handleQuery2(message);
    }

    /**
     * handles messages and populates "query1" table
     * for efficient querying.
     *
     * @param message - Currently preprocessed message
     */
    private void handleQuery1(String message) {
        try {
            //Data Parsing, retrieving hour and url
            String[] data = message.split(" ");
            String[] date = data[1].split(":");

            int hour = Integer.parseInt(date[1]);
            String url = data[4].toLowerCase();

            String key = url + " " + hour;

            UrlHourCounter urlHourCounter = (UrlHourCounter)  accessCounters.get(key);
            if (urlHourCounter == null) {
                accessCounters.put(url + " " + hour, new UrlHourCounter(url, hour, new Long(1)));
            } else {
                accessCounters.put(url + " " + hour, urlHourCounter.incrementCounter());
            }
            /*BoundStatement statement = new BoundStatement(query1).bind(new Long(1), url, hour);
            executeAsyncQuery(statement);*/
            //executeSynchQuery(statement);
        }
        catch (Exception e) {
            System.err.println("ERROR(HandleQuery1):");
            System.err.println(message);
            e.printStackTrace();
        }
    }
    private void handleQuery2(String message) {
        try {
            String[] data = message.split(" ");

            final String userId = data[0];
            final Long date = formatDateInStringToMillis(data[1].replace("[", ""));
            final String url = data[4].toLowerCase();
            //If session is still stored load it else store a new one
            if (userSessions.containsKey(userId)) {
                SiteSession currentSession = (SiteSession) userSessions.get(userId);
                // If session is expired push it to persistent layer and store a new session to main memory
                // Else update the session
                if (currentSession.isExpired() || currentSession.isExpired(date)) {

                    Date startDate = new Date(currentSession.getFirstHitMillis());
                    Date endDate = new Date(currentSession.getLastHitMillis());
                    String id = currentSession.getId();
                    long access = currentSession.getHitCount();
                    long urls = currentSession.getHyperLogLog().cardinality();

                    BoundStatement statement = new BoundStatement(query2).bind(id, startDate, endDate, access, urls);
                    executeAsyncQuery(statement);
                    handleQuery3(currentSession);
                    //executeSynchQuery(statement);
                    userSessions.remove(userId);
                } else {
                    currentSession.update(date, url);
                }
            } else {
                SiteSession currentSession = new SiteSession(userId, date, url);
                userSessions.put(userId, currentSession);
            }
        } catch (ParseException e) {
            System.err.println("ERROR - Parsing date failed");
        } catch (Exception e) {
            System.err.println(e);
            //e.printStackTrace();
        }
    }
    private void handleQuery3(SiteSession siteSession) throws IOException, CardinalityMergeException {
        //Retrieve HyperLogLog from cassandra
        ResultSet result =  session.execute(queryGetHyperLog.bind(siteSession.getId()));
        //Check if it exists
        if (result.isExhausted()) {
            //Create a record with new HyperLogLog userId and unique urls accessed
            BoundStatement statement = new BoundStatement(query3).bind(siteSession.getId(), siteSession.getHyperLogLog().cardinality(), HLLToByteBuffer(siteSession.getHyperLogLog()));
            executeAsyncQuery(statement);
        } else {
            //Retrieve HyperLogLog from ByteBuffer and merge it with the currently proccessing one
            ByteBuffer rebuiltByteBuffer = result.one().getBytes("hyper_log");
            HyperLogLog rebuiltHll = ByteBufferToHLL(rebuiltByteBuffer);
            rebuiltHll.addAll(siteSession.getHyperLogLog());

            BoundStatement statement = new BoundStatement(query3).bind(siteSession.getId(), rebuiltHll.cardinality(), HLLToByteBuffer(rebuiltHll));
            executeAsyncQuery(statement);
        }
    }


    /**
     * Converts byteBuffer to HyperLogLog
     *
     * @param byteBuffer
     * @return HyperLogLog
     * @throws IOException
     */
    private static HyperLogLog ByteBufferToHLL(ByteBuffer byteBuffer) throws IOException {
        byte[] rebuiltBytes = new byte[byteBuffer.getInt()];
        byteBuffer.get(rebuiltBytes);
        return HyperLogLog.Builder.build(rebuiltBytes);
    }

    /**
     * Converts HyperLogLog to ByteBuffer
     *
     * @param hll HyperLogLog
     * @return HyperLogLog in ByteBuffer
     * @throws IOException
     */

    private static ByteBuffer HLLToByteBuffer(HyperLogLog hll) throws IOException {
        byte[] inputBytes = hll.getBytes();
        ByteBuffer byteBuffer = ByteBuffer.allocate(4+inputBytes.length);
        byteBuffer.putInt(inputBytes.length);
        byteBuffer.put(inputBytes);
        byteBuffer.flip();
        return byteBuffer;
    }

    /**
     * Converts date from milliseconds to String
     *
     * @param milis date in milliseconds
     * @return date as String in format dd/MMM/yyyy:HH:mm:ss
     */

    //Date could be stored as String, but it was chose to store as Date so this method is not needed
    private static String formatDateInMiliToString(long milis) {
        Date date = new Date(milis);
        DateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss");
        return formatter.format(date);
    }

    /**
     * Converts date in string to long
     *
     * @param date as String in format dd/MMM/yyyy:HH:mm:ss
     * @return date converted to miliseconds.
     * @throws ParseException
     */

    private static long formatDateInStringToMillis(String date) throws ParseException {
        DateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss");
        return formatter.parse(date).getTime();
    }

    /**
     * Method which executed @BoundStatement synchronously.
     * This is much slower than @executeAsyncQuery so it should not
     * be used unless there is reason to.
     *
     * @param statement going to be executed synchronously
     */
    private void executeSynchQuery(BoundStatement statement) {
        try {
            session.execute(statement);
        } catch (Exception e) {
            System.err.println("ERROR(executeSynchQuery)");
            e.printStackTrace();
        }
    }

    /**
     * Method which executes @BoundStatement asynchronously
     * ResultSetFuture is saved in @LinkedBlockingQueue<ResultSetFuture>.
     * When the Queue is filled the statements will be finished using
     * #ResultSetFuture.getUninterruptibly().
     *
     * @param statement going to be executed asynchronously
     */
    private void executeAsyncQuery(BoundStatement statement) {
        ResultSetFuture resultSetFuture = session.executeAsync(statement);
        try {
            outstandingFutures.put(resultSetFuture);
            if (outstandingFutures.remainingCapacity() == 0) {
                ResultSetFuture rsf = outstandingFutures.take();
                rsf.getUninterruptibly();
            }
        } catch(Exception e) {
            System.err.println("ERROR(executeAsyncQuery)");
            e.printStackTrace();
        }
    }
}
