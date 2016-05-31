package uk.ac.ncl.exchange.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import uk.ac.ncl.exchange.security.Hash;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;


/**
 * Created by Sheryl Coe on 11/03/2015.
 *
 * This class defines the DynamoDB table for users Public Keys.
 *
 */

@DynamoDBTable(tableName = "Public_Keys")
public class PublicKeys implements Serializable {

    @NotNull
    private String Id;

    @NotNull
    @Size(min = 4, max = 20)
    private String userName;

    @NotNull
    private String publicKey;

    @DynamoDBRangeKey(attributeName = "Id")
    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    @DynamoDBHashKey(attributeName = "UserName")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @DynamoDBAttribute(attributeName = "Public Key")
    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public String toString() {
        return "Public_Key{" +
                "Id='" + Id + '\'' +
                ", userName='" + userName + '\'' +
                ", publicKey='" + publicKey + '\'' +
                '}';
    }
}
