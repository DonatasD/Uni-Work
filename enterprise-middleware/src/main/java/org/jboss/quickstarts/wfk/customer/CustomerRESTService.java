package org.jboss.quickstarts.wfk.customer;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
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
 * This class exposes the functionality of {@link CustomerService} over HTTP endpoints as a RESTful resource via JAX-RS.
 * </p>
 * <p/>
 * <p>
 * Full path for accessing the Customer resource is rest/customer .
 * </p>
 * <p/>
 * <p>
 * The resource accepts and produces JSON.
 * </p>
 *
 * @author Donatas Daubaras
 * @see CustomerService
 * @see javax.ws.rs.core.Response
 */

/*
 * The Path annotation defines this as a REST Web Service using JAX-RS.
 * 
 * By placing the Consumes and Produces annotations at the class level the methods all default to JSON. However, they can be
 * overridden by adding the Consumes or Produces annotations to the individual method.
 * 
 * It is Stateless to "inform the container that this RESTful web service should also be treated as an EJB and allow transaction
 * demarcation when accessing the database." - Antonio Goncalves
 */

@Path("/customers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class CustomerRESTService {

    @Inject
    private
    @Named("logger")
    Logger log;

    @Inject
    private CustomerService service;

    /**
     * <p>
     * Search for and return all the Customers. they are sorted by name.
     * </p>
     *
     * @return A Response containing a list of Customers
     */
    @GET
    public Response retrieveAllCustomers() {
        List<Customer> customers = service.findAllOrderedByName();
        return Response.ok(customers).build();
    }

    /**
     * <p>
     * Search for and return a Customer identified by email address.
     * </p>
     * <p/>
     * <p>
     * Path annotation includes very simple regex to differentiate between email addresses and Ids. <strong>DO NOT</strong>
     * attempt to use this regex to validate email addresses.
     * </p>
     *
     * @param email The string parameter value provided as a Customer's email
     * @return A Response containing a single Customer
     */

    @GET
    @Path("/{email:^.+@.+$}")
    public Response retrieveCustomerByEmail(@PathParam("email") String email) {
        Customer customer;
        try {
            customer = service.findByEmail(email);
        } catch (NoResultException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        log.info("Customer findByemail" + email + ": found " + customer.toString());
        return Response.ok(customer).build();
    }

    /**
     * <p>
     * Search for and return a Customer identified by id.
     * </p>
     *
     * @param id The long parameter value provided as a Customer's id
     * @return A Response containing a single Customer
     */

    @GET
    @Path("/{id:[0-9]+}")
    public Response retrieveCustomerById(@PathParam("id") long id) {
        Customer customer = service.findById(id);
        if (customer == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        log.info("Customer findById " + id + ": found " + customer.toString());
        return Response.ok(customer).build();
    }

    /**
     * <p>
     * Creates a new customer from the values provided. Performs validation and will return a JAX-RS response with either 200
     * (ok) or with a map of fields, and related errors.
     * </p>
     *
     * @param customer The Customer object, constructed automatically from JSON input, to be <i>created</i> via
     *                 {@link CustomerService#create(Customer)}
     * @return A Response indicating the outcome of the create operation
     */

    @SuppressWarnings("unused")
    @POST
    public Response createCustomer(Customer customer) {
        log.info("createCustomer started. " + customer.toString());
        if (customer == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        Response.ResponseBuilder builder = null;

        try {
            // Go add the new Customer.
            service.create(customer);

            // Create a "Resource Created" 201 Response and pass the customer back in case it is needed.
            builder = Response.status(Response.Status.CREATED).entity(customer);

            log.info("createCustomer completed. " + customer.toString());
        } catch (ConstraintViolationException ce) {
            log.info("ConstraintViolationException - " + ce.toString());
            // Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            log.info("ValidationException - " + e.toString());
            // Handle the unique constrain violation
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("email", "That email is already used, please use a unique email");
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
     * Updates a customer with the id provided in the {@link Customer}. Performs validation, and will return a JAX-RS response
     * with either 200 (ok), or with a map of fields, and related errors.
     * </p>
     *
     * @param id       The long parameter value provided as the id of the {@link Customer} to be updated
     * @param customer {@link Customer} object, constructed automatically from JSON input, to be <i>updated</i> via
     *                 {@link CustomerService#update(Customer)}
     * @return {@link Response} indicating the outcome of the update operation
     */

    @PUT
    @Path("/{id:[0-9]+}")
    public Response updateCustomer(@PathParam("id") long id, Customer customer) {
        if (customer == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        log.info("updateCustomer started. " + customer.toString());
        if (customer.getId() != id) {
            // The client attempted to update the read-only Id. This is not permitted.
            Response response = Response.status(Response.Status.CONFLICT).entity("The customer id cannot be modified").build();
            throw new WebApplicationException(response);
        }
        if (service.findById(customer.getId()) == null) {
            // Verify that customer exists. Return 404, if not present.
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        Response.ResponseBuilder builder = null;

        try {
            // Apply changes to Customer.
            service.update(customer);

            // Create an OK Response and pass the customer back in case it is needed.
            builder = Response.ok(customer);

            log.info("updateCustomer completed. " + customer.toString());
        } catch (ConstraintViolationException ce) {
            log.info("ConstraintViolationException - " + ce.toString());
            // Handle bean validation
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            log.info("ValidationException - " + e.toString());
            // Handle the unique constrain violation
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("email", "That email is already used, please use a unique email");
            responseObj.put("error", "This is where errors are displayed that are not related to a specific field");
            responseObj.put("anotherError", "You can find this error message in /src/main/java/org/jboss/quickstarts/wfk/rest/CustomerRESTService.java");
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
