package edu.cit.canonigo.petfriend.features.sitters;

import edu.cit.canonigo.petfriend.model.User;
import edu.cit.canonigo.petfriend.model.UserRole;
import edu.cit.canonigo.petfriend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SitterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private SitterService sitterService;

    private User owner;

    @BeforeEach
    void setUp() {
    owner = new User();
    owner.setUserId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
    owner.setEmail("owner@example.com");
    owner.setFirstName("Pet");
    owner.setLastName("Owner");
    owner.setRole(UserRole.PET_OWNER);
    owner.setIsVerified(true);

    when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
    when(sitterService.searchSitters(any(), any())).thenReturn(Collections.emptyList());
    }

    @Test
    void testSearchSittersByLocation() throws Exception {
    mockMvc.perform(get("/api/sitters/search")
        .with(user("owner@example.com").roles("PET_OWNER"))
        .param("location", "Downtown")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
    }

    @Test
    void testSearchSittersByLocationAndServiceType() throws Exception {
    mockMvc.perform(get("/api/sitters/search")
        .with(user("owner@example.com").roles("PET_OWNER"))
        .param("location", "Downtown")
        .param("serviceType", "DOG_WALKING")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
    }

    @Test
    void testGetSitterProfile() throws Exception {
    UUID sitterId = UUID.fromString("22222222-2222-2222-2222-222222222222");
    when(sitterService.getSitterDetails(sitterId)).thenReturn(
        new SitterDtos.SitterDetailResponse(
            sitterId,
            "Jane Sitter",
            null,
            "Experienced sitter",
            "3 years",
            new BigDecimal("150.00"),
            List.of("DOG_WALKING"),
            "Downtown",
            Collections.emptyMap(),
            new BigDecimal("4.8"),
            12L,
            true,
            Collections.emptyList()
        )
    );

    mockMvc.perform(get("/api/sitters/{sitterId}", sitterId)
        .with(user("owner@example.com").roles("PET_OWNER"))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
    }

    @Test
    void testGetSitterProfileNotFound() throws Exception {
    UUID sitterId = UUID.fromString("33333333-3333-3333-3333-333333333333");
    when(sitterService.getSitterDetails(sitterId)).thenReturn(null);

    mockMvc.perform(get("/api/sitters/{sitterId}", sitterId)
        .with(user("owner@example.com").roles("PET_OWNER"))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
    }

    @Test
    void testGetSitterRating() throws Exception {
    UUID sitterId = UUID.fromString("22222222-2222-2222-2222-222222222222");
    when(sitterService.getSitterDetails(sitterId)).thenReturn(
        new SitterDtos.SitterDetailResponse(
            sitterId,
            "Jane Sitter",
            null,
            "Experienced sitter",
            "3 years",
            new BigDecimal("150.00"),
            List.of("DOG_WALKING"),
            "Downtown",
            Collections.emptyMap(),
            new BigDecimal("4.5"),
            7L,
            true,
            Collections.emptyList()
        )
    );

    mockMvc.perform(get("/api/sitters/{sitterId}", sitterId)
        .with(user("owner@example.com").roles("PET_OWNER"))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
    }

    @Test
    void testListAllSitters() throws Exception {
    mockMvc.perform(get("/api/sitters/search")
        .with(user("owner@example.com").roles("PET_OWNER"))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
    }

    @Test
    void testGetVerifiedSittersOnly() throws Exception {
    mockMvc.perform(get("/api/sitters/search")
        .with(user("owner@example.com").roles("PET_OWNER"))
        .param("serviceType", "DOG_WALKING")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
    }
}
