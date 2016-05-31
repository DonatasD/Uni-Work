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

package uk.ac.ncl.exchange.sqs;

import com.amazonaws.services.sqs.model.*;
import org.apache.commons.codec.binary.Base64;

import java.util.List;


/**
 * SQS Operations class
 * <p/>
 * Send Message
 * Read Message
 * Delete Message
 */
public class SQSOperations {

    SQS sqs = new SQS();

    /**
     * method to send a message to the Queue
     *
     * @param message
     */
    public void sendMessageToQueue(byte[] message) {
        //connect to SQS queue
        SQSConnection.connectToSQS();

        String base64EncodedMessage;
        //encode the byte[] to base64 String
        base64EncodedMessage = com.sun.org.apache.xerces.internal.impl.dv.util.Base64.encode(message);

        //send message to queue
        sqs.getQueueService().sendMessage(new SendMessageRequest(sqs.getQueueName(), base64EncodedMessage));
    }


    /**
     * method to retrieve the last message of the Queue
     *
     * @return decodedMessage
     */
    public byte[] receiveLastMessageFromQueue() {
        //connect to SQS service
        SQSConnection.connectToSQS();

        byte[] decodedMessage = null;

        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(sqs.getQueueName());

        List<Message> MESSAGES = sqs.getQueueService().receiveMessage(receiveMessageRequest).getMessages();

        for (Message m : MESSAGES) {
            decodedMessage = Base64.decodeBase64(m.getBody());
        }

        //delete message after it has been retrieved
        String messageReceiptHandle = MESSAGES.get(0).getReceiptHandle();
        sqs.getQueueService().deleteMessage(new DeleteMessageRequest(sqs.getQueueName(), messageReceiptHandle));

        //return message as byte[]
        return decodedMessage;
    }


    /**
     * method to delete last message from queue
     */
    public void deleteLastMessageFromQueue() {
        //connect to SQS service
        SQSConnection.connectToSQS();

        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(sqs.getQueueName());

        List<Message> MESSAGES = sqs.getQueueService().receiveMessage(receiveMessageRequest).getMessages();

        String messageReceiptHandle = MESSAGES.get(MESSAGES.size() - 1).getReceiptHandle();
        sqs.getQueueService().deleteMessage(new DeleteMessageRequest(sqs.getQueueName(), messageReceiptHandle));
    }

    /**
     * method to create a new Queue
     *
     * @param queueName
     */
    public void createQueue(String queueName) {
        //connect to SQS service
        SQSConnection.connectToSQS();

        //create the queue
        CreateQueueRequest createQueueRequest = new CreateQueueRequest(queueName);
        sqs.getQueueService().createQueue(createQueueRequest).getQueueUrl();
    }

    /**
     * method to delete a specified Queue
     *
     * @param queueName
     */
    public void deleteQueue(String queueName) {
        //connect to SQS service
        SQSConnection.connectToSQS();

        //delete the queue
        sqs.getQueueService().deleteQueue(new DeleteQueueRequest(queueName));
    }


}
