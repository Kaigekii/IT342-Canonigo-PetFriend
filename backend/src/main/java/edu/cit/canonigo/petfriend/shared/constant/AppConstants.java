package edu.cit.canonigo.petfriend.shared.constant;

/**
 * Application-wide Constants
 * 
 * Centralized constants used across the application for:
 * - User roles
 * - Booking statuses
 * - Service types
 * - Pet species
 * - HTTP headers
 * - Error codes
 * 
 * Usage:
 * if (user.getRole().equals(AppConstants.ROLE_PET_SITTER)) {
 *   // Handle sitter logic
 * }
 */
public class AppConstants {

    // ==================== USER ROLES ====================
    public static final String ROLE_PET_OWNER = "PET_OWNER";
    public static final String ROLE_PET_SITTER = "PET_SITTER";
    public static final String ROLE_ADMIN = "ADMIN";

    // ==================== BOOKING STATUSES ====================
    public static final String BOOKING_STATUS_PENDING = "PENDING";
    public static final String BOOKING_STATUS_CONFIRMED = "CONFIRMED";
    public static final String BOOKING_STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String BOOKING_STATUS_COMPLETED = "COMPLETED";
    public static final String BOOKING_STATUS_CANCELLED = "CANCELLED";

    // ==================== SERVICE TYPES ====================
    public static final String SERVICE_TYPE_WALKING = "WALKING";
    public static final String SERVICE_TYPE_SITTING = "SITTING";
    public static final String SERVICE_TYPE_GROOMING = "GROOMING";
    public static final String SERVICE_TYPE_TRAINING = "TRAINING";
    public static final String SERVICE_TYPE_VETERINARY = "VETERINARY";

    // ==================== PET SPECIES ====================
    public static final String PET_SPECIES_DOG = "DOG";
    public static final String PET_SPECIES_CAT = "CAT";
    public static final String PET_SPECIES_RABBIT = "RABBIT";
    public static final String PET_SPECIES_HAMSTER = "HAMSTER";
    public static final String PET_SPECIES_BIRD = "BIRD";
    public static final String PET_SPECIES_OTHER = "OTHER";

    // ==================== VERIFICATION STATUSES ====================
    public static final String VERIFICATION_STATUS_PENDING = "PENDING";
    public static final String VERIFICATION_STATUS_APPROVED = "APPROVED";
    public static final String VERIFICATION_STATUS_REJECTED = "REJECTED";

    // ==================== HTTP HEADERS ====================
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_BEARER_PREFIX = "Bearer ";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_CONTENT_TYPE_JSON = "application/json";

    // ==================== PAGINATION ====================
    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;

    // ==================== SECURITY ====================
    public static final String JWT_SECRET_KEY = "your-secret-key"; // Should be in config
    public static final long JWT_EXPIRATION_MS = 86400000; // 24 hours
    public static final String SECURITY_SCHEME_BEARER = "Bearer";

    // ==================== API PATHS ====================
    public static final String API_PREFIX = "/api";
    public static final String API_AUTH = API_PREFIX + "/auth";
    public static final String API_PETS = API_PREFIX + "/pets";
    public static final String API_SITTERS = API_PREFIX + "/sitters";
    public static final String API_BOOKINGS = API_PREFIX + "/bookings";
    public static final String API_MESSAGES = API_PREFIX + "/messages";
    public static final String API_REVIEWS = API_PREFIX + "/reviews";
    public static final String API_ADMIN = API_PREFIX + "/admin";

    // ==================== ERROR CODES ====================
    public static final String ERROR_CODE_UNAUTHORIZED = "UNAUTHORIZED";
    public static final String ERROR_CODE_FORBIDDEN = "FORBIDDEN";
    public static final String ERROR_CODE_NOT_FOUND = "NOT_FOUND";
    public static final String ERROR_CODE_INVALID_INPUT = "INVALID_INPUT";
    public static final String ERROR_CODE_DUPLICATE_ENTRY = "DUPLICATE_ENTRY";
    public static final String ERROR_CODE_INVALID_OPERATION = "INVALID_OPERATION";
    public static final String ERROR_CODE_INTERNAL_ERROR = "INTERNAL_ERROR";

    // ==================== CACHE KEYS ====================
    public static final String CACHE_USER_PREFIX = "user:";
    public static final String CACHE_SITTER_PREFIX = "sitter:";
    public static final String CACHE_BOOKING_PREFIX = "booking:";

    // ==================== TIME UNITS ====================
    public static final long HOUR_IN_MILLISECONDS = 3600000;
    public static final long DAY_IN_MILLISECONDS = 86400000;
    public static final long WEEK_IN_MILLISECONDS = 604800000;

    // ==================== RATING CONSTRAINTS ====================
    public static final int MIN_RATING = 0;
    public static final int MAX_RATING = 5;
    public static final double AVERAGE_RATING_MIN = 0.0;
    public static final double AVERAGE_RATING_MAX = 5.0;

    // ==================== PRICE CONSTRAINTS ====================
    public static final double MIN_PRICE = 0.0;
    public static final double MAX_PRICE = 100000.0;

    // ==================== MESSAGES ====================
    public static final String MSG_SUCCESS = "Operation successful";
    public static final String MSG_CREATED = "Resource created successfully";
    public static final String MSG_UPDATED = "Resource updated successfully";
    public static final String MSG_DELETED = "Resource deleted successfully";
    public static final String MSG_NOT_FOUND = "Resource not found";
    public static final String MSG_UNAUTHORIZED = "Unauthorized access";
    public static final String MSG_FORBIDDEN = "Forbidden";
    public static final String MSG_INVALID_INPUT = "Invalid input";
    public static final String MSG_INTERNAL_ERROR = "Internal server error";

    // Private constructor to prevent instantiation
    private AppConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }
}
