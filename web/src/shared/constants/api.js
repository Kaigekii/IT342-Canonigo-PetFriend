/**
 * API Endpoints for PetFriend Application
 */

const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

export const API_ENDPOINTS = {
  // Authentication
  AUTH: {
    LOGIN: `${API_BASE}/api/auth/login`,
    REGISTER: `${API_BASE}/api/auth/register`,
    ME: `${API_BASE}/api/user/me`,
    PROFILE: `${API_BASE}/api/user/profile`,
  },

  // Pets
  PETS: {
    LIST: `${API_BASE}/api/pets`,
    CREATE: `${API_BASE}/api/pets`,
    GET: (id) => `${API_BASE}/api/pets/${id}`,
    UPDATE: (id) => `${API_BASE}/api/pets/${id}`,
    DELETE: (id) => `${API_BASE}/api/pets/${id}`,
  },

  // Sitters
  SITTERS: {
    SEARCH: `${API_BASE}/api/sitters/search`,
    GET: (id) => `${API_BASE}/api/sitters/${id}`,
    PROFILE: `${API_BASE}/api/sitter-profile`,
    UPDATE_PROFILE: `${API_BASE}/api/sitter-profile`,
    SUBMIT_VERIFICATION: `${API_BASE}/api/sitter-profile/submit-verification`,
  },

  // Bookings
  BOOKINGS: {
    LIST: `${API_BASE}/api/bookings`,
    CREATE: `${API_BASE}/api/bookings`,
    GET: (id) => `${API_BASE}/api/bookings/${id}`,
    UPDATE: (id) => `${API_BASE}/api/bookings/${id}`,
    CANCEL: (id) => `${API_BASE}/api/bookings/${id}/cancel`,
    REQUESTS: `${API_BASE}/api/requests`,
    ACCEPT_REQUEST: (id) => `${API_BASE}/api/requests/${id}/accept`,
    DECLINE_REQUEST: (id) => `${API_BASE}/api/requests/${id}/decline`,
  },

  // Messages
  MESSAGES: {
    THREADS: `${API_BASE}/api/messages/threads`,
    CREATE_THREAD: `${API_BASE}/api/messages/threads`,
    GET_THREAD_MESSAGES: (threadId) => `${API_BASE}/api/messages/threads/${threadId}/messages`,
    SEND_MESSAGE: (threadId) => `${API_BASE}/api/messages/threads/${threadId}/messages`,
  },

  // Reviews
  REVIEWS: {
    CREATE: `${API_BASE}/api/reviews`,
    GET_SITTER_REVIEWS: (sitterId) => `${API_BASE}/api/reviews/sitter/${sitterId}`,
    GET_SITTER_SUMMARY: (sitterId) => `${API_BASE}/api/reviews/sitter/${sitterId}/summary`,
    GET_REVIEWED_BOOKINGS: `${API_BASE}/api/reviews/me/reviewed-bookings`,
  },

  // Admin
  ADMIN: {
    DASHBOARD: `${API_BASE}/api/admin/dashboard`,
    PENDING_SITTERS: `${API_BASE}/api/admin/sitters/pending`,
    APPROVE_SITTER: (id) => `${API_BASE}/api/admin/sitters/${id}/approve`,
    REJECT_SITTER: (id) => `${API_BASE}/api/admin/sitters/${id}/reject`,
    LIST_USERS: `${API_BASE}/api/admin/users`,
    LIST_BOOKINGS: `${API_BASE}/api/admin/bookings`,
  },

  UPLOADS: {
    PROFILE_PHOTO: `${API_BASE}/api/uploads/profile-photo`,
    PET_PHOTO: (id) => `${API_BASE}/api/uploads/pets/${id}/photo`,
  },
};

export default API_ENDPOINTS;
