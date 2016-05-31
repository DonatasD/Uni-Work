/*
 * Copyright (c) 2015. Donatas Daubaras
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package uk.ac.ncl.exchange.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import uk.ac.ncl.exchange.security.Hash;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Donatas Daubaras on 23/02/15.
 * <p/>
 * This class defines DynamoDBTable and the Entity format that is going to be stored or retrieved from DB.
 */
@DynamoDBTable(tableName = "Users")
public class User implements Serializable {

    @NotNull
    @Size(min = 4, max = 20, message = "user name cannot be shorter than 4 characters and longer than 20")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "user name must be a alphanumeric string ")
    private String userName;

    @NotNull
    @Size(min = 6, max = 30, message = "password must be at least 6 and shorter than 30 characters")
    private String password;

    //Sheryl Coe - users public keys have been moved to a separate table (for use with off-line processing)
  /*  @NotNull
    private String publicKey;*/

    @NotNull
    @NotEmpty
    @Email(message = "The email address must be in the format of name@domain.com")
    private String email;

    private String token;

    @DynamoDBHashKey(attributeName = "UserName")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @DynamoDBAttribute(attributeName = "Password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

  /*  @DynamoDBAttribute(attributeName = "Public Key")
    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
*/
    @DynamoDBAttribute(attributeName = "Token")
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @DynamoDBAttribute(attributeName = "Email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", token='" + token + '\'' +
                '}';
        //", publicKey='" + publicKey + '\'' +
    }

    public void prepareUserForRegister() throws NoSuchAlgorithmException {
        token = "";
        password = Hash.HashString(password);
    }

    public void prepareUserForLogin(String token) {
        this.token = token;
    }
}
