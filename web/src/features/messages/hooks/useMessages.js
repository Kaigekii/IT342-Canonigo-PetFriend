"use client";

/**
 * Custom Hook: useMessages
 * Manages messaging state and operations
 */

import { useState, useCallback } from "react";
import { messagesApi } from "../api";

export const useMessages = () => {
  const [threads, setThreads] = useState([]);
  const [currentMessages, setCurrentMessages] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const listThreads = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await messagesApi.listThreads();
      setThreads(Array.isArray(data) ? data : []);
      return data;
    } catch (err) {
      setError(err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  const getThreadMessages = useCallback(async (threadId) => {
    try {
      setLoading(true);
      setError(null);
      const data = await messagesApi.getThreadMessages(threadId);
      setCurrentMessages(Array.isArray(data) ? data : []);
      return data;
    } catch (err) {
      setError(err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  const sendMessage = useCallback(async (threadId, content) => {
    try {
      setLoading(true);
      setError(null);
      const newMessage = await messagesApi.sendMessage(threadId, content);
      setCurrentMessages([...currentMessages, newMessage]);
      return newMessage;
    } catch (err) {
      setError(err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  }, [currentMessages]);

  return {
    threads,
    currentMessages,
    loading,
    error,
    listThreads,
    getThreadMessages,
    sendMessage,
  };
};

export default useMessages;
