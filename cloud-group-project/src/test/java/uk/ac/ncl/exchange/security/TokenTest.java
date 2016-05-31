package uk.ac.ncl.exchange.security;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertTrue;

public class TokenTest {

    private String tok1;
    private String tok2;
    private String timestamp1;
    private String timestamp2;
    private Date t1;
    private Date t2;

    @Test
    public void testTokens() {
        //Generating a token pair
        tok1 = Token.generateUniqueId();
        tok2 = Token.generateUniqueId();

        assertTrue(tok1 != tok2);
    }

    @Test
    public void testTimestamp() {
        //Generating two timestamps
        timestamp1 = Token.generateTimestamp();
        timestamp2 = Token.generateTimestamp();

        assertTrue(timestamp1 != timestamp2);
    }

    @Test
    public void testTimestampExpiration1() {
        timestamp1 = Token.generateTimestamp();
        timestamp2 = Token.generateTimestamp();

        //removing milliseconds (.mmm) from timestamp
        timestamp1 = timestamp1.substring(0, timestamp1.length() - 4);
        timestamp2 = timestamp2.substring(0, timestamp2.length() - 4);

        DateTimeFormatter dateStringFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

        //Parsing the timestamp to joda time
        DateTime time1 = dateStringFormat.parseDateTime(timestamp1);
        DateTime time2 = dateStringFormat.parseDateTime(timestamp2);

        //removing 30 minutes from t1
        time1 = time1.minusMinutes(30);

        //converting joda time to java.util.Date
        t1 = time1.toDate();
        t2 = time2.toDate();

        assertTrue(t1.compareTo(t2) < 0);
    }

    @Test
    public void testTimestampExpiration2() {
        timestamp1 = Token.generateTimestamp();
        timestamp2 = Token.generateTimestamp();

        //removing milliseconds (.mmm) from timestamp
        timestamp1 = timestamp1.substring(0, timestamp1.length() - 4);
        timestamp2 = timestamp2.substring(0, timestamp2.length() - 4);

        DateTimeFormatter dateStringFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

        //Parsing the timestamp to joda time
        DateTime time1 = dateStringFormat.parseDateTime(timestamp1);
        DateTime time2 = dateStringFormat.parseDateTime(timestamp2);

        //removing 30 minutes from t1
        time1 = time1.minusMinutes(30);

        //converting joda time to java.util.Date
        t1 = time1.toDate();
        t2 = time2.toDate();

        assertTrue(t1.before(t2));
    }

    @Test
    public void testTimestampBeforeCurrentTime() {
        timestamp1 = Token.generateTimestamp();
        Date currentDate = new Date();

        //removing milliseconds (.mmm) from timestamp
        timestamp1 = timestamp1.substring(0, timestamp1.length() - 4);

        DateTimeFormatter dateStringFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

        //Parsing the timestamp to joda time
        DateTime time1 = dateStringFormat.parseDateTime(timestamp1);

        //removing 1 minute from t1
        time1 = time1.minusMinutes(1);

        Date t = time1.toDate();

        System.out.println(currentDate);
        System.out.println(t);

        assertTrue(t.before(currentDate));
    }
}
