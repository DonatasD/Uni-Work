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

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

/**
 * A class to provide RSA based encryption and decryption
 */
public class EncryptDecrypt {

    /**
     * method to encrypt a byte array using either Private or Public RSA Key
     *
     * @param input
     * @param key
     * @return encrypted The encrypted byte array
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static byte[] encrypt(byte[] input, Key key) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher rsaCipher = Cipher.getInstance("RSA");
        //Initialise cipher object with key and set it to encrypt mode
        rsaCipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = rsaCipher.doFinal(input);

        return encrypted;
    }

    /**
     * method to decrypt a byte array using either Private or Public RSA Key
     *
     * @param input
     * @param key
     * @return decrypted The decrypted byte array
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static byte[] decrypt(byte[] input, Key key) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher rsaCipher = Cipher.getInstance("RSA");
        //Initialise cipher object with key and set it to decrypt mode
        rsaCipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decrypted = rsaCipher.doFinal(input);

        return decrypted;
    }

}
