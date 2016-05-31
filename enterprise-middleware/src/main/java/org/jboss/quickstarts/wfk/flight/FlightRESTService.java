package org.jboss.quickstarts.wfk.flight;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * <p>
 * This class exposes the functionality of {@link FlightService} over HTTP endpoints as a RESTful resource via JAX-RS.
 * </p>
 * <p/>
 * <p>
 * Full path for accessing the {@link Flight} resource is rest/flight
 * </p>
 * <p/>
 * <p>
 * The resource accepts and produces JSON.
 * </p>
 *
 * @author Donatas Daubaras
 * @see FlightService
 * @see javax.ws.rs.core.Response
 */
@Path("/flights")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class FlightRESTService {

    @Inject
    private
    @Named("logger")
    Logger log;

    @Inject
    private FlightService service;

    /**
     * <p>
     * Search for and return all {@link Flight} objects. They are sorted by {@link Flight#getFlightNumber()}.
     * </p>
     *
     * @return A {@link Response} containing a list of {@link Flight} objects
     */

    @GET
    public Response retrieveAllFlights() {
        List<Flight> flights = service.findAllOrderedByFlightNumber();
        return Response.ok(flights).build();
    }

    /**
     * <p>
     * Search for and return a {@link Flight} identified by flightNumber.
     * </p>
     * <p/>
     * <p>
     * Path annotation includes a regular expression to differentiate {@link Flight#getFlightNumber()} and
     * {@link Flight#getId()}. This regular expression <strong>IS NOT</strong> to validate {@link Flight#getFlightNumber()}.
     * </p>
     * <p>
     * The Path defined must be used to tell the difference between id and flightNumber. Because 11111 is valid id and 11111 is
     * valid flightNumber.
     * </p>
     *
     * @param flightNumber The {@link String} value provided as a {@link Flight#getFlightNumber()}
     * @return {@link Response} containing a single {@link Flight} object
     */
    @GET
    @Path("/flightnumber/{flightNumber:[0-9A-Za-z]+}")
    public Response retrieveFlightByFlightNumber(@PathParam("flightNumber") String flightNumber) {
        Flight flight;
        try {
            flight = service.findByFlightNumber(flightNumber);
        } catch (NoResultException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        log.info("Flight findByflighNumber " + flightNumber + ": found " + flight.toString());
        return Response.ok(flight).build();
    }

    /**
     * <p>
     * Search for and return a {@link Flight} identified by id
     * </p>
     * <p>
     * The Path defined must be used to tell the difference between id and flightNumber. Because 11111 is valid id and 11111 is
     * valid flightNumber.
     * </p>
     *
     * @param id The long parameter value provided as a {@link Flight#getId()}
     * @return {@link Response} containing a single {@link Flight}
     */

    @GET
    @Path("/id/{id:[0-9]+}")
    public Response retriveFlightById(@PathParam("id") Long id) {
        Flight flight = service.findById(id);
        if (flight == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        log.info("Flight findById " + id + ": found " + flight.toString());
        return Response.ok(flight).build();
    }

    /**
     * <p>
     * Creates a new {@link Flight} from the values provided. Performs validation and will return a JAX-RS response with either
     * 200 (ok) or with a map of fields, and related errors.
     * </p>
     *
     * @param flight The {@link Flight} object, constructed automatically from JSON input, to be <i>created</i> via
     *               {@link FlightService#create(Flight)}
     * @return {@link Response} indicating the outcome of the create operation
     */

    @SuppressWarnings("unused")
    @POST
    public Response createFlight(Flight flight) {
        log.info("createFlight started. " + flight.toString());
        if (flight == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        Response.ResponseBuilder builder = null;

        try {
            // Add new Flight
            service.create(flight);

            // Create a "Resource Created" 201 Response and pass it back in case it is needed
            builder = Response.status(Response.Status.CREATED).entity(flight);
        } catch (ConstraintViolationException ce) {
            log.info("ConstraintViolationException - " + ce.toString());
            // Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (FlightValidationException e) {
            log.info("FlightValidationException - " + e.toString());
            // Handle the unique constrain violation
            Map<String, String> responseObj = new HashMap<String, String>();
            identifyFlightViolationException(responseObj, e);
            builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
        } catch (Exception e) {
            log.info("Exception - " + e.toString());
            // Handle generic exceptions
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return builder.build();
    }

    /**
     * <p>
     * Updates a flight with the id provided in the {@link Flight}. Performs validation, and will return a JAX-RS response with
     * either 200 (ok), or with a map of fields, and related errors.
     * </p>
     *
     * @param id     The long parameter value provided as the id of {@link Flight} object to be updated
     * @param flight {@link Flight} object, constructed automatically from JSON input, to be <i>updated</i> via
     *               {@link FlightService#update(Flight)}
     * @return {@link Response} indicating the outcome of the update operation
     */

    @PUT
    @Path("/{id:[0-9]+}")
    public Response updateFlight(@PathParam("id") long id, Flight flight) {
        if (flight == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        log.info("updateFlight started. " + flight.toString());
        if (flight.getId() != id) {
            // The client attempted to update the read-only id. This is not permitted
            Response response = Response.status(Response.Status.CONFLICT).entity("The flight id cannot be modified").build();
            throw new WebApplicationException(response);
        }
        if (service.findById(flight.getId()) == null) {
            // Verify that flight exists. Return 404, if not present.
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        Response.ResponseBuilder builder = null;

        try {
            // Apply changes to Flight
            service.update(flight);

            // Create an OK Response and pass the flight back in case it is needed.
            builder = Response.ok(flight);

            log.info("updateFlight completed. " + flight.toString());
        } catch (ConstraintViolationException ce) {
            log.info("ConstraintViolationException - " + ce.toString());
            // Handle the unique constrain violation
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (FlightValidationException e) {
            log.info("FlightValidationException - " + e.toString());
            // Handle the unique constrain violation
            Map<String, String> responseObj = new HashMap<String, String>();
            identifyFlightViolationException(responseObj, e);
            builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
        } catch (Exception e) {
            log.info("Exception - " + e.toString());
            // Handle generic exceptions
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return builder.build();
    }

    /**
     * <p>
     * This method identifies what the actual problem with {@link FlightValidator} was and stores appropriate message in
     * <code>reponseObj</code>, which should be sent to user.
     * </p>
     *
     * @param responseObj The {@link Map} object where errors are stored.
     * @param e           {@link FlightValidationException} exception that is being to be resolved
     * @see FlightValidationException
     * @see FlightValidationExceptionEnum
     */

    private void identifyFlightViolationException(Map<String, String> responseObj, FlightValidationException e) {
        switch (e.getExceptionType()) {
            case DESTINATION_EXCEPTION:
                responseObj.put("destinationPoint", "Destination point must be different from departure point");
                break;
            case FLIGHT_NUMBER_EXCEPTION:
                responseObj.put("flightNumber", "This flight number is already used, please use a unique flight number");
                break;
            default:
                responseObj.put("unknown", "Unknown error please contact system administrator");
                break;
        }
    }

    /**
     * <p>
     * Creates a JAX-RS "Bad Request" response including a map of all violation fields, and their message. This can be used by
     * calling client applications to display violations to users.
     * </p>
     *
     * @param violations A Set of violations that need to be reported in the Response body
     * @return A Bad Request (400) Response containing all violation messages
     */
    private Response.ResponseBuilder createViolationResponse(Set<ConstraintViolation<?>> violations) {
        log.fine("Validation completed. violations found: " + violations.size());

        Map<String, String> responseObj = new HashMap<String, String>();

        for (ConstraintViolation<?> violation : violations) {
            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        return Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
    }

}
