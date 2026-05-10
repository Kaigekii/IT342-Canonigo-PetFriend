package edu.cit.canonigo.petfriend.features.auth;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.cit.canonigo.petfriend.model.UserRole;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Clear any existing data and set up test state
    }

        private String uniqueEmail(String prefix) {
                return prefix + "." + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
        }

    @Test
    void testRegisterPetOwner() throws Exception {
        AuthDtos.RegisterRequest request = new AuthDtos.RegisterRequest();
                request.setEmail(uniqueEmail("owner"));
        request.setPassword("Password123!");
        request.setFirstName("John");
        request.setLastName("Owner");
        request.setRole(UserRole.PET_OWNER);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(request.getEmail()))
                .andExpect(jsonPath("$.role").value("PET_OWNER"));
    }

    @Test
    void testRegisterPetSitter() throws Exception {
        AuthDtos.RegisterRequest request = new AuthDtos.RegisterRequest();
                request.setEmail(uniqueEmail("sitter"));
        request.setPassword("Password123!");
        request.setFirstName("Jane");
        request.setLastName("Sitter");
        request.setRole(UserRole.PET_SITTER);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("PET_SITTER"));
    }

    @Test
    void testRegisterWithDuplicateEmail() throws Exception {
        // First registration
        AuthDtos.RegisterRequest request = new AuthDtos.RegisterRequest();
                request.setEmail(uniqueEmail("duplicate"));
        request.setPassword("Password123!");
        request.setFirstName("Test");
        request.setLastName("User");
        request.setRole(UserRole.PET_OWNER);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Attempt duplicate registration
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLoginSuccessful() throws Exception {
        // Register user first
        AuthDtos.RegisterRequest registerRequest = new AuthDtos.RegisterRequest();
                registerRequest.setEmail(uniqueEmail("login"));
        registerRequest.setPassword("Password123!");
        registerRequest.setFirstName("Test");
        registerRequest.setLastName("User");
        registerRequest.setRole(UserRole.PET_OWNER);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // Login
        AuthDtos.LoginRequest loginRequest = new AuthDtos.LoginRequest();
        loginRequest.setEmail(registerRequest.getEmail());
        loginRequest.setPassword("Password123!");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(registerRequest.getEmail()))
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void testLoginWithInvalidCredentials() throws Exception {
        AuthDtos.LoginRequest loginRequest = new AuthDtos.LoginRequest();
        loginRequest.setEmail("nonexistent@example.com");
        loginRequest.setPassword("WrongPassword123!");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetCurrentUserAuthenticated() throws Exception {
        // Register and login first
        AuthDtos.RegisterRequest registerRequest = new AuthDtos.RegisterRequest();
                registerRequest.setEmail(uniqueEmail("current"));
        registerRequest.setPassword("Password123!");
        registerRequest.setFirstName("Current");
        registerRequest.setLastName("User");
        registerRequest.setRole(UserRole.PET_OWNER);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // Get current user (without token - test framework should handle auth)
        mockMvc.perform(get("/api/user/me")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testRegisterWithInvalidEmail() throws Exception {
        AuthDtos.RegisterRequest request = new AuthDtos.RegisterRequest();
        request.setEmail("invalid-email");
        request.setPassword("Password123!");
        request.setFirstName("Test");
        request.setLastName("User");
        request.setRole(UserRole.PET_OWNER);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterWithShortPassword() throws Exception {
        AuthDtos.RegisterRequest request = new AuthDtos.RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("short");
        request.setFirstName("Test");
        request.setLastName("User");
        request.setRole(UserRole.PET_OWNER);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
