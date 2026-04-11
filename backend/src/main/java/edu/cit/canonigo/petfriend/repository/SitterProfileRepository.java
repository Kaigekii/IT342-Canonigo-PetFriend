package edu.cit.canonigo.petfriend.repository;

import edu.cit.canonigo.petfriend.model.SitterProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SitterProfileRepository extends JpaRepository<SitterProfile, UUID> {
    Optional<SitterProfile> findByUser_UserId(UUID userId);
}
