/**
 * Sitters Feature API Client
 */

import { apiClient } from "@/shared/utils/api";
import { API_ENDPOINTS } from "@/shared/constants/api";

export const sittersApi = {
  /**
   * Search sitters with filters
   */
  searchSitters: async (location = "", serviceType = "") => {
    const params = new URLSearchParams();
    if (location) params.append("location", location);
    if (serviceType) params.append("serviceType", serviceType);
    return apiClient.get(`${API_ENDPOINTS.SITTERS.SEARCH}?${params.toString()}`);
  },

  /**
   * Get sitter details by ID
   */
  getSitterDetails: async (sitterId) => {
    return apiClient.get(API_ENDPOINTS.SITTERS.GET(sitterId));
  },

  /**
   * Get current sitter's profile
   */
  getMyProfile: async () => {
    return apiClient.get(API_ENDPOINTS.SITTERS.PROFILE);
  },

  /**
   * Update sitter profile
   */
  updateProfile: async (profileData) => {
    return apiClient.put(API_ENDPOINTS.SITTERS.UPDATE_PROFILE, profileData);
  },

  /**
   * Submit for verification
   */
  submitVerification: async () => {
    return apiClient.post(API_ENDPOINTS.SITTERS.SUBMIT_VERIFICATION, {});
  },
};

export default sittersApi;
