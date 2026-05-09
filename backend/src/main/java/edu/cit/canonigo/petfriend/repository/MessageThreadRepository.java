package edu.cit.canonigo.petfriend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.cit.canonigo.petfriend.model.MessageThread;

public interface MessageThreadRepository extends JpaRepository<MessageThread, UUID> {
    List<MessageThread> findByOwner_UserIdOrSitter_UserIdOrderByLastMessageAtDesc(UUID ownerId, UUID sitterId);

    MessageThread findByOwner_UserIdAndSitter_UserId(UUID ownerId, UUID sitterId);
}
