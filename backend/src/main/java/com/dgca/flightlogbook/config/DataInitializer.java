package com.dgca.flightlogbook.config;

import com.dgca.flightlogbook.entity.Aircraft;
import com.dgca.flightlogbook.entity.EngineType;
import com.dgca.flightlogbook.entity.Role;
import com.dgca.flightlogbook.entity.User;
import com.dgca.flightlogbook.entity.FlightLog;
import com.dgca.flightlogbook.entity.FlightLogStatus;
import com.dgca.flightlogbook.repository.AircraftRepository;
import com.dgca.flightlogbook.repository.FlightLogRepository;
import com.dgca.flightlogbook.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Initializes dummy aircraft fleet, instructors, test accounts, and flight logs on application startup
 * if they are not already present in the database.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private AircraftRepository aircraftRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FlightLogRepository flightLogRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initAircraft();
        initUsers();
        initFlightLogs();
    }

    private void initAircraft() {
        seedAircraftIfMissing("VT-C150", "C150", EngineType.SINGLE);
        seedAircraftIfMissing("VT-A320", "A320", EngineType.MULTI);
        seedAircraftIfMissing("VT-A350", "A350", EngineType.MULTI);
        seedAircraftIfMissing("VT-P32", "P32", EngineType.SINGLE);
        seedAircraftIfMissing("VT-C172", "C172", EngineType.SINGLE);
        seedAircraftIfMissing("VT-PA28", "PA28", EngineType.SINGLE);
        seedAircraftIfMissing("VT-B737", "B737", EngineType.MULTI);
    }

    private void seedAircraftIfMissing(String tail, String type, EngineType engine) {
        if (!aircraftRepository.existsByRegistrationTail(tail)) {
            Aircraft aircraft = new Aircraft();
            aircraft.setRegistrationTail(tail);
            aircraft.setTypeDesignator(type);
            aircraft.setEngineType(engine);
            aircraftRepository.save(aircraft);
            log.info("Seeded aircraft: {} ({}, {})", tail, type, engine);
        }
    }

    private void initUsers() {
        // Seed default student
        seedUserIfMissing("student_raj", "Pilot@123", "Raj Kumar", "raj@aviation.in", "SPL-2024-001", Role.STUDENT);

        // Seed default instructors (CFI)
        seedUserIfMissing("cfi_sharma", "Instructor@123", "Capt. Anil Sharma", "sharma@aviation.in", "ATPL-2020-042", Role.CFI);
        seedUserIfMissing("cfi_khanna", "Instructor@123", "Capt. Rajesh Khanna", "khanna@aviation.in", "ATPL-2018-019", Role.CFI);
        seedUserIfMissing("cfi_patel", "Instructor@123", "Capt. Priya Patel", "patel@aviation.in", "ATPL-2021-088", Role.CFI);
        seedUserIfMissing("cfi_malhotra", "Instructor@123", "Capt. Vikram Malhotra", "malhotra@aviation.in", "ATPL-2019-055", Role.CFI);
        seedUserIfMissing("cfi_sen", "Instructor@123", "Capt. Meera Sen", "sen@aviation.in", "ATPL-2022-104", Role.CFI);

        // Seed default admin
        seedUserIfMissing("admin_dgca", "Admin@123", "DGCA Admin", "admin@dgca.gov.in", "ADMIN-001", Role.ADMIN);
    }

    private void seedUserIfMissing(String username, String rawPassword, String fullName, String email, String license, Role role) {
        if (!userRepository.existsByUsername(username)) {
            User user = User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(rawPassword))
                    .fullName(fullName)
                    .email(email)
                    .licenseNumber(license)
                    .role(role)
                    .build();
            userRepository.save(user);
            log.info("Seeded user [{}]: {} ({})", role, fullName, username);
        }
    }

    private void initFlightLogs() {
        if (flightLogRepository.count() == 0) {
            User student = userRepository.findByUsername("student_raj").orElse(null);
            User instructor = userRepository.findByUsername("cfi_sharma").orElse(null);
            Aircraft c150 = aircraftRepository.findByRegistrationTail("VT-C150").orElse(null);
            Aircraft a320 = aircraftRepository.findByRegistrationTail("VT-A320").orElse(null);
            Aircraft p32 = aircraftRepository.findByRegistrationTail("VT-P32").orElse(null);

            if (student != null && instructor != null && c150 != null && a320 != null && p32 != null) {
                // 1. Certified Flight 1 - Cessna 150
                FlightLog log1 = new FlightLog();
                log1.setStudent(student);
                log1.setInstructor(instructor);
                log1.setAircraft(c150);
                log1.setDateUtc(LocalDate.now().minusDays(5));
                log1.setDepartureAerodrome("VIDP");
                log1.setArrivalAerodrome("VIDP");
                log1.setBlockOffUtc(LocalTime.of(9, 0));
                log1.setBlockOnUtc(LocalTime.of(10, 30));
                log1.setTotalFlightTime(90);
                log1.setPicTime(0);
                log1.setDualTime(90);
                log1.setSicTime(0);
                log1.setDayTime(90);
                log1.setNightTime(0);
                log1.setInstrumentTime(20);
                log1.setStatus(FlightLogStatus.CERTIFIED);
                log1.setRemarks("Circuit practice & dual flight training");
                flightLogRepository.save(log1);

                // 2. Certified Flight 2 - Airbus A320
                FlightLog log2 = new FlightLog();
                log2.setStudent(student);
                log2.setInstructor(instructor);
                log2.setAircraft(a320);
                log2.setDateUtc(LocalDate.now().minusDays(3));
                log2.setDepartureAerodrome("VIDP");
                log2.setArrivalAerodrome("VABB");
                log2.setBlockOffUtc(LocalTime.of(18, 0));
                log2.setBlockOnUtc(LocalTime.of(20, 0));
                log2.setTotalFlightTime(120);
                log2.setPicTime(120);
                log2.setDualTime(0);
                log2.setSicTime(0);
                log2.setDayTime(60);
                log2.setNightTime(60);
                log2.setInstrumentTime(60);
                log2.setStatus(FlightLogStatus.CERTIFIED);
                log2.setRemarks("Cross-country navigation VIDP to VABB");
                flightLogRepository.save(log2);

                // 3. Pending Flight 3 - Pilatus P32
                FlightLog log3 = new FlightLog();
                log3.setStudent(student);
                log3.setInstructor(instructor);
                log3.setAircraft(p32);
                log3.setDateUtc(LocalDate.now().minusDays(1));
                log3.setDepartureAerodrome("VABB");
                log3.setArrivalAerodrome("VOBL");
                log3.setBlockOffUtc(LocalTime.of(14, 0));
                log3.setBlockOnUtc(LocalTime.of(15, 15));
                log3.setTotalFlightTime(75);
                log3.setPicTime(75);
                log3.setDualTime(0);
                log3.setSicTime(0);
                log3.setDayTime(75);
                log3.setNightTime(0);
                log3.setInstrumentTime(15);
                log3.setStatus(FlightLogStatus.PENDING);
                log3.setRemarks("Sector flight VABB to VOBL awaiting CFI certification");
                flightLogRepository.save(log3);

                log.info("Seeded initial flight logs for student_raj (2 CERTIFIED, 1 PENDING)");
            }
        }
    }
}
