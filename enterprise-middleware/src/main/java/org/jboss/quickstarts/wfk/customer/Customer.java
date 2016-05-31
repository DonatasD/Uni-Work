package org.jboss.quickstarts.wfk.customer;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * <p>
 * The Customer class represent how customer resources are represented in the application database.
 * </p>
 * <p/>
 * <p>
 * The class also specifies how a customer are retrieved from the database (with @NamedQueris), and acceptable values for
 * Customer fields (with @NotNull, @Pattern, etc.)
 * </p>
 *
 * @author Donatas Daubaras
 */

/*
 * The @NamedQueries included here are for searching against the table that reflects this object. This is the most efficient
 * form of query in JPA though is it more error prone due to the syntax being in a String. This makes it harder to debug.
 */

@Entity
@NamedQueries({
        @NamedQuery(name = Customer.FIND_ALL, query = "SELECT c FROM Customer c ORDER BY c.name ASC"),
        @NamedQuery(name = Customer.FIND_BY_EMAIL, query = "SELECT c FROM Customer c WHERE c.email = :email")
})
@XmlRootElement
@Table(name = "customer", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Customer implements Serializable {

    public static final String FIND_ALL = "Customer.findAll";
    public static final String FIND_BY_EMAIL = "Customer.findByEmail";
    /**
     * Default value included to remove warning. Remove or modify at will. *
     */
    private static final long serialVersionUID = 1L;

    /*
     * The error messages match the ones in the UI so that the user isn't confused by two similar error messages for the same
     * error after hitting submit. This is if the form submits while having validation errors. The only difference is that there
     * are no periods(.) at the end of these message sentences, this gives us a way to verify where the message came from.
     * 
     * Each variable name exactly matches the ones used on the HTML form name attribute so that when an error for that variable
     * occurs it can be sent to the correct input field on the form.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;


    @NotNull
    @Size(min = 1, max = 50, message = "Name cannot be longer than 50 characters")
    @Pattern(regexp = "[A-Za-z-']+", message = "Name must be a non-empty alphabetical string less than 50 characters in length")
    @Column(name = "name")
    private String name;

    @NotNull
    @NotEmpty
    @Email(message = "The email address must be in the format of name@domain.com")
    private String email;

    @NotNull
    @NotEmpty
    @Pattern(regexp = "0[0-9]{10}", message = "The phone number must contain only digits, start by 0 and be of length 11")
    @Column(name = "phoneNumber")
    private String phoneNumber;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "Customer [id=" + id + ", name=" + name + ", email=" + email + ", phoneNumber=" + phoneNumber + "]";
    }
}
