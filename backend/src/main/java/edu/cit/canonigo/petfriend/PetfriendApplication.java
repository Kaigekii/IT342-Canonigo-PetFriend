package edu.cit.canonigo.petfriend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import edu.cit.canonigo.petfriend.model.User;
import edu.cit.canonigo.petfriend.model.UserRole;
import edu.cit.canonigo.petfriend.repository.UserRepository;

@SpringBootApplication
public class PetfriendApplication {

	private static final String DEFAULT_ADMIN_EMAIL = "admin@petfriend.com";
	private static final String DEFAULT_ADMIN_PASSWORD = "Admin123!";

	public static void main(String[] args) {
		SpringApplication.run(PetfriendApplication.class, args);
	}

	@Bean
	CommandLineRunner seedDefaultAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			User admin = userRepository.findByEmail(DEFAULT_ADMIN_EMAIL)
					.orElseGet(() -> new User(
							DEFAULT_ADMIN_EMAIL,
							passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD),
							"Admin",
							"User",
							null,
							null,
							UserRole.ADMIN,
							true
					));

			admin.setRole(UserRole.ADMIN);
			admin.setIsVerified(true);
			admin.setPasswordHash(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD));
			if (admin.getFirstName() == null || admin.getFirstName().isBlank()) {
				admin.setFirstName("Admin");
			}
			if (admin.getLastName() == null || admin.getLastName().isBlank()) {
				admin.setLastName("User");
			}

			userRepository.save(admin);
		};
	}

}
