package org.jboss.quickstarts.wfk.travelagency;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@NamedQueries({
    @NamedQuery(name = TravelAgencyBooking.FIND_ALL, query = "SELECT b FROM TravelAgencyBooking b ORDER BY b.id ASC"),
})
@XmlRootElement
@Table(name = "travelAgencyBooking")
public class TravelAgencyBooking implements Serializable {

    public static final String FIND_ALL = "TravelAgencyBooking.findAll";
    public static final String FIND_BY_CUSTOMER = "TravelAgencyBooking.findByCustomer";

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @NotNull
    @NotEmpty
    @Email(message = "The email address must be in the format of name@domain.com")
    private String customerEmail;

    @NotNull
    @Column(name = "flightId")
    private Long flightId;

    @NotNull
    @Column(name = "hotelId")
    private Long hotelId;

    @NotNull
    @Column(name = "taxiId")
    private Long taxiId;

    @NotNull
    @Column(name = "flightBookingId")
    private Long flightBookingId;

    @NotNull
    @Column(name = "hotelBookingId")
    private Long hotelBookingId;

    @NotNull
    @Column(name = "taxiBookingId")
    private Long taxiBookingId;

    @NotNull
    @Future(message = "Booking date must be in the future")
    @Temporal(TemporalType.DATE)
    @Column(name = "flightBookingDate")
    private Date flightBookingDate;

    @NotNull
    @Future(message = "Booking date must be in the future")
    @Temporal(TemporalType.DATE)
    @Column(name = "hotelBookingDate")
    private Date hotelBookingDate;

    @NotNull
    @Future(message = "Booking date must be in the future")
    @Temporal(TemporalType.DATE)
    @Column(name = "taxiBookingDate")
    private Date taxiBookingDate;

    public Long getTaxiId() {
        return taxiId;
    }

    public Long getTaxiBookingId() {
        return taxiBookingId;
    }

    public Date getTaxiBookingDate() {
        return taxiBookingDate;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public Long getId() {
        return id;
    }

    public Long getFlightBookingId() {
        return flightBookingId;
    }

    public Long getHotelBookingId() {
        return hotelBookingId;
    }

    public Date getFlightBookingDate() {
        return flightBookingDate;
    }

    public Date getHotelBookingDate() {
        return hotelBookingDate;
    }

    public Long getFlightId() {
        return flightId;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setFlightBookingId(Long flightBookingId) {
        this.flightBookingId = flightBookingId;
    }

    public void setHotelBookingId(Long hotelBookingId) {
        this.hotelBookingId = hotelBookingId;
    }

    public void setTaxiBookingId(Long taxiBookingId) {
        this.taxiBookingId = taxiBookingId;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public void setTaxiId(Long taxiId) {
        this.taxiId = taxiId;
    }

    public void setFlightBookingDate(Date flightBookingDate) {
        this.flightBookingDate = flightBookingDate;
    }

    public void setHotelBookingDate(Date hotelBookingDate) {
        this.hotelBookingDate = hotelBookingDate;
    }

    public void setTaxiBookingDate(Date taxiBookingDate) {
        this.taxiBookingDate = taxiBookingDate;
    }

    @Override
    public String toString() {
        return "TravelAgencyBooking [id=" + id + ", customerEmail=" + customerEmail + ", flightId=" + flightId + ", hotelId=" + hotelId + ", taxiId=" + taxiId
            + ", flightBookingId=" + flightBookingId + ", hotelBookingId=" + hotelBookingId + ", taxiBookingId=" + taxiBookingId + ", flightBookingDate=" + flightBookingDate
            + ", hotelBookingDate=" + hotelBookingDate + ", taxiBookingDate=" + taxiBookingDate + "]";
    }

}
