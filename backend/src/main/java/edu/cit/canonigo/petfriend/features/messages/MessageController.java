package edu.cit.canonigo.petfriend.features.messages;

import edu.cit.canonigo.petfriend.model.User;
import edu.cit.canonigo.petfriend.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for messaging between pet owners and sitters
 */
@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class MessageController {

    private final UserRepository userRepository;
    private final MessageService messageService;

    public MessageController(UserRepository userRepository, MessageService messageService) {
        this.userRepository = userRepository;
        this.messageService = messageService;
    }

    /**
     * List all message threads for authenticated user
     * 
     * @param userDetails Authenticated user
     * @return List of message threads
     */
    @GetMapping("/threads")
    public ResponseEntity<?> listThreads(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getAuthenticatedUser(userDetails);
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        List<MessageDtos.ThreadResponse> threads = messageService.listThreads(user);
        return ResponseEntity.ok(threads);
    }

    /**
     * Create a new message thread or get existing one
     * 
     * @param userDetails Authenticated user
     * @param request Request with other user's ID
     * @return Created or existing thread
     */
    @PostMapping("/threads")
    public ResponseEntity<?> createThread(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody MessageDtos.CreateThreadRequest request
    ) {
        User user = getAuthenticatedUser(userDetails);
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        try {
            MessageDtos.ThreadResponse thread = messageService.createThread(user, request.getOtherUserId());
            return ResponseEntity.ok(thread);
        } catch (IllegalArgumentException e) {
            if ("User not found".equals(e.getMessage())) {
                return ResponseEntity.status(404).body("User not found");
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    /**
     * Get all messages in a thread
     * 
     * @param userDetails Authenticated user
     * @param threadId ID of the message thread
     * @return List of messages
     */
    @GetMapping("/threads/{threadId}/messages")
    public ResponseEntity<?> listMessages(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID threadId
    ) {
        User user = getAuthenticatedUser(userDetails);
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        try {
            List<MessageDtos.MessageResponse> messages = messageService.listMessages(threadId, user);
            return ResponseEntity.ok(messages);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("Thread not found");
        } catch (IllegalAccessError e) {
            return ResponseEntity.status(403).body("Forbidden");
        }
    }

    /**
     * Send a message in a thread
     * 
     * @param userDetails Authenticated user
     * @param threadId ID of the message thread
     * @param request Message content
     * @return Sent message
     */
    @PostMapping("/threads/{threadId}/messages")
    public ResponseEntity<?> sendMessage(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID threadId,
            @Valid @RequestBody MessageDtos.CreateMessageRequest request
    ) {
        User user = getAuthenticatedUser(userDetails);
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        try {
            MessageDtos.MessageResponse message = messageService.sendMessage(threadId, user, request.getContent());
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("Thread not found");
        } catch (IllegalAccessError e) {
            return ResponseEntity.status(403).body("Forbidden");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    // ========== Helper Methods ==========

    private User getAuthenticatedUser(UserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }
        return userRepository.findByEmail(userDetails.getUsername()).orElse(null);
    }
}
