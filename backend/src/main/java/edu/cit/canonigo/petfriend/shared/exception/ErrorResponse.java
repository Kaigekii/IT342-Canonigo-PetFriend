package edu.cit.canonigo.petfriend.shared.exception;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard error response format for all API errors
 * 
 * Format:
 * {
 *   "timestamp": "2026-05-09T10:30:00",
 *   "status": 400,
 *   "error": "Bad Request",
 *   "message": "Validation failed",
 *   "path": "/api/pets",
 *   "details": {
 *     "fieldName": "error message"
 *   }
 * }
 */
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> details;

    // Constructor with details
    public ErrorResponse(LocalDateTime timestamp, int status, String error, 
                        String message, String path, Map<String, String> details) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.details = details;
    }

    // Constructor without details
    public ErrorResponse(LocalDateTime timestamp, int status, String error, 
                        String message, String path) {
        this(timestamp, status, error, message, path, null);
    }

    // Getters
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    // Setters
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setDetails(Map<String, String> details) {
        this.details = details;
    }
}
