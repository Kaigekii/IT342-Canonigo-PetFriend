package edu.cit.canonigo.petfriend.shared.exception;

/**
 * Exception thrown when user is not authenticated
 * 
 * Usage:
 * if (user == null) {
 *   throw new UnauthorizedException("User not authenticated");
 * }
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
