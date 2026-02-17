package com.example.booking.service;

import com.example.booking.dto.BookingRequest;
import com.example.booking.model.Booking;
import com.example.booking.model.BookingStatus;
import com.example.booking.model.Facility;
import com.example.booking.model.User;
import com.example.booking.repository.BookingRepository;
import com.example.booking.repository.FacilityRepository;
import com.example.booking.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final FacilityRepository facilityRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository, FacilityRepository facilityRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.facilityRepository = facilityRepository;
        this.userRepository = userRepository;
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Transactional
    public Booking createBooking(BookingRequest request) {
        // Validate Facility
        Facility facility = facilityRepository.findById(request.getFacilityId())
                .orElseThrow(() -> new IllegalArgumentException("Facility not found"));

        // Validate User
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Validate Times
        if (request.getStartTime().isAfter(request.getEndTime())) {
             throw new IllegalArgumentException("Start time must be before end time");
        }

        // Check Conflicts
        List<Booking> conflicts = bookingRepository.findConflictingBookings(
                request.getFacilityId(),
                request.getDate(),
                request.getStartTime(),
                request.getEndTime()
        );

        if (!conflicts.isEmpty()) {
            throw new IllegalArgumentException("Time slot is not available");
        }

        Booking booking = new Booking();
        booking.setFacility(facility);
        booking.setUser(user);
        booking.setDate(request.getDate());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setStatus(BookingStatus.CONFIRMED); // Auto-confirm for simplicity

        return bookingRepository.save(booking);
    }

    public Optional<Booking> updateBooking(Long id, BookingRequest request) {
        return bookingRepository.findById(id).map(existing -> {
             // Validate Times
            if (request.getStartTime().isAfter(request.getEndTime())) {
                throw new IllegalArgumentException("Start time must be before end time");
            }
            
             // Check Conflicts (excluding current booking) -- simplified logic: if generic conflict exists and it's not THIS booking
            List<Booking> conflicts = bookingRepository.findConflictingBookings(
                    existing.getFacility().getId(), // Assuming facility doesn't change for simplicity
                    request.getDate(),
                    request.getStartTime(),
                    request.getEndTime()
            );
            
            boolean conflictExists = conflicts.stream().anyMatch(b -> !b.getId().equals(id));
            if(conflictExists) {
                 throw new IllegalArgumentException("Time slot is not available");
            }

            existing.setDate(request.getDate());
            existing.setStartTime(request.getStartTime());
            existing.setEndTime(request.getEndTime());
            return bookingRepository.save(existing);
        });
    }

    public void cancelBooking(Long id) {
        bookingRepository.findById(id).ifPresent(booking -> {
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);
        });
    }
    
    public List<Booking> checkAvailability(Long facilityId, LocalDate date) {
        // Returns all bookings for that day so frontend can calculate free slots
        return bookingRepository.findByFacilityIdAndDate(facilityId, date);
    }
}
