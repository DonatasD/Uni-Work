/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
(function () {
    'use strict';
    //Define routes for top-level 'app' module
    angular
        .module('app')
        .config(config);

    config.$inject = ['$httpProvider', '$routeProvider'];

    function config($httpProvider, $routeProvider) {
        //Use a HTTP interceptor to add a nonce to every GET request to prevent MSIE from caching responses.
        $httpProvider.interceptors.push('ajaxNonceInterceptor');

        //Note that this app is a single page app, composed of multiple 'views'
        //Each 'view' is some combination of a template and a controller
        //A 'view' is routed to using a URL fragment following a # symbol. For example, to select the 'home' route, the
        // URL is http://localhost:8080/jboss-contacts-angularjs/#/home
        $routeProvider.
            //If URL fragment is '/home', then load the home.html template, with, the default, AppController
            when('/home', {
                templateUrl: 'templates/home.html',
                controller: 'AppController'
                //If URL fragment is '/add', then load the contactForm.html template, with ContactController
            }).when('/add/customer', {
                templateUrl: 'templates/customer/customerForm.html',
                controller: 'CustomerController'
                //If URL fragment is '/edit' followed by an obligatory identifier, use the same controller/template as
                // with '/add'. Appropriate form controls will be chosen based upon URL.
            }).when('/edit/customer/:customerId', {
                templateUrl: 'templates/customer/customerForm.html',
                controller: 'CustomerController'
                //If URL fragment is /about, then load the about.html template with no controller
            }).when('/api', {
                templateUrl: 'templates/api.html'
                // Add a default route
            }).when('/customer', {
            	templateUrl: 'templates/customer.html'
            	// add flight route with AppController
            }).when('/flight',{
            	templateUrl: 'templates/flight.html',
            	controller: 'AppController'
               // If URL fragment is '/add/flight', then load the flightForm.html template, with FlightController
            }).when('/add/flight', {
            	templateUrl: 'templates/flight/flightForm.html',
            	controller: 'FlightController'
            	// If URL fragment is 'edit/flight:flightId, then load the flightForm.html template, with FlighController
            }).when('/edit/flight/:flightId', {
            	templateUrl: 'templates/flight/flightForm.html',
            	controller: 'FlightController'
            	// add booking route with AppController
            }).when('/booking', {
            	templateUrl: 'templates/booking.html',
            	controller: 'AppController'
            	// If URL fragment is '/add/booking', then load the bookingForm.html template, with BookingController
            }).when('/add/booking', {	
            	templateUrl: 'templates/booking/bookingForm.html',
            	controller: 'BookingController'
            	// If URL fragment is 'edit/booking:bookingId, then load the bookingForm.html template, with BookingController
            }).when('/edit/booking/:bookingId', {
            	templateUrl: 'templates/booking/bookingForm.html',
            	controller: 'BookingController'
            }).otherwise({
                redirectTo: '/home'
            });
    }
})();