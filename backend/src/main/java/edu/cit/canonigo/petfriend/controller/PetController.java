package edu.cit.canonigo.petfriend.controller;

import edu.cit.canonigo.petfriend.model.Pet;
import edu.cit.canonigo.petfriend.model.PetSpecies;
import edu.cit.canonigo.petfriend.model.User;
import edu.cit.canonigo.petfriend.model.UserRole;
import edu.cit.canonigo.petfriend.model.VaccinationStatus;
import edu.cit.canonigo.petfriend.repository.PetRepository;
import edu.cit.canonigo.petfriend.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pets")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class PetController {

    private final PetRepository petRepository;
    private final UserRepository userRepository;

    public PetController(PetRepository petRepository, UserRepository userRepository) {
        this.petRepository = petRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<?> listMyPets(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        User user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        if (user.getRole() != UserRole.PET_OWNER) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        List<PetResponse> pets = petRepository.findByOwner_UserIdOrderByCreatedAtDesc(user.getUserId())
                .stream()
                .map(PetResponse::from)
                .toList();

        return ResponseEntity.ok(pets);
    }

    @PostMapping
    public ResponseEntity<?> createPet(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreatePetRequest request
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        User user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        if (user.getRole() != UserRole.PET_OWNER) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        Pet pet = new Pet(
                user,
                request.getName(),
                request.getBreed(),
                request.getAge(),
            request.getWeight(),
                request.getSpecies(),
                request.getSpecialNeeds(),
                request.getVaccinationStatus(),
                request.getPhotoUrl()
        );

        Pet saved = petRepository.save(pet);
        return ResponseEntity.ok(PetResponse.from(saved));
    }

    @PutMapping("/{petId}")
    public ResponseEntity<?> updatePet(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID petId,
            @Valid @RequestBody CreatePetRequest request
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        User user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        if (user.getRole() != UserRole.PET_OWNER) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        Pet pet = petRepository.findById(petId).orElse(null);
        if (pet == null) {
            return ResponseEntity.status(404).body("Pet not found");
        }

        if (!pet.getOwner().getUserId().equals(user.getUserId())) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        pet.setName(request.getName());
        pet.setBreed(request.getBreed());
        pet.setAge(request.getAge());
        pet.setWeight(request.getWeight());
        pet.setSpecies(request.getSpecies());
        pet.setSpecialNeeds(request.getSpecialNeeds());
        pet.setVaccinationStatus(request.getVaccinationStatus());
        pet.setPhotoUrl(request.getPhotoUrl());

        Pet updated = petRepository.save(pet);
        return ResponseEntity.ok(PetResponse.from(updated));
    }

    @DeleteMapping("/{petId}")
    public ResponseEntity<?> deletePet(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID petId
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        User user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        if (user.getRole() != UserRole.PET_OWNER) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        Pet pet = petRepository.findById(petId).orElse(null);
        if (pet == null) {
            return ResponseEntity.status(404).body("Pet not found");
        }

        if (!pet.getOwner().getUserId().equals(user.getUserId())) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        petRepository.delete(pet);
        return ResponseEntity.ok("Pet deleted");
    }

    public static class CreatePetRequest {
        @NotBlank
        private String name;

        private String breed;

        private Integer age;

        private Double weight;

        @NotNull
        private PetSpecies species;

        private String specialNeeds;

        @NotNull
        private VaccinationStatus vaccinationStatus;

        private String photoUrl;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBreed() {
            return breed;
        }

        public void setBreed(String breed) {
            this.breed = breed;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public Double getWeight() {
            return weight;
        }

        public void setWeight(Double weight) {
            this.weight = weight;
        }

        public PetSpecies getSpecies() {
            return species;
        }

        public void setSpecies(PetSpecies species) {
            this.species = species;
        }

        public String getSpecialNeeds() {
            return specialNeeds;
        }

        public void setSpecialNeeds(String specialNeeds) {
            this.specialNeeds = specialNeeds;
        }

        public VaccinationStatus getVaccinationStatus() {
            return vaccinationStatus;
        }

        public void setVaccinationStatus(VaccinationStatus vaccinationStatus) {
            this.vaccinationStatus = vaccinationStatus;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }

        public void setPhotoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
        }
    }

    public static class PetResponse {
        private UUID petId;
        private UUID ownerId;
        private String name;
        private String breed;
        private Integer age;
        private Double weight;
        private PetSpecies species;
        private String specialNeeds;
        private VaccinationStatus vaccinationStatus;
        private String photoUrl;

        public static PetResponse from(Pet pet) {
            PetResponse r = new PetResponse();
            r.petId = pet.getPetId();
            r.ownerId = pet.getOwner().getUserId();
            r.name = pet.getName();
            r.breed = pet.getBreed();
            r.age = pet.getAge();
            r.weight = pet.getWeight();
            r.species = pet.getSpecies();
            r.specialNeeds = pet.getSpecialNeeds();
            r.vaccinationStatus = pet.getVaccinationStatus();
            r.photoUrl = pet.getPhotoUrl();
            return r;
        }

        public UUID getPetId() {
            return petId;
        }

        public UUID getOwnerId() {
            return ownerId;
        }

        public String getName() {
            return name;
        }

        public String getBreed() {
            return breed;
        }

        public Integer getAge() {
            return age;
        }

        public Double getWeight() {
            return weight;
        }

        public PetSpecies getSpecies() {
            return species;
        }

        public String getSpecialNeeds() {
            return specialNeeds;
        }

        public VaccinationStatus getVaccinationStatus() {
            return vaccinationStatus;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }
    }
}
