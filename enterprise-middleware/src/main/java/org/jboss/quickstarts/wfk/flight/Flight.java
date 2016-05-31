package org.jboss.quickstarts.wfk.flight;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * <p>
 * This class is a base class to define a flight bean.
 * </p>
 *
 * @author Donatas Daubaras
 */

@Entity
@NamedQueries({
        @NamedQuery(name = Flight.FIND_ALL, query = "SELECT f FROM Flight f ORDER BY f.flightNumber ASC"),
        @NamedQuery(name = Flight.FIND_BY_NUMBER, query = "SELECT f FROM Flight f WHERE f.flightNumber = :flightNumber")
})
@XmlRootElement
@Table(name = "Flight", uniqueConstraints = @UniqueConstraint(columnNames = "flightNumber"))
public class Flight implements Serializable {

    public static final String FIND_ALL = "Flight.findAll";
    public static final String FIND_BY_NUMBER = "Flight.findByNumber";
    /**
     * Default value included to remove warning. Remove or modify at will. *
     */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @NotNull
    @Pattern(regexp = "[A-Za-z0-9]{5}", message = "Flight number must be a non-empty alpha-numerical string which is 5 characters in length")
    @Column(name = "flightNumber")
    private String flightNumber;

    @NotNull
    @Pattern(regexp = "[A-Z]{3}", message = "Point of departure must be a non-empty alphabetical string, which is upper case and 3 characters in length")
    @Column(name = "departurePoint")
    private String departurePoint;

    @NotNull
    @Pattern(regexp = "[A-Z]{3}", message = "a non-empty alphabetical string, which is upper case, 3 characters in length and different from its point of departure")
    @Column(name = "destinationPoint")
    private String destinationPoint;

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getDeparturePoint() {
        return departurePoint;
    }

    public void setDeparturePoint(String departurePoint) {
        this.departurePoint = departurePoint;
    }

    public String getDestinationPoint() {
        return destinationPoint;
    }

    public void setDestinationPoint(String destinationPoint) {
        this.destinationPoint = destinationPoint;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Flight [id=" + id + ", flightNumber=" + flightNumber + ", departurePoint=" + departurePoint + ", destinationPoint=" + destinationPoint + "]";
    }

}
