package org.jboss.quickstarts.wfk.customer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>
 * This is a Repository class and connects the Service/Control layer (see {@link CustomerService} with the Domain/Entity Object
 * (see {@link Customer}).
 * </p>
 * <p/>
 * <p>
 * There are no access modifiers on the methods making them 'package' scope. They should only be accessed by a Service/Control
 * object.
 * </p>
 *
 * @author Donatas Daubaras
 * @see Customer
 * @see javax.persistence.EntityManager
 */

public class CustomerRepository {
    @Inject
    private
    @Named("logger")
    Logger log;

    @Inject
    private EntityManager em;

    /**
     * <p>
     * Returns a List of all persisted {@link Customer} objects, sorted alphabetically by name.
     * </p>
     *
     * @return List of Customer objects
     */
    List<Customer> findAllOrderedByName() {
        TypedQuery<Customer> query = em.createNamedQuery(Customer.FIND_ALL, Customer.class);
        return query.getResultList();
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
        return em.find(Customer.class, id);
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
        TypedQuery<Customer> query = em.createNamedQuery(Customer.FIND_BY_EMAIL, Customer.class).setParameter("email", email);
        return query.getSingleResult();
    }

    /**
     * <p>
     * Returns a single Customer object, specified by a String name.
     * <p/>
     * <p/>
     * <p>
     * If there is more then one, only the first will be returned.
     * </p>
     *
     * @param name The name field of the Customer to be returned
     * @return The first Customer with the specified name
     */
    Customer findByName(String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Customer> criteria = cb.createQuery(Customer.class);
        Root<Customer> customer = criteria.from(Customer.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new feature in JPA 2.0.
        // criteria.select(customer).where(cb.equal(customer.get(Customer_.name), name));
        criteria.select(customer).where(cb.equal(customer.get("name"), name));
        return em.createQuery(criteria).getSingleResult();
    }

    /**
     * <p>
     * Persists the provided Customer object to the application database using the EntityManager.
     * </p>
     * <p/>
     * <p>
     * {@link javax.persistence.EntityManager#persist(Object) persist(Object)} takes an entity instance, adds it to the context
     * and makes that instance managed (ie future updates to the entity will be tracked)
     * </p>
     * <p/>
     * <p>
     * persist(Object) will set the @GeneratedValue @Id for an object.
     * </p>
     *
     * @param customer The Customer object to be persisted
     * @return The Customer object that has been persisted
     * @throws ConstraintViolationException, ValidationException, Exception
     */

    Customer create(Customer customer) throws Exception {
        // Log the creation of the customer
        log.info("CustomerRepository.create() - Creating " + customer.getName());

        // Write the customer to the database
        em.persist(customer);

        return customer;
    }

    /**
     * <p>
     * Updates an existing Customer object in the application database with the provided Customer object.
     * </p>
     * <p/>
     * <p>
     * {@link javax.persistence.EntityManager#merge(Object) merge(Object)} creates a new instance of your entity, copies the
     * state from the supplied entity, and makes the new copy managed. The instance you pass in will not be managed (any changes
     * you make will not be part of the transaction - unless you call merge again).
     * </p>
     * <p/>
     * <p>
     * merge(Object) however must have an object with the @Id already generated.
     * </p>
     *
     * @param customer The Customer object to be merged with an existing Customer
     * @return The Customer that has been merged
     * @throws ConstraintViolationException, ValidationException, Exception
     */

    Customer update(Customer customer) throws Exception {
        // Log the update of the customer
        log.info("CustomerRepository.update() - Updating " + customer.getName());

        // Either update the customer or add it if it can't be found
        em.merge(customer);

        return customer;
    }

}
