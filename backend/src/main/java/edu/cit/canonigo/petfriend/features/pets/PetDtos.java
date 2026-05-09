package edu.cit.canonigo.petfriend.features.pets;

import edu.cit.canonigo.petfriend.model.PetSpecies;
import edu.cit.canonigo.petfriend.model.VaccinationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * DTOs for pet management operations.
 */
public class PetDtos {

    public static class CreatePetRequest {
        @NotBlank(message = "Pet name is required")
        private String name;

        private String breed;
        private Integer age;
        private Double weight;

        @NotNull(message = "Species is required")
        private PetSpecies species;

        private String specialNeeds;

        @NotNull(message = "Vaccination status is required")
        private VaccinationStatus vaccinationStatus;

        private String photoUrl;

        // Getters and Setters
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
        private String ownerName;
        private String name;
        private String breed;
        private Integer age;
        private Double weight;
        private PetSpecies species;
        private String specialNeeds;
        private VaccinationStatus vaccinationStatus;
        private String photoUrl;

        public static PetResponse from(edu.cit.canonigo.petfriend.model.Pet pet) {
            PetResponse response = new PetResponse();
            response.petId = pet.getPetId();
            response.ownerId = pet.getOwner().getUserId();
            response.ownerName = pet.getOwner().getFirstName() + " " + pet.getOwner().getLastName();
            response.name = pet.getName();
            response.breed = pet.getBreed();
            response.age = pet.getAge();
            response.weight = pet.getWeight();
            response.species = pet.getSpecies();
            response.specialNeeds = pet.getSpecialNeeds();
            response.vaccinationStatus = pet.getVaccinationStatus();
            response.photoUrl = pet.getPhotoUrl();
            return response;
        }

        // Getters
        public UUID getPetId() {
            return petId;
        }

        public UUID getOwnerId() {
            return ownerId;
        }

        public String getOwnerName() {
            return ownerName;
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
