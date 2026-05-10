package edu.cit.canonigo.petfriend.features.bookings;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.cit.canonigo.petfriend.model.Pet;
import edu.cit.canonigo.petfriend.model.ServiceType;
import edu.cit.canonigo.petfriend.model.User;
import edu.cit.canonigo.petfriend.model.UserRole;
import edu.cit.canonigo.petfriend.repository.BookingRepository;
import edu.cit.canonigo.petfriend.repository.PetRepository;
import edu.cit.canonigo.petfriend.repository.SitterProfileRepository;
import edu.cit.canonigo.petfriend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private SitterProfileRepository sitterProfileRepository;

    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        bookingService = new BookingService(bookingRepository, userRepository, petRepository, sitterProfileRepository);
    }

    @Test
    void createBookingShouldFailWhenSitterAlreadyHasOverlappingBooking() {
        UUID ownerId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID sitterId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID petId = UUID.fromString("33333333-3333-3333-3333-333333333333");

        User owner = new User();
        owner.setUserId(ownerId);
        owner.setRole(UserRole.PET_OWNER);

        User sitter = new User();
        sitter.setUserId(sitterId);
        sitter.setRole(UserRole.PET_SITTER);

        Pet pet = new Pet();
        pet.setPetId(petId);
        pet.setOwner(owner);

        BookingDtos.CreateBookingRequest request = new BookingDtos.CreateBookingRequest();
        request.setSitterId(sitterId);
        request.setPetIds(List.of(petId));
        request.setServiceType(ServiceType.WALK);
        request.setDate(LocalDate.of(2026, 6, 1));
        request.setStartTime(LocalTime.of(9, 0));
        request.setEndTime(LocalTime.of(10, 0));

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(userRepository.findById(sitterId)).thenReturn(Optional.of(sitter));
        when(petRepository.findAllById(any())).thenReturn(List.of(pet));
        when(bookingRepository.existsBySitter_UserIdAndDateAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
                eq(sitterId),
                eq(LocalDate.of(2026, 6, 1)),
                anyCollection(),
                eq(LocalTime.of(10, 0)),
                eq(LocalTime.of(9, 0))
        )).thenReturn(true);

        BookingService.BookingException ex = assertThrows(
                BookingService.BookingException.class,
                () -> bookingService.createBooking(ownerId, request)
        );

        assertEquals("Failed to book: sitter is already booked for this time slot", ex.getMessage());
        verify(bookingRepository, never()).save(any());
    }
}
