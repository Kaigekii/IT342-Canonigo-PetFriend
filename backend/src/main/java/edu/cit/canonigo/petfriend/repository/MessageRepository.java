package edu.cit.canonigo.petfriend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.cit.canonigo.petfriend.model.Message;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByThread_ThreadIdOrderByCreatedAtAsc(UUID threadId);

    Message findTop1ByThread_ThreadIdOrderByCreatedAtDesc(UUID threadId);
}
