/**
 * Messages Feature API Client
 */

import { apiClient } from "@/shared/utils/api";
import { API_ENDPOINTS } from "@/shared/constants/api";

export const messagesApi = {
  /**
   * Get all message threads
   */
  listThreads: async () => {
    return apiClient.get(API_ENDPOINTS.MESSAGES.THREADS);
  },

  /**
   * Create or get message thread
   */
  createThread: async (otherUserId) => {
    return apiClient.post(API_ENDPOINTS.MESSAGES.CREATE_THREAD, {
      otherUserId,
    });
  },

  /**
   * Get messages in thread
   */
  getThreadMessages: async (threadId) => {
    return apiClient.get(API_ENDPOINTS.MESSAGES.GET_THREAD_MESSAGES(threadId));
  },

  /**
   * Send message to thread
   */
  sendMessage: async (threadId, content) => {
    return apiClient.post(API_ENDPOINTS.MESSAGES.SEND_MESSAGE(threadId), {
      content,
    });
  },
};

export default messagesApi;
