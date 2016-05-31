package org.jboss.quickstarts.wfk.customer;

import org.apache.http.impl.client.CloseableHttpClient;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>
 * This Service assumes the Control responsibility in the ECB pattern.
 * </p>
 * <p/>
 * <p>
 * The validation is done here so that it may be used by other Boundary Resources. Other Business Logic would go here as well.
 * </p>
 * <p/>
 * <p>
 * There are no access modifiers on the methods, making them 'package' scope. They should only be accessed by a Boundary / Web
 * Service class with public methods.
 * </p>
 *
 * @author Donatas Daubaras
 * @see CustomerValidator
 * @see CustomerRepository
 */

@Dependent
public class CustomerService {
    @Inject
    private
    @Named("logger")
    Logger log;

    @Inject
    private CustomerValidator validator;

    @Inject
    private CustomerRepository crud;

    @Inject
    private
    @Named("httpClient")
    CloseableHttpClient httpClient;

    /**
     * <p>
     * Returns a List of all persisted {@link Customer} objects, sorted alphabetically by name.
     * </p>
     *
     * @return List of Customer objects
     */
    List<Customer> findAllOrderedByName() {
        return crud.findAllOrderedByName();
    }

    /**
     * <p>
     * Returns a single Customer object, specified by a Long id.
     * </p>
     *
     * @param id The id field of the Customer to be returned
     * @return The Customer with the specified id
     */
    Customer findById(Long id) {
        return crud.findById(id);
    }

    /**
     * <p>
     * Returns a single Customer object, specified by a String email.
     * </p>
     * <p/>
     * <p>
     * If there is more than one Customer with the specified email, only the first encountered will be returned.
     * </p>
     *
     * @param email The email field of the Customer to be returned
     * @return The first Customer with the specified email
     */

    Customer findByEmail(String email) {
        return crud.findByEmail(email);
    }

    /**
     * <p>
     * Returns a single Customer object, specified by a String name.
     * </p>
     * <p/>
     * <p>
     * If there is more then one, only the first will be returned.
     * </p>
     *
     * @param name The name field of the Customer to be returned
     * @return The first Customer with the specified name
     */

    Customer findByName(String name) {
        return crud.findByName(name);
    }

    /**
     * <p>
     * Writes the provided Customer object to the application database.
     * </p>
     * <p/>
     * <p>
     * Validates the data in the provided Customer object using a {@link CustomerValidator} object.
     * </p>
     *
     * @param customer The Customer object to be written to the database using a {@link CustomerRepository} object
     * @return The Customer object that has been successfully written to the application database
     * @throws ConstraintViolationException
     * @throws ValidationException
     * @throws Exception
     */

    Customer create(Customer customer) throws Exception {

        log.info("CustomerService.create() - Creating " + customer.getName());

        // Check to make sure the data fits with the parameters in the Customer model and passes validation.
        validator.validateCustomer(customer);
        // Write the customer to the database.
        return crud.create(customer);
    }

    /**
     * <p>
     * Updates an existing Customer object in the application database with the provided Customer object.
     * </p>
     * <p/>
     * <p>
     * Validates the data in the provided Customer object using a CustomerValidator object.
     * </p>
     *
     * @param customer The Customer object to be passed as an update to the application database
     * @return The Customer object that has been successfully updated in the application database
     * @throws ConstraintViolationException
     * @throws ValidationException
     * @throws Exception
     */

    Customer update(Customer customer) throws Exception {
        log.info("CustomerService.update() - Updating " + customer.getName());

        // Check to make sure the data fits with the parameters in the Customer model and passes validation.
        validator.validateCustomer(customer);

        // Write the customer to the database.
        return crud.update(customer);
    }
}
