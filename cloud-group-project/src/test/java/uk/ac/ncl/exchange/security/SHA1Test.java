package uk.ac.ncl.exchange.security;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static org.junit.Assert.*;


public class SHA1Test {

    private static final String s1 = "EXAMPLE STRING TO HASH";
    private static final String s2 = "EXAMPLE STRING TO HASH";
    private static final String s3 = "example string to hash";
    private static final String expectedHash = "6c3d013514ab5208e7fc3b07e71bd0ca1913bcbf80e557f47b1c093815e95934";
    private String s1Digest;
    private String s2Digest;
    private String s3Digest;
    private byte[] fileDigest1;
    private byte[] fileDigest2;

    @Test
    public void testStringExpectedDigest() throws NoSuchAlgorithmException {
        //Compare s1 digest to expected digest
        s1Digest = Hash.HashString(s1);
        assertEquals(s1Digest, expectedHash);
    }

    @Test
    public void testStringExpectedDigest2() throws NoSuchAlgorithmException {
        //Compare s3 digest to expected digest
        s3Digest = Hash.HashString(s3);
        assertTrue(s3Digest != expectedHash);
    }

    @Test
    public void testDigestOfSameStrings() throws NoSuchAlgorithmException {
        //Hashing two identical strings
        s1Digest = Hash.HashString(s1);
        s2Digest = Hash.HashString(s2);

        assertEquals(s1Digest, s2Digest);
    }

    @Test
    public void testDigestOfDifferentStrings() throws NoSuchAlgorithmException {
        //Hashing two different strings
        s1Digest = Hash.HashString(s1);
        s3Digest = Hash.HashString(s3);

        assertTrue(s1Digest != s3Digest);
    }

    @Test
    public void testSameFileDigest() throws IOException, NoSuchAlgorithmException {
        //Creating a temporary file in memory
        File temp1 = File.createTempFile("example", ".txt");
        //Hashing the file twice
        fileDigest1 = Hash.hashFile(temp1);
        fileDigest2 = Hash.hashFile(temp1);

        assertArrayEquals(fileDigest1, fileDigest2);

        temp1.delete();
    }

    @Test
    public void testDifferentFileDigest() throws IOException, NoSuchAlgorithmException {
        //Creating two different temporary files in memory
        File temp1 = File.createTempFile("example", ".txt");
        File temp2 = File.createTempFile("example1", ".txt");
        //Hashing the files
        fileDigest1 = Hash.hashFile(temp1);
        fileDigest2 = Hash.hashFile(temp2);

        assertTrue(!Arrays.equals(fileDigest1, fileDigest2));

        temp1.delete();
        temp2.delete();
    }
}
