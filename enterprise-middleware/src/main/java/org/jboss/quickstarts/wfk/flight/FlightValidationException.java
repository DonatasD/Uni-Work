package org.jboss.quickstarts.wfk.flight;

import javax.validation.ValidationException;

/**
 * <p>
 * This class allows to create flight validation exception, which allows to indicate why exception was thrown.
 * </p>
 * <p/>
 * <p>
 * {@link FlightValidationExceptionEnum} indicates possible exception types using {@link Enum}.
 * </p>
 *
 * @author Donatas Daubaras
 * @see ValidationException
 * @see FlightValidationExceptionEnum
 */

public class FlightValidationException extends ValidationException {
    private static final long serialVersionUID = 1L;
    private FlightValidationExceptionEnum exceptionType;

    public FlightValidationException(String message, FlightValidationExceptionEnum type) {
        super(message);
        this.exceptionType = type;
    }

    public FlightValidationExceptionEnum getExceptionType() {
        return exceptionType;
    }

}
