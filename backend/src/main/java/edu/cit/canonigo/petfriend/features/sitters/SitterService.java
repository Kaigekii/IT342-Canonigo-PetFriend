package edu.cit.canonigo.petfriend.features.sitters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cit.canonigo.petfriend.model.ServiceType;
import edu.cit.canonigo.petfriend.model.SitterProfile;
import edu.cit.canonigo.petfriend.model.User;
import edu.cit.canonigo.petfriend.model.UserRole;
import edu.cit.canonigo.petfriend.repository.ReviewRepository;
import edu.cit.canonigo.petfriend.repository.SitterProfileRepository;
import edu.cit.canonigo.petfriend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * Service for sitter-related business logic
 */
@Service
public class SitterService {

    private final UserRepository userRepository;
    private final SitterProfileRepository sitterProfileRepository;
    private final ReviewRepository reviewRepository;
    private final ObjectMapper objectMapper;

    public SitterService(UserRepository userRepository,
                        SitterProfileRepository sitterProfileRepository,
                        ReviewRepository reviewRepository,
                        ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.sitterProfileRepository = sitterProfileRepository;
        this.reviewRepository = reviewRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Search verified sitters with optional filtering
     */
    public List<SitterDtos.SitterSummaryResponse> searchSitters(String location, String serviceTypeText) {
        ServiceType requestedServiceType = parseServiceTypeOrNull(serviceTypeText);
        List<SitterDtos.SitterSummaryResponse> results = new ArrayList<>();

        // Get all users and filter for pet sitters
        for (User sitter : userRepository.findAll()) {
            if (sitter.getRole() != UserRole.PET_SITTER) {
                continue;
            }
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

            SitterDtos.RatingInfo ratingInfo = buildRatingInfo(sitter.getUserId());
            results.add(new SitterDtos.SitterSummaryResponse(
                    sitter.getUserId(),
                    safeName(sitter),
                    profile.getBio(),
                    profile.getExperience(),
                    profile.getHourlyRate(),
                    services,
                    ratingInfo.getRating(),
                    ratingInfo.getReviewCount(),
                    true,
                    location == null ? "" : location
            ));
        }

        return results;
    }

    /**
     * Get detailed sitter profile with reviews
     */
    public SitterDtos.SitterDetailResponse getSitterDetails(UUID sitterId) {
        User sitter = userRepository.findById(sitterId).orElse(null);
        if (sitter == null || sitter.getRole() != UserRole.PET_SITTER || !Boolean.TRUE.equals(sitter.getIsVerified())) {
            return null;
        }

        SitterProfile profile = sitterProfileRepository.findByUser_UserId(sitterId).orElse(null);
        if (profile == null) {
            return null;
        }

        List<String> services = parseServices(profile.getServicesJson());
        Map<String, SitterDtos.DayAvailability> schedule = parseSchedule(profile.getAvailabilityJson());
        SitterDtos.RatingInfo ratingInfo = buildRatingInfo(sitterId);
        List<SitterDtos.ReviewItem> reviews = buildReviewsFromCompletedBookings(sitterId);

        return new SitterDtos.SitterDetailResponse(
                sitter.getUserId(),
                safeName(sitter),
                profile.getBio(),
                profile.getExperience(),
                profile.getHourlyRate(),
                services,
                schedule,
                ratingInfo.getRating(),
                ratingInfo.getReviewCount(),
                true,
                reviews
        );
    }

    /**
     * Get authenticated sitter's profile
     */
    public SitterDtos.SitterProfileResponse getMyProfile(User sitter) {
        SitterProfile profile = sitterProfileRepository.findByUser_UserId(sitter.getUserId())
                .orElseGet(() -> createEmptyProfile(sitter));
        return toResponse(profile, sitter);
    }

    /**
     * Update authenticated sitter's profile
     */
    public SitterDtos.SitterProfileResponse updateMyProfile(User sitter, SitterDtos.UpsertSitterProfileRequest request) 
            throws JsonProcessingException {
        SitterProfile profile = sitterProfileRepository.findByUser_UserId(sitter.getUserId())
                .orElseGet(() -> createEmptyProfile(sitter));

        profile.setBio(request.getBio());
        profile.setExperience(request.getExperience());
        profile.setHourlyRate(request.getHourlyRate());
        profile.setStudentId(request.getStudentId());
        profile.setReferenceContact(request.getReferenceContact());
        profile.setVerificationDocumentUrl(request.getVerificationDocumentUrl());

        profile.setServicesJson(objectMapper.writeValueAsString(
                request.getServicesOffered() != null ? request.getServicesOffered() : List.of()
        ));
        profile.setAvailabilityJson(objectMapper.writeValueAsString(
                request.getAvailabilitySchedule() != null ? request.getAvailabilitySchedule() : Map.of()
        ));

        sitter.setProfilePhotoUrl(request.getProfilePhotoUrl());
        userRepository.save(sitter);

        SitterProfile saved = sitterProfileRepository.save(profile);
        return toResponse(saved, sitter);
    }

    /**
     * Submit profile for verification
     */
    public void submitVerification(User sitter) {
        if (sitter.getIsVerified() == null || sitter.getIsVerified()) {
            sitter.setIsVerified(false);
            userRepository.save(sitter);
        }
    }

    // ========== Helper Methods ==========

    private SitterProfile createEmptyProfile(User sitter) {
        SitterProfile profile = new SitterProfile();
        profile.setUser(sitter);
        profile.setServicesJson("[]");
        profile.setAvailabilityJson("{}");
        return sitterProfileRepository.save(profile);
    }

    private SitterDtos.SitterProfileResponse toResponse(SitterProfile profile, User sitter) {
        SitterDtos.SitterProfileResponse response = new SitterDtos.SitterProfileResponse();
        response.profileId = profile.getProfileId();
        response.userId = sitter.getUserId();
        response.profilePhotoUrl = sitter.getProfilePhotoUrl();
        response.bio = profile.getBio();
        response.experience = profile.getExperience();
        response.hourlyRate = profile.getHourlyRate();
        response.studentId = profile.getStudentId();
        response.referenceContact = profile.getReferenceContact();
        response.verificationDocumentUrl = profile.getVerificationDocumentUrl();
        response.isVerified = sitter.getIsVerified();

        response.servicesOffered = parseServices(profile.getServicesJson());
        response.availabilitySchedule = parseSchedule(profile.getAvailabilityJson());

        return response;
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

    private Map<String, SitterDtos.DayAvailability> parseSchedule(String scheduleJson) {
        try {
            return objectMapper.readValue(
                    scheduleJson == null || scheduleJson.isBlank() ? "{}" : scheduleJson,
                    new TypeReference<Map<String, SitterDtos.DayAvailability>>() {}
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
        return services.stream().anyMatch(service -> 
            service.equalsIgnoreCase(target) || service.equalsIgnoreCase(serviceType.name())
        );
    }

    private String safeName(User user) {
        String first = user.getFirstName() == null ? "" : user.getFirstName().trim();
        String last = user.getLastName() == null ? "" : user.getLastName().trim();
        String full = (first + " " + last).trim();
        return full.isEmpty() ? user.getEmail() : full;
    }

    private SitterDtos.RatingInfo buildRatingInfo(UUID sitterId) {
        long reviewCount = reviewRepository.countBySitter_UserId(sitterId);
        Double avg = reviewRepository.findAverageRatingBySitterId(sitterId);
        BigDecimal rating = BigDecimal.valueOf(avg == null ? 0.0 : avg).setScale(1, java.math.RoundingMode.HALF_UP);
        return new SitterDtos.RatingInfo(rating, reviewCount);
    }

    private List<SitterDtos.ReviewItem> buildReviewsFromCompletedBookings(UUID sitterId) {
        return reviewRepository.findBySitter_UserIdOrderByCreatedAtDesc(sitterId)
            .stream()
            .limit(10)
            .map(r -> new SitterDtos.ReviewItem(
                r.getReviewer().getFirstName() + " " + r.getReviewer().getLastName(),
                r.getCreatedAt().atOffset(java.time.ZoneOffset.UTC).toLocalDate().toString(),
                r.getRating(),
                r.getComment()
            ))
            .toList();
    }

    /**
     * Parse service type from string or return null if invalid
     */
    private ServiceType parseServiceTypeOrNull(String serviceTypeText) {
        if (serviceTypeText == null || serviceTypeText.isBlank()) {
            return null;
        }
        try {
            return ServiceType.valueOf(serviceTypeText.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
