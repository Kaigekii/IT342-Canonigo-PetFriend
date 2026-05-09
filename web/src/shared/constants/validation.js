/**
 * Validation Constants and Rules
 */

export const VALIDATION = {
  // Email
  EMAIL_PATTERN: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
  EMAIL_MIN_LENGTH: 5,
  EMAIL_MAX_LENGTH: 100,

  // Password
  PASSWORD_MIN_LENGTH: 6,
  PASSWORD_MAX_LENGTH: 100,
  PASSWORD_REQUIRES_UPPERCASE: true,
  PASSWORD_REQUIRES_NUMBER: true,
  PASSWORD_REQUIRES_SPECIAL: true,

  // Name
  NAME_MIN_LENGTH: 2,
  NAME_MAX_LENGTH: 100,

  // Pet
  PET_NAME_MIN_LENGTH: 1,
  PET_NAME_MAX_LENGTH: 50,
  PET_BREED_MIN_LENGTH: 1,
  PET_BREED_MAX_LENGTH: 50,
  PET_AGE_MIN: 0,
  PET_AGE_MAX: 50,

  // Sitter
  SITTER_BIO_MIN_LENGTH: 10,
  SITTER_BIO_MAX_LENGTH: 1000,
  SITTER_HOURLY_RATE_MIN: 0,
  SITTER_HOURLY_RATE_MAX: 10000,
  SITTER_EXPERIENCE_MIN_LENGTH: 5,
  SITTER_EXPERIENCE_MAX_LENGTH: 500,

  // Booking
  BOOKING_NOTES_MAX_LENGTH: 500,

  // Review
  REVIEW_COMMENT_MIN_LENGTH: 5,
  REVIEW_COMMENT_MAX_LENGTH: 2000,
};

export const VALIDATION_MESSAGES = {
  EMAIL_REQUIRED: "Email is required",
  EMAIL_INVALID: "Please enter a valid email address",
  PASSWORD_REQUIRED: "Password is required",
  PASSWORD_TOO_SHORT: `Password must be at least ${VALIDATION.PASSWORD_MIN_LENGTH} characters`,
  PASSWORD_WEAK: "Password must contain uppercase, number, and special character",
  NAME_REQUIRED: "Name is required",
  NAME_TOO_SHORT: `Name must be at least ${VALIDATION.NAME_MIN_LENGTH} characters`,
  NAME_TOO_LONG: `Name must not exceed ${VALIDATION.NAME_MAX_LENGTH} characters`,

  PET_NAME_REQUIRED: "Pet name is required",
  PET_SPECIES_REQUIRED: "Please select a pet species",
  PET_AGE_REQUIRED: "Pet age is required",

  SITTER_BIO_REQUIRED: "Bio is required",
  SITTER_BIO_TOO_SHORT: `Bio must be at least ${VALIDATION.SITTER_BIO_MIN_LENGTH} characters`,
  SITTER_HOURLY_RATE_REQUIRED: "Hourly rate is required",
  SITTER_HOURLY_RATE_INVALID: "Hourly rate must be a valid positive number",
  SITTER_SERVICES_REQUIRED: "Please select at least one service",

  BOOKING_START_DATE_REQUIRED: "Start date is required",
  BOOKING_END_DATE_REQUIRED: "End date is required",
  BOOKING_INVALID_DATE_RANGE: "End date must be after start date",

  REVIEW_RATING_REQUIRED: "Rating is required",
  REVIEW_RATING_INVALID: "Rating must be between 1 and 5",
  REVIEW_COMMENT_REQUIRED: "Comment is required",
  REVIEW_COMMENT_TOO_SHORT: `Comment must be at least ${VALIDATION.REVIEW_COMMENT_MIN_LENGTH} characters`,

  REQUIRED_FIELD: "This field is required",
  INVALID_INPUT: "Invalid input",
};

export const validateEmail = (email) => {
  if (!email) return VALIDATION_MESSAGES.EMAIL_REQUIRED;
  if (!VALIDATION.EMAIL_PATTERN.test(email)) return VALIDATION_MESSAGES.EMAIL_INVALID;
  return null;
};

export const validatePassword = (password) => {
  if (!password) return VALIDATION_MESSAGES.PASSWORD_REQUIRED;
  if (password.length < VALIDATION.PASSWORD_MIN_LENGTH) return VALIDATION_MESSAGES.PASSWORD_TOO_SHORT;
  if (
    VALIDATION.PASSWORD_REQUIRES_UPPERCASE &&
    !/[A-Z]/.test(password)
  ) {
    return VALIDATION_MESSAGES.PASSWORD_WEAK;
  }
  if (VALIDATION.PASSWORD_REQUIRES_NUMBER && !/[0-9]/.test(password)) {
    return VALIDATION_MESSAGES.PASSWORD_WEAK;
  }
  if (VALIDATION.PASSWORD_REQUIRES_SPECIAL && !/[!@#$%^&*]/.test(password)) {
    return VALIDATION_MESSAGES.PASSWORD_WEAK;
  }
  return null;
};

export const validateName = (name) => {
  if (!name) return VALIDATION_MESSAGES.NAME_REQUIRED;
  if (name.length < VALIDATION.NAME_MIN_LENGTH) return VALIDATION_MESSAGES.NAME_TOO_SHORT;
  if (name.length > VALIDATION.NAME_MAX_LENGTH) return VALIDATION_MESSAGES.NAME_TOO_LONG;
  return null;
};

export default {
  VALIDATION,
  VALIDATION_MESSAGES,
  validateEmail,
  validatePassword,
  validateName,
};
