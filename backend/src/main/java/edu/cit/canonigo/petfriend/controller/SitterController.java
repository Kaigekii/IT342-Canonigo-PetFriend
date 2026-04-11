package edu.cit.canonigo.petfriend.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cit.canonigo.petfriend.model.ServiceType;
import edu.cit.canonigo.petfriend.model.SitterProfile;
import edu.cit.canonigo.petfriend.model.User;
import edu.cit.canonigo.petfriend.model.UserRole;
import edu.cit.canonigo.petfriend.repository.ReviewRepository;
import edu.cit.canonigo.petfriend.repository.SitterProfileRepository;
import edu.cit.canonigo.petfriend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/sitters")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class SitterController {

    private final UserRepository userRepository;
    private final SitterProfileRepository sitterProfileRepository;
    private final ReviewRepository reviewRepository;
    private final ObjectMapper objectMapper;

    public SitterController(UserRepository userRepository,
                            SitterProfileRepository sitterProfileRepository,
                            ReviewRepository reviewRepository,
                            ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.sitterProfileRepository = sitterProfileRepository;
        this.reviewRepository = reviewRepository;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchSitters(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "location", required = false) String location,
            @RequestParam(name = "serviceType", required = false) String serviceTypeText
    ) {
        User owner = getAuthenticatedOwner(userDetails);
        if (owner == null) {
            if (userDetails == null) {
                return ResponseEntity.status(401).body("Unauthorized");
            }
            return ResponseEntity.status(403).body("Forbidden");
        }

        ServiceType requestedServiceType = parseServiceTypeOrNull(serviceTypeText);

        List<SitterSummaryResponse> results = new ArrayList<>();

        for (User sitter : userRepository.findByRoleOrderByCreatedAtAsc(UserRole.PET_SITTER)) {
            if (!Boolean.TRUE.equals(sitter.getIsVerified())) {
                continue;
            }

            Optional<SitterProfile> profileOpt = sitterProfileRepository.findByUser_UserId(sitter.getUserId());
            if (profileOpt.isEmpty()) {
                continue;
            }

            SitterProfile profile = profileOpt.get();
            List<String> services = parseServices(profile.getServicesJson());

            if (requestedServiceType != null && !offersService(services, requestedServiceType)) {
                continue;
            }

            RatingInfo ratingInfo = buildRatingInfo(sitter.getUserId());
            results.add(new SitterSummaryResponse(
                    sitter.getUserId(),
                    safeName(sitter),
                    profile.getBio(),
                    profile.getExperience(),
                    profile.getHourlyRate(),
                    services,
                    ratingInfo.rating,
                    ratingInfo.reviewCount,
                    true,
                    location == null ? "" : location
            ));
        }

        return ResponseEntity.ok(results);
    }

    @GetMapping("/{sitterId}")
    public ResponseEntity<?> getSitterDetails(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID sitterId
    ) {
        User owner = getAuthenticatedOwner(userDetails);
        if (owner == null) {
            if (userDetails == null) {
                return ResponseEntity.status(401).body("Unauthorized");
            }
            return ResponseEntity.status(403).body("Forbidden");
        }

        User sitter = userRepository.findById(sitterId).orElse(null);
        if (sitter == null || sitter.getRole() != UserRole.PET_SITTER || !Boolean.TRUE.equals(sitter.getIsVerified())) {
            return ResponseEntity.status(404).body("Sitter not found");
        }

        SitterProfile profile = sitterProfileRepository.findByUser_UserId(sitterId).orElse(null);
        if (profile == null) {
            return ResponseEntity.status(404).body("Sitter profile not found");
        }

        List<String> services = parseServices(profile.getServicesJson());
        Map<String, SitterProfileController.DayAvailability> schedule = parseSchedule(profile.getAvailabilityJson());
        RatingInfo ratingInfo = buildRatingInfo(sitterId);

        List<ReviewItem> reviews = buildReviewsFromCompletedBookings(sitterId);

        SitterDetailResponse response = new SitterDetailResponse(
                sitter.getUserId(),
                safeName(sitter),
                profile.getBio(),
                profile.getExperience(),
                profile.getHourlyRate(),
                services,
                schedule,
                ratingInfo.rating,
                ratingInfo.reviewCount,
                true,
                reviews
        );

        return ResponseEntity.ok(response);
    }

    private User getAuthenticatedOwner(UserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }

        User user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null || user.getRole() != UserRole.PET_OWNER) {
            return null;
        }

        return user;
    }

    private ServiceType parseServiceTypeOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = value.trim().toUpperCase(Locale.ROOT);
        if (normalized.equals("ALL") || normalized.equals("ALL_SERVICES")) {
            return null;
        }

        try {
            return ServiceType.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private List<String> parseServices(String servicesJson) {
        try {
            List<String> parsed = objectMapper.readValue(
                    servicesJson == null || servicesJson.isBlank() ? "[]" : servicesJson,
                    new TypeReference<List<String>>() {}
            );
            return parsed.stream().filter(s -> s != null && !s.isBlank()).map(String::trim).toList();
        } catch (Exception ex) {
            return List.of();
        }
    }

    private Map<String, SitterProfileController.DayAvailability> parseSchedule(String scheduleJson) {
        try {
            return objectMapper.readValue(
                    scheduleJson == null || scheduleJson.isBlank() ? "{}" : scheduleJson,
                    new TypeReference<Map<String, SitterProfileController.DayAvailability>>() {}
            );
        } catch (Exception ex) {
            return Map.of();
        }
    }

    private boolean offersService(List<String> services, ServiceType serviceType) {
        if (services.isEmpty()) {
            return false;
        }

        String target = serviceType.name().toLowerCase(Locale.ROOT);
        return services.stream().anyMatch(service -> service.equalsIgnoreCase(target) || service.equalsIgnoreCase(serviceType.name()));
    }

    private String safeName(User user) {
        String first = user.getFirstName() == null ? "" : user.getFirstName().trim();
        String last = user.getLastName() == null ? "" : user.getLastName().trim();
        String full = (first + " " + last).trim();
        return full.isEmpty() ? user.getEmail() : full;
    }

    private RatingInfo buildRatingInfo(UUID sitterId) {
        long reviewCount = reviewRepository.countBySitter_UserId(sitterId);
        Double avg = reviewRepository.findAverageRatingBySitterId(sitterId);
        BigDecimal rating = BigDecimal.valueOf(avg == null ? 0.0 : avg).setScale(1, java.math.RoundingMode.HALF_UP);
        return new RatingInfo(rating, reviewCount);
    }

        private List<ReviewItem> buildReviewsFromCompletedBookings(UUID sitterId) {
        return reviewRepository.findBySitter_UserIdOrderByCreatedAtDesc(sitterId)
            .stream()
            .limit(10)
            .map(r -> new ReviewItem(
                r.getReviewer().getFirstName() + " " + r.getReviewer().getLastName(),
                r.getCreatedAt().atOffset(java.time.ZoneOffset.UTC).toLocalDate().toString(),
                r.getRating(),
                r.getComment()
            ))
            .toList();
    }

    private static class RatingInfo {
        private final BigDecimal rating;
        private final long reviewCount;

        private RatingInfo(BigDecimal rating, long reviewCount) {
            this.rating = rating;
            this.reviewCount = reviewCount;
        }
    }

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

        public SitterSummaryResponse(UUID sitterId,
                                     String fullName,
                                     String bio,
                                     String experience,
                                     BigDecimal hourlyRate,
                                     List<String> servicesOffered,
                                     BigDecimal rating,
                                     long reviewCount,
                                     boolean verified,
                                     String location) {
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

        public UUID getSitterId() {
            return sitterId;
        }

        public String getFullName() {
            return fullName;
        }

        public String getBio() {
            return bio;
        }

        public String getExperience() {
            return experience;
        }

        public BigDecimal getHourlyRate() {
            return hourlyRate;
        }

        public List<String> getServicesOffered() {
            return servicesOffered;
        }

        public BigDecimal getRating() {
            return rating;
        }

        public long getReviewCount() {
            return reviewCount;
        }

        public boolean isVerified() {
            return verified;
        }

        public String getLocation() {
            return location;
        }
    }

    public static class SitterDetailResponse {
        private final UUID sitterId;
        private final String fullName;
        private final String bio;
        private final String experience;
        private final BigDecimal hourlyRate;
        private final List<String> servicesOffered;
        private final Map<String, SitterProfileController.DayAvailability> availabilitySchedule;
        private final BigDecimal rating;
        private final long reviewCount;
        private final boolean verified;
        private final List<ReviewItem> reviews;

        public SitterDetailResponse(UUID sitterId,
                                    String fullName,
                                    String bio,
                                    String experience,
                                    BigDecimal hourlyRate,
                                    List<String> servicesOffered,
                                    Map<String, SitterProfileController.DayAvailability> availabilitySchedule,
                                    BigDecimal rating,
                                    long reviewCount,
                                    boolean verified,
                                    List<ReviewItem> reviews) {
            this.sitterId = sitterId;
            this.fullName = fullName;
            this.bio = bio;
            this.experience = experience;
            this.hourlyRate = hourlyRate;
            this.servicesOffered = servicesOffered;
            this.availabilitySchedule = availabilitySchedule;
            this.rating = rating;
            this.reviewCount = reviewCount;
            this.verified = verified;
            this.reviews = reviews;
        }

        public UUID getSitterId() {
            return sitterId;
        }

        public String getFullName() {
            return fullName;
        }

        public String getBio() {
            return bio;
        }

        public String getExperience() {
            return experience;
        }

        public BigDecimal getHourlyRate() {
            return hourlyRate;
        }

        public List<String> getServicesOffered() {
            return servicesOffered;
        }

        public Map<String, SitterProfileController.DayAvailability> getAvailabilitySchedule() {
            return availabilitySchedule;
        }

        public BigDecimal getRating() {
            return rating;
        }

        public long getReviewCount() {
            return reviewCount;
        }

        public boolean isVerified() {
            return verified;
        }

        public List<ReviewItem> getReviews() {
            return reviews;
        }
    }

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

        public String getReviewerName() {
            return reviewerName;
        }

        public String getDate() {
            return date;
        }

        public int getRating() {
            return rating;
        }

        public String getComment() {
            return comment;
        }
    }
}
