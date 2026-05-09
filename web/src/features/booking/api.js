/**
 * Bookings Feature API Client
 */

import { apiClient } from "@/shared/utils/api";
import { API_ENDPOINTS } from "@/shared/constants/api";

export const bookingsApi = {
  /**
   * Get all bookings for current user
   */
  listBookings: async () => {
    return apiClient.get(API_ENDPOINTS.BOOKINGS.LIST);
  },

  /**
   * Get booking by ID
   */
  getBooking: async (bookingId) => {
    return apiClient.get(API_ENDPOINTS.BOOKINGS.GET(bookingId));
  },

  /**
   * Create new booking
   */
  createBooking: async (bookingData) => {
    return apiClient.post(API_ENDPOINTS.BOOKINGS.CREATE, bookingData);
  },

  /**
   * Update booking
   */
  updateBooking: async (bookingId, bookingData) => {
    return apiClient.put(API_ENDPOINTS.BOOKINGS.UPDATE(bookingId), bookingData);
  },

  /**
   * Cancel booking
   */
  cancelBooking: async (bookingId) => {
    return apiClient.post(API_ENDPOINTS.BOOKINGS.CANCEL(bookingId), {});
  },

  /**
   * Get booking requests (for sitters)
   */
  listRequests: async () => {
    return apiClient.get(API_ENDPOINTS.BOOKINGS.REQUESTS);
  },

  /**
   * Accept booking request
   */
  acceptRequest: async (requestId) => {
    return apiClient.post(API_ENDPOINTS.BOOKINGS.ACCEPT_REQUEST(requestId), {});
  },

  /**
   * Decline booking request
   */
  declineRequest: async (requestId) => {
    return apiClient.post(API_ENDPOINTS.BOOKINGS.DECLINE_REQUEST(requestId), {});
  },
};

export default bookingsApi;
