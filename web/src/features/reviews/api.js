/**
 * Reviews Feature API Client
 */

import { apiClient } from "@/shared/utils/api";
import { API_ENDPOINTS } from "@/shared/constants/api";

export const reviewsApi = {
  /**
   * Submit review for a booking
   */
  submitReview: async (bookingId, rating, comment) => {
    return apiClient.post(API_ENDPOINTS.REVIEWS.CREATE, {
      bookingId,
      rating,
      comment,
    });
  },

  /**
   * Get reviews for a sitter
   */
  getSitterReviews: async (sitterId) => {
    return apiClient.get(API_ENDPOINTS.REVIEWS.GET_SITTER_REVIEWS(sitterId));
  },

  /**
   * Get sitter review summary (rating and count)
   */
  getSitterReviewSummary: async (sitterId) => {
    return apiClient.get(API_ENDPOINTS.REVIEWS.GET_SITTER_SUMMARY(sitterId));
  },

  /**
   * Get bookings already reviewed by current user
   */
  getReviewedBookings: async () => {
    return apiClient.get(API_ENDPOINTS.REVIEWS.GET_REVIEWED_BOOKINGS);
  },
};

export default reviewsApi;
