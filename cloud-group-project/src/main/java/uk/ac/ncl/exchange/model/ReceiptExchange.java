package uk.ac.ncl.exchange.model;

public class ReceiptExchange {

    /**
     * Created by Sheryl Coe - 10/03/2015.
     * Last Updated By: Sheryl Coe
     * Last Updated Date: 10/03/2015
     * <p/>
     * This is the wrapper class for the Send receipt for exchange message.
     * <p/>
     * String exchangeId         The reference ID for the exchange transaction
     * String signedHashReceipt    The evidence of receipt for the file to be exchanged (signed by the the receiver against the hash of the evidence of origin)
     * String receiverPublicKeyID      The public key used by the receiver to create the signedHashReceipt
     */


    private Long id;
    private String signedHashReceipt;
    private String receiverPublicKeyID;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSignedHashReceipt() {
        return signedHashReceipt;
    }

    public void setSignedHashReceipt(String signedHashReceipt) {
        this.signedHashReceipt = signedHashReceipt;
    }

    public String getReceiverPublicKeyID() {
        return receiverPublicKeyID;
    }

    public void setReceiverPublicKeyID(String receiverPublicKeyID) {
        this.receiverPublicKeyID = receiverPublicKeyID;
    }
}
