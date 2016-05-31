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

import uk.ac.ncl.exchange.model.Exchange;
import uk.ac.ncl.exchange.util.Status;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.security.spec.ECField;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Donatas Daubaras on 02/03/15.
 */
@Stateless
public class ExchangeRepository {

    @Inject
    @Named("logger")
    private Logger log;

    @Inject
    private EntityManager em;

    public Exchange findById(Long id) {
        return em.find(Exchange.class, id);
    }

    public Exchange findByName(String exchangeName){
        TypedQuery<Exchange> query = em.createNamedQuery(Exchange.FIND_BY_EXCHANGE_NAME, Exchange.class)
                .setParameter("exchangeName", exchangeName);
        return query.getSingleResult();
    }

    public List<Exchange> findByUserName(String userName) {
        TypedQuery<Exchange> query = em.createNamedQuery(Exchange.FIND_BY_USERNAME, Exchange.class)
                .setParameter("userNameAsSender", userName)
                .setParameter("userNameAsReceiver", userName);
        return query.getResultList();
    }

    public Exchange create(Exchange exchange) {

        // Write the exchange to the database
        em.persist(exchange);

        // Return the exchange persisted
        return exchange;
    }

    public Exchange update(Exchange exchange) {

        // Either update the exchange or
        // add it if it can't be found
        em.merge(exchange);

        // Return the exchange merged
        return  exchange;
    }

    /**
     * Method to cancel an exchange, based on the id
     *
     * @param id The exchange id
     * @return The canceled exchange
     */
    public Exchange cancel(Long id) {
        log.info("ExchangeRepository.cancel() " + id);

        Exchange exchange = findById(id);
        // Check if status is SUCCESS
        if (exchange.getStatus() == Status.SUCCESS) {
            log.info("ExchangeRepository.cancel() - Cannot abort Successful exchange");
        } else if (exchange.getStatus() == Status.ABORT) {
            log.info("ExchangeRepository.cancel() - Cannot abort Aborted exchange");
        } else {
            // Check if status is IN_PROGRESS
            if (exchange.getStatus() == Status.IN_PROGRESS) {
                if (exchange.getId() != null) {
                    exchange.setStatus(Status.ABORT);
                    update(exchange);
                } else {
                    log.info("ExchangeRepository.cancel() - No Exchange was found.");
                }
            }
        }
        return exchange;
    }
}
