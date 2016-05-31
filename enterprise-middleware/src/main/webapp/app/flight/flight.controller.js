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
	angular.module('app.flight').controller('FlightController',
			FlightController);

	FlightController.$inject = [ '$scope', '$routeParams', '$location',
			'Flight', 'messageBag' ];

	function FlightController($scope, $routeParams, $location, Flight,
			messageBag) {
		// Assign Flight service to $scope
		$scope.flights = Flight;
		// Assign messageBag service to $scope variable
		$scope.messages = messageBag;

		$scope.flight = {};
		$scope.create = true;

		// If $routeParams has :flightId then load the specified flight, and
		// display edit controls on flightForm
		if ($routeParams.hasOwnProperty('flightId')) {
			$scope.flight = $scope.flights.get({
				pathParams : "id",
				flightId : $routeParams.flightId
			});
			$scope.create = false;
		}

		// Define a reset function, that clears the prototype new Flight
		// object, and consequently, the form
		$scope.reset = function() {
			// Sets the form to it's pristine state
			if ($scope.flightForm) {
				$scope.flightForm.$setPristine();
			}
			// Clear input fields. If $scope.flight was set to an empty object
			// {},
			// then invalid form values would not be reset.
			// By specifying all properties, input fields with invalid values
			// are also reset.
			$scope.flight = {
				flightNumber : "",
				departurePoint : "",
				destinationPoint : ""
			};

			// clear messages
			$scope.messages.clear();
		};
		
		// Define an addFlight() function, which creates a new flight via
		// the
		// REST service,
		// using those details provided and displaying any error messages
		$scope.addFlight = function() {
			$scope.messages.clear();

			$scope.flights.save($scope.flight,
			// Successful query
			function(data) {

				// Update the list of flights
				$scope.flights.data.push(data);

				// Clear the form
				$scope.reset();

				// Add success message
				$scope.messages.push('success', 'Flight added');
				// Error
			}, function(result) {
				for ( var error in result.data) {
					$scope.messages.push('danger', result.data[error]);
				}
			});
		};
		// Define a saveFlight() function, which saves the current flight
		// using the REST service
		// and displays any error messages
		$scope.saveFlight = function() {
			$scope.messages.clear();
			$scope.flight.$update(
			// Successful query
			function(data) {
				// Find the flight locally by id and update it
				var idx = _.findIndex($scope.flights.data, {
					'id' : $scope.flight.id
				});
				$scope.flights.data[idx] = data;
				// Add success message
				$scope.messages.push('success', 'Flight saved');
				// Error
			}, function(result) {
				for ( var error in result.data) {
					$scope.messages.push('danger', result.data[error]);
				}
			})
		};
	}
})();