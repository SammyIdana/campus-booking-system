package com.example.booking.service;

import com.example.booking.model.Facility;
import com.example.booking.model.Role;
import com.example.booking.model.User;
import com.example.booking.repository.FacilityRepository;
import com.example.booking.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final FacilityRepository facilityRepository;

    public DataSeeder(UserRepository userRepository, FacilityRepository facilityRepository) {
        this.userRepository = userRepository;
        this.facilityRepository = facilityRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            userRepository.save(new User(null, "Admin User", "admin@example.com", Role.ADMIN));
            userRepository.save(new User(null, "John Doe", "john@example.com", Role.STUDENT));
            userRepository.save(new User(null, "Jane Smith", "jane@example.com", Role.STAFF));
        }

        if (facilityRepository.count() == 0) {
            facilityRepository.save(new Facility(null, "Main Hall", "Building A", 500));
            facilityRepository.save(new Facility(null, "Conference Room 1", "Building B", 20));
            facilityRepository.save(new Facility(null, "Tennis Court", "Sports Complex", 4));
        }
    }
}
