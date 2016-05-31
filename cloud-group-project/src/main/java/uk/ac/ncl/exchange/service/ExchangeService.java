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

import com.amazonaws.services.dynamodbv2.model.InternalServerErrorException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import org.apache.commons.io.IOUtils;
import uk.ac.ncl.exchange.data.ExchangeRepository;
import uk.ac.ncl.exchange.data.SessionRepository;
import uk.ac.ncl.exchange.data.UserRepository;
import uk.ac.ncl.exchange.manager.S3Manager;
import uk.ac.ncl.exchange.model.Exchange;
import uk.ac.ncl.exchange.model.ReceiptExchange;
import uk.ac.ncl.exchange.model.Session;
import uk.ac.ncl.exchange.model.ExchangeBean;
import uk.ac.ncl.exchange.util.Status;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by Donatas Daubaras on 03/03/15.
 * Last Updated By: Sheryl Coe
 * Last Updated Date: 12/03/2015
 *
 * This takes the StartExchange object and applies it to the MySQL database and S3 database.
 */
@Stateless
public class ExchangeService {

    public static final String PREFIX = "/";

    @Inject
    private S3Manager manager;

    @Inject
    private UserRepository userRepository;

    @Inject
    private SessionRepository sessionRepository;
    
    @Inject
    private ExchangeRepository exchangeRepository;

    @Inject
    private EntityManager em;

    @Inject
    private
    @Named("logger")
    Logger log;

    public Exchange findById(Long id) {
        return exchangeRepository.findById(id);
    }

    public Exchange findByName(String exchangeName) {
        return exchangeRepository.findByName(exchangeName);
    }

    public List<Exchange> findAllByUserName(String userName) {
        return exchangeRepository.findByUserName(userName);
    }

    public Exchange create(Exchange exchange) {
        return exchangeRepository.create(exchange);
    }

    public Exchange update(Exchange exchange) {
        return exchangeRepository.update(exchange);
    }

    public String createFileAndGetFileName(String base64EncodedBody, String fileExtension, String userName) throws IOException {
        byte[] content = Base64.decodeBase64(base64EncodedBody);
        String fileName = createDocument(userName, fileExtension, content);
        String[] tempFile = fileName.split(PREFIX);
        fileName = tempFile[tempFile.length - 1];
        return fileName;
    }

    public void cancelExchange(Long id) {
        AmazonS3 client = new AmazonS3Client();
        Exchange exchange = em.find(Exchange.class, id);
        String userName = exchange.getSender();
        String document = exchange.getDocument();
        client.deleteObject("fair-exchange", userName + PREFIX + "Documents" + PREFIX + document);
    }

    /**
     * Sheryl Coe - 12/03/2015
     * Sets the exchange details ready for the exchange update
     *
     * @param receiptExchange Information from receiver sending the NRR (proof of receipt), includes exchange ID, signed receipt and the receiver public key ID
     * @throws java.io.IOException
     */
    public void sendReceipt(ReceiptExchange receiptExchange) throws IOException {

        Exchange exchangeDB = exchangeRepository.findById(receiptExchange.getId());
        log.info(exchangeDB.toString());

        //Only update the database if the status of the transaction is IN_PROGRESS
        if (exchangeDB.getStatus().equals(Status.IN_PROGRESS)) {
            //updateExchange(exchangeDB);
            exchangeDB.setReceipt(receiptExchange.getSignedHashReceipt());
            exchangeDB.setReceiverPublicKeyID(receiptExchange.getReceiverPublicKeyID());
            exchangeDB.setStatus(Status.SUCCESS);

            exchangeRepository.update(exchangeDB);
            log.info("ExchangeService.sendReceipt() - Exchange has been updated with receipt, receiver public key ID and status:SUCCESS.");
            } else {
            log.info("ExchangeService.sendReceipt() - Can only send receipt for an IN_PROGRESS exchange.");
            }
    }

    /**
     * Creates a document for exchange
     *
     * @param userName
     * @param format
     * @param content
     * @throws java.io.IOException
     */
    public String createDocument(String userName, String format, byte[] content) throws IOException {
        //check if file name is unique
        log.info("createDocument - generating fileName");
        String fileName = generateFileName(userName, manager.DOCUMENT_FOLDER, format);
        //Request to insert file
        log.info("createDocument - creating document in S3");
        log.info(fileName);
        createFile(fileName, content);
        return fileName;
    }

    /**
     * Creates a receipt for the exchange
     *
     * @param userName
     * @param format
     * @param content
     * @throws IOException
     */
    public String createReceipt(String userName, String format, byte[] content) throws IOException {
        //check if file name is unique
        log.info("createReceipt - generating fileName");
        String fileName = generateFileName(userName, manager.RECEIPT_FOLDER, format);
        //Request to insert file
        log.info("createReceipt - creating receipt in S3");
        createFile(fileName, content);
        return fileName;
    }

    public S3Object getDocument(String userName, String fileName) {
        log.info("getDocument - starting to acquire the document");
        AmazonS3 client = manager.getClient();
        String key = userName + PREFIX + manager.DOCUMENT_FOLDER + PREFIX + fileName;
        log.info("getDocument - getting document " + key);
        return client.getObject(new GetObjectRequest(manager.BUCKET_NAME, key));
    }

    /**
     * Creates a specified file in S3
     *
     * @param fileName - fileName
     * @param content  - content
     */
    private void createFile(String fileName, byte[] content) throws IOException {
        AmazonS3 client = manager.getClient();
        File file = createTempFile(fileName, content);
        client.putObject(new PutObjectRequest(manager.BUCKET_NAME, fileName, file));
        log.info("createFile - successful");
    }

    /**
     * @param userName - used for user specific folder
     * @param folder   - indicating file type
     * @param format   - indicates format
     * @return String of full document path
     */
    private String generateFileName(String userName, String folder, String format) {
        UUID uuid = UUID.randomUUID();
        /*
            generate file path and name
            guarantee uniqueness
         */
        String fileName = userName + PREFIX + folder + PREFIX + uuid.toString() + "." + format;
        return exists(fileName) ? generateFileName(userName, folder, format) : fileName;
    }

    /**
     * @param fileName check if curring file exists in S3
     * @return boolean indicating if it exists or not
     */
    private boolean exists(String fileName) throws InternalServerErrorException {
        AmazonS3 client = manager.getClient();
        try {
            //get metadata to check if file exists (not that expensive as getting the object)
            client.getObjectMetadata(manager.BUCKET_NAME, fileName);
        } catch (AmazonS3Exception e) {
            if (e.getStatusCode() != 404) {
                log.info("exists - Internal system error");
                throw new InternalServerErrorException("Internal system error occured");
            }
            log.info("exists - fileName generated successfully");
            return false;
        }
        log.info("exists - fileName exists");
        return true;
    }

    private File createTempFile(String fileName, byte[] content) throws IOException {
        String[] temp = fileName.split("\\.");
        log.info(temp.length + "");
        String format = temp[temp.length - 1];
        File file = File.createTempFile(fileName, format);
        file.deleteOnExit();
        FileUtils.writeByteArrayToFile(file, content);
        return file;
    }
}
