package edu.cit.canonigo.petfriend.shared.exception;

/**
 * Exception thrown when an operation is invalid in the current context
 * 
 * Usage:
 * if (booking.getStatus().equals(BookingStatus.CANCELLED)) {
 *   throw new InvalidOperationException("Cannot modify a cancelled booking");
 * }
 */
public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException(String message) {
        super(message);
    }

    public InvalidOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
