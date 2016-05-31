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

package uk.ac.ncl.exchange.rest;

import uk.ac.ncl.exchange.service.SessionService;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by Donatas Daubaras on 08/03/15.
 */
@Provider
@Authenticate
@Priority(Priorities.AUTHORIZATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    @Inject
    SessionService sessionService;
    @Inject
    private
    @Named("logger")
    Logger log;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        log.info("Authenticating token");
        String token = containerRequestContext.getHeaderString("Authorization");
        if (token == null || token.trim().equals("") || !sessionService.authenticateToken(token)) {
            log.info("Authentication failed");
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", "Authentication failed");
            containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(responseObj).build());
        } else {
            log.info("Authentication successful");
        }
    }
}
