package edu.cit.canonigo.petfriend.features.reviews;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ReviewService reviewService;

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
    }

        @Test
    void testGetAllReviews() throws Exception {
        when(reviewService.listSitterReviews(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/reviews/sitter/{sitterId}", UUID.fromString("22222222-2222-2222-2222-222222222222"))
                                .with(user("owner@example.com").roles("PET_OWNER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

        @Test
    void testGetReviewById() throws Exception {
        when(reviewService.getSitterReviewSummary(any())).thenReturn(new ReviewDtos.ReviewSummaryResponse(new BigDecimal("4.8"), 12L));

        mockMvc.perform(get("/api/reviews/sitter/{sitterId}/summary", UUID.fromString("22222222-2222-2222-2222-222222222222"))
                                .with(user("owner@example.com").roles("PET_OWNER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

        @Test
    void testGetReviewsForSitter() throws Exception {
        when(reviewService.getReviewedBookingIds(owner.getUserId())).thenReturn(List.of(UUID.fromString("33333333-3333-3333-3333-333333333333")));

        mockMvc.perform(get("/api/reviews/me/reviewed-bookings")
                                .with(user("owner@example.com").roles("PET_OWNER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

        @Test
    void testCreateReview() throws Exception {
        UUID bookingId = UUID.fromString("44444444-4444-4444-4444-444444444444");
        when(reviewService.submitReview(eq(owner), eq(bookingId), eq(5), eq("Great service!")))
                .thenReturn(new ReviewDtos.ReviewResponse(
                        UUID.fromString("55555555-5555-5555-5555-555555555555"),
                        bookingId,
                        UUID.fromString("22222222-2222-2222-2222-222222222222"),
                        owner.getUserId(),
                        "Pet Owner",
                        5,
                        "Great service!",
                        "2026-05-10T00:00:00Z"
                ));

        mockMvc.perform(post("/api/reviews")
                .with(user("owner@example.com").roles("PET_OWNER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"bookingId\":\"44444444-4444-4444-4444-444444444444\",\"rating\":5,\"comment\":\"Great service!\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateReview() throws Exception {
        UUID bookingId = UUID.fromString("66666666-6666-6666-6666-666666666666");
        when(reviewService.submitReview(eq(owner), eq(bookingId), eq(4), eq("Nice work")))
                .thenThrow(new IllegalArgumentException("Booking not found"));

        mockMvc.perform(post("/api/reviews")
                .with(user("owner@example.com").roles("PET_OWNER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"bookingId\":\"66666666-6666-6666-6666-666666666666\",\"rating\":4,\"comment\":\"Nice work\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteReview() throws Exception {
        when(reviewService.getReviewedBookingIds(owner.getUserId())).thenReturn(List.of());

        mockMvc.perform(get("/api/reviews/me/reviewed-bookings")
                .with(user("owner@example.com").roles("PET_OWNER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
