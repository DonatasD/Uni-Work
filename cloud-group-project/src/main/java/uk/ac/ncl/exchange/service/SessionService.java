package uk.ac.ncl.exchange.service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import uk.ac.ncl.exchange.data.SessionRepository;
import uk.ac.ncl.exchange.data.UserRepository;
import uk.ac.ncl.exchange.manager.DynamoDBManager;
import uk.ac.ncl.exchange.model.Session;
import uk.ac.ncl.exchange.model.User;
import uk.ac.ncl.exchange.validation.SessionValidation;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.logging.Logger;

/**
 * Created by B4047409 on 25/02/2015.
 */
public class SessionService {

    @Inject
    DynamoDBManager manager;
    @Inject
    SessionRepository sessionRepository;
    @Inject
    UserRepository userRepository;
    @Inject
    UserService userService;
    @Inject
    SessionValidation validation;
    @Inject
    @Named("logger")
    private Logger log;

    public Session findByToken(String token) {
        return sessionRepository.findByToken(token);
    }

    /**
     * Method to create and persist a new token or return still valid token
     *
     * @param userName - String defining user name
     */
    public Session createToken(String userName) {
        log.info("createToken - finding user");
        User user = userRepository.findByUserName(userName);
        // check if user already has associated token
        log.info("createToken - checking if token is created");
        if (user.getToken() != null && !user.getToken().equals("")) {
            log.info("createToken - loading session");
            Session session = sessionRepository.findByToken(user.getToken());
            // check if his session has not expired yet
            log.info("createToken - checking if loaded session exists");
            if (session != null) {
                log.info("createToken - checking is session's timestamp is expired");
                if (!validation.timestampExpired(session)) {
                    log.info("createToken - valid token is already created");
                    return session;
                } else {
                    log.info("createToken - remove expired token");
                    deleteExpiredSession(session);
                }
            }
        }
        log.info("createToken - initialise new session");
        Session session = Session.init(userName);
        /* 
            While token with uniqueId already exists recreate session to make sure it is unique for sure.
            Should not happen very often check UUID guarantees.
         */
        log.info("createToken - check if created token was not created before");
        int count = 1;
        while (validation.tokenExists(session.getToken())) {
            log.warning("createToken - regenerating session with attempt[" + count + "]");
            session = Session.init(userName);
            count++;
        }
        // Link user data with newly generated token
        log.info("createToken - prepare token to be persisted");
        user.prepareUserForLogin(session.getToken());
        // Persist session and user
        DynamoDBMapper mapper = manager.getDynamoDBMapper();
        log.info("createToken - persist session");
        //TODO hash token
        mapper.save(session);
        log.info("createToken - update user's token in persistent layer");
        mapper.save(user);
        return session;
    }

    /**
     * Authenticate provided token
     *
     * @param token - provided token
     * @return boolean indicating authentication outcome
     */
    public boolean authenticateToken(String token) {
        log.info("authenticateToken - retrieving session from database");
        Session session = sessionRepository.findByToken(token);
        if (session == null) {
            log.info("authenticationToken - session could not be found");
            log.info("authenticateToken - token authentication failed");
            return false;
        }
        log.info("authenticateToken - retrieve user linked with this session");
        User user = userRepository.findByUserName(session.getUserName());
        log.info("authenticateToken - check is session's timestamp is expired");
        if (validation.timestampExpired(session)) {
            log.info("authenticateToken - token is expired");
            log.info("authenticateToken - check if such user exists");
            if (user != null) {
                log.info("authenticateToken - remove expired token from user table");
                userService.deleteToken(user);
            }
            log.info("authenticateToken - remove expired session from Session table");
            deleteExpiredSession(session);
            log.info("authenticateToken - token authentication failed");
            return false;
        }
        log.info("authenticateToken - check if session token matches token persisted in User table");
        if (session.getToken().equals(user.getToken())) {
            updateSessionTimestamp(session);
            log.info("authenticateToken - successful");
            return true;
        }
        log.info("authenticateToken - token authentication failed");
        return false;
    }

    /**
     * Deletes specified session
     *
     * @param session - Session which is going to be deleted
     */
    public void deleteExpiredSession(Session session) {
        DynamoDBMapper mapper = manager.getDynamoDBMapper();
        mapper.delete(session);
    }

    /**
     * Updates specified session's timestamp
     *
     * @param session - This sessions's timestamp will be updated
     */
    private void updateSessionTimestamp(Session session) {
        session.updateSessionTimestamp();
        DynamoDBMapper mapper = manager.getDynamoDBMapper();
        mapper.save(session);
    }
}
