package com.dgca.flightlogbook.controller;

import com.dgca.flightlogbook.dto.FlightLogRequest;
import com.dgca.flightlogbook.dto.FlightLogResponse;
import com.dgca.flightlogbook.entity.User;
import com.dgca.flightlogbook.repository.UserRepository;
import com.dgca.flightlogbook.service.FlightLogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Logbook management controller – CRUD operations for flight logs.
 */
@RestController
@RequestMapping("/api/logs")
public class FlightLogController {

    @Autowired
    private FlightLogService flightLogService;

    @Autowired
    private UserRepository userRepository;

    /**
     * POST /api/logs – Add a new flight record.
     * Triggers block time calculation and DGCA capacity/day-night validations.
     * STUDENT may only create logs for their own studentId.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'CFI', 'ADMIN')")
    public ResponseEntity<FlightLogResponse> createFlightLog(
            @Valid @RequestBody FlightLogRequest request,
            Authentication authentication) {
        enforceStudentOwnership(authentication, request.getStudentId());
        FlightLogResponse response = flightLogService.createFlightLog(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/logs/student/{id} – Retrieve a specific student's logs
     * with optional date range and status filtering.
     * STUDENT may only view their own logs.
     *
     * Query params: fromDate, toDate (yyyy-MM-dd), status (PENDING|CERTIFIED|REJECTED)
     */
    @GetMapping("/student/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'CFI', 'ADMIN')")
    public ResponseEntity<List<FlightLogResponse>> getStudentLogs(
            @PathVariable Long id,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String status,
            Authentication authentication) {
        enforceStudentOwnership(authentication, id);
        List<FlightLogResponse> logs = flightLogService.getStudentLogs(id, fromDate, toDate, status);
        return ResponseEntity.ok(logs);
    }

    /**
     * PUT /api/logs/{id} – Update a flight record (only if PENDING).
     * STUDENT may only update their own logs.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<FlightLogResponse> updateFlightLog(
            @PathVariable Long id,
            @Valid @RequestBody FlightLogRequest request,
            Authentication authentication) {
        enforceStudentOwnership(authentication, request.getStudentId());
        FlightLogResponse response = flightLogService.updateFlightLog(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * If the authenticated user has ROLE_STUDENT, enforce that the target
     * studentId matches their own user ID. ADMIN and CFI are unrestricted.
     */
    private void enforceStudentOwnership(Authentication authentication, Long targetStudentId) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isCfi = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CFI"));
        if (isAdmin || isCfi) {
            return;
        }
        // Must be STUDENT – check ownership
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Authenticated user not found"));
        if (!user.getId().equals(targetStudentId)) {
            throw new AccessDeniedException("Students may only access their own logs");
        }
    }
}

