package edu.cit.canonigo.petfriend.shared.util;

import java.time.LocalDateTime;

/**
 * Generic API Response Wrapper
 * 
 * Provides consistent response format for all API endpoints:
 * {
 *   "success": true,
 *   "message": "Success message",
 *   "data": { ... },
 *   "timestamp": "2026-05-09T10:30:00",
 *   "errorCode": null
 * }
 * 
 * Usage:
 * return ApiResponse.success("Pet created", pet);
 * return ApiResponse.error("Invalid email", "INVALID_INPUT");
 */
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    private String errorCode;

    /**
     * Private constructor
     */
    private ApiResponse(boolean success, String message, T data, String errorCode) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
        this.errorCode = errorCode;
    }

    /**
     * Creates a success response with data
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }

    /**
     * Creates a success response without data
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null, null);
    }

    /**
     * Creates an error response
     */
    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return new ApiResponse<>(false, message, null, errorCode);
    }

    /**
     * Creates an error response without error code
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, null);
    }

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getErrorCode() {
        return errorCode;
    }

    // Setters
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
