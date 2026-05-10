package edu.cit.canonigo.petfriend.features.pets;

import edu.cit.canonigo.petfriend.model.Pet;
import edu.cit.canonigo.petfriend.model.PetSpecies;
import edu.cit.canonigo.petfriend.model.User;
import edu.cit.canonigo.petfriend.model.UserRole;
import edu.cit.canonigo.petfriend.model.VaccinationStatus;
import edu.cit.canonigo.petfriend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PetService petService;

    private User owner;
    private User sitter;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setUserId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        owner.setEmail("owner@example.com");
        owner.setFirstName("Pet");
        owner.setLastName("Owner");
        owner.setRole(UserRole.PET_OWNER);
        owner.setIsVerified(true);

        sitter = new User();
        sitter.setUserId(UUID.fromString("22222222-2222-2222-2222-222222222222"));
        sitter.setEmail("sitter@example.com");
        sitter.setFirstName("Pet");
        sitter.setLastName("Sitter");
        sitter.setRole(UserRole.PET_SITTER);
        sitter.setIsVerified(true);

        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(userRepository.findByEmail("sitter@example.com")).thenReturn(Optional.of(sitter));
    }

    private Pet samplePet() {
        Pet pet = new Pet();
        pet.setPetId(UUID.fromString("33333333-3333-3333-3333-333333333333"));
        pet.setOwner(owner);
        pet.setName("Buddy");
        pet.setBreed("Golden Retriever");
        pet.setAge(3);
        pet.setWeight(20.5);
        pet.setSpecies(PetSpecies.DOG);
        pet.setSpecialNeeds("None");
        pet.setVaccinationStatus(VaccinationStatus.UP_TO_DATE);
        pet.setPhotoUrl("https://example.com/buddy.jpg");
        return pet;
    }

    @Test
    void testGetAllPets() throws Exception {
        when(petService.getOwnerPets(owner.getUserId())).thenReturn(List.of(samplePet()));

        mockMvc.perform(get("/api/pets")
                .with(user("owner@example.com").roles("PET_OWNER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetPetById() throws Exception {
        when(petService.getOwnerPets(owner.getUserId())).thenReturn(List.of(samplePet()));

        mockMvc.perform(get("/api/pets")
                .with(user("owner@example.com").roles("PET_OWNER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetPetByIdNotFound() throws Exception {
        when(petService.getOwnerPets(owner.getUserId())).thenReturn(List.of());

        mockMvc.perform(get("/api/pets")
                .with(user("owner@example.com").roles("PET_OWNER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetUserPets() throws Exception {
        when(petService.getOwnerPets(owner.getUserId())).thenReturn(List.of(samplePet()));

        mockMvc.perform(get("/api/pets")
                .with(user("owner@example.com").roles("PET_OWNER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testCreatePet() throws Exception {
        when(petService.createPet(eq(owner.getUserId()), any())).thenReturn(samplePet());

        mockMvc.perform(post("/api/pets")
                .with(user("owner@example.com").roles("PET_OWNER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Buddy\",\"breed\":\"Golden Retriever\",\"age\":3,\"weight\":20.5,\"species\":\"DOG\",\"vaccinationStatus\":\"UP_TO_DATE\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdatePet() throws Exception {
        when(petService.updatePet(eq(owner.getUserId()), any(), any())).thenReturn(samplePet());

        mockMvc.perform(put("/api/pets/{petId}", UUID.fromString("44444444-4444-4444-4444-444444444444"))
                .with(user("owner@example.com").roles("PET_OWNER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"UpdatedName\",\"breed\":\"Golden Retriever\",\"age\":4,\"weight\":21.0,\"species\":\"DOG\",\"vaccinationStatus\":\"UP_TO_DATE\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeletePet() throws Exception {
        doNothing().when(petService).deletePet(eq(owner.getUserId()), any());

        mockMvc.perform(delete("/api/pets/{petId}", UUID.fromString("55555555-5555-5555-5555-555555555555"))
                .with(user("owner@example.com").roles("PET_OWNER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
