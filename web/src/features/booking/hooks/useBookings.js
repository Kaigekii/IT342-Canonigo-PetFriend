"use client";

/**
 * Custom Hook: useBookings
 * Manages bookings state and operations
 */

import { useState, useCallback } from "react";
import { bookingsApi } from "../api";

export const useBookings = () => {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const listBookings = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await bookingsApi.listBookings();
      setBookings(Array.isArray(data) ? data : []);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }, []);

  const createBooking = useCallback(async (bookingData) => {
    try {
      setLoading(true);
      setError(null);
      const newBooking = await bookingsApi.createBooking(bookingData);
      setBookings([...bookings, newBooking]);
      return newBooking;
    } catch (err) {
      setError(err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  }, [bookings]);

  const cancelBooking = useCallback(async (bookingId) => {
    try {
      setLoading(true);
      setError(null);
      const updated = await bookingsApi.cancelBooking(bookingId);
      setBookings(bookings.map(b => b.bookingId === bookingId ? updated : b));
      return updated;
    } catch (err) {
      setError(err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  }, [bookings]);

  return {
    bookings,
    loading,
    error,
    listBookings,
    createBooking,
    cancelBooking,
  };
};

export default useBookings;
