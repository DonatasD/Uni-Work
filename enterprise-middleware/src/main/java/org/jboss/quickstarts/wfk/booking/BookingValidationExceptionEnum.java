package org.jboss.quickstarts.wfk.booking;

import org.jboss.quickstarts.wfk.flight.FlightValidationException;

/**
 * Enumeration class to store possible {@link FlightValidationException#getExceptionType()}
 *
 * @author Donatas Daubaras
 * @see FlightValidationException
 */

public enum BookingValidationExceptionEnum {
    INVALID_FLIGHT_ID, INVALID_CUSTOMER_ID, FLIGHT_AND_DATE_UNIQUENESS
}
