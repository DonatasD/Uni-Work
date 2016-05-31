package org.jboss.quickstarts.wfk.travelagency;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
 * A suite of tests, run with {@link org.jboss.arquillian Arquillian} to test the JAX-RS endpoint for TravelAgency creation
 * functionality (see {@link TravelAgencyRESTService#createTravelAgencyBooking(TravelAgencyBooking)
 * createTravelAgency(TravelAgencyBooking)}).
 * </p>
 * 
 * @author Donatas Daubaras
 * @see TravelAgencyRestService
 *
 */
@RunWith(Arquillian.class)
public class TravelAgencyRegistrationTest {

    @Inject
    TravelAgencyRestService travelAgencyRestService;

    @Inject
    @Named("logger")
    Logger log;

    private final static DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * <p>
     * Compiles an Archive using Shrinkwrap, containing those external dependencies necessary to run the tests.
     * </p>
     *
     * @return Micro test war to be deployed and executed.
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
            .addClasses(TravelAgencyBooking.class,
                TravelAgencyRestService.class,
                TravelAgencyRepository.class,
                TravelAgencyService.class,
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
        TravelAgencyBooking travelAgencyBooking = createTravelAgencyInstance("d.daubaras@ncl.ac.uk", 10001, 1027, 100, "2020-12-10", "2020-12-10", "2020-12-10");
        Response response = travelAgencyRestService.createTravelAgencyBooking(travelAgencyBooking);

        if (response.getStatus() == 201) {
            Response responseFlight = travelAgencyRestService.removeFlightBooking(travelAgencyBooking.getFlightBookingId());
            Response responseHotel = travelAgencyRestService.removeHotelBooking(travelAgencyBooking.getHotelBookingId());
            Response responseTaxi = travelAgencyRestService.removeTaxiBooking(travelAgencyBooking.getTaxiBookingId());
            assertEquals("Unexpected response status for deleting flight", 204, responseFlight.getStatus());
            assertEquals("Unexpected response status for deleting hotel", 204, responseHotel.getStatus());
            assertEquals("Unexpected response status for deleting taxi", 204, responseTaxi.getStatus());
        } else {
            if (travelAgencyBooking.getHotelBookingId() != null) {
                Response responseHotel = travelAgencyRestService.removeHotelBooking(travelAgencyBooking.getHotelBookingId());
                assertEquals("Unexpected response status for deleting hotel", 400, responseHotel.getStatus());
            }
            if (travelAgencyBooking.getFlightBookingId() != null) {
                Response responseFlight = travelAgencyRestService.removeFlightBooking(travelAgencyBooking.getFlightBookingId());
                assertEquals("Unexpected response status for deleting flight", 400, responseFlight.getStatus());
            }
            if (travelAgencyBooking.getTaxiBookingId() != null) {
                Response responseTaxi = travelAgencyRestService.removeTaxiBooking(travelAgencyBooking.getTaxiBookingId());
                assertEquals("Unexpected response status for deleting taxi", 400, responseTaxi.getStatus());
            }
        }
        log.info("New travelAgencyBooking was persisted and returned " + response.getStatus());
    }

    private TravelAgencyBooking createTravelAgencyInstance(String customerEmail, long flightId, long hotelId, long taxiId, String flightBookingDate, String hotelBookingDate,
        String taxiBookingDate) throws Exception {
        TravelAgencyBooking travelAgencyBooking = new TravelAgencyBooking();
        travelAgencyBooking.setCustomerEmail(customerEmail);
        travelAgencyBooking.setFlightId(flightId);
        travelAgencyBooking.setHotelId(hotelId);
        travelAgencyBooking.setTaxiId(taxiId);
        travelAgencyBooking.setFlightBookingDate(df.parse(flightBookingDate));
        travelAgencyBooking.setHotelBookingDate(df.parse(hotelBookingDate));
        travelAgencyBooking.setTaxiBookingDate(df.parse(taxiBookingDate));
        return travelAgencyBooking;
    }
}
