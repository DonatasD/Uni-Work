package org.jboss.quickstarts.wfk.flight;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;

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

/**
 * 
 * <p>
 * A suite of tests, run with {@link org.jboss.arquillian Arquillian} to test the JAX-RS endpoint for Flight creation
 * functionality (see {@link FlightRESTService#createFlight(Flight) createFlight(Flight)}).
 * </p>
 * 
 * @author Donatas Daubaras
 * @see FlightRESTService
 *
 */
@RunWith(Arquillian.class)
public class FlightRegistrationTest {
    @Inject
    FlightRESTService flightRESTService;

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
        Flight flight = createFlightInstance("ABCDE", "NCL", "VLN");
        Response response = flightRESTService.createFlight(flight);

        assertEquals("Unexpected response status", 201, response.getStatus());
        log.info("New flight was persisted and returned " + response.getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(2)
    public void testInvalidCreate() throws Exception {
        Flight flight = createFlightInstance("", "", "");
        Response response = flightRESTService.createFlight(flight);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotEquals("reponse.getEntity should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 3, ((Map<String, String>) response.getEntity()).size());
        log.info("Invalid flight creationg attempt failed with return code " + response.getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(3)
    public void testDuplicateFlightNumber() throws Exception {
        // Create an initial flight
        Flight flight = createFlightInstance("ABCDE", "NCL", "VLN");
        flightRESTService.createFlight(flight);

        // Create a different flight with the same flight Number
        Flight anotherFlight = createFlightInstance("ABCDE", "NCL", "VLN");
        Response response = flightRESTService.createFlight(anotherFlight);

        assertEquals("Unexpected response status", 409, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 1, ((Map<String, String>) response.getEntity()).size());
        log.info("Duplicate flight cannot be created attempt failed with return code " + response.getStatus());
    }

    private Flight createFlightInstance(String flightNumber, String departurePoint, String destinationPoint) {
        Flight flight = new Flight();
        flight.setFlightNumber(flightNumber);
        flight.setDeparturePoint(departurePoint);
        flight.setDestinationPoint(destinationPoint);
        return flight;
    }
}