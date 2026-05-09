/**
 * Shared API Client Utilities
 * Provides fetch wrapper with error handling and authentication
 */

class ApiClient {
  constructor() {
    this.baseURL = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";
    this.defaultHeaders = {
      "Content-Type": "application/json",
    };
  }

  /**
   * Get authorization token from localStorage
   */
  getAuthToken() {
    if (typeof window === "undefined") return null;
    return localStorage.getItem("token");
  }

  /**
   * Get headers with authentication
   */
  getHeaders(customHeaders = {}) {
    const headers = { ...this.defaultHeaders, ...customHeaders };
    const token = this.getAuthToken();
    if (token) {
      headers["Authorization"] = `Bearer ${token}`;
    }
    return headers;
  }

  /**
   * Make a GET request
   */
  async get(url, options = {}) {
    return this.request(url, {
      method: "GET",
      ...options,
    });
  }

  /**
   * Make a POST request
   */
  async post(url, data, options = {}) {
    return this.request(url, {
      method: "POST",
      body: JSON.stringify(data),
      ...options,
    });
  }

  /**
   * Make a PUT request
   */
  async put(url, data, options = {}) {
    return this.request(url, {
      method: "PUT",
      body: JSON.stringify(data),
      ...options,
    });
  }

  /**
   * Make a DELETE request
   */
  async delete(url, options = {}) {
    return this.request(url, {
      method: "DELETE",
      ...options,
    });
  }

  /**
   * Core request method with error handling
   */
  async request(url, options = {}) {
    try {
      const response = await fetch(url, {
        headers: this.getHeaders(options.headers),
        ...options,
      });

      // Handle 401 - redirect to login
      if (response.status === 401) {
        if (typeof window !== "undefined") {
          localStorage.removeItem("token");
          window.location.href = "/login";
        }
        throw new Error("Unauthorized - Please log in again");
      }

      // Handle other errors
      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        const error = new Error(
          errorData.message || `API Error: ${response.status} ${response.statusText}`
        );
        error.status = response.status;
        error.data = errorData;
        throw error;
      }

      // Parse response
      const data = await response.json();
      return data;
    } catch (error) {
      console.error("API Client Error:", error);
      throw error;
    }
  }
}

// Export singleton instance
export const apiClient = new ApiClient();

export default apiClient;
