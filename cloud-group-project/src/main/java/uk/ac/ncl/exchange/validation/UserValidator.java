package uk.ac.ncl.exchange.validation;

import uk.ac.ncl.exchange.data.UserRepository;
import uk.ac.ncl.exchange.model.User;

import javax.ejb.DuplicateKeyException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class UserValidator {
    @Inject
    private Validator validator;

    @Inject
    private UserRepository userRepository;

    @Inject
    private
    @Named("logger")
    Logger log;

    /**
     * Validates user and throws proper exceptions if certain properties are not met.
     *
     * @param user
     * @throws ConstraintViolationException
     * @throws ValidationException
     */
    public void validateUser(User user) throws ConstraintViolationException, DuplicateKeyException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }
        if (isRegistered(user.getUserName())) {
            throw new DuplicateKeyException("Please choose different username");
        }
    }

    /**
     * Checks if userName is already in use.
     *
     * @param userName user name that is being checked
     * @return boolean indicating whether userName is already in use.
     */
    public boolean isRegistered(String userName) {
        // Check in the DynamoDB using UserRepository if user exists
        log.info("isRegistered - Checking if User is already registered");
        User user = userRepository.findByUserName(userName);
        return user != null;
    }
}
