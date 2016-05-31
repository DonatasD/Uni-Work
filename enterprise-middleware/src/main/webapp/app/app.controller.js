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
(function() {
	'use strict';
	angular.module('app').controller('AppController', AppController);

	AppController.$inject = [ '$scope', '$filter', 'Customer', 'Flight',
			'Booking', 'messageBag' ];

	function AppController($scope, $filter, Customer, Flight, Booking,
			messageBag) {
		// Assign Customer service to $scope variable
		$scope.customers = Customer;
		// Assign Flight service to $scope variable
		$scope.flights = Flight;
		// Assign Booking service to $scope variable
		$scope.bookings = Booking;
		// Assign Messages service to $scope variable
		$scope.messages = messageBag;

		// Divide customer list into several sub lists according to the first
		// character of their firstName property
		var getHeadings = function(bookings) {
			var headings = {};
			for (var i = 0; i < bookings.length; i++) {
				var startsWithLetter = bookings[i].customer.name.charAt(0)
						.toUpperCase();
				// If we have encountered that first letter before then add the
				// booking to that list, else create it
				if (headings.hasOwnProperty(startsWithLetter)) {
					headings[startsWithLetter].push(bookings[i]);
				} else {
					headings[startsWithLetter] = [ bookings[i] ];
				}
			}
			return headings;
		};

		// Upon initial loading of the controller, populate a list of Customers
		// and their letter headings
		$scope.customers.data = $scope.customers.query(
		// Successful query
		function(data) {
			$scope.customers.data = data;
			$scope.customersList = $scope.customers.data;
			// Keep the customers list headings in sync with the underlying
			// customers
		},
		// Error
		function(result) {
			for ( var error in result.data) {
				$scope.messages.push('danger', result.data[error]);
			}
		});

		$scope.flights.data = $scope.flights.query(function(data) {
			$scope.flights.data = data;
			$scope.flightsList = $scope.flights.data;
		},
		// Error
		function(result) {
			for ( var error in result.data) {
				$scope.messages.push('danger', result.data[error]);
			}
		});

		$scope.bookings.data = $scope.bookings.query(function(data) {
			$scope.bookings.data = data;
			$scope.bookingsList = getHeadings($scope.bookings.data);
			$scope.$watchCollection('bookings.data', function(newBookings,
					oldBookings) {
				$scope.bookingsList = getHeadings(newBookings);
			});
		},
		// Error
		function(result) {
			for ( var error in result.data) {
				$scope.messages.push('danger', result.data[error]);
			}
		});

		// Boolean flag representing whether the details of the customers are
		// expanded inline
		$scope.details = false;

		$scope.flightDetails = false;

		$scope.bookingDetails = false;

		// Default search string
		$scope.search = "";

		// Continuously filter the content of the customers list according to
		// the contents of $scope.search
		$scope.$watch('search', function(newValue, oldValue) {
			$scope.bookingsList = getHeadings($filter('filter')($scope.bookings.data,
					$scope.search));
		});
	}
})();