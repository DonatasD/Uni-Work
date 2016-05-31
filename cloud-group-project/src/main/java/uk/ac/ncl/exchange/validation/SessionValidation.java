package uk.ac.ncl.exchange.validation;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import uk.ac.ncl.exchange.data.SessionRepository;
import uk.ac.ncl.exchange.data.UserRepository;
import uk.ac.ncl.exchange.model.Session;
import uk.ac.ncl.exchange.service.UserService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Created by Donatas Daubaras on 03/03/2015.
 */
public class SessionValidation {
    @Inject
    private SessionRepository sessionRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserService userService;

    @Inject
    @Named("logger")
    private Logger log;

    /**
     * Method to check of the session's timestamp has expired
     *
     * @param session
     * @return boolean indicating if session is expired
     */
    public boolean timestampExpired(Session session) {
        log.info("timestampExpired - Checking if timestamp has expired");
        Date currentDate = new Date();

        //Get the session's timestamp as string
        String timestamp = session.getTimestamp();
        //Removing the last 4 characters from the timestamp (milliseconds)
        //in order to parse as joda time
        //timestamp = timestamp.substring(0, timestamp.length() - 4);

        //Using Joda time
        DateTimeFormatter dateStringFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        //Parsing the timestamp to joda time
        DateTime time = dateStringFormat.parseDateTime(timestamp);
        //Adding 30 minutes
        time = time.plusMinutes(30);
        //Converting joda time to java.util.Date
        Date date = time.toDate();
        log.info("timestampExpired - current time: " + currentDate.toString());
        log.info("timestampExpired - timestamp expires: " + date.toString());
        //Comparing timestamp +30 minutes to current time
        return date.compareTo(currentDate) < 0;
    }

    /**
     * checks if timestamp was created before the current time
     *
     * @param session
     * @return boolean indicating if the the timestamp
     * was created before the current time
     */
    private boolean timestampBeforeCurrentTime(Session session) {
        log.info("timestampBeforeCurrentTime - Checking if timestamp is created before current time");
        Date currentDate = new Date();

        String timestamp = session.getTimestamp();

        //timestamp = timestamp.substring(0, timestamp.length() - 4);

        DateTimeFormatter dateStringFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

        DateTime time = dateStringFormat.parseDateTime(timestamp);

        Date date = time.toDate();
        //True if timestamp is before current time
        return currentDate.compareTo(date) > 0;
    }

    /**
     * Check if Token already exists in a Session
     *
     * @param token - String indicating token that is being checked
     * @return boolean indicating if session already exists
     */
    public boolean tokenExists(String token) {
        log.info("tokenExists - Checking if Token already exists");
        //check if session exists
        return sessionRepository.findByToken(token) != null;
    }
}
