package org.jboss.quickstarts.wfk.customer;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.quickstarts.wfk.util.Resources;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.Map;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * <p>
 * A suite of tests, run with {@link org.jboss.arquillian Arquillian} to test the JAX-RS endpoint for Customer creation
 * functionality (see {@link CustomerRESTService#createCustomer(Customer) createCustomer(Customer)}).
 * </p>
 *
 * @author Donatas Daubaras
 * @see CustomerRESTService
 */
@RunWith(Arquillian.class)
public class CustomerRegistrationTest {
    @Inject
    CustomerRESTService customerRESTService;
    @Inject
    @Named("logger")
    Logger log;

    /**
     * <p>
     * Compiles an Archive using Shrinkwrap, containing those external dependencies necessary to run the tests.
     * </p>
     *
     * @return Micro test war to be deployd and executed.
     */
    @Deployment
    public static Archive<?> createTestArchive() {
        // HttpComponents and org.JSON are required by CustomerService
        File[] libs = Maven.resolver().loadPomFromFile("pom.xml").resolve(
            "org.apache.httpcomponents:httpclient:4.3.2",
            "org.json:json:20140107"
            ).withTransitivity().asFile();

        Archive<?> archive = ShrinkWrap
            .create(WebArchive.class, "test.war")
            .addClasses(Customer.class,
                CustomerRESTService.class,
                CustomerRepository.class,
                CustomerValidator.class,
                CustomerService.class,
                Resources.class)
            .addAsLibraries(libs)
            .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
            .addAsWebInfResource("arquillian-ds.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

        return archive;
    }

    @Test
    @InSequence(1)
    public void testRegister() throws Exception {
        Customer customer = createCustomerInstance("Donatas", "d.daubaras@ncl.ac.uk", "07591440518");
        Response response = customerRESTService.createCustomer(customer);

        assertEquals("Unexpected response status", 201, response.getStatus());
        log.info("New customer was persisted and returned status " + response.getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(2)
    public void testInvalidRegister() throws Exception {
        Customer customer = createCustomerInstance("", "", "");
        Response response = customerRESTService.createCustomer(customer);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotEquals("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 3,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Invalid customer register attempt failed with return code " + response.getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(3)
    public void testDuplicateEmail() throws Exception {
        // Register an initial user
        Customer customer = createCustomerInstance("Donatas", "d.daubaras@ncl.ac.uk", "07591440518");
        customerRESTService.createCustomer(customer);

        // Register a different user with the same email
        Customer anotherCustomer = createCustomerInstance("Donatas", "d.daubaras@ncl.ac.uk", "07591440518");
        Response response = customerRESTService.createCustomer(anotherCustomer);

        assertEquals("Unexpected response status", 409, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Duplicate customer cannot register attempt failed with return code " + response.getStatus());
    }

    /**
     * <p>
     * A utility method to construct a {@link org.jboss.quickstarts.wfk.customer.Customer Customer} object for use in testing.
     * This object is not persisted.
     * </p>
     *
     * @param name The name of the Customer being created
     * @param email The email address of the Customer being created
     * @param phoneNumber The phone number of Customer being created
     * @return The Customer object created
     */

    private Customer createCustomerInstance(String name, String email, String phoneNumber) {
        Customer customer = new Customer();
        customer.setName(name);
        customer.setEmail(email);
        customer.setPhoneNumber(phoneNumber);
        return customer;
    }
}
