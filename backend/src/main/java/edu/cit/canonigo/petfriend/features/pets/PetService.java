package edu.cit.canonigo.petfriend.features.pets;

import edu.cit.canonigo.petfriend.model.Pet;
import edu.cit.canonigo.petfriend.model.User;
import edu.cit.canonigo.petfriend.model.UserRole;
import edu.cit.canonigo.petfriend.repository.PetRepository;
import edu.cit.canonigo.petfriend.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

/**
 * Service layer for pet management.
 * Handles CRUD operations for pets.
 */
@Service
public class PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;

    public PetService(PetRepository petRepository, UserRepository userRepository) {
        this.petRepository = petRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get all pets for the authenticated owner.
     */
    public List<Pet> getOwnerPets(UUID ownerId) {
        return petRepository.findByOwner_UserIdOrderByCreatedAtDesc(ownerId);
    }

    /**
     * Create a new pet.
     */
    public Pet createPet(UUID ownerId, PetDtos.CreatePetRequest request) throws PetException {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new PetException("Owner not found"));

        if (owner.getRole() != UserRole.PET_OWNER) {
            throw new PetException("Only pet owners can create pets");
        }

        Pet pet = new Pet(
            owner,
            request.getName(),
            request.getBreed(),
            request.getAge(),
            request.getWeight(),
            request.getSpecies(),
            request.getSpecialNeeds(),
            request.getVaccinationStatus(),
            request.getPhotoUrl()
        );

        return petRepository.save(pet);
    }

    /**
     * Update an existing pet.
     */
    public Pet updatePet(UUID ownerId, UUID petId, PetDtos.CreatePetRequest request) throws PetException {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new PetException("Owner not found"));

        if (owner.getRole() != UserRole.PET_OWNER) {
            throw new PetException("Only pet owners can update pets");
        }

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new PetException("Pet not found"));

        if (!pet.getOwner().getUserId().equals(owner.getUserId())) {
            throw new PetException("You cannot update a pet that doesn't belong to you");
        }

        pet.setName(request.getName());
        pet.setBreed(request.getBreed());
        pet.setAge(request.getAge());
        pet.setWeight(request.getWeight());
        pet.setSpecies(request.getSpecies());
        pet.setSpecialNeeds(request.getSpecialNeeds());
        pet.setVaccinationStatus(request.getVaccinationStatus());
        pet.setPhotoUrl(request.getPhotoUrl());

        return petRepository.save(pet);
    }

    /**
     * Delete a pet.
     */
    public void deletePet(UUID ownerId, UUID petId) throws PetException {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new PetException("Owner not found"));

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new PetException("Pet not found"));

        if (!pet.getOwner().getUserId().equals(owner.getUserId())) {
            throw new PetException("You cannot delete a pet that doesn't belong to you");
        }

        petRepository.delete(pet);
    }

    /**
     * Custom exception for pet-related errors.
     */
    public static class PetException extends Exception {
        public PetException(String message) {
            super(message);
        }
    }
}
