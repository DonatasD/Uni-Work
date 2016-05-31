package org.jboss.quickstarts.wfk.travelagency;

import java.io.Serializable;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TravelAgencyService {

    @Inject
    private @Named("logger") Logger log;

    @Inject
    private @Named("httpClient") CloseableHttpClient httpClient;

    @Inject
    private TravelAgencyRepository crud;

    private final static String SCHEME = "http";
    private final static String[] HEADER = { "Content-type", "application/json" };

    private final static String FLIGHT_HOST = "jbosscontactsangularjs-110336260.rhcloud.com";
    private final static String FLIGHT_PATH = "/rest/flights/";
    private final static String FLIGHT_CUSTOMER_PATH = "/rest/customers/";
    private final static String FLIGHT_BOOKING_PATH = "/rest/bookings/";

    private final static String HOTEL_HOST = "travel.gsp8181.co.uk";
    private final static String HOTEL_PATH = "/rest/hotels/";
    private final static String HOTEL_CUSTOMER_PATH = "/rest/customers/";
    private final static String HOTEL_BOOKING_PATH = "/rest/bookings/";

    private final static String TAXI_HOST = "jbosscontactsangularjs-110060653.rhcloud.com";
    private final static String TAXI_PATH = "/rest/taxis/";
    private final static String TAXI_CUSTOMER_PATH = "/rest/customers/";
    private final static String TAXI_BOOKING_PATH = "/rest/bookings/";

    private final static DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * <p>
     * This service makes a GET request to a Taxi service and gets all the taxis available.
     * </p>
     * 
     * @return {@link String} object, which is {@link JSONArray#toString()}
     * @throws Exception
     */
    String retrieveAllTaxis() throws Exception {
        log.info("TravelAgencyService.retrieveAllTaxis() - Getting all taxis");
        URI uri = new URIBuilder()
            .setScheme(SCHEME)
            .setHost(TAXI_HOST)
            .setPath(TAXI_PATH)
            .build();

        log.info("Making request to " + uri.toString());
        HttpGet getReq = new HttpGet(uri);
        CloseableHttpResponse response = httpClient.execute(getReq);
        String responseBody = EntityUtils.toString(response.getEntity());
        JSONArray responseJson = new JSONArray(responseBody);
        HttpClientUtils.closeQuietly(response);
        log.info("Response in JSON: " + responseJson.toString());
        return responseJson.toString();
    }

    /**
     * <p>
     * This service makes a GET request to Taxi/Customer service and retrieves all the customers registered there.
     * </p>
     * 
     * @return {@link String} object, which is {@link JSONArray#toString()}
     * @throws Exception
     */
    String retrieveAllTaxiCustomers() throws Exception {
        log.info("TravelAgencyService.retrieveAllTaxiCustomers() - Getting all taxi customers");
        URI uri = new URIBuilder()
            .setScheme(SCHEME)
            .setHost(TAXI_HOST)
            .setPath(TAXI_CUSTOMER_PATH)
            .build();

        log.info("Making request to " + uri.toString());
        HttpGet getReq = new HttpGet(uri);
        CloseableHttpResponse response = httpClient.execute(getReq);
        String responseBody = EntityUtils.toString(response.getEntity());
        JSONArray responseJson = new JSONArray(responseBody);
        HttpClientUtils.closeQuietly(response);
        log.info("Response in JSON: " + responseJson.toString());
        return responseJson.toString();
    }

    /**
     * <p>
     * This service makes a GET request to Taxi/Booking service and retrieves all the bookings registered in there.
     * </p>
     * 
     * @return {@link String} object, which is {@link JSONArray#toString()}
     * @throws Exception
     */
    String retrieveAllTaxiBookings() throws Exception {
        log.info("TravelAgencyService.retrieveAllTaxiBookings() - Getting all bookings registered in taxi service");
        URI uri = new URIBuilder()
            .setScheme(SCHEME)
            .setHost(TAXI_HOST)
            .setPath(TAXI_BOOKING_PATH)
            .build();

        log.info("Making request to " + uri.toString());
        HttpGet getReq = new HttpGet(uri);
        CloseableHttpResponse response = httpClient.execute(getReq);
        String responseBody = EntityUtils.toString(response.getEntity());
        JSONArray responseJson = new JSONArray(responseBody);
        HttpClientUtils.closeQuietly(response);
        log.info("Response in JSON: " + responseJson.toString());
        return responseJson.toString();
    }

    /**
     * <p>
     * Finds the taxi customer by predefined email as {@link String}. Returns the customer in {@link String} format, which is
     * retrieve from {@link JSONObject}
     * </p>
     * 
     * @param email {@link String} indicating taxi customer email
     * @return {@link String} representing the customer that was found with corresponding <code>email</code>
     * @throws Exception
     */
    String retrieveTaxiCustomerByEmail(String email) throws Exception {
        log.info("TravelAgencyService.retrieveTaxiCustomerByEmail() - Getting taxi service customer by email");
        JSONArray dataJson = new JSONArray(retrieveAllTaxiCustomers());
        log.info("TravelAgencyService.retrieveTaxiCustomerByEmail() - Searching customer with email: " + email);
        for (int i = 0; i < dataJson.length(); i++) {
            if (dataJson.getJSONObject(i).getString("email").equals(email)) {
                return dataJson.getJSONObject(i).toString();
            }
        }
        throw new WebApplicationException(Response.Status.BAD_REQUEST);
    }

    /**
     * <p>
     * This service makes a GET request to a Flight service and gets all the Flights available.
     * </p>
     * 
     * <p>
     * The method returns {@link String} object, because {@link JSONArray} is not {@link Serializable}.
     * </p>
     * 
     * @return {@link String} object, which is {@link JSONArray#toString()}
     * @throws Exception
     */
    String retrieveAllFlights() throws Exception {
        log.info("TravelAgencyService.retrieveAllFlights() - Getting all flights");
        URI uri = new URIBuilder()
            .setScheme(SCHEME)
            .setHost(FLIGHT_HOST)
            .setPath(FLIGHT_PATH)
            .build();

        log.info("Making request to " + uri.toString());
        HttpGet getReq = new HttpGet(uri);
        CloseableHttpResponse response = httpClient.execute(getReq);
        String responseBody = EntityUtils.toString(response.getEntity());
        JSONArray responseJson = new JSONArray(responseBody);
        HttpClientUtils.closeQuietly(response);
        log.info("Response in JSON: " + responseJson.toString());
        return responseJson.toString();
    }

    /**
     * <p>
     * This service makes a GET request to a Flight/Customer service and retrieves all the customers registered there.
     * </p>
     * <p>
     * The method returns {@link String} object, because {@link JSONArray} is not {@link Serializable}.
     * </p>
     * 
     * @return {@link String} object, which is {@link JSONArray#toString()}
     * @throws Exception
     */
    String retrieveAllFlightCustomers() throws Exception {
        log.info("TravelAgencyService.retrieveAllFlightCustomer() - Getting all customer registered in flight service");
        URI uri = new URIBuilder()
            .setScheme(SCHEME)
            .setHost(FLIGHT_HOST)
            .setPath(FLIGHT_CUSTOMER_PATH)
            .build();

        log.info("Making request to " + uri.toString());
        HttpGet getReq = new HttpGet(uri);
        CloseableHttpResponse response = httpClient.execute(getReq);
        String responseBody = EntityUtils.toString(response.getEntity());
        JSONArray responseJson = new JSONArray(responseBody);
        HttpClientUtils.closeQuietly(response);
        log.info("Response in JSON: " + responseJson.toString());
        return responseJson.toString();
    }

    /**
     * <p>
     * This service makes a GET request to a Flight/Booking service and retrieves all the bookings registered in there.
     * </p>
     * <p>
     * The method returns {@link String} object, because {@link JSONArray} is not {@link Serializable}.
     * </p>
     * 
     * @return {@link String} object, which is {@link JSONArray#toString()}
     * @throws Exception
     */
    String retrieveAllFlightBookings() throws Exception {
        log.info("TravelAgencyService.retrieveAllFlightBookings() - Getting all bookings registered in flight service");
        URI uri = new URIBuilder()
            .setScheme(SCHEME)
            .setHost(FLIGHT_HOST)
            .setPath(FLIGHT_BOOKING_PATH)
            .build();

        log.info("Making request to " + uri.toString());
        HttpGet getReq = new HttpGet(uri);
        CloseableHttpResponse response = httpClient.execute(getReq);
        String responseBody = EntityUtils.toString(response.getEntity());
        JSONArray responseJson = new JSONArray(responseBody);
        HttpClientUtils.closeQuietly(response);
        log.info("Response in JSON: " + responseJson.toString());
        return responseJson.toString();
    }

    /**
     * <p>
     * Finds the flight customer by predefined email as {@link String}. Returns the customer in {@link String} format, which is
     * retrieved from {@link JSONObject}
     * </p>
     * 
     * @param email {@link String} indicating flight customer email.
     * @return {@link String} representing the customer that was found with corresponding <code>email</code>
     * @throws Exception
     */
    String retrieveFlightCustomerByEmail(String email) throws Exception {
        log.info("TravelAgencyService.retrieveFlightCustomerByEmail() - Getting flight service customer by email");
        URI uri = new URIBuilder()
            .setScheme(SCHEME)
            .setHost(FLIGHT_HOST)
            .setPath(FLIGHT_CUSTOMER_PATH + email)
            .build();

        log.info("Making request to " + uri.toString());
        HttpGet getReq = new HttpGet(uri);
        CloseableHttpResponse response = httpClient.execute(getReq);
        String responseBody = EntityUtils.toString(response.getEntity());
        JSONObject responseJson = new JSONObject(responseBody);
        HttpClientUtils.closeQuietly(response);
        log.info("Response in JSON: " + responseJson.toString());
        return responseJson.toString();
    }

    /**
     * <p>
     * This service makes a GET request to Hotel service and retrieves all available hotels.
     * </p>
     * 
     * <p>
     * The method returns {@link String} object, because {@link JSONArray} is not {@link Serializable}.
     * </p>
     * 
     * @return {@link String} object, which is {@link JSONArray#toString()}
     * @throws Exception
     */
    String retrieveAllHotels() throws Exception {
        log.info("TravelAgencyService.retrieveAllFlightBookings() - Getting all bookings registered in flight service");
        URI uri = new URIBuilder()
            .setScheme(SCHEME)
            .setHost(HOTEL_HOST)
            .setPath(HOTEL_PATH)
            .build();

        log.info("Making request to " + uri.toString());
        HttpGet getReq = new HttpGet(uri);
        CloseableHttpResponse response = httpClient.execute(getReq);
        String responseBody = EntityUtils.toString(response.getEntity());
        JSONArray responseJson = new JSONArray(responseBody);
        HttpClientUtils.closeQuietly(response);
        log.info("Response in JSON: " + responseJson.toString());
        return responseJson.toString();
    }

    /**
     * <p>
     * This service makes a GET request to a Hotel/Customer service and retrieves all the customers registered there.
     * </p>
     * <p>
     * The method returns {@link String} object, because {@link JSONArray} is not {@link Serializable}.
     * </p>
     * 
     * @return {@link String} object, which is {@link JSONArray#toString()}
     * @throws Exception
     */
    String retrieveAllHotelCustomers() throws Exception {
        log.info("TravelAgencyService.retrieveAllHotelCustomer() - Getting all customer registered in hotel service");
        URI uri = new URIBuilder()
            .setScheme(SCHEME)
            .setHost(HOTEL_HOST)
            .setPath(HOTEL_CUSTOMER_PATH)
            .build();

        log.info("Making request to " + uri.toString());
        HttpGet getReq = new HttpGet(uri);
        CloseableHttpResponse response = httpClient.execute(getReq);
        String responseBody = EntityUtils.toString(response.getEntity());
        JSONArray responseJson = new JSONArray(responseBody);
        HttpClientUtils.closeQuietly(response);
        log.info("Response in JSON: " + responseJson.toString());
        return responseJson.toString();
    }

    /**
     * <p>
     * This service makes a GET request to a Hotel/Booking service and retrieves all the bookings registered in there.
     * </p>
     * <p>
     * The method returns {@link String} object, because {@link JSONArray} is not {@link Serializable}.
     * </p>
     * 
     * @return {@link String} object, which is {@link JSONArray#toString()}
     * @throws Exception
     */
    String retrieveAllHotelBookings() throws Exception {
        log.info("TravelAgencyService.retrieveAllHotelBookings() - Getting all bookings registered in hotel service");
        URI uri = new URIBuilder()
            .setScheme(SCHEME)
            .setHost(HOTEL_HOST)
            .setPath(HOTEL_BOOKING_PATH)
            .build();

        log.info("Making request to " + uri.toString());
        HttpGet getReq = new HttpGet(uri);
        CloseableHttpResponse response = httpClient.execute(getReq);
        String responseBody = EntityUtils.toString(response.getEntity());
        JSONArray responseJson = new JSONArray(responseBody);
        HttpClientUtils.closeQuietly(response);
        log.info("Response in JSON: " + responseJson.toString());
        return responseJson.toString();
    }

    /**
     * <p>
     * Finds the taxi customer by predefined email as {@link String}. Returns the customer in {@link String} format, which is
     * retrieved from {@link JSONObject}
     * </p>
     * 
     * @param email {@link String} indicating hotel customer email.
     * @return {@link String} representing the customer that was found with corresponding <code>email</code>
     * @throws Exception
     */
    String retrieveHotelCustomerByEmail(String email) throws Exception {
        log.info("TravelAgencyService.retrieveHotelCustomerByEmail() - Getting hotel service customer by email");
        JSONArray dataJson = new JSONArray(retrieveAllHotelCustomers());
        log.info("TravelAgencyService.retrieveHotelCustomerByEmail() - Searching customer with email: " + email);
        for (int i = 0; i < dataJson.length(); i++) {
            if (dataJson.getJSONObject(i).getString("email").equals(email)) {
                return dataJson.getJSONObject(i).toString();
            }
        }
        throw new WebApplicationException(Response.Status.BAD_REQUEST);
    }

    /**
     * <p>
     * This service allows to create booking object using external flight service.
     * </p>
     * 
     * <p>
     * This service takes parameter as {@link JSONObject}, which should provide needed data to create a booking.
     * <ul>
     * <li><b>"customerId" : {@link Long}</b></li>
     * <li><b>"flightId" : {@link Long}</b></li>
     * <li><b>"bookingDate" : {@link Date}</b></li>
     * </ul>
     * This {@link JSONObject} is then passed to external service to deal with it. The response is retrieved as
     * {@link JSONObject} and converted to {@link String} and returned.
     * </p>
     * 
     * @param jsonFlightBooking {@link JSONObject} object that provides details to create a new flight booking.
     * @return {@link String} object which is retrieved from {@link JSONObject#toString()}
     * @throws JSONException
     * @throws Exception
     */
    String createFlightBooking(JSONObject jsonFlightBooking) throws JSONException, Exception {
        log.info("TravelAgencyService.createFlightBooking() - Creating a booking in flight service");
        URI uri = new URIBuilder()
            .setScheme(SCHEME)
            .setHost(FLIGHT_HOST)
            .setPath(FLIGHT_BOOKING_PATH)
            .build();
        // Formatting POST request
        log.info("Making request to " + uri.toString());
        HttpPost postReq = new HttpPost(uri);
        StringEntity jsonParams = new StringEntity(jsonFlightBooking.toString());
        postReq.addHeader(HEADER[0], HEADER[1]);
        postReq.setEntity(jsonParams);
        log.info("Sending POST REQ with JSON: " + jsonFlightBooking.toString());

        CloseableHttpResponse response = httpClient.execute(postReq);
        String responseBody = EntityUtils.toString(response.getEntity());
        JSONObject responseJson = new JSONObject(responseBody);
        HttpClientUtils.closeQuietly(response);
        log.info("Response in JSON: " + responseJson.toString());
        return responseJson.toString();
    }

    /**
     * <p>
     * This service allows to create booking object using external taxi service.
     * </p>
     * 
     * <p>
     * This service takes parameter as {@link JSONObject}, which should provide needed data to create a booking.
     * <ul>
     * <li><b>"customerId" : {@link Long}</b></li>
     * <li><b>"taxiId" : {@link Long}</b></li>
     * <li><b>"bookingDate" : {@link Date}</b></li>
     * </ul>
     * This {@link JSONObject} is then passed to external service to deal with it. The response is retrieved as
     * {@link JSONObject} and converted to {@link String} and returned.
     * </p>
     * 
     * @param jsonTaxiBooking
     * @return
     * @throws Exception
     */
    String createTaxiBooking(JSONObject jsonTaxiBooking) throws Exception {
        log.info("TravelAgencyService.createTaxiBooking() - Creating a booking in taxi service");
        URI uri = new URIBuilder()
            .setScheme(SCHEME)
            .setHost(TAXI_HOST)
            .setPath(TAXI_BOOKING_PATH)
            .build();
        // Formatting POST request
        log.info("Making request to " + uri.toString());
        HttpPost postReq = new HttpPost(uri);
        StringEntity jsonParams = new StringEntity(jsonTaxiBooking.toString());
        postReq.addHeader(HEADER[0], HEADER[1]);
        postReq.setEntity(jsonParams);
        log.info("Sending POST REQ with JSON: " + jsonTaxiBooking.toString());

        CloseableHttpResponse response = httpClient.execute(postReq);
        String responseBody = EntityUtils.toString(response.getEntity());
        JSONObject responseJson = new JSONObject(responseBody);
        HttpClientUtils.closeQuietly(response);
        log.info("Response in JSON: " + responseJson.toString());
        return responseJson.toString();
    }

    /**
     * <p>
     * Creates a hotel booking in hotel service
     * </p>
     * 
     * <p>
     * This service takes parameter as {@link JSONObject}, which should provide needed data to create a booking.
     * <ul>
     * <li><b>"customerId" : {@link Long}</b></li>
     * <li><b>"hotelId" : {@link Long}</b></li>
     * <li><b>"bookingDate" : {@link Date}</b></li>
     * </ul>
     * This {@link JSONObject} is then passed to external service to deal with it. The response is retrieved as
     * {@link JSONObject} and converted to {@link String} and returned.
     * </p>
     * 
     * @param jsonHotelBooking {@link JSONObject} identifying hotel booking parameters
     * @return {@link String} indicating response transformed from {@link JSONObject}
     * @throws JSONException
     * @throws Exception
     */
    String createHotelBooking(JSONObject jsonHotelBooking) throws JSONException, Exception {
        log.info("TravelAgencyService.createHotelBooking() - Creating a booking in hotel service");
        URI uri = new URIBuilder()
            .setScheme(SCHEME)
            .setHost(HOTEL_HOST)
            .setPath(HOTEL_BOOKING_PATH)
            .build();
        // Format POST request
        log.info("Making request to " + uri.toString());
        HttpPost postReq = new HttpPost(uri);
        Long hotelId = jsonHotelBooking.getLong("hotelId");
        Long customerId = jsonHotelBooking.getLong("customerId");
        String date = jsonHotelBooking.getString("bookingDate");

        jsonHotelBooking = new JSONObject();

        JSONObject customerJson = new JSONObject();
        customerJson.put("id", customerId);

        JSONObject hotelJson = new JSONObject();
        hotelJson.put("id", hotelId);

        jsonHotelBooking.put("customer", customerJson);
        jsonHotelBooking.put("hotel", hotelJson);
        jsonHotelBooking.put("bookingDate", date);

        StringEntity jsonParams = new StringEntity(jsonHotelBooking.toString());
        postReq.addHeader(HEADER[0], HEADER[1]);
        postReq.setEntity(jsonParams);
        log.info("Sending POST REQ with JSON: " + jsonHotelBooking.toString());

        CloseableHttpResponse response = httpClient.execute(postReq);
        String responseBody = EntityUtils.toString(response.getEntity());
        JSONObject responseJson = new JSONObject(responseBody);
        HttpClientUtils.closeQuietly(response);
        log.info("Response in JSON: " + responseJson.toString());
        return responseJson.toString();
    }

    /**
     * <p>
     * Allows to remove flight booking from flight service
     * </p>
     * 
     * @param id {@link Long} identifying the flight booking that is going to be removed
     * @return {@link String} indicating response
     * @throws Exception
     */
    String removeFlightBooking(Long id) throws Exception {
        log.info("TravelAgencyService.removeFlightBooking() - Deleting a flight booking");
        URI uri = new URIBuilder()
            .setScheme(SCHEME)
            .setHost(FLIGHT_HOST)
            .setPath(FLIGHT_BOOKING_PATH + id)
            .build();
        log.info("Making DELETE request to " + uri.toString());
        HttpDelete deleteReq = new HttpDelete(uri);

        CloseableHttpResponse response = httpClient.execute(deleteReq);
        HttpClientUtils.closeQuietly(response);
        return response.toString();
    }

    /**
     * <p>
     * Allows to remove hotel bookings from hotel service
     * </p>
     * 
     * @param id {@link Long} identifying the hotel booking that is going to be removed
     * @return {@link String} indicating response
     * @throws Exception
     */
    String removeHotelBooking(Long id) throws Exception {
        log.info("TravelAgencyService.removeHotelBooking() - Deleting a hotel booking");
        URI uri = new URIBuilder()
            .setScheme(SCHEME)
            .setHost(HOTEL_HOST)
            .setPath(HOTEL_BOOKING_PATH + id)
            .build();
        log.info("Making DELETE request to " + uri.toString());
        HttpDelete deleteReq = new HttpDelete(uri);

        CloseableHttpResponse response = httpClient.execute(deleteReq);
        HttpClientUtils.closeQuietly(response);
        return response.toString();
    }

    /**
     * <p>
     * Allows to remove taxi bookings from taxi service
     * </p>
     * 
     * @param id {@link Long} indicating response
     * @return {@link String} indicating response
     * @throws Exception
     */
    String removeTaxiBooking(Long id) throws Exception {
        log.info("TravelAgencyService.removeTaxiBooking() - Deleting a taxi booking");
        URI uri = new URIBuilder()
            .setScheme(SCHEME)
            .setHost(TAXI_HOST)
            .setPath(TAXI_BOOKING_PATH + id)
            .build();
        log.info("Making DELETE request to " + uri.toString());
        HttpDelete deleteReq = new HttpDelete(uri);

        CloseableHttpResponse response = httpClient.execute(deleteReq);
        HttpClientUtils.closeQuietly(response);
        return response.toString();
    }

    /**
     * <p>
     * Returns a {@link TravelAgencyBooking} with <code>id</code>
     * </p>
     * 
     * @param id {@link Long} identifying which {@link TravelAgencyBooking} instance will be returned
     * @return {@link TravelAgencyBooking} with corresponding <code>id</code>
     * @throws Exception
     */
    TravelAgencyBooking findById(Long id) throws Exception {
        return crud.findById(id);
    }

    /**
     * <p>
     * Returns all created {@link TravelAgencyBooking}
     * </p>
     * 
     * @return {@link List} of {@link TravelAgencyBooking}
     */
    List<TravelAgencyBooking> findAll() {
        return crud.findAll();
    }

    /**
     * <p>
     * Creates a {@link TravelAgencyBooking}, which indicates what resources are booked in other services
     * </p>
     * 
     * @param travelAgencyBooking {@link TravelAgencyBooking} that is trying to be created
     * @return {@link TravelAgencyBooking} that was creating
     * @throws Exception
     */
    TravelAgencyBooking createTravelAgencyBooking(TravelAgencyBooking travelAgencyBooking) throws Exception {
        JSONObject flightCustomer = new JSONObject(retrieveFlightCustomerByEmail(travelAgencyBooking.getCustomerEmail()));
        log.info("Flight Customer " + flightCustomer.toString());
        Long flightCustomerId = flightCustomer.getLong("id");

        JSONObject hotelCustomer = new JSONObject(retrieveHotelCustomerByEmail(travelAgencyBooking.getCustomerEmail()));
        log.info("Hotel Customer " + hotelCustomer.toString());
        Long hotelCustomerId = hotelCustomer.getLong("id");

        JSONObject taxiCustomer = new JSONObject(retrieveTaxiCustomerByEmail(travelAgencyBooking.getCustomerEmail()));
        log.info("Taxi Customer " + taxiCustomer.toString());
        Long taxiCustomerId = taxiCustomer.getLong("id");

        // Create JSONObject to make createFlightBooking
        JSONObject flightBookingJson = new JSONObject();
        flightBookingJson.put("customerId", flightCustomerId);
        flightBookingJson.put("flightId", travelAgencyBooking.getFlightId());
        flightBookingJson.put("bookingDate", df.format(travelAgencyBooking.getFlightBookingDate()));

        // Create JSONObject to make createHotelBooking
        JSONObject hotelBookingJson = new JSONObject();
        hotelBookingJson.put("customerId", hotelCustomerId);
        hotelBookingJson.put("hotelId", travelAgencyBooking.getHotelId());
        hotelBookingJson.put("bookingDate", df.format(travelAgencyBooking.getHotelBookingDate()));

        JSONObject taxiBookingJson = new JSONObject();
        taxiBookingJson.put("customerId", taxiCustomerId);
        taxiBookingJson.put("taxiId", travelAgencyBooking.getTaxiId());
        taxiBookingJson.put("bookingDate", df.format(travelAgencyBooking.getTaxiBookingDate()));

        // Get responses and convert to JSONObject
        log.info("FlightJSON: " + flightBookingJson.toString());
        JSONObject flightResponse = new JSONObject(createFlightBooking(flightBookingJson));
        travelAgencyBooking.setFlightBookingId(flightResponse.getLong("id"));
        log.info("FlightBooking created");

        log.info("TaxiJSON: " + taxiBookingJson.toString());
        JSONObject taxiResponse = new JSONObject(createTaxiBooking(taxiBookingJson));
        travelAgencyBooking.setTaxiBookingId(taxiResponse.getLong("id"));
        log.info("TaxiBooking created");
        
        log.info("HotelJSON: " + hotelBookingJson.toString());
        JSONObject hotelResponse = new JSONObject(createHotelBooking(hotelBookingJson));
        travelAgencyBooking.setHotelBookingId(hotelResponse.getLong("id"));
        log.info("HotelBooking created");

        return crud.create(travelAgencyBooking);
    }

    /**
     * <p>
     * Cancels hotel booking that was made. If booking is not found throws {@link WebApplicationException} indicating
     * {@link Response.Status#NOT_FOUND}.
     * </p>
     * 
     * @param travelAgencyBooking {@link TravelAgencyBooking} indicating, which booking needs to be cancelled
     * @return {@link String} identifying the outcome of this method.
     * @throws Exception
     */
    String cancelHotelBooking(TravelAgencyBooking travelAgencyBooking) throws Exception {
        log.info("cancelUpHotelBooking " + travelAgencyBooking.toString());
        JSONArray hotelBookingJson = new JSONArray(retrieveAllHotelBookings());
        for (int i = 0; i < hotelBookingJson.length(); i++) {
            if (hotelBookingJson.getJSONObject(i).getString("bookingDate").equals(df.format(travelAgencyBooking.getHotelBookingDate())) &&
                new Long(hotelBookingJson.getJSONObject(i).getJSONObject("hotel").getLong("id")).equals(travelAgencyBooking.getHotelId())) {
                return removeHotelBooking(hotelBookingJson.getJSONObject(i).getLong("id"));
            }
        }
        throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    /**
     * <p>
     * Cancel flight booking that was made. If booking is not found {@link WebApplicationException} indicating
     * {@link Response.Status#NOT_FOUND}.
     * </p>
     * 
     * @param travelAgencyBooking {@link TravelAgencyBooking} indicating, which booking needs to be cancelled
     * @return {@link String} identifying the outcome of this method
     * @throws Exception
     */
    String cancelFlightBooking(TravelAgencyBooking travelAgencyBooking) throws Exception {
        log.info("cancelUpFlightBooking " + travelAgencyBooking.toString());
        JSONArray flightBookingJson = new JSONArray(retrieveAllFlightBookings());
        for (int i = 0; i < flightBookingJson.length(); i++) {
            if (flightBookingJson.getJSONObject(i).getString("bookingDate").equals(df.format(travelAgencyBooking.getFlightBookingDate())) &&
                new Long(flightBookingJson.getJSONObject(i).getLong("flightId")).equals(travelAgencyBooking.getFlightId())) {
                return removeFlightBooking(flightBookingJson.getJSONObject(i).getLong("id"));
            }
        }
        throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    /**
     * <p>
     * Cancel taxi bookings that was made. If booking is not found {@link WebApplicationException} indicating
     * {@link Response.Status#NOT_FOUND}
     * </p>
     * 
     * @param travelAgencyBooking {@link TravelAgencyBooking} indicating, which booking needs to be cancelled
     * @return {@link String} identifying the outcome of this method
     * @throws Exception
     */
    String cancelTaxiBooking(TravelAgencyBooking travelAgencyBooking) throws Exception {
        log.info("cancelUpTaxiBooking " + travelAgencyBooking.toString());
        JSONArray taxiBookingJson = new JSONArray(retrieveAllTaxiBookings());
        for (int i = 0; i < taxiBookingJson.length(); i++) {
            if (taxiBookingJson.getJSONObject(i).getString("bookingDate").equals(df.format(travelAgencyBooking.getTaxiBookingDate())) &&
                new Long(taxiBookingJson.getJSONObject(i).getLong("taxiId")).equals(travelAgencyBooking.getTaxiId())) {
                return removeTaxiBooking(taxiBookingJson.getJSONObject(i).getLong("id"));
            }
        }
        throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    TravelAgencyBooking deleteTravelAgencyBooking(TravelAgencyBooking travelAgencyBooking) throws Exception {
        log.info("TravelAgencyService.delete() - Deleting " + travelAgencyBooking.toString());
        
        TravelAgencyBooking deletedTravelAgencyBooking = null;

        if (travelAgencyBooking.getId() != null) {
            deletedTravelAgencyBooking = crud.delete(travelAgencyBooking);
            cancelFlightBooking(travelAgencyBooking);
        cancelHotelBooking(travelAgencyBooking);
        cancelTaxiBooking(travelAgencyBooking);
        } else {
            log.info("ContactService.delete() - No ID was found so can't Delete.");
        }

        return deletedTravelAgencyBooking;
    }

}
