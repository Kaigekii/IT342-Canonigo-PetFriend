package edu.cit.canonigo.petfriend.features.admin;

import edu.cit.canonigo.petfriend.model.BookingStatus;
import edu.cit.canonigo.petfriend.model.ServiceType;
import edu.cit.canonigo.petfriend.model.UserRole;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Objects for Admin features
 */
public class AdminDtos {

    /**
     * Response DTO for admin dashboard with statistics
     */
    public static class DashboardResponse {
        private final long totalUsers;
        private final long petOwners;
        private final long petSitters;
        private final long pendingApprovals;
        private final long totalBookings;
        private final BigDecimal monthRevenue;
        private final BigDecimal platformFees;
        private final BigDecimal sitterPayouts;
        private final List<ActivityItem> recentActivity;

        public DashboardResponse(long totalUsers, long petOwners, long petSitters, long pendingApprovals,
                                long totalBookings, BigDecimal monthRevenue, BigDecimal platformFees,
                                BigDecimal sitterPayouts, List<ActivityItem> recentActivity) {
            this.totalUsers = totalUsers;
            this.petOwners = petOwners;
            this.petSitters = petSitters;
            this.pendingApprovals = pendingApprovals;
            this.totalBookings = totalBookings;
            this.monthRevenue = monthRevenue;
            this.platformFees = platformFees;
            this.sitterPayouts = sitterPayouts;
            this.recentActivity = recentActivity;
        }

        public long getTotalUsers() { return totalUsers; }
        public long getPetOwners() { return petOwners; }
        public long getPetSitters() { return petSitters; }
        public long getPendingApprovals() { return pendingApprovals; }
        public long getTotalBookings() { return totalBookings; }
        public BigDecimal getMonthRevenue() { return monthRevenue; }
        public BigDecimal getPlatformFees() { return platformFees; }
        public BigDecimal getSitterPayouts() { return sitterPayouts; }
        public List<ActivityItem> getRecentActivity() { return recentActivity; }
    }

    /**
     * Activity item for dashboard timeline
     */
    public static class ActivityItem {
        private final String type;
        private final String message;
        private final String ago;

        public ActivityItem(String type, String message, String ago) {
            this.type = type;
            this.message = message;
            this.ago = ago;
        }

        public String getType() { return type; }
        public String getMessage() { return message; }
        public String getAgo() { return ago; }
    }

    /**
     * Pending sitter item for approval queue
     */
    public static class PendingSitterItem {
        private final UUID sitterId;
        private final String fullName;
        private final String email;
        private final String studentId;
        private final String bio;
        private final String experience;
        private final BigDecimal hourlyRate;
        private final String servicesJson;
        private final String verificationDocumentUrl;
        private final Instant submittedAt;

        public PendingSitterItem(UUID sitterId, String fullName, String email, String studentId,
                                String bio, String experience, BigDecimal hourlyRate, String servicesJson,
                                String verificationDocumentUrl, Instant submittedAt) {
            this.sitterId = sitterId;
            this.fullName = fullName;
            this.email = email;
            this.studentId = studentId;
            this.bio = bio;
            this.experience = experience;
            this.hourlyRate = hourlyRate;
            this.servicesJson = servicesJson;
            this.verificationDocumentUrl = verificationDocumentUrl;
            this.submittedAt = submittedAt;
        }

        public UUID getSitterId() { return sitterId; }
        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
        public String getStudentId() { return studentId; }
        public String getBio() { return bio; }
        public String getExperience() { return experience; }
        public BigDecimal getHourlyRate() { return hourlyRate; }
        public String getServicesJson() { return servicesJson; }
        public String getVerificationDocumentUrl() { return verificationDocumentUrl; }
        public Instant getSubmittedAt() { return submittedAt; }
    }

    /**
     * Request DTO for rejecting sitter
     */
    public static class RejectRequest {
        @NotBlank(message = "Reason is required")
        private String reason;

        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    /**
     * Generic action response
     */
    public static class ActionResponse {
        private final String message;

        public ActionResponse(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }
    }

    /**
     * Admin user item for user listing
     */
    public static class AdminUserItem {
        private final UUID userId;
        private final String fullName;
        private final String email;
        private final UserRole role;
        private final boolean verified;
        private final Instant createdAt;

        public AdminUserItem(UUID userId, String fullName, String email, UserRole role,
                            boolean verified, Instant createdAt) {
            this.userId = userId;
            this.fullName = fullName;
            this.email = email;
            this.role = role;
            this.verified = verified;
            this.createdAt = createdAt;
        }

        public UUID getUserId() { return userId; }
        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
        public UserRole getRole() { return role; }
        public boolean isVerified() { return verified; }
        public Instant getCreatedAt() { return createdAt; }
    }

    /**
     * Admin booking item for booking listing
     */
    public static class AdminBookingItem {
        private final UUID bookingId;
        private final String ownerName;
        private final String sitterName;
        private final ServiceType serviceType;
        private final LocalDate date;
        private final BookingStatus status;
        private final BigDecimal totalAmount;
        private final String currency;

        public AdminBookingItem(UUID bookingId, String ownerName, String sitterName, ServiceType serviceType,
                               LocalDate date, BookingStatus status, BigDecimal totalAmount, String currency) {
            this.bookingId = bookingId;
            this.ownerName = ownerName;
            this.sitterName = sitterName;
            this.serviceType = serviceType;
            this.date = date;
            this.status = status;
            this.totalAmount = totalAmount;
            this.currency = currency;
        }

        public UUID getBookingId() { return bookingId; }
        public String getOwnerName() { return ownerName; }
        public String getSitterName() { return sitterName; }
        public ServiceType getServiceType() { return serviceType; }
        public LocalDate getDate() { return date; }
        public BookingStatus getStatus() { return status; }
        public BigDecimal getTotalAmount() { return totalAmount; }
        public String getCurrency() { return currency; }
    }
}
