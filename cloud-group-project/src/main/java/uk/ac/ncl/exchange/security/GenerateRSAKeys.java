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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

/**
 * A class to generate an RSA Key pair
 */
public class GenerateRSAKeys {
    private static int keyLength;
    private Key privateKey;
    private Key publicKey;

    /**
     * RSA key pair constructor
     *
     * @throws GeneralSecurityException
     */
    public GenerateRSAKeys() throws GeneralSecurityException {
        //use a fixed size of key bits length
        keyLength = 1024;

        //initialise Key generator
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keyLength);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        //separate public - private keys using the Key interface
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
    }

    public final Key getPrivateKey() {
        return privateKey;
    }

    public final Key getPublicKey() {
        return publicKey;
    }

    /**
     * method to convert any Key to hex representation
     *
     * @param key
     * @return hexKey
     */
    public StringBuffer convertKeyToHexString(Key key) {
        StringBuffer hexKey = new StringBuffer();
        for (int i = 0; i < key.getEncoded().length; i++) {
            if (key.getEncoded()[i] >= 0 && key.getEncoded()[i] <= 15) hexKey.append('0');

            hexKey.append(Integer.toHexString(0xFF & key.getEncoded()[i]));
        }
        return hexKey;
    }

    /**
     * method to store the keys to specified paths
     *
     * @param privateKeyPathName
     * @param publicKeyPathName
     */
    public final void toFileSystem(String privateKeyPathName, String publicKeyPathName) {
        FileOutputStream privateKeyOutputStream = null;
        FileOutputStream publicKeyOutputStream = null;

        try {
            File privateKeyFile = new File(privateKeyPathName);
            File publicKeyFile = new File(publicKeyPathName);

            privateKeyOutputStream = new FileOutputStream(privateKeyFile);
            privateKeyOutputStream.write(privateKey.getEncoded());
            publicKeyOutputStream = new FileOutputStream(publicKeyFile);
            publicKeyOutputStream.write(publicKey.getEncoded());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (privateKeyOutputStream != null) {
                    privateKeyOutputStream.close();
                }
                if (publicKeyOutputStream != null) {
                    publicKeyOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
