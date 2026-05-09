/**
 * Admin Feature API Client
 */

import { apiClient } from "@/shared/utils/api";
import { API_ENDPOINTS } from "@/shared/constants/api";

export const adminApi = {
  /**
   * Get admin dashboard
   */
  getDashboard: async () => {
    return apiClient.get(API_ENDPOINTS.ADMIN.DASHBOARD);
  },

  /**
   * Get pending sitters
   */
  getPendingSitters: async () => {
    return apiClient.get(API_ENDPOINTS.ADMIN.PENDING_SITTERS);
  },

  /**
   * Approve sitter
   */
  approveSitter: async (sitterId) => {
    return apiClient.post(API_ENDPOINTS.ADMIN.APPROVE_SITTER(sitterId), {});
  },

  /**
   * Reject sitter
   */
  rejectSitter: async (sitterId, reason = "") => {
    return apiClient.post(API_ENDPOINTS.ADMIN.REJECT_SITTER(sitterId), {
      reason,
    });
  },

  /**
   * List all users
   */
  listUsers: async () => {
    return apiClient.get(API_ENDPOINTS.ADMIN.LIST_USERS);
  },

  /**
   * List all bookings
   */
  listBookings: async () => {
    return apiClient.get(API_ENDPOINTS.ADMIN.LIST_BOOKINGS);
  },
};

export default adminApi;
