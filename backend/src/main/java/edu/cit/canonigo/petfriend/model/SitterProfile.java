package edu.cit.canonigo.petfriend.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sitter_profiles")
public class SitterProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID profileId;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(length = 255)
    private String experience;

    @Column(precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @Column(columnDefinition = "TEXT")
    private String servicesJson;

    @Column(columnDefinition = "TEXT")
    private String availabilityJson;

    @Column(length = 100)
    private String studentId;

    @Column(length = 255)
    private String referenceContact;

    @Column(length = 500)
    private String verificationDocumentUrl;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    public SitterProfile() {
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public UUID getProfileId() {
        return profileId;
    }

    public void setProfileId(UUID profileId) {
        this.profileId = profileId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public String getServicesJson() {
        return servicesJson;
    }

    public void setServicesJson(String servicesJson) {
        this.servicesJson = servicesJson;
    }

    public String getAvailabilityJson() {
        return availabilityJson;
    }

    public void setAvailabilityJson(String availabilityJson) {
        this.availabilityJson = availabilityJson;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
