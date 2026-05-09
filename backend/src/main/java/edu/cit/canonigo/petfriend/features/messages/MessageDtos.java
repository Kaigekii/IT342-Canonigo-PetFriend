package edu.cit.canonigo.petfriend.features.messages;

import edu.cit.canonigo.petfriend.model.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

/**
 * Data Transfer Objects for Messaging features
 */
public class MessageDtos {

    /**
     * Request DTO to create a new message thread
     */
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

    /**
     * Request DTO to send a message
     */
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

    /**
     * Response DTO for message thread summary
     */
    public static class ThreadResponse {
        private UUID threadId;
        private UUID otherUserId;
        private String otherUserName;
        private UserRole otherUserRole;
        private String lastMessage;
        private Instant lastMessageAt;

        public ThreadResponse(UUID threadId, UUID otherUserId, String otherUserName, 
                             UserRole otherUserRole, String lastMessage, Instant lastMessageAt) {
            this.threadId = threadId;
            this.otherUserId = otherUserId;
            this.otherUserName = otherUserName;
            this.otherUserRole = otherUserRole;
            this.lastMessage = lastMessage;
            this.lastMessageAt = lastMessageAt;
        }

        public UUID getThreadId() { return threadId; }
        public UUID getOtherUserId() { return otherUserId; }
        public String getOtherUserName() { return otherUserName; }
        public UserRole getOtherUserRole() { return otherUserRole; }
        public String getLastMessage() { return lastMessage; }
        public Instant getLastMessageAt() { return lastMessageAt; }
    }

    /**
     * Response DTO for individual message
     */
    public static class MessageResponse {
        private UUID messageId;
        private UUID threadId;
        private UUID senderId;
        private String senderName;
        private String content;
        private Instant createdAt;

        public MessageResponse(UUID messageId, UUID threadId, UUID senderId, 
                              String senderName, String content, Instant createdAt) {
            this.messageId = messageId;
            this.threadId = threadId;
            this.senderId = senderId;
            this.senderName = senderName;
            this.content = content;
            this.createdAt = createdAt;
        }

        public UUID getMessageId() { return messageId; }
        public UUID getThreadId() { return threadId; }
        public UUID getSenderId() { return senderId; }
        public String getSenderName() { return senderName; }
        public String getContent() { return content; }
        public Instant getCreatedAt() { return createdAt; }
    }
}
