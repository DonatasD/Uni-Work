package org.jboss.quickstarts.wfk.booking;

import org.jboss.quickstarts.wfk.customer.Customer;
import org.jboss.quickstarts.wfk.flight.Flight;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * This class is a base class to define a booking bean.
 * </p>
 * <p/>
 * <p>
 * This bean stores the id of {@link Flight} and {@link Customer} objects. This bean is meant to store data about booked flights
 * by defined user.
 * </p>
 *
 * @author Donatas Daubaras
 * @see Flight
 * @see Customer
 */

@Entity
@NamedQueries({
    @NamedQuery(name = Booking.FIND_ALL, query = "SELECT b FROM Booking b ORDER BY b.customerId ASC"),
    @NamedQuery(name = Booking.FIND_BY_CUSTOMER, query = "SELECT b FROM Booking b WHERE b.customerId = :customerId"),
    @NamedQuery(name = Booking.FIND_BY_DATE_AND_FLIGHT, query = "SELECT b FROM Booking b WHERE b.bookingDate = :bookingDate AND b.flightId = :flightId")
})
@XmlRootElement
@Table(name = "Booking", uniqueConstraints = @UniqueConstraint(columnNames = { "bookingDate", "flightId" }))
public class Booking implements Serializable {

    public static final String FIND_ALL = "Booking.findAll";
    public static final String FIND_BY_CUSTOMER = "Booking.findByCustomer";
    public static final String FIND_BY_DATE_AND_FLIGHT = "Booking.findByDateAndFlight";
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @NotNull
    @Column(name = "customerId")
    private Long customerId;

    @NotNull
    @Column(name = "flightId")
    private Long flightId;

    @ManyToOne
    @JoinColumn(name = "customerId", updatable = false, insertable = false, referencedColumnName = "id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "flightId", updatable = false, insertable = false, referencedColumnName = "id")
    private Flight flight;

    @NotNull
    @Future(message = "Booking date must be in the future")
    @Column(name = "bookingDate")
    @Temporal(TemporalType.DATE)
    private Date bookingDate;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Long getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    @Override
    public String toString() {
        return "Booking [id=" + id + ", customer=" + customer + ", flight=" + flight + ", bookingDate=" + bookingDate + "]";
    }

}
