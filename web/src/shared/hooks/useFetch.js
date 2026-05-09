"use client";

/**
 * Custom Hook: useFetch
 * Wrapper around fetch with loading, error, and data states
 */

import { useState, useEffect, useCallback } from "react";
import { apiClient } from "@/shared/utils/api";

export const useFetch = (url, options = {}) => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchData = useCallback(async () => {
    if (!url) return;
    
    try {
      setLoading(true);
      setError(null);
      const result = await apiClient.get(url);
      setData(result);
    } catch (err) {
      setError(err.message || "Failed to fetch data");
    } finally {
      setLoading(false);
    }
  }, [url]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  return { data, loading, error, refetch: fetchData };
};

/**
 * Custom Hook: useApi
 * Generic hook for making API requests with manual trigger
 */

export const useApi = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const execute = useCallback(async (method, url, payload = null) => {
    try {
      setLoading(true);
      setError(null);
      let result;

      if (method === "GET") {
        result = await apiClient.get(url);
      } else if (method === "POST") {
        result = await apiClient.post(url, payload);
      } else if (method === "PUT") {
        result = await apiClient.put(url, payload);
      } else if (method === "DELETE") {
        result = await apiClient.delete(url);
      } else {
        throw new Error(`Unsupported method: ${method}`);
      }

      setData(result);
      return result;
    } catch (err) {
      const errorMessage = err.message || "An error occurred";
      setError(errorMessage);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  return { data, loading, error, execute };
};

export default useFetch;
