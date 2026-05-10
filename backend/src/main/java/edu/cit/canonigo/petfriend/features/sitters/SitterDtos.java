package edu.cit.canonigo.petfriend.features.sitters;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Data Transfer Objects for Sitter features
 */
public class SitterDtos {

    /**
     * Response DTO for sitter search results
     */
    public static class SitterSummaryResponse {
        private final UUID sitterId;
        private final String fullName;
        private final String bio;
        private final String experience;
        private final BigDecimal hourlyRate;
        private final List<String> servicesOffered;
        private final BigDecimal rating;
        private final long reviewCount;
        private final boolean verified;
        private final String location;

        public SitterSummaryResponse(UUID sitterId, String fullName, String bio, String experience,
                                     BigDecimal hourlyRate, List<String> servicesOffered,
                                     BigDecimal rating, long reviewCount, boolean verified, String location) {
            this.sitterId = sitterId;
            this.fullName = fullName;
            this.bio = bio;
            this.experience = experience;
            this.hourlyRate = hourlyRate;
            this.servicesOffered = servicesOffered;
            this.rating = rating;
            this.reviewCount = reviewCount;
            this.verified = verified;
            this.location = location;
        }

        public UUID getSitterId() { return sitterId; }
        public String getFullName() { return fullName; }
        public String getBio() { return bio; }
        public String getExperience() { return experience; }
        public BigDecimal getHourlyRate() { return hourlyRate; }
        public List<String> getServicesOffered() { return servicesOffered; }
        public BigDecimal getRating() { return rating; }
        public long getReviewCount() { return reviewCount; }
        public boolean isVerified() { return verified; }
        public String getLocation() { return location; }
    }

    /**
     * Response DTO for detailed sitter profile
     */
    public static class SitterDetailResponse {
        private final UUID sitterId;
        private final String fullName;
        private final String bio;
        private final String experience;
        private final BigDecimal hourlyRate;
        private final List<String> servicesOffered;
        private final String location;
        private final Map<String, DayAvailability> availabilitySchedule;
        private final BigDecimal rating;
        private final long reviewCount;
        private final boolean verified;
        private final List<ReviewItem> reviews;
        public SitterDetailResponse(UUID sitterId, String fullName, String bio, String experience,
                                    BigDecimal hourlyRate, List<String> servicesOffered, String location,
                                    Map<String, DayAvailability> availabilitySchedule,
                                    BigDecimal rating, long reviewCount, boolean verified,
                                    List<ReviewItem> reviews) {
            this.sitterId = sitterId;
            this.fullName = fullName;
            this.bio = bio;
            this.experience = experience;
            this.hourlyRate = hourlyRate;
            this.servicesOffered = servicesOffered;
            this.location = location;
            this.availabilitySchedule = availabilitySchedule;
            this.rating = rating;
            this.reviewCount = reviewCount;
            this.verified = verified;
            this.reviews = reviews;
        }

        public UUID getSitterId() { return sitterId; }
        public String getFullName() { return fullName; }
        public String getBio() { return bio; }
        public String getExperience() { return experience; }
        public BigDecimal getHourlyRate() { return hourlyRate; }
        public List<String> getServicesOffered() { return servicesOffered; }
        public Map<String, DayAvailability> getAvailabilitySchedule() { return availabilitySchedule; }
        public BigDecimal getRating() { return rating; }
        public long getReviewCount() { return reviewCount; }
        public boolean isVerified() { return verified; }
        public List<ReviewItem> getReviews() { return reviews; }
        public String getLocation() { return location; }
    }

    /**
     * Request DTO for updating sitter profile
     */
    public static class UpsertSitterProfileRequest {
        private String profilePhotoUrl;
        private String bio;
        private String experience;
        private BigDecimal hourlyRate;
        private List<String> servicesOffered;
        private Map<String, DayAvailability> availabilitySchedule;
        private String studentId;
        private String referenceContact;
        private String verificationDocumentUrl;
        private String location;

        public String getProfilePhotoUrl() { return profilePhotoUrl; }
        public void setProfilePhotoUrl(String profilePhotoUrl) { this.profilePhotoUrl = profilePhotoUrl; }

        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }

        public String getExperience() { return experience; }
        public void setExperience(String experience) { this.experience = experience; }

        public BigDecimal getHourlyRate() { return hourlyRate; }
        public void setHourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; }

        public List<String> getServicesOffered() { return servicesOffered; }
        public void setServicesOffered(List<String> servicesOffered) { this.servicesOffered = servicesOffered; }

        public Map<String, DayAvailability> getAvailabilitySchedule() { return availabilitySchedule; }
        public void setAvailabilitySchedule(Map<String, DayAvailability> availabilitySchedule) { 
            this.availabilitySchedule = availabilitySchedule; 
        }

        public String getStudentId() { return studentId; }
        public void setStudentId(String studentId) { this.studentId = studentId; }

        public String getReferenceContact() { return referenceContact; }
        public void setReferenceContact(String referenceContact) { this.referenceContact = referenceContact; }

        public String getVerificationDocumentUrl() { return verificationDocumentUrl; }
        public void setVerificationDocumentUrl(String verificationDocumentUrl) { 
            this.verificationDocumentUrl = verificationDocumentUrl; 
        }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
    }

    /**
     * Response DTO for sitter profile details
     */
    public static class SitterProfileResponse {
        public UUID profileId;
        public UUID userId;
        public String profilePhotoUrl;
        public String bio;
        public String experience;
        public BigDecimal hourlyRate;
        public List<String> servicesOffered;
        public Map<String, DayAvailability> availabilitySchedule;
        public String location;
        public String studentId;
        public String referenceContact;
        public String verificationDocumentUrl;
        public Boolean isVerified;
    }

    /**
     * Review item embedded in sitter details
     */
    public static class ReviewItem {
        private final String reviewerName;
        private final String date;
        private final int rating;
        private final String comment;

        public ReviewItem(String reviewerName, String date, int rating, String comment) {
            this.reviewerName = reviewerName;
            this.date = date;
            this.rating = rating;
            this.comment = comment;
        }

        public String getReviewerName() { return reviewerName; }
        public String getDate() { return date; }
        public int getRating() { return rating; }
        public String getComment() { return comment; }
    }

    /**
     * Rating statistics for a sitter
     */
    public static class RatingInfo {
        private final BigDecimal rating;
        private final long reviewCount;

        public RatingInfo(BigDecimal rating, long reviewCount) {
            this.rating = rating;
            this.reviewCount = reviewCount;
        }

        public BigDecimal getRating() { return rating; }
        public long getReviewCount() { return reviewCount; }
    }

    /**
     * Time slot availability
     */
    public static class DayAvailability {
        private String startTime;
        private String endTime;

        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }

        public String getEndTime() { return endTime; }
        public void setEndTime(String endTime) { this.endTime = endTime; }
    }
}
