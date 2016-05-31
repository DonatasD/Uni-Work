package org.jboss.quickstarts.wfk.flight;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>
 * This class validates that <code>Flight</code> meets all the defined constrains.
 * </p>
 *
 * @author Donatas Daubaras
 * @see Flight
 */

public class FlightValidator {

    @Inject
    private Validator validator;

    @Inject
    private FlightRepository crud;

    /**
     * <p>
     * Validates the given Flight object and throws exceptions based on the type of error. If the error is standard bean
     * validation errors then it will throw ConstraintViolationException with the set of the constrains violated.
     * </p>
     * <p/>
     * <p>
     * If the error is caused because flight object contains same departure and destination point it throws ValidationException.
     * </p>
     *
     * @param flight The Flight object to be validated
     * @throws ConstraintViolationException If Bean Validation error exists
     * @throws ValidationException          If flight departure and destination points are the same
     */

    void validateFlight(Flight flight) throws ConstraintViolationException, FlightValidationException {
        Set<ConstraintViolation<Flight>> violations = validator.validate(flight);

        // Create a bean validator and check for issues
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }
        // Check if destination is different from departure point
        if (!destinationDifferentFromDeparture(flight.getDeparturePoint(), flight.getDestinationPoint())) {
            throw new FlightValidationException("Destination Different From Departure Violation", FlightValidationExceptionEnum.DESTINATION_EXCEPTION);
        }
        //Check if registration number is unique
        if (flightNumberAlreadyExists(flight.getFlightNumber(), flight.getId())) {
            throw new FlightValidationException("Unique Flight Number Violation", FlightValidationExceptionEnum.FLIGHT_NUMBER_EXCEPTION);
        }

    }

    /**
     * <p>
     * Check if flight that the flight has different destination and departure points.
     * </p>
     *
     * @param destination Destination point I.E. (VLN)
     * @param departure   Departure point I.E. (NCL)
     * @return boolean which represents whether destination and departure points are different. If one of the are null return
     * false.
     */

    boolean destinationDifferentFromDeparture(String departure, String destination) {
        if (destination == null || departure == null) {
            return false;
        }
        return !destination.equals(departure);
    }

    /**
     * <p>
     * Checks if a flight with the same flightNumber is already registered. This is the only way to easily capture the
     * "@UniqueConstraint(columnNames = "flightNumber")" constraint from Flight class.
     * </p>
     * <p/>
     * <p>
     * Since Update will be using an email that is already in the database we need to make sure that it is the flightNumber from
     * the record being updated.
     * </p>
     *
     * @param flightNumber The flight number to check is unique
     * @param id           The Flight id to check the flightNumber against if it was found
     * @return boolean which represents whether the email was found, and if so it belongs to the flight with id
     */
    boolean flightNumberAlreadyExists(String flightNumber, Long id) {
        Flight flight = null;
        Flight flightWithId = null;
        try {
            flight = crud.findByFlightNumber(flightNumber);
        } catch (NoResultException e) {
            // ignore
        }
        if (flight != null && id != null) {
            try {
                flightWithId = crud.findById(id);
                if (flightWithId != null && flightWithId.getFlightNumber().equals(flightNumber)) {
                    flight = null;
                }
            } catch (NoResultException e) {
                // ignore
            }
        }
        return flight != null;
    }
}
