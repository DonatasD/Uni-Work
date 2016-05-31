package org.jboss.quickstarts.wfk.booking;

import org.jboss.quickstarts.wfk.customer.Customer;
import org.jboss.quickstarts.wfk.flight.Flight;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class BookingValidator {

    @Inject
    private Validator validator;

    @Inject
    private BookingRepository crud;

    /**
     * <p>
     * Validates {@link Booking} object and throws appropriate exceptions if constrains are violated
     * </p>
     * <p/>
     * <p>
     * {@link ConstraintViolationException} is thrown if if where are bean validation errors. {@link BookingValidationException}
     * is thrown if the bean does not meet {@link BookingValidator#customerIdExists(Long)},
     * {@link BookingValidator#flightIdExists(Long)} or {@link BookingValidator#flightAndDateAlreadyExists(Date, Long)}
     * </p>
     *
     * @param booking {@link Booking} that is being validated
     * @throws ConstraintViolationException
     * @throws BookingValidationException
     */

    public void validateBooking(Booking booking) throws ConstraintViolationException, BookingValidationException {
        // Create a bean validator and check for issues
        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }
        if (!customerIdExists(booking.getCustomerId())) {
            throw new BookingValidationException("Invalid Customer Id", BookingValidationExceptionEnum.INVALID_CUSTOMER_ID);
        }
        if (!flightIdExists(booking.getFlightId())) {
            throw new BookingValidationException("Invalid Flight Id", BookingValidationExceptionEnum.INVALID_FLIGHT_ID);
        }
        if (flightAndDateAlreadyExists(booking.getBookingDate(), booking.getFlightId())) {
            throw new BookingValidationException("The booking with the same booking date and flight id already exists", BookingValidationExceptionEnum.FLIGHT_AND_DATE_UNIQUENESS);
        }
    }

    /**
     * <p>
     * Return true if there is a {@link Customer} with <code>customerId</code>.
     * </p>
     *
     * @param customerId {@link Long} indicating {@link Customer#getId()}
     * @return boolean which represents if such customer with <code>customerId</code> exists in application database.
     */
    boolean customerIdExists(Long customerId) {
        return crud.findCustomerById(customerId) != null;
    }

    /**
     * <p>
     * Returns true if there is a {@link Flight} with <code>flightId</code>
     * </p>
     *
     * @param flightId {@link Long} indicating {@link Flight#getId()}
     * @return boolean which represents if such flight with <code>flighId</code> exists in application database.
     */

    boolean flightIdExists(Long flightId) {
        return crud.findFlightById(flightId) != null;
    }

    /**
     * <p>
     * Check if a {@link Booking} with the same bookingDate and flightId already exists. This allows to capture
     * '@UniqueConstraint(columnNames = { "bookingDate", "flightId" })' constrain from {@link Booking} class.
     * </p>
     * <p/>
     * <p/>
     * Since Update is not supported we do not need to check that this {@link Booking} with <code>id</code> already exists in
     * application database.
     *
     * @param bookingDate {@link Date} object indicating <code>bookingDate</code>
     * @param flightId    {@link Long} object indicating <code>flightId</code>
     * @param id          {@link Long} object indicating {@link Booking#getId()}
     * @return boolean which represents whether the {@link Booking} already exists, which specified <code>bookingDate</code> and
     * <code>flightId</code>
     */

    boolean flightAndDateAlreadyExists(Date bookingDate, Long flightId) {
        Booking booking = null;
        try {
            booking = crud.findByDateAndFlight(bookingDate, flightId);
        } catch (NoResultException e) {
            // ignore
        }
        return booking != null;
    }

}
