package com.school.roster.school_roster_backend.init;

import com.school.roster.school_roster_backend.entity.User;
import com.school.roster.school_roster_backend.entity.enums.Role;
import com.school.roster.school_roster_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class DatabaseInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initializeDatabase() {
        return args -> {
            boolean adminExists = userRepository.findAll().stream()
                    .anyMatch(user -> user.getRoles() != null && user.getRoles().contains(Role.ADMIN));

            if (!adminExists) {
                User admin = new User();
                admin.setId(generateRandom12DigitId());
                admin.setEmail("admin@school.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRoles(Set.of(Role.ADMIN));
                userRepository.save(admin);

                System.out.println("✅ Default Admin user created: email=admin@school.com password=admin123");
            }
        };
    }

    private String generateRandom12DigitId() {
        long number = (long) (Math.random() * 900000000000L) + 100000000000L;
        return String.valueOf(number);
    }
}
