package edu.cit.canonigo.petfriend.features.messages;

import edu.cit.canonigo.petfriend.model.User;
import edu.cit.canonigo.petfriend.model.UserRole;
import edu.cit.canonigo.petfriend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private MessageService messageService;

    private User owner;
    private User sitter;

    @BeforeEach
    void setUp() {
    owner = new User();
    owner.setUserId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
    owner.setEmail("owner@example.com");
    owner.setFirstName("Pet");
    owner.setLastName("Owner");
    owner.setRole(UserRole.PET_OWNER);
    owner.setIsVerified(true);

    sitter = new User();
    sitter.setUserId(UUID.fromString("22222222-2222-2222-2222-222222222222"));
    sitter.setEmail("sitter@example.com");
    sitter.setFirstName("Pet");
    sitter.setLastName("Sitter");
    sitter.setRole(UserRole.PET_SITTER);
    sitter.setIsVerified(true);

    when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
    when(userRepository.findById(sitter.getUserId())).thenReturn(Optional.of(sitter));
    }

    @Test
    @WithMockUser(username = "owner@example.com")
    void testGetAllMessages() throws Exception {
    when(messageService.listThreads(owner)).thenReturn(List.of());

    mockMvc.perform(get("/api/messages/threads")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "owner@example.com")
    void testGetConversation() throws Exception {
    UUID threadId = UUID.fromString("33333333-3333-3333-3333-333333333333");
    when(messageService.listMessages(eq(threadId), eq(owner))).thenReturn(List.of());

    mockMvc.perform(get("/api/messages/threads/{threadId}/messages", threadId)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "owner@example.com")
    void testSendMessage() throws Exception {
    UUID threadId = UUID.fromString("33333333-3333-3333-3333-333333333333");
    when(messageService.sendMessage(eq(threadId), eq(owner), eq("Hello!")))
        .thenReturn(new MessageDtos.MessageResponse(
            UUID.fromString("44444444-4444-4444-4444-444444444444"),
            threadId,
            owner.getUserId(),
            "Pet Owner",
            "Hello!",
            Instant.parse("2026-05-10T00:00:00Z")
        ));

    mockMvc.perform(post("/api/messages/threads/{threadId}/messages", threadId)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"content\":\"Hello!\"}"))
        .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "owner@example.com")
    void testGetUserConversations() throws Exception {
    UUID otherUserId = sitter.getUserId();
    when(messageService.createThread(eq(owner), eq(otherUserId)))
        .thenReturn(new MessageDtos.ThreadResponse(
            UUID.fromString("55555555-5555-5555-5555-555555555555"),
            otherUserId,
            "Pet Sitter",
            UserRole.PET_SITTER,
            "",
            Instant.parse("2026-05-10T00:00:00Z")
        ));

    mockMvc.perform(post("/api/messages/threads")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"otherUserId\":\"22222222-2222-2222-2222-222222222222\"}"))
        .andExpect(status().isOk());
    }
}
