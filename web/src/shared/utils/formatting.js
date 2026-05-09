/**
 * Date Formatting and Manipulation Utilities
 */

/**
 * Format date as "MMM DD, YYYY"
 */
export const formatDate = (date) => {
  if (!date) return "";
  const d = new Date(date);
  const options = { year: "numeric", month: "short", day: "numeric" };
  return d.toLocaleDateString("en-US", options);
};

/**
 * Format date and time as "MMM DD, YYYY HH:mm AM/PM"
 */
export const formatDateTime = (date) => {
  if (!date) return "";
  const d = new Date(date);
  const options = { year: "numeric", month: "short", day: "numeric", hour: "numeric", minute: "2-digit" };
  return d.toLocaleDateString("en-US", options);
};

/**
 * Format time as "HH:mm AM/PM"
 */
export const formatTime = (date) => {
  if (!date) return "";
  const d = new Date(date);
  const options = { hour: "numeric", minute: "2-digit" };
  return d.toLocaleTimeString("en-US", options);
};

/**
 * Get relative time string (e.g., "2 hours ago")
 */
export const getRelativeTime = (date) => {
  if (!date) return "";
  const d = new Date(date);
  const now = new Date();
  const diffMs = now - d;
  const diffMins = Math.floor(diffMs / 60000);
  const diffHours = Math.floor(diffMs / 3600000);
  const diffDays = Math.floor(diffMs / 86400000);

  if (diffMins < 1) return "just now";
  if (diffMins < 60) return `${diffMins} minute${diffMins > 1 ? "s" : ""} ago`;
  if (diffHours < 24) return `${diffHours} hour${diffHours > 1 ? "s" : ""} ago`;
  if (diffDays < 7) return `${diffDays} day${diffDays > 1 ? "s" : ""} ago`;

  return formatDate(date);
};

/**
 * Format duration between two dates
 */
export const formatDuration = (startDate, endDate) => {
  if (!startDate || !endDate) return "";
  const start = new Date(startDate);
  const end = new Date(endDate);
  const diffMs = end - start;
  const diffHours = Math.floor(diffMs / 3600000);
  const diffDays = Math.floor(diffHours / 24);

  if (diffDays > 0) {
    const hours = diffHours % 24;
    return `${diffDays} day${diffDays > 1 ? "s" : ""}${hours > 0 ? ` ${hours}h` : ""}`;
  }

  return `${diffHours}h`;
};

/**
 * Check if date is in the past
 */
export const isPastDate = (date) => {
  if (!date) return false;
  return new Date(date) < new Date();
};

/**
 * Check if date is today
 */
export const isToday = (date) => {
  if (!date) return false;
  const d = new Date(date);
  const today = new Date();
  return (
    d.getDate() === today.getDate() &&
    d.getMonth() === today.getMonth() &&
    d.getFullYear() === today.getFullYear()
  );
};

/**
 * Format currency
 */
export const formatCurrency = (amount, currency = "PHP") => {
  if (amount === null || amount === undefined) return "";
  const numAmount = parseFloat(amount);
  const currencySymbols = {
    PHP: "₱",
    USD: "$",
    EUR: "€",
    GBP: "£",
  };
  const symbol = currencySymbols[currency] || currency;
  return `${symbol}${numAmount.toFixed(2)}`;
};

/**
 * Format rating as "X.X / 5.0"
 */
export const formatRating = (rating) => {
  if (rating === null || rating === undefined) return "";
  const numRating = parseFloat(rating);
  return `${numRating.toFixed(1)} / 5.0`;
};

export default {
  formatDate,
  formatDateTime,
  formatTime,
  getRelativeTime,
  formatDuration,
  isPastDate,
  isToday,
  formatCurrency,
  formatRating,
};
