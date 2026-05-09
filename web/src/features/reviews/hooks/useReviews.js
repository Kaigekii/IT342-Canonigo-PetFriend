"use client";

/**
 * Custom Hook: useReviews
 * Manages reviews state and operations
 */

import { useState, useCallback } from "react";
import { reviewsApi } from "../api";

export const useReviews = () => {
  const [reviews, setReviews] = useState([]);
  const [reviewSummary, setReviewSummary] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const getSitterReviews = useCallback(async (sitterId) => {
    try {
      setLoading(true);
      setError(null);
      const data = await reviewsApi.getSitterReviews(sitterId);
      setReviews(Array.isArray(data) ? data : []);
      return data;
    } catch (err) {
      setError(err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  const getSitterReviewSummary = useCallback(async (sitterId) => {
    try {
      setLoading(true);
      setError(null);
      const data = await reviewsApi.getSitterReviewSummary(sitterId);
      setReviewSummary(data);
      return data;
    } catch (err) {
      setError(err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  const submitReview = useCallback(async (bookingId, rating, comment) => {
    try {
      setLoading(true);
      setError(null);
      const newReview = await reviewsApi.submitReview(bookingId, rating, comment);
      setReviews([...reviews, newReview]);
      return newReview;
    } catch (err) {
      setError(err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  }, [reviews]);

  return {
    reviews,
    reviewSummary,
    loading,
    error,
    getSitterReviews,
    getSitterReviewSummary,
    submitReview,
  };
};

export default useReviews;
