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

package uk.ac.ncl.exchange.manager;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

/**
 * Created by Donatas Daubaras on 02/03/15.
 */
public class S3Manager {

    public final static String BUCKET_NAME = "fair-exchange";
    public final static String DOCUMENT_FOLDER = "Documents";
    public final static String RECEIPT_FOLDER = "Receipts";
    private final AmazonS3 client = new AmazonS3Client(new ProfileCredentialsProvider());

    public S3Manager() {
        client.setRegion(Region.getRegion(Regions.EU_WEST_1));
        if (!client.doesBucketExist(BUCKET_NAME)) {
            client.createBucket("fair-exchange");
        }
    }

    private void createFolder() {
        //S3Object temp = client.getObject(new GetObjectRequest(BUCKET_NAME, "temp"));
    }

    public AmazonS3 getClient() {
        return client;
    }
}
