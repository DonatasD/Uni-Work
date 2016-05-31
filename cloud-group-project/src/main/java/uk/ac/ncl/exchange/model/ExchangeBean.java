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

/**
 * Created by Donatas Daubaras on 24/02/15.
 * Last Updated By: Sheryl Coe
 * Last Updated Date: 09/03/2015
 * <p/>
 * This is the wrapper class for the Start exchange message.
 * <p/>
 * String file              The file to be exchanged
 * String fileExtension     The file to be exchanged extension type
 * String signedHashFile    The evidence of origin for the file to be exchanged (signed by the the sender against the hash of the file)
 * String receiver          The user who will receive the file
 * String exchangeName      The description of the exchange the user will see in the client, an exchange identifier the user can understand eg. House Contract Document (this is determined by the client application)
 * String senderPublicKey   The public key used by the sender to created the signedHashFile
 */

public class ExchangeBean {

    private String file;
    private String fileExtension;
    private String signedHashFile;
    private String receiver;
    private String exchangeName;
    private String senderPublicKeyID;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getSignedHashFile() {
        return signedHashFile;
    }

    public void setSignedHashFile(String signedHashFile) {
        this.signedHashFile = signedHashFile;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
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

}
