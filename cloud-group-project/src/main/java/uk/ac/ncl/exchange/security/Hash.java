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

package uk.ac.ncl.exchange.security;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.SerializationUtils;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Donatas Daubaras on 24/02/15.
 */
public class Hash {

    /**
     * Method to hash a File
     *
     * @param file
     * @return digest The hash output
     * @throws NoSuchAlgorithmException
     */
    public static byte[] hashFile(File file) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.reset();
        byte[] buffer = SerializationUtils.serialize(file);
        md.update(buffer);
        byte[] digest = md.digest();

        return digest;
    }

    /**
     * method to hash a String (checkSum)
     *
     * @param input
     * @return hexString Hex representation of the hashed String
     * @throws NoSuchAlgorithmException
     */
    public static String HashString(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.reset();
        byte[] buffer = input.getBytes();
        md.update(buffer);
        byte[] digest = md.digest();

        String hexStr = Base64.encodeBase64String(digest);

        return hexStr;
    }

    public static byte[] HashStringToByte(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.reset();
        byte[] buffer = input.getBytes();
        md.update(buffer);
        return md.digest();
    }

    public static byte[] HashByteToByte(byte[] input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(input);
    }

}
