package org.jboss.quickstarts.wfk.travelagency;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
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

import org.json.JSONException;
import org.json.JSONObject;

@Path("/agency")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class TravelAgencyRestService {
    @Inject
    private @Named("logger") Logger log;

    @Inject
    private TravelAgencyService service;

    /**
     * <p>
     * Returns all available taxis from external Service.
     * </p>
     * 
     * @return {@link Response} containing all possible taxi on external service
     */
    @GET
    @Path("/taxis")
    public Response retrieveAllTaxis() {
        log.info("TravelAgencyRestService retrieveAllTaxis() started.");
        Response.ResponseBuilder builder = null;
        try {
            String response = service.retrieveAllTaxis();
            builder = Response.ok(response);
            log.info("TravelAgencyRestService retrieveAllTaxis() completed.");
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
     * Returns all available hotel's customer
     * </p>
     * 
     * @return {@link Response} containing all possible customers registered on Taxi service.
     */
    @GET
    @Path("/taxis/customers")
    public Response retrieveAllTaxiCustomers() {
        log.info("TravelAgencyRestService retrieveAllTaxiCustomers() started.");
        Response.ResponseBuilder builder = null;
        try {
            String response = service.retrieveAllTaxiCustomers();
            builder = Response.ok(response);
            log.info("TravelAgencyRestService retrieveAllTaxiCustomers() completed.");
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
     * Returns all bookings that were made on taxi service.
     * </p>
     * 
     * @return {@link Response} containing all possible taxi bookings registered on Taxi service
     */
    @GET
    @Path("/taxis/bookings")
    public Response retrieveAllTaxiBookings() {
        log.info("TravelAgencyRestService retrieveAllTaxiBookings() started.");
        Response.ResponseBuilder builder = null;

        try {
            String response = service.retrieveAllTaxiBookings();
            builder = Response.ok(response);
            log.info("TravelAgencyRestService retrieveAllTaxiBookings() completed.");
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
     * Returns a specific taxi's customer by its email.
     * </p>
     * 
     * @param email {@link String} indicates taxis customer's object email
     * @return {@link Response} which contains taxi's booking object with predefined <code>email</code>.
     */
    @GET
    @Path("taxis/customer/{email:^.+@.+$}")
    public Response retrieveTaxiCustomerByEmail(@PathParam("email") String email) {
        log.info("TravelAgencyRestService retrieveTaxiCustomerByEmail() " + email + " started");
        Response.ResponseBuilder builder = null;
        try {
            String response = service.retrieveTaxiCustomerByEmail(email);
            builder = Response.ok(response);
            log.info("TravelAgencyRestService retrieveTaxiCustomerByEmail() " + email + " completed");
        } catch (Exception e) {
            log.info("Exception - " + e.toString());
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            responseObj.put("notFound", "Customer with this email cannot be found");
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return builder.build();
    }

    /**
     * <p>
     * Returns all available flights from external Service.
     * </p>
     * 
     * @return {@link Response} containing all possible flights on external service
     */
    @GET
    @Path("/flights")
    public Response retrieveAllFlights() {
        log.info("TravelAgencyRestService retrieveAllFlights() started.");
        Response.ResponseBuilder builder = null;
        try {
            String response = service.retrieveAllFlights();
            builder = Response.ok(response);
            log.info("TravelAgencyRestService retrieveAllFlights() completed.");
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
     * Returns all available flight's customers
     * </p>
     * 
     * @return {@link Response} containing all possible customers registered on Flight service.
     */
    @GET
    @Path("/flights/customers")
    public Response retrieveAllFlightCustomers() {
        log.info("TravelAgencyRestService retrieveAllFlightCustomers() started.");
        Response.ResponseBuilder builder = null;
        try {
            String response = service.retrieveAllFlightCustomers();
            builder = Response.ok(response);
            log.info("TravelAgencyRestService retrieveAllFlightCustomers() completed.");
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
     * Returns all bookings that were made on flight service.
     * </p>
     * 
     * @return {@link Response} containing all possible flight bookings registered on Flight service.
     */
    @GET
    @Path("/flights/bookings")
    public Response retrieveAllFlightBookings() {
        log.info("TravelAgencyRestService retrieveAllFlightBookings() started.");
        Response.ResponseBuilder builder = null;

        try {
            String response = service.retrieveAllFlightBookings();
            builder = Response.ok(response);
            log.info("TravelAgencyRestService retrieveAllFlightBookings() completed.");
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
     * Returns a specific flight's customer by its email.
     * </p>
     * 
     * @param email {@link String} indicates flight customer's object email
     * @return {@link Response} which contains flight's booking object with predefined <code>email</code>.
     */
    @GET
    @Path("flights/customers/{email:^.+@.+$}")
    public Response retrieveFlightCustomerByEmail(@PathParam("email") String email) {
        log.info("TravelAgencyRestService retrieveFlightCustomerByEmail() " + email + " started");
        Response.ResponseBuilder builder = null;
        try {
            String response = service.retrieveFlightCustomerByEmail(email);
            builder = Response.ok(response);
            log.info("TravelAgencyRestService retrieveFlightCustomerByEmail() " + email + " completed");
        } catch (Exception e) {
            log.info("Exception - " + e.toString());
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            responseObj.put("notFound", "Customer with this email cannot be found");
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return builder.build();
    }

    /**
     * <p>
     * Returns all possible hotels
     * </p>
     * 
     * @return {@link Response} containing all hotel objects created in external service
     */
    @GET
    @Path("/hotels")
    public Response retrieveAllHotels() {
        log.info("TravelAgencyRestService retrieveAllHotels() started.");
        Response.ResponseBuilder builder = null;
        try {
            String response = service.retrieveAllHotels();
            builder = Response.ok(response);
            log.info("TravelAgencyRestService retrieveAllHotels() completed.");
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
     * Returns all available hotels's customers
     * </p>
     * 
     * @return {@link Response} containing all possible customers registered on Hotel service.
     */
    @GET
    @Path("hotels/customers")
    public Response retrieveAllHotelCustomers() {
        log.info("TravelAgencyRestService retrieveAllHotelCustomers() started.");
        Response.ResponseBuilder builder = null;
        try {
            String response = service.retrieveAllHotelCustomers();
            builder = Response.ok(response);
            log.info("TravelAgencyRestService retrieveAllHotelCustomers() completed.");
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
     * This can be used to check if the booking we made is actually created on hotel service.
     * 
     * @see retrieveHotelBookingById(Long id)
     *      </p>
     * 
     * @return {@link Response} containing all possible flight bookings registered on Flight service.
     */
    @GET
    @Path("/hotels/bookings")
    public Response retrieveAllHotelBookings() {
        log.info("TravelAgencyRestService retrieveAllHotelBookings() started.");
        Response.ResponseBuilder builder = null;

        try {
            String response = service.retrieveAllHotelBookings();
            builder = Response.ok(response);
            log.info("TravelAgencyRestService retrieveAllHotelBookings() completed.");
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
     * Returns a specific hotel's customer by its email.
     * </p>
     * 
     * @param email {@link String} indicates hotel's customer's object email
     * @return {@link Response} which contains hotel's booking object with predefined <code>email</code>
     */
    @GET
    @Path("hotels/customers/{email:^.+@.+$}")
    public Response retrieveHotelCustomerByEmail(@PathParam("email") String email) {
        log.info("TravelAgencyRestService retrieveHotelCustomerByEmail() " + email + " started");
        Response.ResponseBuilder builder = null;
        try {
            String response = service.retrieveHotelCustomerByEmail(email);
            builder = Response.ok(response);
            log.info("TravelAgencyRestService retrieveHotelCustomerByEmail() " + email + " completed");
        } catch (WebApplicationException e) {
            log.info("Exception - " + e.getMessage());
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("notFound", "The customer could not be found");
            builder = Response.status(e.getResponse().getStatus()).entity(responseObj);
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
     * Makes a request to external flight service and creates a booking object, which is specified as {@link JSONObject} in
     * <code>booking</code>
     * </p>
     * 
     * <p>
     * Possible status are:
     * <li>{@value Response.Status#CREATED} indicates it was created properly</li>
     * <li>{@value Response.Status#UNSUPPORTED_MEDIA_TYPE} indicates that the data which was sent to this service unsupported</li>
     * <li>{@value Response.Status#BAD_REQUEST} bad request was made. Something bad happened in general</li>
     * </p>
     * 
     * @param booking {@link String}, which can be parsed to {@link JSONObject}
     * @return {@link Response} indicating whether or not creation succeeded
     */
    @POST
    @Path("/flights/bookings")
    public Response createFlightBooking(String booking) {
        log.info("TravelAgencyRestService createFlightBooking() started. " + booking);
        Response.ResponseBuilder builder = null;
        try {
            JSONObject jsonFlightCustomer = new JSONObject(booking);
            String response = service.createFlightBooking(jsonFlightCustomer);
            builder = Response.status(Response.Status.CREATED).entity(response);
            log.info("TravelAgencyRestService createFlightBooking() completed.");
        } catch (JSONException e) {
            log.info("JSONException - " + e.toString());
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("JSONerror", e.getMessage());
            responseObj.put("JSONFormat", "JSON provided is not supported");
            builder = Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity(responseObj);
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
     * Makes a request to external hotel service and creates a booking object, which is specified as {@link JSONObject} in
     * <code>booking</code>
     * </p>
     * 
     * <p>
     * Possible status are:
     * <li>{@value Response.Status#CREATED} indicates it was created properly</li>
     * <li>{@value Response.Status#UNSUPPORTED_MEDIA_TYPE} indicates that the data which was sent to this service unsupported</li>
     * <li>{@value Response.Status#BAD_REQUEST} bad request was made. Something bad happened in general</li>
     * </p>
     * 
     * @param booking {@link String}, which can be parsed to {@link JSONObject}
     * @return {@link Response} indicating whether or not creation succeeded
     */
    @POST
    @Path("/hotels/bookings")
    public Response createHotelBooking(String booking) {
        log.info("TravelAgencyRestService createHotelBooking() started. " + booking);
        Response.ResponseBuilder builder = null;
        try {
            JSONObject jsonHotelBooking = new JSONObject(booking);
            String response = service.createHotelBooking(jsonHotelBooking);
            builder = Response.status(Response.Status.CREATED).entity(response);
            log.info("TravelAgencyRestService createHotelBooking() completed.");
        } catch (JSONException e) {
            log.info("JSONException - " + e.toString());
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("JSONerror", e.getMessage());
            responseObj.put("FormatError", "JSON provided is not supported");
            builder = Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity(responseObj);
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
     * Makes a request to external taxi service and created a booking object, which is specified as {@link JSONObject} in
     * <code>booking</code>
     * </p>
     * 
     * <p>
     * Possible status are:
     * <li>{@value Response.Status#CREATED} indicates it was created properly</li>
     * <li>{@value Response.Status#UNSUPPORTED_MEDIA_TYPE} indicates that the data which was sent to this service unsupported</li>
     * <li>{@value Response.Status#BAD_REQUEST} bad request was made. Something bad happened in general</li>
     * </p>
     * 
     * @param booking
     * @return
     */
    @POST
    @Path("/taxis/bookings")
    public Response createTaxiBooking(String booking) {
        log.info("TravelAgencyRestService createTaxiBooking() started. " + booking);
        Response.ResponseBuilder builder = null;
        try {
            JSONObject jsonTaxiBooking = new JSONObject(booking);
            String response = service.createTaxiBooking(jsonTaxiBooking);
            builder = Response.status(Response.Status.CREATED).entity(response);
            log.info("TravelAgencyRestService createTaxiBooking() completed.");
        } catch (JSONException e) {
            log.info("JSONException - " + e.toString());
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("JSONerror", e.getMessage());
            responseObj.put("FormatError", "JSON provided is not supported");
            builder = Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity(responseObj);
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
     * Makes a request to flight service and removes a booking object, which contains <code>id</code>.
     * </p>
     * 
     * @param id {@link Long} specifying, which flight booking is trying to be deleted
     * @return {@link Response} indicating whether or not it succeeded deleting the flight booking.
     */
    @DELETE
    @Path("/flights/bookings/{id:[0-9]+}")
    public Response removeFlightBooking(@PathParam("id") Long id) {
        log.info("TravelAgencyRestService removeFlightBooking() started. " + id);
        Response.ResponseBuilder builder = null;
        try {
            service.removeFlightBooking(id);
            builder = Response.noContent();
            log.info("TravelAgencyRestService removeFlightBooking() completed.");
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
     * Makes a request to hotel service and removes a booking object, which contains <code>id</code>.
     * </p>
     * 
     * @param id {@link Long} specifying, which hotel booking is trying to be deleted
     * @return {@link Response} indicating whether or not it succeeded deleting the hotel booking.
     */
    @DELETE
    @Path("/hotels/bookings/{id:[0-9]+}")
    public Response removeHotelBooking(@PathParam("id") Long id) {
        log.info("TravelAgencyRestService removeHotelBooking() started. " + id);
        Response.ResponseBuilder builder = null;
        try {
            service.removeHotelBooking(id);
            builder = Response.noContent();
            log.info("TravelAgencyRestService removeHotelBooking() completed.");
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
     * Makes a request to taxi service and removes a booking object, which contains <code>id</code>.
     * </p>
     * 
     * @param id {@link Long} specifying, which taxi booking is trying to be deleted
     * @return {@link Response} indicating whether or not it succeeded deleting the taxi booking.
     */
    @DELETE
    @Path("/taxis/bookings/{id:[0-9]+}")
    public Response removeTaxiBooking(@PathParam("id") Long id) {
        log.info("TravelAgencyRestService removeTaxiBooking() started. " + id);
        Response.ResponseBuilder builder = null;
        try {
            service.removeTaxiBooking(id);
            builder = Response.noContent();
            log.info("TravelAgencyRestService removeTaxiBooking() completed.");
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
     * Returns all {@link TravelAgencyBooking} stored in the database.
     * </p>
     * 
     * @return {@link Response} indicating all the {@link TravelAgencyBooking}
     */
    @GET
    public Response retrieveAllTravelAgencyBookings() {
        List<TravelAgencyBooking> travelAgencyBookings = service.findAll();
        return Response.ok(travelAgencyBookings).build();
    }

    /**
     * <p>
     * Creates a {@link TravelAgencyBooking}, while as well booking the service in all other external services. In case one of
     * the bookings in other services fails we try to rollback and delete any partial bookings that were made.
     * </p>
     * 
     * @param travelAgencyBooking {@link TravelAgencyBooking}
     * @return {@link Response} indicating whether or not it succeeded in creating.
     */
    @SuppressWarnings("unused")
    @POST
    public Response createTravelAgencyBooking(TravelAgencyBooking travelAgencyBooking) {
        log.info("TravelAgencyRestService createTravelAgencyBooking() started. " + travelAgencyBooking.toString());
        if (travelAgencyBooking == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        Response.ResponseBuilder builder = null;
        Map<String, String> responseObj = new HashMap<String, String>();

        try {
            travelAgencyBooking = service.createTravelAgencyBooking(travelAgencyBooking);
        } catch (Exception e) {
            log.info("Failed to create Bookings");
            responseObj.put("error", "failed to create bookings");
        }
        // If someone failed rollback
        if (travelAgencyBooking.getFlightBookingId() == null || travelAgencyBooking.getHotelBookingId() == null || travelAgencyBooking.getTaxiBookingId() == null) {
            // rollback flight booking
            if (travelAgencyBooking.getFlightBookingId() != null) {
                try {
                    log.info("Cancelling flight booking Started");
                    service.cancelFlightBooking(travelAgencyBooking);
                    log.info("Cancelling flight booking Completed");
                } catch (Exception e) {
                    log.info("Failed to clean flight booking");
                    responseObj.put("failedToCleanFlight", "failed to cancel flight booking");
                }
            }
            // rollback hotel booking
            if (travelAgencyBooking.getHotelBookingId() != null) {
                try {
                    log.info("Cancelling hotel booking Started");
                    service.cancelHotelBooking(travelAgencyBooking);
                    log.info("Cancelling hotel booking Completed");

                } catch (Exception e) {
                    log.info("Failed to clean hotel booking");
                    responseObj.put("failedToCleanHotel", "failed to cancel hotel booking");
                }
            }
            // rollback taxi booking
            if (travelAgencyBooking.getTaxiBookingId() != null) {
                try {
                    log.info("Cancelling taxi booking Started");
                    service.cancelTaxiBooking(travelAgencyBooking);
                    log.info("Cancelling taxi booking Completed");

                } catch (Exception e) {
                    log.info("Failed to clean taxi booking");
                    responseObj.put("failedToCleanTaxi", "failed to cancel taxi booking");
                }
            }
            log.info("TravelAgencyRestService createTravelAgencyBooking() failed to create. " + travelAgencyBooking.toString());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        } else {
            log.info("TravelAgencyRestService createTravelAgencyBooking() completed. " + travelAgencyBooking.toString());
            builder = Response.status(Response.Status.CREATED).entity(travelAgencyBooking);
        }
        return builder.build();
    }

    /**
     * <p>
     * Deletes a {@link TravelAgencyBooking} from the databases and as well cancels the bookings in other services. The bookings
     * in other services data is contained in {@link TravelAgencyBooking}.
     * </p>
     * 
     * @param id {@link Long} that is contained by one of {@link TravelAgencyBooking} objects.
     * @return {@link Response} indicating whether or not it succeeded deleting this booking.
     */
    @DELETE
    @Path("/{id:[0-9]+}")
    public Response deleteTravelAgencyBooking(@PathParam("id") Long id) {
        log.info("TravelAgencyRestService deleteTravelAgencyBooking() started. " + id);
        Response.ResponseBuilder builder = null;

        try {
            TravelAgencyBooking travelAgencyBooking = service.findById(id);
            if (travelAgencyBooking != null) {
                service.deleteTravelAgencyBooking(travelAgencyBooking);
            } else {
                log.info("TravelAgencyRestService deleteTravelAgencyBooking() - no travel agency booking was found with matching ID, so can't delete");
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            builder = Response.noContent();
            log.info("TravelAgencyRestService deleteTravelAgencyBooking() completed " + travelAgencyBooking.toString());
        } catch (Exception e) {
            log.info("Exception - " + e.toString());
            // Handle generic exceptions
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }
        return builder.build();
    }
}
