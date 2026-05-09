package edu.cit.canonigo.petfriend.features.bookings;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import edu.cit.canonigo.petfriend.model.BookingStatus;
import edu.cit.canonigo.petfriend.model.Pet;
import edu.cit.canonigo.petfriend.model.ServiceType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class BookingDtos {

    public static class CreateBookingRequest {
        @NotNull
        private UUID sitterId;

        @NotEmpty
        private List<UUID> petIds;

        @NotNull
        private ServiceType serviceType;

        @NotNull
        private LocalDate date;

        @NotNull
        private LocalTime startTime;

        @NotNull
        private LocalTime endTime;

        private String specialInstructions;

        // Getters and Setters
        public UUID getSitterId() {
            return sitterId;
        }

        public void setSitterId(UUID sitterId) {
            this.sitterId = sitterId;
        }

        public List<UUID> getPetIds() {
            return petIds;
        }

        public void setPetIds(List<UUID> petIds) {
            this.petIds = petIds;
        }

        public ServiceType getServiceType() {
            return serviceType;
        }

        public void setServiceType(ServiceType serviceType) {
            this.serviceType = serviceType;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public LocalTime getStartTime() {
            return startTime;
        }

        public void setStartTime(LocalTime startTime) {
            this.startTime = startTime;
        }

        public LocalTime getEndTime() {
            return endTime;
        }

        public void setEndTime(LocalTime endTime) {
            this.endTime = endTime;
        }

        public String getSpecialInstructions() {
            return specialInstructions;
        }

        public void setSpecialInstructions(String specialInstructions) {
            this.specialInstructions = specialInstructions;
        }
    }

    public static class UpdateSitterBookingStatusRequest {
        @NotNull
        private BookingStatus status;

        public BookingStatus getStatus() {
            return status;
        }

        public void setStatus(BookingStatus status) {
            this.status = status;
        }
    }

    public static class UpdateOwnerBookingStatusRequest {
        @NotNull
        private BookingStatus status;

        public BookingStatus getStatus() {
            return status;
        }

        public void setStatus(BookingStatus status) {
            this.status = status;
        }
    }

    public static class BookingResponse {
        private UUID bookingId;
        private UUID ownerId;
        private String ownerName;
        private UUID sitterId;
        private String sitterName;
        private ServiceType serviceType;
        private LocalDate date;
        private LocalTime startTime;
        private LocalTime endTime;
        private BookingStatus status;
        private List<String> petNames;
        private List<UUID> petIds;
        private BigDecimal totalAmount;
        private String currency;

        public static BookingResponse from(edu.cit.canonigo.petfriend.model.Booking booking) {
            BookingResponse r = new BookingResponse();
            r.bookingId = booking.getBookingId();
            r.ownerId = booking.getOwner().getUserId();
            r.ownerName = booking.getOwner().getFirstName() + " " + booking.getOwner().getLastName();
            r.sitterId = booking.getSitter() != null ? booking.getSitter().getUserId() : null;
            r.sitterName = booking.getSitter() != null
                    ? booking.getSitter().getFirstName() + " " + booking.getSitter().getLastName()
                    : null;
            r.serviceType = booking.getServiceType();
            r.date = booking.getDate();
            r.startTime = booking.getStartTime();
            r.endTime = booking.getEndTime();
            r.status = booking.getStatus();
            r.petNames = booking.getPets().stream().map(Pet::getName).toList();
            r.petIds = booking.getPets().stream().map(Pet::getPetId).toList();
            r.totalAmount = booking.getTotalAmount();
            r.currency = booking.getCurrency();
            return r;
        }

        // Getters
        public UUID getBookingId() {
            return bookingId;
        }

        public UUID getOwnerId() {
            return ownerId;
        }

        public String getOwnerName() {
            return ownerName;
        }

        public UUID getSitterId() {
            return sitterId;
        }

        public String getSitterName() {
            return sitterName;
        }

        public ServiceType getServiceType() {
            return serviceType;
        }

        public LocalDate getDate() {
            return date;
        }

        public LocalTime getStartTime() {
            return startTime;
        }

        public LocalTime getEndTime() {
            return endTime;
        }

        public BookingStatus getStatus() {
            return status;
        }

        public List<String> getPetNames() {
            return petNames;
        }

        public List<UUID> getPetIds() {
            return petIds;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public String getCurrency() {
            return currency;
        }
    }
}
