/**
 * Pets Feature API Client
 */

import { apiClient } from "@/shared/utils/api";
import { API_ENDPOINTS } from "@/shared/constants/api";

export const petsApi = {
  /**
   * Get all pets for current user
   */
  listPets: async () => {
    return apiClient.get(API_ENDPOINTS.PETS.LIST);
  },

  /**
   * Get pet by ID
   */
  getPet: async (petId) => {
    return apiClient.get(API_ENDPOINTS.PETS.GET(petId));
  },

  /**
   * Create new pet
   */
  createPet: async (petData) => {
    return apiClient.post(API_ENDPOINTS.PETS.CREATE, petData);
  },

  /**
   * Update pet
   */
  updatePet: async (petId, petData) => {
    return apiClient.put(API_ENDPOINTS.PETS.UPDATE(petId), petData);
  },

  /**
   * Delete pet
   */
  deletePet: async (petId) => {
    return apiClient.delete(API_ENDPOINTS.PETS.DELETE(petId));
  },
};

export default petsApi;
