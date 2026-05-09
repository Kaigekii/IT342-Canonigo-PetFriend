package edu.cit.canonigo.petfriend.controller;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.canonigo.petfriend.model.Message;
import edu.cit.canonigo.petfriend.model.MessageThread;
import edu.cit.canonigo.petfriend.model.User;
import edu.cit.canonigo.petfriend.model.UserRole;
import edu.cit.canonigo.petfriend.repository.BookingRepository;
import edu.cit.canonigo.petfriend.repository.MessageRepository;
import edu.cit.canonigo.petfriend.repository.MessageThreadRepository;
import edu.cit.canonigo.petfriend.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class MessageController {

    private final MessageThreadRepository messageThreadRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public MessageController(
            MessageThreadRepository messageThreadRepository,
            MessageRepository messageRepository,
            UserRepository userRepository,
            BookingRepository bookingRepository
    ) {
        this.messageThreadRepository = messageThreadRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    @GetMapping("/threads")
    public ResponseEntity<?> listThreads(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getAuthenticatedUser(userDetails);
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        List<MessageThread> threads = messageThreadRepository
                .findByOwner_UserIdOrSitter_UserIdOrderByLastMessageAtDesc(user.getUserId(), user.getUserId());

        List<ThreadResponse> responses = threads.stream()
                .map(thread -> ThreadResponse.from(thread, user, messageRepository))
                .toList();

        return ResponseEntity.ok(responses);
    }

    @PostMapping("/threads")
    public ResponseEntity<?> createThread(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateThreadRequest request
    ) {
        User user = getAuthenticatedUser(userDetails);
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        User otherUser = userRepository.findById(request.getOtherUserId()).orElse(null);
        if (otherUser == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        if (user.getRole() == otherUser.getRole()) {
            return ResponseEntity.badRequest().body("Invalid recipient role");
        }

        User owner = user.getRole() == UserRole.PET_OWNER ? user : otherUser;
        User sitter = user.getRole() == UserRole.PET_SITTER ? user : otherUser;

        if (owner.getRole() != UserRole.PET_OWNER || sitter.getRole() != UserRole.PET_SITTER) {
            return ResponseEntity.badRequest().body("Invalid roles for messaging");
        }

        boolean hasBooking = bookingRepository.existsByOwner_UserIdAndSitter_UserId(owner.getUserId(), sitter.getUserId());
        if (!hasBooking) {
            return ResponseEntity.status(403).body("Messaging is limited to existing bookings");
        }

        MessageThread existing = messageThreadRepository.findByOwner_UserIdAndSitter_UserId(owner.getUserId(), sitter.getUserId());
        if (existing != null) {
            return ResponseEntity.ok(ThreadResponse.from(existing, user, messageRepository));
        }

        MessageThread thread = new MessageThread();
        thread.setOwner(owner);
        thread.setSitter(sitter);
        thread.setLastMessageAt(Instant.now());

        MessageThread saved = messageThreadRepository.save(thread);
        return ResponseEntity.ok(ThreadResponse.from(saved, user, messageRepository));
    }

    @GetMapping("/threads/{threadId}/messages")
    public ResponseEntity<?> listMessages(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID threadId
    ) {
        User user = getAuthenticatedUser(userDetails);
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        MessageThread thread = messageThreadRepository.findById(threadId).orElse(null);
        if (thread == null) {
            return ResponseEntity.status(404).body("Thread not found");
        }

        if (!isParticipant(thread, user)) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        List<MessageResponse> responses = messageRepository
                .findByThread_ThreadIdOrderByCreatedAtAsc(threadId)
                .stream()
                .map(MessageResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @PostMapping("/threads/{threadId}/messages")
    public ResponseEntity<?> sendMessage(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID threadId,
            @Valid @RequestBody CreateMessageRequest request
    ) {
        User user = getAuthenticatedUser(userDetails);
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        MessageThread thread = messageThreadRepository.findById(threadId).orElse(null);
        if (thread == null) {
            return ResponseEntity.status(404).body("Thread not found");
        }

        if (!isParticipant(thread, user)) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        boolean hasBooking = bookingRepository.existsByOwner_UserIdAndSitter_UserId(
                thread.getOwner().getUserId(),
                thread.getSitter().getUserId()
        );
        if (!hasBooking) {
            return ResponseEntity.status(403).body("Messaging is limited to existing bookings");
        }

        Message message = new Message();
        message.setThread(thread);
        message.setSender(user);
        message.setContent(request.getContent().trim());

        Message saved = messageRepository.save(message);

        thread.setLastMessageAt(Instant.now());
        messageThreadRepository.save(thread);

        return ResponseEntity.ok(MessageResponse.from(saved));
    }

    private User getAuthenticatedUser(UserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }
        return userRepository.findByEmail(userDetails.getUsername()).orElse(null);
    }

    private boolean isParticipant(MessageThread thread, User user) {
        return thread.getOwner().getUserId().equals(user.getUserId())
                || thread.getSitter().getUserId().equals(user.getUserId());
    }

    public static class CreateThreadRequest {
        @NotNull
        private UUID otherUserId;

        public UUID getOtherUserId() {
            return otherUserId;
        }

        public void setOtherUserId(UUID otherUserId) {
            this.otherUserId = otherUserId;
        }
    }

    public static class CreateMessageRequest {
        @NotBlank
        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public static class ThreadResponse {
        private UUID threadId;
        private UUID otherUserId;
        private String otherUserName;
        private UserRole otherUserRole;
        private String lastMessage;
        private Instant lastMessageAt;

        public static ThreadResponse from(MessageThread thread, User viewer, MessageRepository messageRepository) {
            ThreadResponse response = new ThreadResponse();
            response.threadId = thread.getThreadId();

            User otherUser = thread.getOwner().getUserId().equals(viewer.getUserId())
                    ? thread.getSitter()
                    : thread.getOwner();

            response.otherUserId = otherUser.getUserId();
            response.otherUserName = otherUser.getFirstName() + " " + otherUser.getLastName();
            response.otherUserRole = otherUser.getRole();

            Message last = messageRepository.findTop1ByThread_ThreadIdOrderByCreatedAtDesc(thread.getThreadId());
            response.lastMessage = last != null ? last.getContent() : "";
            response.lastMessageAt = last != null ? last.getCreatedAt() : thread.getLastMessageAt();

            return response;
        }

        public UUID getThreadId() {
            return threadId;
        }

        public UUID getOtherUserId() {
            return otherUserId;
        }

        public String getOtherUserName() {
            return otherUserName;
        }

        public UserRole getOtherUserRole() {
            return otherUserRole;
        }

        public String getLastMessage() {
            return lastMessage;
        }

        public Instant getLastMessageAt() {
            return lastMessageAt;
        }
    }

    public static class MessageResponse {
        private UUID messageId;
        private UUID threadId;
        private UUID senderId;
        private String senderName;
        private String content;
        private Instant createdAt;

        public static MessageResponse from(Message message) {
            MessageResponse response = new MessageResponse();
            response.messageId = message.getMessageId();
            response.threadId = message.getThread().getThreadId();
            response.senderId = message.getSender().getUserId();
            response.senderName = message.getSender().getFirstName() + " " + message.getSender().getLastName();
            response.content = message.getContent();
            response.createdAt = message.getCreatedAt();
            return response;
        }

        public UUID getMessageId() {
            return messageId;
        }

        public UUID getThreadId() {
            return threadId;
        }

        public UUID getSenderId() {
            return senderId;
        }

        public String getSenderName() {
            return senderName;
        }

        public String getContent() {
            return content;
        }

        public Instant getCreatedAt() {
            return createdAt;
        }
    }
}
