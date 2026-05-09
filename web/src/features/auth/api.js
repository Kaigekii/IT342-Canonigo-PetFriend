/**
 * Auth Feature API Client
 */

import { apiClient } from "@/shared/utils/api";
import { API_ENDPOINTS } from "@/shared/constants/api";

export const authApi = {
  /**
   * Login with email and password
   */
  login: async (email, password) => {
    return apiClient.post(API_ENDPOINTS.AUTH.LOGIN, {
      email,
      password,
    });
  },

  /**
   * Register new user
   */
  register: async (formData) => {
    return apiClient.post(API_ENDPOINTS.AUTH.REGISTER, formData);
  },

  /**
   * Get current user
   */
  getCurrentUser: async () => {
    return apiClient.get(API_ENDPOINTS.AUTH.ME);
  },

  /**
   * Update user profile
   */
  updateProfile: async (profileData) => {
    return apiClient.put(API_ENDPOINTS.AUTH.PROFILE, profileData);
  },

  /**
   * Logout user (client-side only)
   */
  logout: () => {
    if (typeof window !== "undefined") {
      localStorage.removeItem("token");
    }
  },
};

export default authApi;
