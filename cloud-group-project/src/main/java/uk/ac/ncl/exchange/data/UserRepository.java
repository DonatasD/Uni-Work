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

package uk.ac.ncl.exchange.data;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import uk.ac.ncl.exchange.manager.DynamoDBManager;
import uk.ac.ncl.exchange.model.User;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by Donatas Daubaras on 22/02/15.
 * <p/>
 * This class is meant to retrieve User data from DynamoDB.
 *
 * @see uk.ac.ncl.exchange.manager.DynamoDBManager
 * @see com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
 */

public class UserRepository {

    @Inject
    private DynamoDBManager manager;

    /**
     * Finds User by userName
     *
     * @param userName User identity
     * @return User which matches userName
     */
    public User findByUserName(String userName) {
        DynamoDBMapper mapper = manager.getDynamoDBMapper();
        return mapper.load(User.class, userName);
    }

    /**
     * Retrieves all the users with predefined limit.
     *
     * @return List<User> containing all the users.
     * @see com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
     */
    public List<User> findAllOrderByUserName() {
        DynamoDBMapper mapper = manager.getDynamoDBMapper();
        DynamoDBScanExpression expression = new DynamoDBScanExpression();
        //SAFE CHECK
        expression.setLimit(1000);
        return mapper.scan(User.class, expression);
    }

    public void createUser(User user) {
        DynamoDBMapper mapper = manager.getDynamoDBMapper();
        mapper.save(user);
    }
}
