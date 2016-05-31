package uk.ac.ncl.exchange.security;

import org.apache.commons.lang3.SerializationUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.security.*;

/**
 * A class to provide Signing and Verification methods
 * <p/>
 * Created by Panos on 26/02/2015.
 */
public class SignVerifyFile {

    /**
     * Method to hash a file and sign (encrypt) it,
     * using the private key
     *
     * @param file
     * @param privateKey
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static byte[] signFile(File file, Key privateKey) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.reset();
        //Hash the file
        byte[] buffer = SerializationUtils.serialize(file);
        md.update(buffer);
        byte[] digest = md.digest();

        Cipher rsaCipher = Cipher.getInstance("RSA");
        //Initialize cipher object with key and set it to decrypt mode
        rsaCipher.init(Cipher.ENCRYPT_MODE, privateKey);
        //Encrypt
        byte[] signedFile = rsaCipher.doFinal(digest);

        return signedFile;
    }

    /**
     * Method to sign (encrypt) any object, using the private key
     *
     * @param input
     * @param privateKey
     * @return signed The signed object
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static byte[] sign(byte[] input, Key privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher rsaCipher = Cipher.getInstance("RSA");
        //Initialize cipher object with key and set it to decrypt mode
        rsaCipher.init(Cipher.ENCRYPT_MODE, privateKey);
        //Encrypt
        byte[] signed = rsaCipher.doFinal(input);

        return signed;
    }

    /**
     * Method to sign EOR or EOO
     *
     * @param input
     * @param privateKey
     * @return The signed input
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    public static byte[] signEvidence(byte[] input, Key privateKey) throws NoSuchAlgorithmException, InvalidKeyException,
            SignatureException {
        Signature sig = Signature.getInstance("SHA256withRSA");
        PrivateKey prK = (PrivateKey) privateKey;
        sig.initSign(prK);
        sig.update(input);
        byte[] signedEvidence = sig.sign();

        return signedEvidence;
    }

    /**
     * Method to verify (decrypt) a signed file, using the public key
     *
     * @param signedFile
     * @param publicKey
     * @return verified The verified byte array
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static byte[] verify(byte[] signedFile, Key publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher rsaCipher = Cipher.getInstance("RSA");

        //Initialise cipher object with key and set it to encrypt mode
        rsaCipher.init(Cipher.DECRYPT_MODE, publicKey);
        //Decrypt
        byte[] verified = rsaCipher.doFinal(signedFile);
        return verified;
    }


}
