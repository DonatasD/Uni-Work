package org.jboss.quickstarts.wfk.flight;

import org.apache.http.impl.client.CloseableHttpClient;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>
 * This Service assumes the Control responsibility in the ECB pattern.
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
 * @see FlightValidator
 * @see FlightRepository
 */
@Dependent
public class FlightService {

    @Inject
    private
    @Named("logger")
    Logger log;

    @Inject
    private FlightValidator validator;

    @Inject
    private FlightRepository crud;

    @Inject
    private
    @Named("httpClient")
    CloseableHttpClient httpClient;

    /**
     * <p>
     * Returns a List of all persisted {@link Flight} objects, sorted by flightNumber
     * </p>
     *
     * @return List of Flight Objects
     */

    List<Flight> findAllOrderedByFlightNumber() {
        return crud.findAllOrderedByFlightNumber();
    }

    /**
     * <p>
     * Returns a single Flight object, specified by a Long id.
     * </p>
     *
     * @param id The id field of the Flight to be returned
     * @return The Flight with the specified id
     */

    Flight findById(Long id) {
        return crud.findById(id);
    }

    /**
     * <p>
     * Returns a single Flight object, specified by a String flightNumber.
     * </p>
     * <p/>
     * <p>
     * If there is more than one Flight with the specified flightNumber, only the first encountered will be returned.
     * </p>
     *
     * @param flightNumber The flightNumber field of the Flight to be returned
     * @return The first Flight with the specified flightNumber
     */

    Flight findByFlightNumber(String flightNumber) {
        return crud.findByFlightNumber(flightNumber);
    }

    /**
     * <p>
     * Writes the provided Flight object to the application database.
     * </p>
     * <p/>
     * <p>
     * Validates the data in the provided Flight object using a {@link FlightValidator} object.
     * </p>
     *
     * @param flight The Flight object to be written to the database using a {@link FlightRepository} object.
     * @return The Flight object that has been successfully written to the application database
     * @throws ConstraintViolationException
     * @throws ValidationException
     * @throws Exception
     */

    Flight create(Flight flight) throws Exception {
        log.info("FlightService.create() - Creating " + flight.getFlightNumber());
        // Check to make sure the data fits with the parameters in the Flight model and passes validation
        validator.validateFlight(flight);
        // Write the flight to the database
        return crud.create(flight);
    }

    /**
     * <p>
     * Updates and existing Flight object in the application database with the provided Flight object.
     * </p>
     * <p/>
     * <p>
     * Validates the data in the provided Flight object using FlightValidator object.
     * </p>
     *
     * @param flight The Flight object to be passed as an update to the application database.
     * @return The Flight object that has been successfully updated in the application database
     * @throws ConstraintViolationException
     * @throws ValidationException
     * @throws Exception
     */

    Flight update(Flight flight) throws Exception {
        log.info("FlightService.update() - Updating " + flight.getFlightNumber());

        // Check to make sure the data fits with the parameters in the Flight model and passes validation.
        validator.validateFlight(flight);

        // Write the flight to the database.
        return crud.update(flight);
    }
}
