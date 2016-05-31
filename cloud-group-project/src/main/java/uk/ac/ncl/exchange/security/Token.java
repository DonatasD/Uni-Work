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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Donatas Daubaras on 23/02/15.
 */
public class Token {

    /**
     * Method to generate a unique session id.
     * Gives 122bit security 6 bits are lost due to pseudo randomness.
     *
     * @return uniqueId The session's unique identifier
     */
    public static String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Method to generate a timestamp for the session
     *
     * @return timeStamp The session's timestamp
     */
    public static String generateTimestamp() {
        Date d = new Date();
        Timestamp t = new Timestamp(d.getTime());
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(t);
        return timeStamp;
    }
}
