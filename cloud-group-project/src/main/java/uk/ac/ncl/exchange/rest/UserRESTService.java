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

package uk.ac.ncl.exchange.rest;


import com.amazonaws.AmazonServiceException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import uk.ac.ncl.exchange.data.SessionRepository;
import uk.ac.ncl.exchange.data.UserRepository;
import uk.ac.ncl.exchange.model.AddPublicKey;
import uk.ac.ncl.exchange.model.PublicKeys;
import uk.ac.ncl.exchange.model.Session;
import uk.ac.ncl.exchange.model.User;
import uk.ac.ncl.exchange.service.EmailService;
import uk.ac.ncl.exchange.service.KeyService;
import uk.ac.ncl.exchange.service.SessionService;
import uk.ac.ncl.exchange.service.UserService;

import javax.ejb.DuplicateKeyException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * <p>
 * Exposes rest services that allows to control user authentication and creation.
 * </p>
 * Created by Donatas Daubaras on 22/02/15.
 *
 * Last Updated By: Sheryl Coe
 * Last Updated Date: 11/03/2015
 *
 */
@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class UserRESTService {

    @Inject
    private
    @Named("logger")
    Logger log;

    @Inject
    private UserRepository userRepository;

    @Inject
    private SessionRepository sessionRepository;

    @Inject
    private SessionService sessionService;

    @Inject
    private UserService userService;

    @Inject
    private EmailService emailService;

    @Inject
    KeyService keyService;

    @POST
    @Authenticate
    @Path("/validate-token")
    public Response authorize() {
        return Response.ok().build();
    }
    
    @GET
    @Authenticate
    public String listAllUsers() throws JSONException {
        log.info("listAllUsers - listing all users");
        List<User> users = userRepository.findAllOrderByUserName();
        JSONArray jsonArray = new JSONArray();
        for (User user : users) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userName", user.getUserName());
            jsonArray.put(jsonObject);
        }
        return jsonArray.toString();
    }

    /**
     * Registers user with specified details.
     *
     * @param user - User object that is trying to be created
     * @return Response with 200 on success
     */
    @Path("/register")
    @POST
    public Response register(User user) {
        //Create empty Response builder
        Response.ResponseBuilder builder;
        try {
            //Try to register user using UserService.register
            log.info("Registering User - " + user.getUserName());
            userService.register(user);
            //Create 200 Response
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("success", "Email to " + user.getEmail() + " was sent to verify this account");
            builder = Response.ok().entity(responseObj);
        } catch (DuplicateKeyException e) {
            // 409 response. Handle the unique constrain violation (user name already exists)
            log.info("InvalidInputException - " + e.toString());
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("userName", e.toString());
            builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
        } catch (ConstraintViolationException e) {
            // Handle bean validation issues (validation constrain i.e. password size, userName size)
            log.info("ConstraintViolationException - " + e.toString());
            builder = createViolationResponse(e.getConstraintViolations());
        } catch (NoSuchAlgorithmException e) {
            // 500 response Internal System Error (password hashing failed)
            log.info("NoSuchAlgorithmException - " + e.toString());
            Map<String, String> responseObj = new HashMap<>();
            builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseObj);
        } catch (Exception e) {
            // 400 response bad request
            log.info("Exception - " + e.toString());
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return builder.build();
    }

    /**
     * Authenticates user with his provided details. Issues a new token or returns an existing valid token
     *
     * @param user - contains user login details (userName, password)
     * @return token newly generated or existing valid one
     */
    @Path("/login")
    @POST
    public Response login(User user) {
        Response.ResponseBuilder builder;
        try {
            log.info("login - authenticating user");
            if (userService.authenticate(user)) {
                // Generate new or currently valid token
                log.info("login - authentication successful");
                log.info("login - creating new token");
                Session session = sessionService.createToken(user.getUserName());
                // Respond 200 with session token
                Map<String, String> responseObj = new HashMap<>();
                responseObj.put("token", session.getToken());
                builder = Response.status(Response.Status.OK).entity(responseObj);
            } else {
                // Return 401 Unauthorized (authentication failed)
                log.info("login - authentication failed");
                Map<String, String> responseObj = new HashMap<>();
                responseObj.put("error", "Bad login details");
                builder = Response.status(Response.Status.UNAUTHORIZED).entity(responseObj);
            }
        } catch (NoSuchAlgorithmException e) {
            // Respond 500 Internal Server Error (Hashing password has thrown an exception)
            log.severe("login - NoSuchAlgorithmException " + e.toString());
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", "Internal server error");
            builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseObj);
        } catch (Exception e) {
            log.info("login - " + e.toString());
            // Respond 400 Bad Request (user might not have specified all needed details)
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", "Bad login details");
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return builder.build();
    }

/*    *//**
     * Sheryl Coe - this is no longer needed as the users public keys have been moved to a separate table
     *
     * Updates user's public key. Authentication details has to be provided as well (userName, token)
     *
     * @param user - contains user userName, token and new public key
     * @return on success returns 200 response
     *//*
    @Path("/update-public-key")
    @POST
    @Authenticate
    public Response updatePublicKey(User user) {
        Response.ResponseBuilder builder;
        try {
            log.info("updatePublicKey - updating public key");
            String result = userService.updatePublicKey(user);
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("public key", result);
            builder = Response.status(Response.Status.OK).entity(responseObj);
        } catch (AmazonServiceException ase) {
            // Respond 500 Internal Server Error
            log.info("updatePublicKey - " + "Failed to update public key attribute");
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", "Internal server error");
            builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseObj);
        } catch (Exception e) {
            log.info("login - " + e.toString());
            // Respond 400 Bad Request (user might not have specified all needed details)
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", "Bad update public key details");
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return builder.build();

    }*/


    /**
     * Adds a user's public key. Authentication details has to be provided as well (userName, token)
     *
     * @param publicKey - contains userName, public key, session token to ensure user authorized
     * @return on success returns 200 response
     */
    @Path("/keys")
    @POST
    @Authenticate
    public Response createPublicKey(@HeaderParam("Authorization") String token, PublicKeys publicKey) {
        Response.ResponseBuilder builder;
        try {
            log.info("addPublicKey - adding new public key for user");
            //String publicKeyId = userService.addPublicKey(addPublicKey, token);
            String publicKeyId = keyService.createPublicKey(publicKey, token);
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("publicKeyId", publicKeyId);
            builder = Response.ok().entity(responseObj);
        } catch (ConstraintViolationException e) {
                // Handle bean validation issues (validation constrain i.e. password size, userName size)
                log.info("ConstraintViolationException - " + e.toString());
                builder = createViolationResponse(e.getConstraintViolations());
        } catch (Exception e) {
            log.info("login - " + e.toString());
            // Respond 400 Bad Request (user might not have specified all needed details)
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", "Bad update public key details");
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return builder.build();
    }

    /**
     * Creates a response that contains violations passed to method
     *
     * @param violations that occurred
     * @return Response which indicates violations
     */
    private Response.ResponseBuilder createViolationResponse(Set<ConstraintViolation<?>> violations) {
        log.fine("Validation completed. violations found: " + violations.size());
        Map<String, String> responseObj = new HashMap<>();
        for (ConstraintViolation<?> violation : violations) {
            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
        }
        // 400 response bad request
        return Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
    }

}
