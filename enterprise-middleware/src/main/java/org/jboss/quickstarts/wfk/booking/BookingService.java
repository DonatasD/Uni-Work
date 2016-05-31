package org.jboss.quickstarts.wfk.booking;

import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolationException;

import org.apache.http.impl.client.CloseableHttpClient;
import org.jboss.quickstarts.wfk.customer.Customer;

/**
 * <p>
 * Service class for {@link Booking}. It assumes the Control responsibility in the ECP pattern.
 * </p>
 * <p/>
 * <p>
 * The validation is done here so that it may be used by other Boundary Resources. Other Business Logic would go here as well.
 * </p>
 * <p/>
 * <p>
 * There are no access modifiers on the methods, making them 'package' scope. They should only be accessed by a Boundary / Web
 * Service class with public methods.
 * </p>
 *
 * @author Donatas Daubaras
 * @see BookingValidator
 * @see BookingRepository
 */
@Dependent
public class BookingService {

    @Inject
    private @Named("logger") Logger log;

    @Inject
    private BookingValidator validator;

    @Inject
    private BookingRepository crud;

    @Inject
    private @Named("httpClient") CloseableHttpClient httpClient;

    Booking findById(Long id) {
        return crud.findById(id);
    }

    /**
     * <p>
     * Returns a {@link List} of all persisted {@link Booking} objects, sorted by {@link Booking#getCustomerId()}.
     * </p>
     *
     * @return {@link List} of {@link Booking} objects.
     */

    List<Booking> findAllOrderedByCustomerId() {
        return crud.findAllOrderedByCustomerId();
    }

    /**
     * <p>
     * Returns a {@link List} of {@link Booking} objects that were made by defined {@link Customer} object.
     * </p>
     *
     * @param customer {@link Customer} object that made {@link List} of {@link Booking} objects that are returned.
     * @return {@link List} of {@link Booking} objects that was created by <code>customer</code>
     */

    List<Booking> findAllByCustomer(Customer customer) {
        return crud.findAllByCustomerId(customer.getId());
    }

    /**
     * <p>
     * Writes the provided {@link Booking} object to the application database.
     * </p>
     * <p/>
     * <p>
     * Validates the data in the provided {@link Booking} object using {@link BookingValidator}.
     * </p>
     *
     * @param booking {@link Booking} object to be written to application database using {@link BookingRepository}
     * @return {@link Booking} that has been successfully written to application database.
     * @throws ConstraintViolationException
     * @throws BookingValidationException
     * @throws Exception
     */
    Booking create(Booking booking) throws Exception {
        log.info("BookingService.create() - Creating " + booking.toString());
        booking.setCustomer(crud.findCustomerById(booking.getCustomerId()));
        booking.setFlight(crud.findFlightById(booking.getFlightId()));

        // Check that booking contains valid data.
        validator.validateBooking(booking);
        // Write the booking to the database
        return crud.create(booking);
    }

    /**
     * <p>
     * Deletes the provided {@link Booking} object from application database if found there.
     * </p>
     *
     * @param booking {@link Booking} object to be removed from the application database
     * @return {@link Booking} object that has been successfully removed from application database; or null
     * @throws Exception
     */

    Booking delete(Booking booking) throws Exception {
        log.info("BookingService.delete() - Deleting " + booking.toString());

        Booking deletedBooking = null;

        if (booking.getId() != null) {
            deletedBooking = crud.delete(booking);
        } else {
            log.info("BookingService.delete() - No ID was found so cannot Delete.");
        }

        return deletedBooking;
    }
}
