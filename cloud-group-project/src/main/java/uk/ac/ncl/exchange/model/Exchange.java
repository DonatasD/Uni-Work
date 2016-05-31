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

import org.hibernate.validator.constraints.NotEmpty;
import uk.ac.ncl.exchange.util.Status;

import javax.ejb.Stateless;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Created by Donatas Daubaras on 04/03/15.
 *
 * Last Updated By: Sheryl Coe
 * Last Updated Date: 11/03/2015
 *
 * This table holds the information about each document exchange. The sender public key ID and receiver public key ID are stored with each exchange, this links to the Public_Key table which
 * holds a list of all users and their public keys (for historical reference).
 */
@Entity

@NamedQueries({
        @NamedQuery(name = Exchange.FIND_BY_ID, query = "SELECT e FROM Exchange e WHERE e.id = :id"),
        @NamedQuery(name = Exchange.FIND_BY_USERNAME, query = "SELECT e FROM Exchange e WHERE e.sender = :userNameAsSender OR e.receiver = :userNameAsReceiver"),
        @NamedQuery(name = Exchange.FIND_BY_EXCHANGE_NAME, query = "SELECT e FROM Exchange e WHERE e.exchangeName = :exchangeName")
})

@Stateless
@XmlRootElement
@Table(name = "exchange")
public class Exchange implements Serializable {

    public static final String FIND_BY_ID = "Exchange.findById";
    public static final String FIND_BY_USERNAME = "Exchange.findByUserName";
    public static final String FIND_BY_EXCHANGE_NAME = "Exchange.findByExchangeName";
	
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @NotNull
    @NotEmpty
    private String sender;

    @NotNull
    @NotEmpty
    private String receiver;
    
    @NotNull
    @NotEmpty
    private String document;

    private String documentHash;
    
    private String receipt;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @NotNull
    @NotEmpty
    private String exchangeName;

    @NotNull
    @NotEmpty
    private String senderPublicKeyID;

    private String receiverPublicKeyID;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getDocumentHash() {
        return documentHash;
    }

    public void setDocumentHash(String documentHash) {
        this.documentHash = documentHash;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public String getSenderPublicKeyID() {
        return senderPublicKeyID;
    }

    public void setSenderPublicKeyID(String senderPublicKeyID) {
        this.senderPublicKeyID = senderPublicKeyID;
    }

    public String getReceiverPublicKeyID() {
        return receiverPublicKeyID;
    }

    public void setReceiverPublicKeyID(String receiverPublicKeyID) {
        this.receiverPublicKeyID = receiverPublicKeyID;
    }

    @Override
    public String toString() {
        return "Exchange{" +
                "id=" + id +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", document='" + document + '\'' +
                ", documentHash='" + documentHash + '\'' +
                ", receipt='" + receipt + '\'' +
                ", status=" + status +
                ", exchangeName='" + exchangeName + '\'' +
                ", senderPublicKeyID='" + senderPublicKeyID + '\'' +
                ", receiverPublicKeyID='" + receiverPublicKeyID + '\'' +
                '}';
    }
}
