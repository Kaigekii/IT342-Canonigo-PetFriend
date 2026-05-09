"use client";

/**
 * Custom Hook: useSitters
 * Manages sitters search and details
 */

import { useState, useCallback } from "react";
import { sittersApi } from "../api";

export const useSitters = () => {
  const [sitters, setSitters] = useState([]);
  const [sitterDetails, setSitterDetails] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const searchSitters = useCallback(async (location = "", serviceType = "") => {
    try {
      setLoading(true);
      setError(null);
      const data = await sittersApi.searchSitters(location, serviceType);
      setSitters(Array.isArray(data) ? data : []);
      return data;
    } catch (err) {
      setError(err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  const getSitterDetails = useCallback(async (sitterId) => {
    try {
      setLoading(true);
      setError(null);
      const data = await sittersApi.getSitterDetails(sitterId);
      setSitterDetails(data);
      return data;
    } catch (err) {
      setError(err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  return {
    sitters,
    sitterDetails,
    loading,
    error,
    searchSitters,
    getSitterDetails,
  };
};

export default useSitters;
