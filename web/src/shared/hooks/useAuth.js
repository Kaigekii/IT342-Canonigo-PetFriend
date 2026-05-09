"use client";

/**
 * Custom Hook: useAuth
 * Manages authentication state and user info
 */

import { useState, useEffect, useCallback } from "react";
import { useRouter } from "next/navigation";
import { apiClient } from "@/shared/utils/api";
import { API_ENDPOINTS } from "@/shared/constants/api";

export const useAuth = () => {
  const router = useRouter();
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  /**
   * Fetch current user info
   */
  const fetchCurrentUser = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const token = localStorage.getItem("token");

      if (!token) {
        setUser(null);
        setIsAuthenticated(false);
        return;
      }

      const userData = await apiClient.get(API_ENDPOINTS.AUTH.ME);
      setUser(userData);
      setIsAuthenticated(true);
    } catch (err) {
      console.error("Failed to fetch user:", err);
      setUser(null);
      setIsAuthenticated(false);
      localStorage.removeItem("token");
    } finally {
      setLoading(false);
    }
  }, []);

  /**
   * Login user
   */
  const login = useCallback(async (email, password) => {
    try {
      setLoading(true);
      setError(null);

      const response = await apiClient.post(API_ENDPOINTS.AUTH.LOGIN, {
        email,
        password,
      });

      if (response.token) {
        localStorage.setItem("token", response.token);
        setUser(response.user || {});
        setIsAuthenticated(true);
        return response;
      }
    } catch (err) {
      const errorMessage = err.message || "Login failed";
      setError(errorMessage);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  /**
   * Register user
   */
  const register = useCallback(async (formData) => {
    try {
      setLoading(true);
      setError(null);

      const response = await apiClient.post(API_ENDPOINTS.AUTH.REGISTER, formData);

      if (response.token) {
        localStorage.setItem("token", response.token);
        setUser(response.user || {});
        setIsAuthenticated(true);
        return response;
      }
    } catch (err) {
      const errorMessage = err.message || "Registration failed";
      setError(errorMessage);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  /**
   * Logout user
   */
  const logout = useCallback(() => {
    localStorage.removeItem("token");
    setUser(null);
    setIsAuthenticated(false);
    router.push("/login");
  }, [router]);

  /**
   * Check if user is logged in on component mount
   */
  useEffect(() => {
    fetchCurrentUser();
  }, [fetchCurrentUser]);

  return {
    user,
    loading,
    error,
    isAuthenticated,
    login,
    register,
    logout,
    refetch: fetchCurrentUser,
  };
};

export default useAuth;
