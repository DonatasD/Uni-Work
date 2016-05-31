package org.jboss.quickstarts.wfk.booking;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.quickstarts.wfk.customer.Customer;

@Path("/bookings")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class BookingRESTService {

    @Inject
    private @Named("logger") Logger log;

    @Inject
    private BookingService service;

    /**
     * <p>
     * Search and return all the {@link Booking}. They are sorted by {@link Booking#getCustomerId()}
     * </p>
     *
     * @return A Response containing list of Bookings.
     */

    @GET
    public Response retrieveAllBookings() {
        List<Booking> bookings = service.findAllOrderedByCustomerId();
        return Response.ok(bookings).build();
    }

    @GET
    @Path("{id:[0-9]+}")
    public Response retrieveBookingById(@PathParam("id") Long id) {
        Booking booking = service.findById(id);
        return Response.ok(booking).build();
    }

    @GET
    @Path("/customer")
    public Response retrieveBookingsByCustomer(Customer customer) {
        List<Booking> bookings = service.findAllByCustomer(customer);
        return Response.ok(bookings).build();
    }

    /**
     * <p>
     * Creates a new booking from the values provided. You only need to provided flightId and customerId, booking date is set to
     * current system date. If validation is passed a response with 200(ok) is returned. Otherwise we return response with
     * related errors.
     * </p>
     *
     * @param booking {@link Booking} object constructed from JSON input.
     * @return A Response indicating the outcome of the create operation
     */

    @POST
    public Response createBooking(Booking booking) {
        log.info("createBooking started. " + booking.toString());

        if (booking == null || booking.getCustomerId() == null || booking.getFlightId() == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        Response.ResponseBuilder builder = null;

        try {
            // Add new Booking;
            service.create(booking);

            // Create a "Resource Created" 201 response and pass the booking back in case it is needed.
            builder = Response.status(Response.Status.CREATED).entity(booking);

            log.info("createBooking completed. " + booking.toString());
        } catch (ConstraintViolationException ce) {
            log.info("ConstraintViolationException - " + ce.toString());
            // Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (BookingValidationException e) {
            log.info("BookingValidationException - " + e.toString());
            // Handle the unique constrain violation
            Map<String, String> responseObj = new HashMap<String, String>();
            identifyBookingViolationException(responseObj, e);
            builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
        } catch (Exception e) {
            log.info("Exception - " + e.toString());
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return builder.build();
    }

    /**
     * <p>
     * Deletes a {@link Booking} with <code>id</code> provided.
     * </p>
     *
     * @param id The id of {@link Booking} object that is trying to be deleted.
     * @return {@link Response} indicating the outcome of the delete operation.
     */
    @DELETE
    @Path("/{id:[0-9]+}")
    public Response deleteBooking(@PathParam("id") Long id) {
        log.info("deleteBooking started. Booking ID = " + id);
        Response.ResponseBuilder builder = null;
        try {
            Booking booking = service.findById(id);
            if (booking != null) {
                service.delete(booking);
            } else {
                log.info("BookingRESTService - deleteBooking - No booking with matching ID was found so cannot Delete.");
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            builder = Response.noContent();
            log.info("deleteBooking completed. " + booking.toString());
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
     * This method allows to identify why {@link BookingValidationException} was thrown and send client appropriate message.
     * </p>
     *
     * @param responseObj {@link Map} object, which will be added with appropriate errors.
     * @param e {@link BookingValidationException} that was thrown.
     */

    private void identifyBookingViolationException(Map<String, String> responseObj, BookingValidationException e) {
        switch (e.getExceptionType()) {
            case FLIGHT_AND_DATE_UNIQUENESS:
                responseObj.put("FlightAndDate", "The combination of FlightId and bookingDate must be unique");
                break;
            case INVALID_CUSTOMER_ID:
                responseObj.put("CustomerId", "The booking cannot be made for this customer, because such customer does not exist");
                break;
            case INVALID_FLIGHT_ID:
                responseObj.put("FlightId", "The booking cannot be made for this flight, because such flight does not exists");
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
        log.fine("Validation completed. Violations found: " + violations.size());

        Map<String, String> responseObj = new HashMap<String, String>();

        for (ConstraintViolation<?> violation : violations) {
            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
        }
        return Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
    }

}
