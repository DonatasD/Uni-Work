package uk.ac.ncl.exchange.security;

import org.junit.Test;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.GeneralSecurityException;
import java.security.Key;
import static org.junit.Assert.assertTrue;

public class SignVerifyTest {

    private static final byte[] input = {1, 2, 3, 4, 5};
    private byte[] signed;
    private byte[] verified;

    @Test
    public void testSignVerifyFile() throws IOException, GeneralSecurityException {
        //Creating a temporary file in memory
        File temp = File.createTempFile("example", ".txt");
        GenerateRSAKeys rsa = new GenerateRSAKeys();
        Key privateKey = rsa.getPrivateKey();
        Key publicKey = rsa.getPublicKey();
        //Hashing the created file
        byte[] fileDigest = Hash.hashFile(temp);

        //Signing and verifying the file
        signed = SignVerifyFile.signFile(temp, privateKey);
        verified = SignVerifyFile.verify(signed, publicKey);

        assertTrue(SignatureVerification.isVerified(fileDigest, verified));

        temp.delete();
    }

    @Test
    public void testSignVerifyByteArray() throws IOException, GeneralSecurityException {
        GenerateRSAKeys rsa = new GenerateRSAKeys();
        Key privateKey = rsa.getPrivateKey();
        Key publicKey = rsa.getPublicKey();

        //Signing and verifying a byte array
        signed = SignVerifyFile.sign(input, privateKey);
        verified = SignVerifyFile.verify(signed, publicKey);

        assertTrue(SignatureVerification.isVerified(input, verified));
    }

    @Test
    public void testSignEvidence() throws GeneralSecurityException {
        GenerateRSAKeys rsa = new GenerateRSAKeys();
        PrivateKey privateKey = (PrivateKey) rsa.getPrivateKey();
        PublicKey publicKey = (PublicKey) rsa.getPublicKey();

        byte[] s = SignVerifyFile.signEvidence(input, privateKey);

        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(privateKey);

        sig.initVerify(publicKey);
        sig.update(input);

        boolean x = sig.verify(s);

        System.out.println(x);
    }

    @Test
    public void testVerifyDocuments() throws GeneralSecurityException, UnsupportedEncodingException {
        GenerateRSAKeys rsa = new GenerateRSAKeys();
        Key privateKey = rsa.getPrivateKey();
        Key publicKey = rsa.getPublicKey();

        //convert public key to string
        String publicKeyString = org.apache.commons.codec.binary.Base64.encodeBase64String(publicKey.getEncoded());
        //example document in string format
        String document = "Example";
        //document hash in string Base64 format
        String documentHash = Hash.HashString(document);

        byte[] documentHashBytes = org.apache.commons.codec.binary.Base64.decodeBase64(documentHash);

        byte[] signedDocument = SignVerifyFile.sign(documentHashBytes, privateKey);

        String signedDocumentString = org.apache.commons.codec.binary.Base64.encodeBase64String(signedDocument);

        boolean x = SignatureVerification.verifyDocuments(signedDocumentString, document, publicKeyString);

        assertTrue(x);
    }
}
