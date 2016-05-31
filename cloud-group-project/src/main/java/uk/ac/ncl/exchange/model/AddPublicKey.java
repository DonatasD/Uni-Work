package uk.ac.ncl.exchange.model;

/**
 * Created by Sheryl Coe 11/03/2015.
 * Last Updated By: Sheryl Coe
 * Last Updated Date: 11/03/2015
 * <p/>
 * This is the wrapper class for the add users public key message.
 *
 * String publicKey         The public key to be added (the username will be taken from the session
 * String fileExtension     The file to be exchanged extension type
 * String signedHashFile    The evidence of origin for the file to be exchanged (signed by the the sender against the hash of the file)
 * String receiver          The user who will receive the file
 * String exchangeName      The description of the exchange the user will see in the client, an exchange identifier the user can understand eg. House Contract Document (this is determined by the client application)
 * String senderPublicKey   The public key used by the sender to created the signedHashFile
 *
 */
public class AddPublicKey {

    private String publicKey;

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }


}
