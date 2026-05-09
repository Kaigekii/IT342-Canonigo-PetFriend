package edu.cit.canonigo.petfriend.shared.util;

import java.util.regex.Pattern;

/**
 * Centralized Validation Utilities
 * 
 * Provides common validation patterns and helper methods used across the application.
 * 
 * Features:
 * - Email validation
 * - Password strength validation
 * - Name validation
 * - Phone number validation
 * - URL validation
 * - Consistent validation error messages
 * 
 * Usage:
 * if (!ValidationUtil.isValidEmail(email)) {
 *   throw new InvalidOperationException(ValidationUtil.INVALID_EMAIL_MESSAGE);
 * }
 */
public class ValidationUtil {

    // Validation Patterns
    public static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@(.+)$"
    );

    public static final Pattern PHONE_PATTERN = Pattern.compile(
            "^(\\+\\d{1,3})?[0-9]{7,15}$"
    );

    public static final Pattern NAME_PATTERN = Pattern.compile(
            "^[a-zA-Z\\s'-]{2,100}$"
    );

    public static final Pattern URL_PATTERN = Pattern.compile(
            "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$", 
            Pattern.CASE_INSENSITIVE
    );

    // Validation Messages
    public static final String INVALID_EMAIL_MESSAGE = "Invalid email format";
    public static final String INVALID_PASSWORD_MESSAGE = "Password must be at least 6 characters";
    public static final String INVALID_NAME_MESSAGE = "Name must be 2-100 characters and contain only letters, spaces, hyphens, or apostrophes";
    public static final String INVALID_PHONE_MESSAGE = "Invalid phone number format";
    public static final String INVALID_URL_MESSAGE = "Invalid URL format";
    public static final String EMPTY_STRING_MESSAGE = "Field cannot be empty";
    public static final String STRING_TOO_SHORT_MESSAGE = "Field is too short";
    public static final String STRING_TOO_LONG_MESSAGE = "Field is too long";

    // Length Constraints
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_PASSWORD_LENGTH = 100;
    public static final int MIN_NAME_LENGTH = 2;
    public static final int MAX_NAME_LENGTH = 100;
    public static final int MIN_EMAIL_LENGTH = 5;
    public static final int MAX_EMAIL_LENGTH = 255;
    public static final int MIN_PHONE_LENGTH = 7;
    public static final int MAX_PHONE_LENGTH = 20;
    public static final int MAX_COMMENT_LENGTH = 1000;
    public static final int MAX_DESCRIPTION_LENGTH = 5000;

    /**
     * Validates if email is in correct format
     * 
     * @param email the email to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        if (email.length() < MIN_EMAIL_LENGTH || email.length() > MAX_EMAIL_LENGTH) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validates if password meets minimum requirements
     * 
     * @param password the password to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return password.length() >= MIN_PASSWORD_LENGTH && 
               password.length() <= MAX_PASSWORD_LENGTH;
    }

    /**
     * Validates if name is in correct format
     * 
     * @param name the name to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return NAME_PATTERN.matcher(name.trim()).matches();
    }

    /**
     * Validates if phone number is in correct format
     * 
     * @param phone the phone to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.replaceAll("[\\s-]", "")).matches();
    }

    /**
     * Validates if URL is in correct format
     * 
     * @param url the URL to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        return URL_PATTERN.matcher(url).matches();
    }

    /**
     * Validates if string is not empty and has valid length
     * 
     * @param str the string to validate
     * @param minLength minimum length (inclusive)
     * @param maxLength maximum length (inclusive)
     * @return true if valid, false otherwise
     */
    public static boolean isValidString(String str, int minLength, int maxLength) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        int length = str.trim().length();
        return length >= minLength && length <= maxLength;
    }

    /**
     * Validates if a string is not null or empty
     * 
     * @param str the string to validate
     * @return true if not empty, false otherwise
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    /**
     * Validates if a number is positive
     * 
     * @param number the number to validate
     * @return true if positive, false otherwise
     */
    public static boolean isPositive(Number number) {
        if (number == null) {
            return false;
        }
        return number.doubleValue() > 0;
    }

    /**
     * Validates if rating is between 0 and 5
     * 
     * @param rating the rating to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidRating(Double rating) {
        if (rating == null) {
            return false;
        }
        return rating >= 0 && rating <= 5;
    }

    /**
     * Sanitizes string input by removing leading/trailing whitespace
     * 
     * @param input the input to sanitize
     * @return sanitized string or empty string if null
     */
    public static String sanitize(String input) {
        return input == null ? "" : input.trim();
    }
}
