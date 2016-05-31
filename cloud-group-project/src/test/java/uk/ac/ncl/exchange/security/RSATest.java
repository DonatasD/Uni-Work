package uk.ac.ncl.exchange.security;

import org.junit.Test;

import java.security.GeneralSecurityException;
import java.security.Key;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

public class RSATest {

    private static final byte[] dataToEncrypt = {1, 2, 3, 4, 5};
    private static final String algorithm = "RSA";
    private byte[] encryptedData;
    private byte[] decryptedData;

    @Test
    public void testKey() throws GeneralSecurityException {
        //Creating and checking the two RSA keys are not the same
        GenerateRSAKeys rsa = new GenerateRSAKeys();
        Key privateKey = rsa.getPrivateKey();
        Key publicKey = rsa.getPublicKey();

        assertThat(privateKey, not(publicKey));
    }

    @Test
    public void testAlgorithm() throws GeneralSecurityException {
        GenerateRSAKeys rsa = new GenerateRSAKeys();
        Key privateKey = rsa.getPrivateKey();
        //Getting the encryption algorithm used
        String alg = privateKey.getAlgorithm();

        assertEquals(alg, algorithm);
    }

    @Test
    public void testEncryptionDecryption_Public_Private() throws GeneralSecurityException {
        GenerateRSAKeys rsa = new GenerateRSAKeys();
        Key privateKey = rsa.getPrivateKey();
        Key publicKey = rsa.getPublicKey();
        //Encrypting and decrypting an input byte array using public key for encryption, private for decryption
        encryptedData = EncryptDecrypt.encrypt(dataToEncrypt, publicKey);
        decryptedData = EncryptDecrypt.decrypt(encryptedData, privateKey);

        assertArrayEquals(dataToEncrypt, decryptedData);
    }

    @Test
    public void testEncryptionDecryption_Private_Public() throws GeneralSecurityException {
        GenerateRSAKeys rsa = new GenerateRSAKeys();
        Key privateKey = rsa.getPrivateKey();
        Key publicKey = rsa.getPublicKey();
        //Encrypting and decrypting an input byte array using private key for encryption, public for decryption
        encryptedData = EncryptDecrypt.encrypt(dataToEncrypt, privateKey);
        decryptedData = EncryptDecrypt.decrypt(encryptedData, publicKey);

        assertArrayEquals(dataToEncrypt, decryptedData);
    }
}
