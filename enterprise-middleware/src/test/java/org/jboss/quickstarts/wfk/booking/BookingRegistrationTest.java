package org.jboss.quickstarts.wfk.booking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.quickstarts.wfk.flight.Flight;
import org.jboss.quickstarts.wfk.flight.FlightRESTService;
import org.jboss.quickstarts.wfk.flight.FlightRepository;
import org.jboss.quickstarts.wfk.flight.FlightService;
import org.jboss.quickstarts.wfk.flight.FlightValidationException;
import org.jboss.quickstarts.wfk.flight.FlightValidationExceptionEnum;
import org.jboss.quickstarts.wfk.flight.FlightValidator;
import org.jboss.quickstarts.wfk.util.Resources;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * <p>
 * A suite of tests, run with {@link org.jboss.arquillian Arquillian} to test the JAX-RS endpoint for Booking creation
 * functionality (see {@link BookingRESTService#createBooking(Booking) createBooking(Booking)}).
 * </p>
 * 
 * @author Donatas Daubaras
 * @see BookingRESTService
 *
 */
@RunWith(Arquillian.class)
public class BookingRegistrationTest {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    BookingRESTService bookingRESTService;

    private final static DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

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
            .addClasses(Flight.class,
                FlightRESTService.class,
                FlightRepository.class,
                FlightValidator.class,
                FlightService.class,
                FlightValidationException.class,
                FlightValidationExceptionEnum.class,
                Resources.class)
            .addAsLibraries(libs)
            .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
            .addAsWebInfResource("arquillian-ds.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

        return archive;
    }

    @Test
    @InSequence(1)
    public void testCreate() throws Exception {
        Booking booking = createBookingInstance(new Long(10000), new Long(10000), "2014-12-10");
        Response response = bookingRESTService.createBooking(booking);

        assertEquals("Unexpected response status", 201, response.getStatus());
        log.info("New booking was persisted and returned " + response.getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(2)
    public void testInvalidCreate() throws Exception {
        Booking booking = createBookingInstance(new Long(0), new Long(0), "2013-12-10");
        Response response = bookingRESTService.createBooking(booking);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotEquals("reponse.getEntity should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 3, ((Map<String, String>) response.getEntity()).size());
        log.info("Invalid booking creationg attempt failed with return code " + response.getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(3)
    public void testInvalidCustomerId() throws Exception {
        Booking booking = createBookingInstance(new Long(0), new Long(10000), "2014-12-10");
        Response response = bookingRESTService.createBooking(booking);
        
        assertEquals("Unexpected response status", 409, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 1, ((Map<String, String>) response.getEntity()).size());
        log.info("Duplicate booking cannot be created attempt failed with return code " + response.getStatus());   
    }
    
    @SuppressWarnings("unchecked")
    @Test
    @InSequence(4)
    public void testInvalidFlightId() throws Exception {
        Booking booking = createBookingInstance(new Long(10000), new Long(0), "2014-12-10");
        Response response = bookingRESTService.createBooking(booking);
        
        assertEquals("Unexpected response status", 409, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 1, ((Map<String, String>) response.getEntity()).size());
        log.info("Duplicate booking cannot be created attempt failed with return code " + response.getStatus());   
    }
    
    @SuppressWarnings("unchecked")
    @Test
    @InSequence(5)
    public void duplicateFlightIdAndDate() throws Exception {
        // Create an initial Booking
        Booking booking = createBookingInstance(new Long(10000), new Long(10000), "2014-12-10");
        bookingRESTService.createBooking(booking);
        
        Booking anotherBooking = createBookingInstance(new Long(10000), new Long(10000), "2014-12-10");
        Response response = bookingRESTService.createBooking(anotherBooking);
        
        assertEquals("Unexpected response status", 409, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 1, ((Map<String, String>) response.getEntity()).size());
        log.info("Duplicate booking cannot be created attempt failed with return code " + response.getStatus());
    }

    private Booking createBookingInstance(Long customerId, Long flightId, String bookingDate) throws Exception {
        Booking booking = new Booking();
        booking.setBookingDate(df.parse(bookingDate));
        booking.setCustomerId(customerId);
        booking.setFlightId(flightId);
        return booking;
    }
}
