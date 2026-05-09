package edu.cit.canonigo.petfriend.shared.exception;

/**
 * Exception thrown when user doesn't have permission to perform an action
 * 
 * Usage:
 * if (!booking.getOwnerId().equals(currentUser.getId())) {
 *   throw new ForbiddenException("You don't have permission to modify this booking");
 * }
 */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}
