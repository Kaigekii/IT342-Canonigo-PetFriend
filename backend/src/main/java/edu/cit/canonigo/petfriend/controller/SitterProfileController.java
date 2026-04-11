package edu.cit.canonigo.petfriend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cit.canonigo.petfriend.model.SitterProfile;
import edu.cit.canonigo.petfriend.model.User;
import edu.cit.canonigo.petfriend.model.UserRole;
import edu.cit.canonigo.petfriend.repository.SitterProfileRepository;
import edu.cit.canonigo.petfriend.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping({"/api/sitter-profile", "/api/sitters/profile"})
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class SitterProfileController {

    private final UserRepository userRepository;
    private final SitterProfileRepository sitterProfileRepository;
    private final ObjectMapper objectMapper;

    public SitterProfileController(UserRepository userRepository, SitterProfileRepository sitterProfileRepository, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.sitterProfileRepository = sitterProfileRepository;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public ResponseEntity<?> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User sitter = getAuthenticatedSitter(userDetails);
        if (sitter == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        SitterProfile profile = sitterProfileRepository.findByUser_UserId(sitter.getUserId())
                .orElseGet(() -> createEmptyProfile(sitter));

        return ResponseEntity.ok(toResponse(profile, sitter));
    }

    @PutMapping
    public ResponseEntity<?> updateMyProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpsertSitterProfileRequest request
    ) {
        User sitter = getAuthenticatedSitter(userDetails);
        if (sitter == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        SitterProfile profile = sitterProfileRepository.findByUser_UserId(sitter.getUserId())
                .orElseGet(() -> createEmptyProfile(sitter));

        profile.setBio(request.getBio());
        profile.setExperience(request.getExperience());
        profile.setHourlyRate(request.getHourlyRate());
        profile.setStudentId(request.getStudentId());
        profile.setReferenceContact(request.getReferenceContact());
        profile.setVerificationDocumentUrl(request.getVerificationDocumentUrl());

        try {
            profile.setServicesJson(objectMapper.writeValueAsString(
                    request.getServicesOffered() != null ? request.getServicesOffered() : List.of()
            ));
            profile.setAvailabilityJson(objectMapper.writeValueAsString(
                    request.getAvailabilitySchedule() != null ? request.getAvailabilitySchedule() : Map.of()
            ));
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Invalid profile payload");
        }

        sitter.setProfilePhotoUrl(request.getProfilePhotoUrl());
        userRepository.save(sitter);

        SitterProfile saved = sitterProfileRepository.save(profile);
        return ResponseEntity.ok(toResponse(saved, sitter));
    }

    @PostMapping("/submit-verification")
    public ResponseEntity<?> submitVerification(@AuthenticationPrincipal UserDetails userDetails) {
        User sitter = getAuthenticatedSitter(userDetails);
        if (sitter == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        if (sitter.getIsVerified() == null || sitter.getIsVerified()) {
            sitter.setIsVerified(false);
            userRepository.save(sitter);
        }

        return ResponseEntity.ok("Verification submitted");
    }

    private User getAuthenticatedSitter(UserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }
        User user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null || user.getRole() != UserRole.PET_SITTER) {
            return null;
        }
        return user;
    }

    private SitterProfile createEmptyProfile(User sitter) {
        SitterProfile profile = new SitterProfile();
        profile.setUser(sitter);
        profile.setServicesJson("[]");
        profile.setAvailabilityJson("{}");
        return sitterProfileRepository.save(profile);
    }

    private SitterProfileResponse toResponse(SitterProfile profile, User sitter) {
        SitterProfileResponse response = new SitterProfileResponse();
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

        try {
            response.servicesOffered = objectMapper.readValue(
                    profile.getServicesJson() != null ? profile.getServicesJson() : "[]",
                    new TypeReference<List<String>>() {}
            );
        } catch (Exception ex) {
            response.servicesOffered = List.of();
        }

        try {
            response.availabilitySchedule = objectMapper.readValue(
                    profile.getAvailabilityJson() != null ? profile.getAvailabilityJson() : "{}",
                    new TypeReference<Map<String, DayAvailability>>() {}
            );
        } catch (Exception ex) {
            response.availabilitySchedule = Map.of();
        }

        return response;
    }

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

        public String getProfilePhotoUrl() {
            return profilePhotoUrl;
        }

        public void setProfilePhotoUrl(String profilePhotoUrl) {
            this.profilePhotoUrl = profilePhotoUrl;
        }

        public String getBio() {
            return bio;
        }

        public void setBio(String bio) {
            this.bio = bio;
        }

        public String getExperience() {
            return experience;
        }

        public void setExperience(String experience) {
            this.experience = experience;
        }

        public BigDecimal getHourlyRate() {
            return hourlyRate;
        }

        public void setHourlyRate(BigDecimal hourlyRate) {
            this.hourlyRate = hourlyRate;
        }

        public List<String> getServicesOffered() {
            return servicesOffered;
        }

        public void setServicesOffered(List<String> servicesOffered) {
            this.servicesOffered = servicesOffered;
        }

        public Map<String, DayAvailability> getAvailabilitySchedule() {
            return availabilitySchedule;
        }

        public void setAvailabilitySchedule(Map<String, DayAvailability> availabilitySchedule) {
            this.availabilitySchedule = availabilitySchedule;
        }

        public String getStudentId() {
            return studentId;
        }

        public void setStudentId(String studentId) {
            this.studentId = studentId;
        }

        public String getReferenceContact() {
            return referenceContact;
        }

        public void setReferenceContact(String referenceContact) {
            this.referenceContact = referenceContact;
        }

        public String getVerificationDocumentUrl() {
            return verificationDocumentUrl;
        }

        public void setVerificationDocumentUrl(String verificationDocumentUrl) {
            this.verificationDocumentUrl = verificationDocumentUrl;
        }
    }

    public static class DayAvailability {
        private String startTime;
        private String endTime;

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }
    }

    public static class SitterProfileResponse {
        public UUID profileId;
        public UUID userId;
        public String profilePhotoUrl;
        public String bio;
        public String experience;
        public BigDecimal hourlyRate;
        public List<String> servicesOffered;
        public Map<String, DayAvailability> availabilitySchedule;
        public String studentId;
        public String referenceContact;
        public String verificationDocumentUrl;
        public Boolean isVerified;
    }
}
