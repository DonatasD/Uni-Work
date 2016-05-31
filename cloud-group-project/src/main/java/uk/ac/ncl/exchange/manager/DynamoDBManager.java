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
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;

/**
 * Created by Donatas Daubaras on 23/02/15.
 * <p/>
 * This class initiates DynamoDBManager, which allows to access DynamoDB.
 * To use this manager credentials has to be stored in ~/.aws/credentials
 *
 * @see com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
 * @see com.amazonaws.services.dynamodbv2.document.DynamoDB
 * @see com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
 */
public class DynamoDBManager {

    public final static String USER_TABLE_NAME = "Users";
    //Initialises client using ~/.aws/credentials
    private final AmazonDynamoDBClient client = new AmazonDynamoDBClient(new ProfileCredentialsProvider());

    /**
     * Basic constructor, to specify needed region
     */
    public DynamoDBManager() {
        client.setRegion(Regions.EU_WEST_1);
    }

    /**
     * Allows to retrieve AmazonDynamoDBClient
     *
     * @return AmazonDynamoDBClient
     */
    public AmazonDynamoDBClient getClient() {
        return client;
    }

    /**
     * Allows to retrieve DynamoDB
     *
     * @return DynamoDB
     */
    public DynamoDB getDynamoDB() {
        return new DynamoDB(client);
    }

    /**
     * Allows to retrieve DynamoDBMapper
     *
     * @return DynamoDBMapper
     */
    public DynamoDBMapper getDynamoDBMapper() {
        return new DynamoDBMapper(client);
    }
}
