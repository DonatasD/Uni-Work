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

package uk.ac.ncl.exchange.service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import uk.ac.ncl.exchange.data.PublicKeyRepository;
import uk.ac.ncl.exchange.data.SessionRepository;
import uk.ac.ncl.exchange.data.UserRepository;
import uk.ac.ncl.exchange.manager.DynamoDBManager;
import uk.ac.ncl.exchange.model.AddPublicKey;
import uk.ac.ncl.exchange.model.PublicKeys;
import uk.ac.ncl.exchange.model.User;
import uk.ac.ncl.exchange.model.Session;
import uk.ac.ncl.exchange.security.Hash;
import uk.ac.ncl.exchange.validation.UserValidator;

import javax.ejb.DuplicateKeyException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by Donatas Daubaras on 23/02/15.
 * <p/>
 * This class is responsible for providing services to user (login, register, add a public key).
 *
 * Last Updated By: Sheryl Coe
 * Last Updated Date: 11/03/2015
 *
 */

public class UserService {

    @Inject
    DynamoDBManager manager;

    @Inject
    private UserRepository userRepository;

    @Inject
    private SessionRepository sessionRepository;

    @Inject
    private PublicKeyRepository publicKeyRepository;

    @Inject
    private UserValidator uservalidator;

    @Inject
    private EmailService emailService;

    @Inject
    @Named("logger")
    private Logger log;

    /**
     * Register the user with provided details to persistent layer (S3).
     * Before persisting user it is being checking using UserValidator.
     * Before persisting user to database all not necessary data is removed.
     *
     * @param user User that is being registered
     * @throws Exception
     */
    public void register(User user) throws DuplicateKeyException, ConstraintViolationException, NoSuchAlgorithmException {
        // Validate user
        uservalidator.validateUser(user);

        //Remove not needed data and hash password
        log.info("register - removed not needed data");
        user.prepareUserForRegister();

        //Send verification email to user
        emailService.verifyEmail(user.getEmail());

        // Persist user
        log.info("register - registering user to database");
        userRepository.createUser(user);
    }

    /**
     * Authenticates user to check if the correct details are provided.
     * boolean indicates whether the authentication was successful or not.
     * Authentication can fail if NoSuchAlgorithmException occurs during password hashing.
     *
     * @param user
     * @return boolean indicating if authentication was successful or not
     */
    public boolean authenticate(User user) throws NoSuchAlgorithmException, IOException {
        // check if user is registered in database
        log.info("authenticate - checking if user is registered");
        if (!uservalidator.isRegistered(user.getUserName())) {
            return false;
        }
        User userFromDB = userRepository.findByUserName(user.getUserName());
        log.info("authenticate - checking if email is verified");
        //Check if email is verified
        if (!emailService.isEmailVerified(userFromDB.getEmail())) {
            return false;
        }
        // check if password matches
        log.info("authenticate - checking if password matches");
        return userFromDB.getPassword().equals(Hash.HashString(user.getPassword()));
    }

/*    *//**
     * Sheryl Coe - this is no longer needed as the users public keys have been moved to a separate table
     *
     * Method to update user's public key
     *
     * @param user
     * @return UpdateItemResult
     *//*

    public String updatePublicKey(User user) throws AmazonServiceException {

        log.info("updatePublicKey - start update");
        Map<String, AttributeValueUpdate> updateItems =
                new HashMap<String, AttributeValueUpdate>();
        //get the attribute UserName as key
        HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
        key.put("UserName", new AttributeValue().withS(user.getUserName()));
        //update the attribute Public Key with user public key
        updateItems.put("Public Key",
                new AttributeValueUpdate()
                        .withValue(new AttributeValue().withS(user.getPublicKey())));

        ReturnValue returnValues = ReturnValue.ALL_NEW;
        //do update operation on table with key and update item
        UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                .withTableName("Users")
                .withKey(key)
                .withAttributeUpdates(updateItems)
                .withReturnValues(returnValues);

        UpdateItemResult result = manager.getClient().updateItem(updateItemRequest);
        log.info("updatePublicKey - update success");
        return result.getAttributes().get("Public Key").getS();
    }*/

    /**
     * Sheryl Coe
     *
     * Method to add user's public key
     *
     * @param addPublicKey - contains public key (username will come from the session)
     * @return returns the unique id for the public key
     * @throws Exception
     */

    public String addPublicKey(AddPublicKey addPublicKey, String token) throws Exception {

        Session session = sessionRepository.findByToken(token);
        PublicKeys publicKeys = new PublicKeys();

        String publicKeyID = generateUniqueId();
        log.info("addPublicKey - new UUID created for public key: "+publicKeyID);
        publicKeys.setId(publicKeyID);
        publicKeys.setUserName(session.getUserName());
        publicKeys.setPublicKey(addPublicKey.getPublicKey());

        log.info("addPublicKey - registering new public key to database");
        // Create DynamoDBMapper, which is responsible to persist data to DynamoDB
        publicKeyRepository.createPublicKey(publicKeys);
        return publicKeyID;
    }

    /**
     * Method to generate a unique public key id.
     * Gives 122bit security 6 bits are lost due to pseudo randomness.
     *
     * @return uniqueId The session's unique identifier
     */
    public static String generateUniqueId() {
        return UUID.randomUUID().toString();
    }


    public void deleteToken(User user) {
        user.setToken("");
        DynamoDBMapper mapper = manager.getDynamoDBMapper();
        mapper.save(user);
    }
}
