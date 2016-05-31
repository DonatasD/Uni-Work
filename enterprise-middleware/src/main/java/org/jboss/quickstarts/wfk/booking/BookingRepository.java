package org.jboss.quickstarts.wfk.booking;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolationException;

import org.jboss.quickstarts.wfk.customer.Customer;
import org.jboss.quickstarts.wfk.flight.Flight;

/**
 * <p>
 * This is a Repository class that connects the Service/Control layer (see {@link BookingService}) with the Domain/Entity Object
 * (see {@link Booking}).
 * </p>
 * <p/>
 * <p>
 * There are no access modifiers on the methods making them 'package' scope. They should only be accessed by a Service/Control
 * object.
 * </p>
 *
 * @author Donatas Daubaras
 * @see Booking
 * @see javax.persistence.EntityManager
 */

public class BookingRepository {

    @Inject
    private @Named("logger") Logger log;

    @Inject
    private EntityManager em;

    /**
     * <p>
     * Returns a single {@link Booking} object, specified by a {@link Long} id.
     * </p>
     *
     * @param id {@link Long} object indicating {@link Booking} with the corresponding id to be returned.
     * @return {@link Booking} object with specified <code>id</code>
     */
    Booking findById(Long id) {
        return em.find(Booking.class, id);
    }

    /**
     * <p>
     * Return a List of all persisted {@link Booking} objects, sorted by customer id.
     * </p>
     *
     * @return List of Booking objects
     */
    List<Booking> findAllOrderedByCustomerId() {
        TypedQuery<Booking> query = em.createNamedQuery(Booking.FIND_ALL, Booking.class);
        return query.getResultList();
    }

    /**
     * <p>
     * This method is needed to verify that {@link Booking} is made for registered {@link Customer}
     * </p>
     * 
     * <p>
     * Returns a single {@link Customer} object, specified by a {@link Long} customerId or null if not found.
     * </p>
     *
     * @param customerId the id field of {@link Customer} to be returned.
     * @return {@link Customer} with the specified id or null if not found.
     * @see BookingValidator#customerIdExists(Long)
     */
    Customer findCustomerById(Long customerId) {
        return em.find(Customer.class, customerId);
    }

    List<Booking> findAllByCustomerId(Long id) {
        TypedQuery<Booking> query = em.createNamedQuery(Booking.FIND_BY_CUSTOMER, Booking.class).setParameter("customerId", id);
        return query.getResultList();
    }

    /**
     * <p>
     * This method is needed to verify that {@link Booking} is made for registered {@link Flight}
     * </p>
     * <p/>
     * <p>
     * Returns a single {@link Flight} object, specified by a {@link Long} flightId or null if not found.
     * </p>
     *
     * @param flightId the id field of {@link Flight} to be returned.
     * @return {@link Flight} with the specified id or null if not found.
     */

    Flight findFlightById(Long flightId) {
        return em.find(Flight.class, flightId);
    }

    /**
     * <p>
     * Return a single {@link Booking} object found from the application database. If multiple Bookings are found return first
     * one.
     * </p>
     *
     * @param bookingDate {@link Date} object indicating when the booking was made.
     * @param flightId {@link Long} object indicating flight's id.
     * @return {@link Booking} object, which contains <code>bookingDate</code> and <code>flightId</code>.
     */

    Booking findByDateAndFlight(Date bookingDate, Long flightId) {
        TypedQuery<Booking> query = em.createNamedQuery(Booking.FIND_BY_DATE_AND_FLIGHT, Booking.class).setParameter("bookingDate", bookingDate).setParameter("flightId", flightId);
        return query.getSingleResult();
    }

    /**
     * <p>
     * Creates {@link Booking} object in the application database with the provided {@link Booking} object.
     * </p>
     * <p/>
     * <p>
     * {@link BookingService} is responsible to check that the object passed to this method is valid.
     * </p>
     *
     * @param booking {@link Booking} object to me created
     * @return {@link Booking} that was created
     * @throws ConstraintViolationException
     * @throws BookingValidationException
     * @throws Exception
     * @see javax.persistence.EntityManager#persist(Object)
     * @see BookingService#create(Booking)
     */

    Booking create(Booking booking) throws Exception {
        // Log the creation of the booking
        log.info("BookingRepository.create() - Creating " + booking.toString());

        // Write booking to database
        em.persist(booking);

        return booking;
    }

    /**
     * <p>
     * Deletes the provided {@link Booking} object from the application database if found there.
     * </p>
     *
     * @param booking {@link Booking} object to be removed from the application database
     * @return The {@link Booking} object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    Booking delete(Booking booking) throws Exception {
        log.info("BookingRepository.delete()" + booking.toString());

        if (booking.getId() != null) {
            em.remove(em.merge(booking));
        } else {
            log.info("BookingRepository.delete() - No ID was found so can't Delete.");
            return null;
        }
        return booking;
    }

}
