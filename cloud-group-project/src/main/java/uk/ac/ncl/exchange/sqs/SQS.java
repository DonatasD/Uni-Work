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

package uk.ac.ncl.exchange.sqs;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSClient;

/**
 * SQS object representation
 */
public class SQS {
    private static String ACCESS_KEY;
    private static String SECRET_KEY;
    private static AmazonSQSClient queueService;
    private static Region region;
    private final String queueName;

    public SQS() {
        ACCESS_KEY = "";
        SECRET_KEY = "";
        queueName = "";
        //Create an SQS client
        queueService = new AmazonSQSClient(new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY));
        //set region where queue belongs (in our case EU WEST 1)
        region = Region.getRegion(Regions.EU_WEST_1);
    }

    public SQS(String queueName) {
        ACCESS_KEY = "";
        SECRET_KEY = "";
        this.queueName = queueName;
        //Create an SQS client
        queueService = new AmazonSQSClient(new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY));
        //set region where queue belongs (in our case EU WEST 1)
        region = Region.getRegion(Regions.EU_WEST_1);
    }

    public String getQueueName() {
        return queueName;
    }

    public AmazonSQSClient getQueueService() {
        return queueService;
    }

    public Region getRegion() {
        return region;
    }


}
