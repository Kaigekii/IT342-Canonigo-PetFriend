package edu.cit.canonigo.petfriend.repository;

import edu.cit.canonigo.petfriend.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PetRepository extends JpaRepository<Pet, UUID> {
    List<Pet> findByOwner_UserIdOrderByCreatedAtDesc(UUID ownerId);
}
