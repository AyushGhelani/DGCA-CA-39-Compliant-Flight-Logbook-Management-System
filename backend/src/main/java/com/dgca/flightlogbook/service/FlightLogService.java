package com.dgca.flightlogbook.service;

import com.dgca.flightlogbook.dto.FlightLogRequest;
import com.dgca.flightlogbook.dto.FlightLogResponse;
import com.dgca.flightlogbook.entity.*;
import com.dgca.flightlogbook.exception.BusinessValidationException;
import com.dgca.flightlogbook.exception.ResourceNotFoundException;
import com.dgca.flightlogbook.repository.AircraftRepository;
import com.dgca.flightlogbook.repository.FlightLogRepository;
import com.dgca.flightlogbook.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Core service implementing DGCA CA-39 compliant flight log business logic.
 * <p>
 * Key validations:
 * <ul>
 *   <li>Block time auto-calculation (blockOn - blockOff in UTC)</li>
 *   <li>Capacity split verification: PIC + SIC + Dual == totalFlightTime</li>
 *   <li>Day/Night split verification: dayTime + nightTime == totalFlightTime</li>
 *   <li>Record locking: CERTIFIED logs cannot be modified</li>
 * </ul>
 */
@Service
public class FlightLogService {

    @Autowired
    private FlightLogRepository flightLogRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AircraftRepository aircraftRepository;

    // ──────────────────────────── CREATE ────────────────────────────

    /**
     * Create a new flight log entry with full DGCA validation.
     */
    @Transactional
    public FlightLogResponse createFlightLog(FlightLogRequest request) {
        // 1. Resolve entities
        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student not found with id: " + request.getStudentId()));

        if (student.getRole() != Role.STUDENT) {
            throw new BusinessValidationException(
                    "User with id " + request.getStudentId() + " is not a STUDENT");
        }

        Aircraft aircraft = aircraftRepository.findById(request.getAircraftId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aircraft not found with id: " + request.getAircraftId()));

        User instructor = null;
        if (request.getInstructorId() != null) {
            instructor = userRepository.findById(request.getInstructorId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Instructor not found with id: " + request.getInstructorId()));
            if (instructor.getRole() != Role.CFI) {
                throw new BusinessValidationException(
                        "User with id " + request.getInstructorId() + " is not a CFI");
            }
        }

        // 2. Block time calculation (UTC)
        int totalFlightTime = calculateBlockTime(request);

        // 3. Capacity split verification: PIC + SIC + Dual == totalFlightTime
        int capacitySum = request.getPicTime()
                + (request.getSicTime() != null ? request.getSicTime() : 0)
                + (request.getDualTime() != null ? request.getDualTime() : 0);
        if (capacitySum != totalFlightTime) {
            throw new BusinessValidationException(
                    "Capacity split mismatch: PIC(" + request.getPicTime()
                    + ") + SIC(" + request.getSicTime()
                    + ") + Dual(" + request.getDualTime()
                    + ") = " + capacitySum
                    + " does not equal total block time of " + totalFlightTime + " minutes");
        }

        // 4. Day/Night split verification: dayTime + nightTime == totalFlightTime
        int dayNightSum = request.getDayTime()
                + (request.getNightTime() != null ? request.getNightTime() : 0);
        if (dayNightSum != totalFlightTime) {
            throw new BusinessValidationException(
                    "Day/Night split mismatch: Day(" + request.getDayTime()
                    + ") + Night(" + request.getNightTime()
                    + ") = " + dayNightSum
                    + " does not equal total block time of " + totalFlightTime + " minutes");
        }

        // 5. Instrument time cannot exceed total flight time
        int instrumentTime = request.getInstrumentTime() != null ? request.getInstrumentTime() : 0;
        if (instrumentTime > totalFlightTime) {
            throw new BusinessValidationException(
                    "Instrument time (" + instrumentTime
                    + " min) cannot exceed total flight time (" + totalFlightTime + " min)");
        }

        // 6. Build and persist entity
        FlightLog log = FlightLog.builder()
                .dateUtc(request.getDateUtc())
                .departureAerodrome(request.getDepartureAerodrome().toUpperCase())
                .arrivalAerodrome(request.getArrivalAerodrome().toUpperCase())
                .blockOffUtc(request.getBlockOffUtc())
                .blockOnUtc(request.getBlockOnUtc())
                .totalFlightTime(totalFlightTime)
                .picTime(request.getPicTime())
                .sicTime(request.getSicTime() != null ? request.getSicTime() : 0)
                .dualTime(request.getDualTime() != null ? request.getDualTime() : 0)
                .dayTime(request.getDayTime())
                .nightTime(request.getNightTime() != null ? request.getNightTime() : 0)
                .instrumentTime(instrumentTime)
                .remarks(request.getRemarks())
                .status(FlightLogStatus.PENDING)
                .student(student)
                .instructor(instructor)
                .aircraft(aircraft)
                .build();

        FlightLog saved = flightLogRepository.save(log);
        return mapToResponse(saved);
    }

    // ──────────────────────────── READ ────────────────────────────

    /**
     * Retrieve all logs for a student, with optional date range and status filtering.
     */
    @Transactional(readOnly = true)
    public List<FlightLogResponse> getStudentLogs(Long studentId,
                                                   LocalDate fromDate,
                                                   LocalDate toDate,
                                                   String status) {
        // Validate student exists
        if (!userRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with id: " + studentId);
        }

        FlightLogStatus logStatus = null;
        if (status != null && !status.isBlank()) {
            try {
                logStatus = FlightLogStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BusinessValidationException(
                        "Invalid status filter: '" + status + "'. Must be PENDING, CERTIFIED, or REJECTED");
            }
        }

        List<FlightLog> logs;
        if (fromDate == null && toDate == null && logStatus == null) {
            logs = flightLogRepository.findByStudentIdWithDetails(studentId);
        } else {
            logs = flightLogRepository.findByStudentIdFiltered(studentId, fromDate, toDate, logStatus);
        }

        return logs.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // ──────────────────────────── UPDATE ────────────────────────────

    /**
     * Update a flight log – only allowed if status is PENDING.
     */
    @Transactional
    public FlightLogResponse updateFlightLog(Long logId, FlightLogRequest request) {
        FlightLog existing = flightLogRepository.findById(logId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Flight log not found with id: " + logId));

        if (existing.getStatus() != FlightLogStatus.PENDING) {
            throw new IllegalStateException(
                    "Cannot update flight log with status " + existing.getStatus()
                    + ". Only PENDING logs can be modified.");
        }

        // Resolve updated entities
        Aircraft aircraft = aircraftRepository.findById(request.getAircraftId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aircraft not found with id: " + request.getAircraftId()));

        User instructor = null;
        if (request.getInstructorId() != null) {
            instructor = userRepository.findById(request.getInstructorId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Instructor not found with id: " + request.getInstructorId()));
        }

        // Re-run block time and split validations
        int totalFlightTime = calculateBlockTime(request);

        int capacitySum = request.getPicTime()
                + (request.getSicTime() != null ? request.getSicTime() : 0)
                + (request.getDualTime() != null ? request.getDualTime() : 0);
        if (capacitySum != totalFlightTime) {
            throw new BusinessValidationException(
                    "Capacity split mismatch on update: sum = " + capacitySum
                    + ", expected = " + totalFlightTime);
        }

        int dayNightSum = request.getDayTime()
                + (request.getNightTime() != null ? request.getNightTime() : 0);
        if (dayNightSum != totalFlightTime) {
            throw new BusinessValidationException(
                    "Day/Night split mismatch on update: sum = " + dayNightSum
                    + ", expected = " + totalFlightTime);
        }

        // Apply updates
        existing.setDateUtc(request.getDateUtc());
        existing.setDepartureAerodrome(request.getDepartureAerodrome().toUpperCase());
        existing.setArrivalAerodrome(request.getArrivalAerodrome().toUpperCase());
        existing.setBlockOffUtc(request.getBlockOffUtc());
        existing.setBlockOnUtc(request.getBlockOnUtc());
        existing.setTotalFlightTime(totalFlightTime);
        existing.setPicTime(request.getPicTime());
        existing.setSicTime(request.getSicTime() != null ? request.getSicTime() : 0);
        existing.setDualTime(request.getDualTime() != null ? request.getDualTime() : 0);
        existing.setDayTime(request.getDayTime());
        existing.setNightTime(request.getNightTime() != null ? request.getNightTime() : 0);
        existing.setInstrumentTime(request.getInstrumentTime() != null ? request.getInstrumentTime() : 0);
        existing.setRemarks(request.getRemarks());
        existing.setAircraft(aircraft);
        existing.setInstructor(instructor);

        FlightLog updated = flightLogRepository.save(existing);
        return mapToResponse(updated);
    }

    // ──────────────────── CERTIFICATION ────────────────────────

    /**
     * Get all PENDING logs assigned to a specific instructor.
     */
    @Transactional(readOnly = true)
    public List<FlightLogResponse> getPendingLogsForInstructor(Long instructorId) {
        if (!userRepository.existsById(instructorId)) {
            throw new ResourceNotFoundException("Instructor not found with id: " + instructorId);
        }
        return flightLogRepository.findPendingByInstructorId(instructorId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    /**
     * Certify a flight log – permanently locks the record.
     */
    @Transactional
    public FlightLogResponse certifyLog(Long logId, Long instructorId) {
        FlightLog log = flightLogRepository.findById(logId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Flight log not found with id: " + logId));

        if (log.getStatus() != FlightLogStatus.PENDING) {
            throw new IllegalStateException(
                    "Flight log is already " + log.getStatus() + " and cannot be certified again.");
        }

        // Verify the certifying instructor is the assigned instructor
        if (log.getInstructor() == null || !log.getInstructor().getId().equals(instructorId)) {
            throw new BusinessValidationException(
                    "Only the assigned instructor (id: "
                    + (log.getInstructor() != null ? log.getInstructor().getId() : "none")
                    + ") can certify this log.");
        }

        log.setStatus(FlightLogStatus.CERTIFIED);
        FlightLog saved = flightLogRepository.save(log);
        return mapToResponse(saved);
    }

    /**
     * Reject a flight log.
     */
    @Transactional
    public FlightLogResponse rejectLog(Long logId, Long instructorId, String reason) {
        FlightLog log = flightLogRepository.findById(logId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Flight log not found with id: " + logId));

        if (log.getStatus() != FlightLogStatus.PENDING) {
            throw new IllegalStateException("Flight log is already " + log.getStatus());
        }

        if (log.getInstructor() == null || !log.getInstructor().getId().equals(instructorId)) {
            throw new BusinessValidationException("Only the assigned instructor can reject this log.");
        }

        log.setStatus(FlightLogStatus.REJECTED);
        log.setRemarks((log.getRemarks() != null ? log.getRemarks() + " | " : "")
                + "REJECTED: " + reason);
        FlightLog saved = flightLogRepository.save(log);
        return mapToResponse(saved);
    }

    // ──────────────────── BLOCK TIME CALCULATION ────────────────────

    /**
     * Calculate total block time in minutes: blockOnUtc - blockOffUtc.
     * Handles overnight flights (block on is next day).
     */
    private int calculateBlockTime(FlightLogRequest request) {
        long minutes = Duration.between(request.getBlockOffUtc(), request.getBlockOnUtc()).toMinutes();

        // Handle overnight flights where blockOn is the next day
        if (minutes < 0) {
            minutes += 24 * 60; // add 24 hours
        }

        if (minutes <= 0) {
            throw new BusinessValidationException(
                    "Block On time must be after Block Off time. Calculated duration: " + minutes + " minutes");
        }

        return (int) minutes;
    }

    // ──────────────────── ENTITY → DTO MAPPING ────────────────────

    private FlightLogResponse mapToResponse(FlightLog log) {
        return FlightLogResponse.builder()
                .id(log.getId())
                .dateUtc(log.getDateUtc())
                .departureAerodrome(log.getDepartureAerodrome())
                .arrivalAerodrome(log.getArrivalAerodrome())
                .blockOffUtc(log.getBlockOffUtc())
                .blockOnUtc(log.getBlockOnUtc())
                .totalFlightTime(log.getTotalFlightTime())
                .picTime(log.getPicTime())
                .sicTime(log.getSicTime())
                .dualTime(log.getDualTime())
                .dayTime(log.getDayTime())
                .nightTime(log.getNightTime())
                .instrumentTime(log.getInstrumentTime())
                .remarks(log.getRemarks())
                .status(log.getStatus())
                .studentId(log.getStudent().getId())
                .studentName(log.getStudent().getFullName())
                .instructorId(log.getInstructor() != null ? log.getInstructor().getId() : null)
                .instructorName(log.getInstructor() != null ? log.getInstructor().getFullName() : null)
                .aircraftId(log.getAircraft().getId())
                .aircraftRegistration(log.getAircraft().getRegistrationTail())
                .aircraftType(log.getAircraft().getTypeDesignator())
                .build();
    }
}
