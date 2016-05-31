package uk.ac.ncl.exchange.data;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import uk.ac.ncl.exchange.manager.DynamoDBManager;
import uk.ac.ncl.exchange.model.Session;

import javax.inject.Inject;

/**
 * Created by B4047409 on 25/02/2015.
 */
public class SessionRepository {

    @Inject
    private DynamoDBManager manager;

    /**
     * Method to find Session by Token uniqueId
     *
     * @param token key of the session
     * @return Session that matches the uniqueId token
     */
    public Session findByToken(String token) {
        return manager.getDynamoDBMapper().load(Session.class, token);
    }
}
