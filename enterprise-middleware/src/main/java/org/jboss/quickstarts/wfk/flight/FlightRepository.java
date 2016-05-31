package org.jboss.quickstarts.wfk.flight;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Donatas Daubaras
 * @see Flight
 * @see javax.persistence.EntityManager
 */

public class FlightRepository {

    @Inject
    private
    @Named("logger")
    Logger log;

    @Inject
    private EntityManager em;

    /**
     * <p>
     * Return a List of all persisted {@link Flight} objects, sorted by flight number
     * </p>
     *
     * @return List of Flight objects
     */
    List<Flight> findAllOrderedByFlightNumber() {
        TypedQuery<Flight> query = em.createNamedQuery(Flight.FIND_ALL, Flight.class);
        return query.getResultList();
    }

    /**
     * <p>
     * Returns a single Flight object, specified by a Long id.
     * </p>
     *
     * @param id The id field of the Flight to be returned
     * @return The Flight with specified id
     */
    Flight findById(Long id) {
        return em.find(Flight.class, id);
    }

    /**
     * <p>
     * Returns a single Flight object, specified by a String flightNumber
     * </p>
     * <p/>
     * <p>
     * If there is more than one Flight with the specified FlightNumber, only the first encountered will be returned.
     * </p>
     *
     * @param flightNumber The flight number of the Flight to be returned
     * @return The first Flight with the specified flight number
     */
    Flight findByFlightNumber(String flightNumber) {
        TypedQuery<Flight> query = em.createNamedQuery(Flight.FIND_BY_NUMBER, Flight.class).setParameter("flightNumber", flightNumber);
        return query.getSingleResult();
    }

    /**
     * <p>
     * Returns a {@link Flight} object that was specified in parameters if object was written properly.
     * </p>
     *
     * @param flight {@link Flight} object that is trying to be created
     * @return {@link Flight} object if it was written to the database
     * @throws ConstraintViolationException
     * @throws FlightValidationException
     * @throws Exception
     */

    Flight create(Flight flight) throws Exception {
        // Log the creation of the flight
        log.info("FlightRepository.create() - Creating " + flight.getFlightNumber());

        // Write the flight to the database
        em.persist(flight);

        return flight;
    }

    /**
     * <p>
     * Updates an existing Flight object in the application database with the provided Flight object.
     * </p>
     * <p/>
     * <p>
     * {@link javax.persistence.EntityManager#merge(Object) merge(Object)} creates a new instance of your entity, copies the
     * state from the supplied entity, and makes the new copy managed. The instance you pass in will not be managed (any changes
     * you make will not be part of the transaction - unless you call merge again).
     * </p>
     * <p/>
     * <p>
     * merge(Object) however must have an object with the @Id already generate
     * </p>
     *
     * @param flight The Flight object to be merged with an existing Flight
     * @return The Flight that has been merged
     * @throws ConstraintViolationException, ValidationException, Exception
     */

    Flight update(Flight flight) throws Exception {
        // Log the update of the flight
        log.info("FlightRepository.update() - Updating " + flight.getFlightNumber());

        //Either update the flight or add it if it can't be found
        em.merge(flight);

        return flight;
    }

}
