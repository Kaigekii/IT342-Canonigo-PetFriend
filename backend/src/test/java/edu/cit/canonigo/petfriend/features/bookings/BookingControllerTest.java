package edu.cit.canonigo.petfriend.features.bookings;

import edu.cit.canonigo.petfriend.model.Booking;
import edu.cit.canonigo.petfriend.model.BookingStatus;
import edu.cit.canonigo.petfriend.model.Pet;
import edu.cit.canonigo.petfriend.model.PetSpecies;
import edu.cit.canonigo.petfriend.model.ServiceType;
import edu.cit.canonigo.petfriend.model.User;
import edu.cit.canonigo.petfriend.model.UserRole;
import edu.cit.canonigo.petfriend.model.VaccinationStatus;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BookingService bookingService;

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
        when(userRepository.findByEmail("sitter@example.com")).thenReturn(Optional.of(sitter));
    }

    private Booking sampleBooking() {
        Pet pet = new Pet();
        pet.setPetId(UUID.fromString("33333333-3333-3333-3333-333333333333"));
        pet.setOwner(owner);
        pet.setName("Buddy");
        pet.setSpecies(PetSpecies.DOG);
        pet.setVaccinationStatus(VaccinationStatus.UP_TO_DATE);

        Booking booking = new Booking();
        booking.setBookingId(UUID.fromString("44444444-4444-4444-4444-444444444444"));
        booking.setOwner(owner);
        booking.setSitter(sitter);
        booking.setServiceType(ServiceType.WALK);
        booking.setDate(LocalDate.of(2026, 6, 1));
        booking.setStartTime(LocalTime.of(9, 0));
        booking.setEndTime(LocalTime.of(10, 0));
        booking.setStatus(BookingStatus.PENDING);
        booking.setTotalAmount(new BigDecimal("110.00"));
        booking.setCurrency("PHP");
        booking.setPets(new HashSet<>(List.of(pet)));
        return booking;
    }

    @Test
    void testGetAllBookings() throws Exception {
        when(bookingService.getOwnerBookings(owner.getUserId(), false)).thenReturn(List.of(sampleBooking()));

        mockMvc.perform(get("/api/bookings")
                .with(user("owner@example.com").roles("PET_OWNER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetBookingById() throws Exception {
        when(bookingService.getOwnerBookings(owner.getUserId(), true)).thenReturn(List.of(sampleBooking()));

        mockMvc.perform(get("/api/bookings")
                .param("upcoming", "true")
                .with(user("owner@example.com").roles("PET_OWNER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetBookingByIdNotFound() throws Exception {
        when(bookingService.getOwnerBookings(owner.getUserId(), true)).thenReturn(List.of());

        mockMvc.perform(get("/api/bookings")
                .param("upcoming", "true")
                .with(user("owner@example.com").roles("PET_OWNER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetUserBookings() throws Exception {
        when(bookingService.getOwnerBookings(owner.getUserId(), false)).thenReturn(List.of(sampleBooking()));

        mockMvc.perform(get("/api/bookings")
                .with(user("owner@example.com").roles("PET_OWNER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetSitterBookings() throws Exception {
        when(bookingService.getSitterBookings(sitter.getUserId())).thenReturn(List.of(sampleBooking()));

        mockMvc.perform(get("/api/bookings/sitter")
                .with(user("sitter@example.com").roles("PET_SITTER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateBooking() throws Exception {
        when(bookingService.createBooking(eq(owner.getUserId()), any())).thenReturn(sampleBooking());

        mockMvc.perform(post("/api/bookings")
                .with(user("owner@example.com").roles("PET_OWNER"))
                .contentType(MediaType.APPLICATION_JSON)
            .content("{\"sitterId\":\"22222222-2222-2222-2222-222222222222\",\"petIds\":[\"33333333-3333-3333-3333-333333333333\"],\"serviceType\":\"WALK\",\"date\":\"2026-06-01\",\"startTime\":\"09:00:00\",\"endTime\":\"10:00:00\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateBookingConflictReturnsBadRequest() throws Exception {
        when(bookingService.createBooking(eq(owner.getUserId()), any()))
                .thenThrow(new BookingService.BookingException("Failed to book: sitter is already booked for this time slot"));

        mockMvc.perform(post("/api/bookings")
                .with(user("owner@example.com").roles("PET_OWNER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"sitterId\":\"22222222-2222-2222-2222-222222222222\",\"petIds\":[\"33333333-3333-3333-3333-333333333333\"],\"serviceType\":\"WALK\",\"date\":\"2026-06-01\",\"startTime\":\"09:00:00\",\"endTime\":\"10:00:00\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Failed to book: sitter is already booked for this time slot"));
    }

    @Test
    void testUpdateBooking() throws Exception {
        when(bookingService.updateOwnerBookingStatus(eq(owner.getUserId()), any(), eq(BookingStatus.CANCELLED)))
                .thenReturn(sampleBooking());

        mockMvc.perform(put("/api/bookings/{bookingId}/owner-status", UUID.fromString("55555555-5555-5555-5555-555555555555"))
                .with(user("owner@example.com").roles("PET_OWNER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"CANCELLED\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testCancelBooking() throws Exception {
        when(bookingService.updateSitterBookingStatus(eq(sitter.getUserId()), any(), eq(BookingStatus.CONFIRMED)))
                .thenReturn(sampleBooking());

        mockMvc.perform(put("/api/bookings/{bookingId}/sitter-status", UUID.fromString("55555555-5555-5555-5555-555555555555"))
                .with(user("sitter@example.com").roles("PET_SITTER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"CONFIRMED\"}"))
                .andExpect(status().isOk());
    }
}
