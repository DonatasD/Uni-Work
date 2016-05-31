package uk.ac.ncl.exchange.security;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

public class SignatureVerification {
    /**
     * method to verify whether a digital signature is valid or not
     * by comparing the initial hash to the decrypted signature
     *
     * @param digest
     * @param decrypted
     * @return true or false
     */
    public static boolean isVerified(byte[] digest, byte[] decrypted) {
        if (Arrays.equals(digest, decrypted)) {
            return true;
        }
        return false;
    }

    /**
     * Method to verify a signed file, by comparing the file's hash to the verification output
     *
     * @param signedHash - The signed document
     * @param document   - The document
     * @param publicKey  - The Public Key for the verification
     * @return boolean indicating if the verification is successful
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws NoSuchPaddingException
     * @throws InvalidKeySpecException
     */
    public static boolean verifyDocuments(String signedHash, String document, String publicKey) throws NoSuchAlgorithmException, UnsupportedEncodingException, IllegalBlockSizeException,
            InvalidKeyException, BadPaddingException, NoSuchPaddingException, InvalidKeySpecException {



        // 1. Hash the document and get the bytes
        byte[] documentBytes = org.apache.commons.codec.binary.Base64.decodeBase64(document);
        byte[] documentHash = Hash.HashByteToByte(documentBytes);


        // 2. Convert String Key to PublicKey
        byte[] data = org.apache.commons.codec.binary.Base64.decodeBase64(publicKey);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key puK = keyFactory.generatePublic(spec);
        // Get signedHash bytes
        byte[] signedHashBytes = org.apache.commons.codec.binary.Base64.decodeBase64(signedHash);
        // 3. Decrypt - Verify signedHash
        byte[] verifiedBytes = SignVerifyFile.verify(signedHashBytes, puK);

        System.out.println("-------" + ((PublicKey) puK).toString());

        // 4. Compare verifiedHash bytes to documentHash bytes
        return isVerified(documentHash, verifiedBytes);
    }

    /**
     * Method to verify the receiver's evidence of receipt
     *
     * @param signedHashByA     - The signed document
     * @param evidenceOfReceipt - The receiver's evidence of receipt
     * @param publicKey         - The Public Key for the verification
     * @return boolean indicating if the verification is successful
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws NoSuchPaddingException
     */
    public static boolean verifyReceiverEOR(String signedHashByA, String evidenceOfReceipt, String publicKey) throws InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException,
            InvalidKeyException, BadPaddingException, NoSuchPaddingException, SignatureException {

        String algorithm = "SHA256withRSA";

        // 1. Convert String Key to PublicKey
        byte[] data = org.apache.commons.codec.binary.Base64.decodeBase64(publicKey);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey puK = keyFactory.generatePublic(spec);

        // 2. Convert signedHash and evidenceOfReceipt
        byte[] signedHashBytes = org.apache.commons.codec.binary.Base64.decodeBase64(signedHashByA);
        byte[] evidenceOfReceiptBytes = org.apache.commons.codec.binary.Base64.decodeBase64(evidenceOfReceipt);

        // 3. Verify evidenceOfReceipt
        Signature sig = Signature.getInstance(algorithm);
        sig.initVerify(puK);
        sig.update(signedHashBytes);

        return sig.verify(evidenceOfReceiptBytes);
    }


}
