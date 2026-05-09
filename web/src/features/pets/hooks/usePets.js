"use client";

/**
 * Custom Hook: usePets
 * Manages pets state and operations
 */

import { useState, useCallback } from "react";
import { petsApi } from "../api";

export const usePets = () => {
  const [pets, setPets] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const listPets = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await petsApi.listPets();
      setPets(Array.isArray(data) ? data : []);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }, []);

  const createPet = useCallback(async (petData) => {
    try {
      setLoading(true);
      setError(null);
      const newPet = await petsApi.createPet(petData);
      setPets([...pets, newPet]);
      return newPet;
    } catch (err) {
      setError(err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  }, [pets]);

  const deletePet = useCallback(async (petId) => {
    try {
      setLoading(true);
      setError(null);
      await petsApi.deletePet(petId);
      setPets(pets.filter(p => p.petId !== petId));
    } catch (err) {
      setError(err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  }, [pets]);

  return {
    pets,
    loading,
    error,
    listPets,
    createPet,
    deletePet,
  };
};

export default usePets;
