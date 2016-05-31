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

import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.*;
import uk.ac.ncl.exchange.manager.SESManager;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.logging.Logger;


public class EmailService {
    
    //TODO generate random email address
    private final String GLOBAL_SENDER = "rundong.chang419@gmail.com";
    private final String confirmationSubject = "This is a email for confirmation";
    private final String successSubject = "This is a email for success";
    
    @Inject
    private SESManager manager;
    
    @Inject
    @Named("logger")
    private Logger log;
    
    /**
     * Sends email address to receiver, which contains content and subject as title
     *
     * @param sender  sender email address
     * @param receiver receive email address
     * @param content email content
     * @param title email subject
     * @throws IOException
     */
    public void sendMail(String sender, String receiver, String content, String title) throws IOException {
        log.info("sendMail - starting to send the email");
        // Construct an object to contain the recipient's address.
        Destination destination = new Destination().withToAddresses(new String[]{receiver});
        // Create the subject and body of the message.
        Content subject = new Content().withData(title);
        Content textBody = new Content().withData(content);
        Body body = new Body().withText(textBody);
        // Create a message with the specified subject and body.
        Message message = new Message().withSubject(subject).withBody(body);
        // Assemble the email.
        SendEmailRequest request = new SendEmailRequest().withSource(sender).withDestination(destination).withMessage(message);
        try {
            log.info("sendMail - Attempting to send email to " + receiver);
            AmazonSimpleEmailServiceClient client = manager.getClient();
            // Send the email.
            client.sendEmail(request);
            log.info("sendMail - Email sent successfully");
        } catch (Exception ex) {
            log.info("The email was not sent.");
            log.info("Error message: " + ex.getMessage());
        }
    }
    /**
     * Method to send a email for confirmation
     *
     * @param receiver - receiver's email
     * @param content -
     * @throws IOException
     */
    public void emailForConfirmation(String receiver, String content) throws IOException {
        log.info("Attempting to send an email for confirmation...");
        sendMail(GLOBAL_SENDER, receiver, content, confirmationSubject);
    }

    /**
     * Method to send a email for success
     *
     * @param receiver - receiver's emails
     * @param body - email's body
     * @throws IOException
     */
    public void emailForSuccess(String receiver, String body) throws IOException {
        log.info("Attempting to send an email for success ...");
        sendMail(GLOBAL_SENDER, receiver, body, successSubject);
    }

    /**
     * Sends verification request to provided email
     *  
     * @param email - email that is being verified
     */
    public void verifyEmail(String email) {
        log.info("verifyEmail - sending verification email to " + email);
        AmazonSimpleEmailServiceClient client = manager.getClient();
        client.verifyEmailIdentity(new VerifyEmailIdentityRequest().withEmailAddress(email));
    }

    /**
     * Checks if provided email is verified with amazon SES
     *
     * @param email - provided email
     * @return boolean indicating email verification
     */
    public boolean isEmailVerified(String email) {
        log.info("isEmailVerified - checking if email is verified");
        AmazonSimpleEmailServiceClient client = manager.getClient();
        // Retrieves all emails associated in amazon SES 
        ListIdentitiesResult list = client.listIdentities(new ListIdentitiesRequest().withIdentityType(IdentityType.EmailAddress));
        GetIdentityVerificationAttributesRequest request = new GetIdentityVerificationAttributesRequest();
        request.setIdentities(list.getIdentities());
        // Retrieves verification statuses
        GetIdentityVerificationAttributesResult result = client.getIdentityVerificationAttributes(request);
        // Check if email is associated AND status is SUCCESS
        return result.getVerificationAttributes().containsKey(email) && result.getVerificationAttributes().get(email).getVerificationStatus().equals("Success");
    }

}
