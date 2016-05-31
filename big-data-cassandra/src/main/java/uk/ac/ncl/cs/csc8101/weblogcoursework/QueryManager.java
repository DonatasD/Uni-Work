package uk.ac.ncl.cs.csc8101.weblogcoursework;

import com.datastax.driver.core.*;
import com.datastax.driver.core.querybuilder.QueryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static com.datastax.driver.core.querybuilder.QueryBuilder.*;

/**
 * Created by don on 13/02/15.
 */
public class QueryManager {

    private final static Cluster cluster;
    private final static Session session;

    private final static ArrayList<String> urls;
    private final static int startHour;
    private final static int endHour;

    private final static String userId;

    static {
        cluster = new Cluster.Builder()
                .addContactPoint("127.0.0.1")
                .build();

        final Session bootstrapSession = cluster.connect();
        bootstrapSession.execute("CREATE KEYSPACE IF NOT EXISTS csc8101 WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1 }");
        bootstrapSession.close();

        session = cluster.connect("csc8101");


        //Input values
        startHour = 1;
        endHour = 10;
        urls = new ArrayList<String>(){{
            add("/english/teams/teamqualify160.htm");
            add("/french/news/11128.htm");
        }};
        userId = "1000";
    }
    
    public static void main(String[] args) {
        try {
            query1(startHour,endHour,urls);
            query2(userId);
            query3(userId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
            cluster.close();
        }
    }

    private static void query1(int startHour, int endHour, ArrayList<String> urls) {
        final PreparedStatement selectPs = session.prepare("SELECT * FROM query1 WHERE url IN ? AND hour>=? AND hour<=?");
        ResultSet resultSet = session.execute( new BoundStatement(selectPs).bind(urls,startHour,endHour));
        if(!resultSet.isExhausted()) {
            System.out.println("url\thour\tcount");
            for (Row row: resultSet.all()) {
                System.out.println(row.getString("url")
                        +"\t"+row.getInt("hour")
                        +"\t"+row.getLong("counter_value"));
            }
        }
    }
    private static void query2(String userId) {
        Statement statement = QueryBuilder.select()
                .all()
                .from("query2")
                .where(eq("user_id", userId))
                .limit(100);
                //limit optional

        ResultSet rs = session.execute(statement);
        if(!rs.isExhausted()) {
            System.out.println("clientId\tstart_time\tend_time\tnumber_of_accesses\tnumber_of_distinct_urls_accessed");
            for (Row row: rs.all()) {
                System.out.println(row.getString("user_id")
                        +"\t"+row.getDate("session_start")
                        +"\t"+row.getDate("session_end")
                        +"\t"+row.getLong("no_access")
                        + "\t" + row.getLong("distinct_url_accessed"));
            }
        }
    }
    private static void query3(String userId) {
        Statement statement = QueryBuilder.select()
                .all()
                .from("query3")
                .where(eq("user_id", userId))
                .limit(100);
                //limit optional

        ResultSet rs = session.execute(statement);
        if(!rs.isExhausted()) {
            System.out.println("clientId\tnumber_of_distinct_urls_accessed");
            for(Row row: rs.all()) {
                System.out.println(row.getString("user_id")
                        + "\t" + row.getLong("distinct_url_accessed"));
            }
        }
    }
}
