package edu.cit.canonigo.petfriend.shared.exception;

/**
 * Exception thrown when a requested resource is not found
 * 
 * Usage:
 * if (pet == null) {
 *   throw new ResourceNotFoundException("Pet with ID " + id + " not found");
 * }
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
