package org.jboss.quickstarts.wfk.booking;

import javax.validation.ValidationException;

/**
 * <p>
 * This class allows to create booking validation exception, which allows to indicate why exception was thrown.
 * </p>
 * <p/>
 * <p>
 * {@link BookingValidationExceptionEnum} indicates possible exception types using {@link Enum}.
 * </p>
 *
 * @author Donatas Daubaras
 * @see ValidationException
 * @see BookingValidationExceptionEnum
 */

public class BookingValidationException extends ValidationException {

    private static final long serialVersionUID = 1L;
    private BookingValidationExceptionEnum exceptionType;

    public BookingValidationException(String message, BookingValidationExceptionEnum type) {
        super(message);
        this.exceptionType = type;
    }

    public BookingValidationExceptionEnum getExceptionType() {
        return exceptionType;
    }

}
