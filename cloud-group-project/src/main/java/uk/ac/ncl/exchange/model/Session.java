package uk.ac.ncl.exchange.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import uk.ac.ncl.exchange.security.Token;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Created by B4047409 on 25/02/2015.
 * <p/>
 * A class to define the DynamoDBTable and the Entity format to be stored or retrieved from the DB
 */
@DynamoDBTable(tableName = "Sessions")
public class Session implements Serializable {

    @NotNull
    private String token;

    @NotNull
    @Size(min = 4, max = 20)
    private String userName;

    @NotNull
    private String timestamp;

    /**
     * Initialises new Session for specified user.
     *
     * @param userName
     * @return created Session
     */
    public static Session init(String userName) {
        Session session = new Session();
        session.setToken(Token.generateUniqueId());
        session.setTimestamp(Token.generateTimestamp());
        session.setUserName(userName);
        return session;
    }

    @DynamoDBHashKey(attributeName = "Token")
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @DynamoDBAttribute(attributeName = "UserName")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @DynamoDBAttribute(attributeName = "TimeStamp")
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Session {" +
                "token='" + token + '\'' +
                ", userName='" + userName + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }

    public void updateSessionTimestamp() {
        timestamp = Token.generateTimestamp();
    }
}
