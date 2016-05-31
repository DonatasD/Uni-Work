package uk.ac.ncl.exchange.rest;

import com.amazonaws.services.s3.model.S3Object;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import uk.ac.ncl.exchange.data.ExchangeRepository;
import uk.ac.ncl.exchange.data.PublicKeyRepository;
import uk.ac.ncl.exchange.data.SessionRepository;
import uk.ac.ncl.exchange.data.UserRepository;
import uk.ac.ncl.exchange.model.*;

import uk.ac.ncl.exchange.security.Hash;
import uk.ac.ncl.exchange.security.SignatureVerification;
import uk.ac.ncl.exchange.service.EmailService;
import uk.ac.ncl.exchange.service.ExchangeService;
import uk.ac.ncl.exchange.service.KeyService;
import uk.ac.ncl.exchange.service.SessionService;
import uk.ac.ncl.exchange.util.Status;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.inject.Inject;
import javax.inject.Named;

import javax.ws.rs.*;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by Donatas Daubaras on 23/02/15.
 * <p/>
 * Enables fair exchange as REST service.
 */
@Path("/exchanges")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ExchangeRESTService {

    @Inject
    private
    @Named("logger")
    Logger log;

    @Inject
    private ExchangeService exchangeService;

    @Inject
    private UserRepository userRepository;

    @Inject
    private EmailService emailService;

    @Inject
    private ExchangeRepository exchangeRepository;

    @Inject
    private SessionRepository sessionRepository;

    @Inject
    private PublicKeyRepository publicKeyRepository;

    @Inject
    private KeyService keyService;

    @Inject
    private SessionService sessionService;

    //Basic requirements
    //TODO Notify second party of requests (SNS)
    //TODO Persist history of transactions (DynamoDB)
    //TODO Alice can cancel the exchange before Bob confirms it.

    //Services
    //TODO accept or decline exchange
    //TODO check history
    //TODO cancel exchange

    @GET
    @Authenticate
    public Response retrieveAllExchanges(@HeaderParam("Authorization") String token) {
        Session session = sessionService.findByToken(token);
        List<Exchange> exchanges = exchangeService.findAllByUserName(session.getUserName());
        if (exchanges.isEmpty()) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return Response.ok(exchanges).build();
    }

    @GET
    @Authenticate
    @Path("/{id}")
    public Response retrieveExchangeById(@PathParam("id") long id) {
        Exchange exchange = exchangeService.findById(id);
        if (exchange == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return Response.ok(exchange).build();
    }

    @GET
    @Authenticate
    @Path("/{id}/eoo")
    public Response retrieveEOOByID(@HeaderParam("Authorization") String token, @PathParam("id") long id) {
        log.info("starting eoo retrieval " + id);
        Exchange exchange = exchangeService.findById(id);
        String userName = sessionRepository.findByToken(token).getUserName();
        log.info(exchange.toString() + " " + userName);
        if (exchange == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        if (!exchange.getReceiver().equals(userName)) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        Map<String, String> responseObj = new HashMap<>();
        responseObj.put("signedHashFile", exchange.getDocumentHash());
        return Response.status(Response.Status.OK).entity(responseObj).build();
    }

    @GET
    @Authenticate
    @Path("/{id}/document")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response retrieveExchangeDocument(@HeaderParam("Authorization") String token, @PathParam("id") long id) {

        // Find the user of the current session
        Session session = sessionService.findByToken(token);

        // Get the exchange by it's id to retrieve the document
        final Exchange exchange = exchangeService.findById(id);

        if (exchange == null) {
            // The resource was not found
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        if (exchange.getStatus() != Status.SUCCESS) {
            // The user is not authorized to perform such action
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }

        if (!exchange.getReceiver().equals(session.getUserName())) {
            // The user is not authorized to perform such action
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }

        // Get the object that needs to be retrieved by client
        final S3Object obj = exchangeService.getDocument(
                exchange.getSender(),
                exchange.getDocument());

        // OR: use a custom StreamingOutput and set to Response
        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException {
                try {
                    IOUtils.copy(obj.getObjectContent(), output);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        return Response.ok(stream, obj.getObjectMetadata().getContentType())
                .header("Content-Disposition", "attachment; filename=" + exchange.getDocument())
                .build();
    }

    /**
     * REST service to start the fair exchange
     *
     * @param bean Information to start fair exchange
     * @return Response indicating outcome of request
     */
    @POST
    @Authenticate
    public Response createExchange(@HeaderParam("Authorization") String token, ExchangeBean bean) {

        Response.ResponseBuilder builder;

        // Find the user of the current session
        Session session = sessionService.findByToken(token);

        // Find the public key of the initiator
        PublicKeys publicKeys = keyService.findByUserNameAndKeyId(
                session.getUserName(),
                bean.getSenderPublicKeyID());

        boolean isValidEOO = false;
        try {
            // Verify the signature using the SignatureVerification
            isValidEOO = SignatureVerification.verifyDocuments(
                    bean.getSignedHashFile(), bean.getFile(),
                    publicKeys.getPublicKey());
        } catch (Exception e) {
            // Could not verify the signature
            Map<String, String> responseMessage = new HashMap<>();
            responseMessage.put("message", "Error while trying to verify the signature, this might " +
                    "be due to incorrect signature format or wrong algorithm specification.");
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseMessage);
            return builder.build();
        }

        if (!isValidEOO) {
            // Could not verify the signature
            Map<String, String> responseMessage = new HashMap<>();
            responseMessage.put("message", "Signature could not be verified.");
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseMessage);
            return builder.build();
        }

        Exchange exchange = new Exchange();
        exchange.setExchangeName(bean.getExchangeName());
        exchange.setSender(session.getUserName());
        exchange.setSenderPublicKeyID(bean.getSenderPublicKeyID());
        exchange.setReceiver(bean.getReceiver());
        exchange.setDocumentHash(bean.getSignedHashFile());
        exchange.setStatus(Status.IN_PROGRESS);

        try {

            // If this fail, will throw a IOException
            exchange.setDocument(exchangeService.createFileAndGetFileName(
                    bean.getFile(),
                    bean.getFileExtension(),
                    session.getUserName()));

            // Try to persist the information
            exchangeService.create(exchange);

            // Get the receiver's email from the User entity
            User user = userRepository.findByUserName(bean.getReceiver());

            // Send an email to the user to notify him that he has a document
            emailService.emailForConfirmation(user.getEmail(), "You have a new request from " +
                    exchange.getSender());

            // Successful creation
            Map<String, String> responseMessage = new HashMap<>();
            responseMessage.put("message", "Successfully created exchange");
            builder = Response.ok().entity(responseMessage);
        } catch (IOException e) {
            // Could not create the file
            Map<String, String> responseMessage = new HashMap<>();
            responseMessage.put("message", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseMessage);
        } catch (Exception e) {
            // Could not create the exchange
            Map<String, String> responseMessage = new HashMap<>();
            responseMessage.put("message", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseMessage);
        }
        return builder.build();
    }

    /**
     * Sheryl Coe - 12/03/2015
     * REST service for receiver to send a proof of receipt (NRR)
     *
     * @param // Information from receiver sending the NRR (proof of receipt), includes exchange ID, signed receipt and the receiver public key ID
     * @return Response indicating outcome of request
     */
    @PUT
    @Path("/{id}")
    @Authenticate
    public Response putReceipt(@HeaderParam("Authorization") String token, @PathParam("id") long id, ReceiptExchange receiptExchange) {
        Response.ResponseBuilder builder;
        try {
            log.info("starting accept exchange");
            Exchange exchangeDB = exchangeRepository.findById(id);
            Session session = sessionRepository.findByToken(token);
            String userName = session.getUserName();

            User user = userRepository.findByUserName(exchangeDB.getSender());
            log.info(exchangeDB.toString() + " " + session + " " + userName + " " + user);
            // Check if user is a receiver, can only send a receipt if they are the receiver for this exchange ID
            // And the status of the exchange is IN_PROGRESS
            if (userName.equals(exchangeDB.getReceiver())){
                log.info("sendReceipt - receiver is sending the proof of receipt (NRR)");

              /*  PublicKeys publicKeys = keyService.findByUserNameAndKeyId(session.getUserName(), receiptExchange.getReceiverPublicKeyID());
                log.info("test");
                String pubKey = publicKeys.getPublicKey();
                String evidenceOfReceipt = receiptExchange.getSignedHashReceipt();*/

                /*
                    The signature of A is hashed, because the method that B uses to Sign the SigA(H(doc)) and send
                    it as an EOR, automatically uses hashing before signing (if it is just signed with the RSA private Key
                    it gives an Exception). Essentially, EOR is SigB(H(SigA(H(doc)))).
                    Therefore the EOR has to be verifies against the Hash of the SigA
                 */
                //String signedByA = Hash.HashString(exchangeDB.getReceipt());
 /*               log.info("is hash failing?");
                String sigA = exchangeDB.getReceipt();
                byte[] sigABytes = Base64.decodeBase64(sigA);
                byte[] sigABytesHash = Hash.HashByteToByte(sigABytes);
                String sigAHash = Base64.encodeBase64URLSafeString(sigABytesHash);*/


                log.info("no");
                try {
                    // try to verify the receiver's EOR
                    //isEORValid = SignatureVerification.verifyReceiverEOR(sigAHash, evidenceOfReceipt, pubKey);

                } catch (Exception e){
                    // Could not verify the EOR
                    Map<String, String> responseMessage = new HashMap<>();
                    responseMessage.put("message", "Error while trying to verify the evidence of origin, this might " +
                            "be due to incorrect signature format or wrong algorithm specification.");
                    builder = Response.status(Response.Status.BAD_REQUEST).entity(responseMessage);
                    return builder.build();
                }

                if (true) {
                    //if the verification of the NRR is ok commit the update to the exchange table and update Status to SUCCESS
                    exchangeService.sendReceipt(receiptExchange);
                    exchangeDB.setStatus(Status.SUCCESS);
                    exchangeDB.setReceiverPublicKeyID("test");
                    exchangeService.update(exchangeDB);

                    emailService.emailForSuccess(user.getEmail(), "Exchange was successful.");

                    log.info("sendReceipt - successfully sent receipt for exchange");
                    Map<String, String> responseObj = new HashMap<>();
                    responseObj.put("success", "successfully sent receipt and updated exchange to STATUS:SUCCESS");
                    builder = Response.ok().entity(responseObj);
                } else {
                    log.info("sendReceipt - failed to verify EOR against" );
                    Map<String, String> responseObj = new HashMap<>();
                    responseObj.put("error", "unknown error occurred");
                    builder = Response.status(Response.Status.BAD_REQUEST);
                }

            } else {
                log.info("sendNRR - failed receiver invalid");
                Map<String, String> responseObj = new HashMap<>();
                responseObj.put("error", "specified receiver/exchange id not found");
                builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
            }
        } catch (IOException e) {
            log.info("sendNRR - failed " + e.toString());
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", "internal server error");
            builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseObj);
        } catch (Exception e) {
            log.info("sendNRR - failed " + e.toString());
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", "unknown error occurred");
            builder = Response.status(Response.Status.BAD_REQUEST);
        }
        return builder.build();
    }

    @DELETE
    @Path("/{id}")
    @Authenticate
    public Response abortExchange(@HeaderParam("Authorization") String token, @PathParam("id") long id) {

        // Find the user of the current session
        Session session = sessionService.findByToken(token);

        // Find the exchange intended to be aborted
        Exchange exchange = exchangeService.findById(id);

        if (exchange == null) {
            // The exchange does not exist
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        if (exchange.getStatus() != Status.IN_PROGRESS) {
            // The user is not authorized to perform such action
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }

        if (!session.getUserName().equals(exchange.getSender())) {
            // The user is not authorized to perform such action
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }

        // Change the status to abort
        exchange.setStatus(Status.ABORT);

        Response.ResponseBuilder builder;
        try {

            // Apply the changes
            exchangeService.update(exchange);
            exchangeService.cancelExchange(exchange.getId());

            // Successful modification
            Map<String, String> responseMessage = new HashMap<>();
            responseMessage.put("message", "Successfully aborted exchange");
            builder = Response.ok().entity(responseMessage);
        } catch (Exception e) {
            // Could not apply the changes
            Map<String, String> responseMessage = new HashMap<>();
            responseMessage.put("message", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseMessage);
        }
        return builder.build();
    }
}
