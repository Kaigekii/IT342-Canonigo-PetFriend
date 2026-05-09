package edu.cit.canonigo.petfriend.features.messages;

import edu.cit.canonigo.petfriend.model.Message;
import edu.cit.canonigo.petfriend.model.MessageThread;
import edu.cit.canonigo.petfriend.model.User;
import edu.cit.canonigo.petfriend.model.UserRole;
import edu.cit.canonigo.petfriend.repository.BookingRepository;
import edu.cit.canonigo.petfriend.repository.MessageRepository;
import edu.cit.canonigo.petfriend.repository.MessageThreadRepository;
import edu.cit.canonigo.petfriend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Service for messaging business logic
 */
@Service
public class MessageService {

    private final MessageThreadRepository messageThreadRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public MessageService(MessageThreadRepository messageThreadRepository,
                         MessageRepository messageRepository,
                         UserRepository userRepository,
                         BookingRepository bookingRepository) {
        this.messageThreadRepository = messageThreadRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    /**
     * Get all message threads for a user
     */
    public List<MessageDtos.ThreadResponse> listThreads(User user) {
        List<MessageThread> threads = messageThreadRepository
                .findByOwner_UserIdOrSitter_UserIdOrderByLastMessageAtDesc(user.getUserId(), user.getUserId());

        return threads.stream()
                .map(thread -> toThreadResponse(thread, user))
                .toList();
    }

    /**
     * Create or get existing message thread between user and another user
     */
    public MessageDtos.ThreadResponse createThread(User user, UUID otherUserId) {
        User otherUser = userRepository.findById(otherUserId).orElse(null);
        if (otherUser == null) {
            throw new IllegalArgumentException("User not found");
        }

        // Verify roles are different (owner and sitter)
        if (user.getRole() == otherUser.getRole()) {
            throw new IllegalArgumentException("Invalid recipient role");
        }

        User owner = user.getRole() == UserRole.PET_OWNER ? user : otherUser;
        User sitter = user.getRole() == UserRole.PET_SITTER ? user : otherUser;

        if (owner.getRole() != UserRole.PET_OWNER || sitter.getRole() != UserRole.PET_SITTER) {
            throw new IllegalArgumentException("Invalid roles for messaging");
        }

        // Verify a booking exists between owner and sitter
        boolean hasBooking = bookingRepository.existsByOwner_UserIdAndSitter_UserId(
                owner.getUserId(), sitter.getUserId());
        if (!hasBooking) {
            throw new IllegalStateException("Messaging is limited to existing bookings");
        }

        // Check if thread already exists
        MessageThread existing = messageThreadRepository.findByOwner_UserIdAndSitter_UserId(
                owner.getUserId(), sitter.getUserId());
        if (existing != null) {
            return toThreadResponse(existing, user);
        }

        // Create new thread
        MessageThread thread = new MessageThread();
        thread.setOwner(owner);
        thread.setSitter(sitter);
        thread.setLastMessageAt(Instant.now());

        MessageThread saved = messageThreadRepository.save(thread);
        return toThreadResponse(saved, user);
    }

    /**
     * Get all messages in a thread
     */
    public List<MessageDtos.MessageResponse> listMessages(UUID threadId, User user) {
        MessageThread thread = messageThreadRepository.findById(threadId).orElse(null);
        if (thread == null) {
            throw new IllegalArgumentException("Thread not found");
        }

        if (!isParticipant(thread, user)) {
            throw new IllegalAccessError("Forbidden");
        }

        return messageRepository.findByThread_ThreadIdOrderByCreatedAtAsc(threadId)
                .stream()
                .map(this::toMessageResponse)
                .toList();
    }

    /**
     * Send a message in a thread
     */
    public MessageDtos.MessageResponse sendMessage(UUID threadId, User sender, String content) {
        MessageThread thread = messageThreadRepository.findById(threadId).orElse(null);
        if (thread == null) {
            throw new IllegalArgumentException("Thread not found");
        }

        if (!isParticipant(thread, sender)) {
            throw new IllegalAccessError("Forbidden");
        }

        // Verify booking still exists
        boolean hasBooking = bookingRepository.existsByOwner_UserIdAndSitter_UserId(
                thread.getOwner().getUserId(), thread.getSitter().getUserId());
        if (!hasBooking) {
            throw new IllegalStateException("Messaging is limited to existing bookings");
        }

        Message message = new Message();
        message.setThread(thread);
        message.setSender(sender);
        message.setContent(content.trim());

        Message saved = messageRepository.save(message);

        // Update thread's last message timestamp
        thread.setLastMessageAt(Instant.now());
        messageThreadRepository.save(thread);

        return toMessageResponse(saved);
    }

    // ========== Helper Methods ==========

    private MessageDtos.ThreadResponse toThreadResponse(MessageThread thread, User viewer) {
        User otherUser = thread.getOwner().getUserId().equals(viewer.getUserId())
                ? thread.getSitter()
                : thread.getOwner();

        Message lastMsg = messageRepository.findTop1ByThread_ThreadIdOrderByCreatedAtDesc(thread.getThreadId());
        String lastMessage = lastMsg != null ? lastMsg.getContent() : "";
        Instant lastMessageAt = lastMsg != null ? lastMsg.getCreatedAt() : thread.getLastMessageAt();

        return new MessageDtos.ThreadResponse(
                thread.getThreadId(),
                otherUser.getUserId(),
                otherUser.getFirstName() + " " + otherUser.getLastName(),
                otherUser.getRole(),
                lastMessage,
                lastMessageAt
        );
    }

    private MessageDtos.MessageResponse toMessageResponse(Message message) {
        return new MessageDtos.MessageResponse(
                message.getMessageId(),
                message.getThread().getThreadId(),
                message.getSender().getUserId(),
                message.getSender().getFirstName() + " " + message.getSender().getLastName(),
                message.getContent(),
                message.getCreatedAt()
        );
    }

    private boolean isParticipant(MessageThread thread, User user) {
        return thread.getOwner().getUserId().equals(user.getUserId())
                || thread.getSitter().getUserId().equals(user.getUserId());
    }
}
