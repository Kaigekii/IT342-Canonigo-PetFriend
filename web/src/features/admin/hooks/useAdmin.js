"use client";

/**
 * Custom Hook: useAdmin
 * Manages admin dashboard and operations
 */

import { useState, useCallback } from "react";
import { adminApi } from "../api";

export const useAdmin = () => {
  const [dashboard, setDashboard] = useState(null);
  const [users, setUsers] = useState([]);
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const getDashboard = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await adminApi.getDashboard();
      setDashboard(data);
      return data;
    } catch (err) {
      setError(err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  const listUsers = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await adminApi.listUsers();
      setUsers(Array.isArray(data) ? data : []);
      return data;
    } catch (err) {
      setError(err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  const listBookings = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await adminApi.listBookings();
      setBookings(Array.isArray(data) ? data : []);
      return data;
    } catch (err) {
      setError(err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  return {
    dashboard,
    users,
    bookings,
    loading,
    error,
    getDashboard,
    listUsers,
    listBookings,
  };
};

export default useAdmin;
