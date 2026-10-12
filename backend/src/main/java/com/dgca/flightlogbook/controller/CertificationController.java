package com.dgca.flightlogbook.controller;

import com.dgca.flightlogbook.dto.FlightLogResponse;
import com.dgca.flightlogbook.entity.User;
import com.dgca.flightlogbook.repository.UserRepository;
import com.dgca.flightlogbook.service.FlightLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Certification controller – CFI workflow for certifying/rejecting flight logs.
 */
@RestController
@RequestMapping("/api/certification")
public class CertificationController {

    @Autowired
    private FlightLogService flightLogService;

    @Autowired
    private UserRepository userRepository;

    /**
     * GET /api/certification/pending?instructorId={id}
     * Returns logs assigned to this CFI that are awaiting signature.
     */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('CFI')")
    public ResponseEntity<List<FlightLogResponse>> getPendingLogs(
            @RequestParam Long instructorId,
            Authentication authentication) {
        enforceCfiOwnership(authentication, instructorId);
        List<FlightLogResponse> pending = flightLogService.getPendingLogsForInstructor(instructorId);
        return ResponseEntity.ok(pending);
    }

    /**
     * POST /api/certification/{logId}/certify?instructorId={id}
     * Changes status to CERTIFIED, permanently locking the record.
     */
    @PostMapping("/{logId}/certify")
    @PreAuthorize("hasRole('CFI')")
    public ResponseEntity<FlightLogResponse> certifyLog(
            @PathVariable Long logId,
            @RequestParam Long instructorId,
            Authentication authentication) {
        enforceCfiOwnership(authentication, instructorId);
        FlightLogResponse response = flightLogService.certifyLog(logId, instructorId);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/certification/{logId}/reject?instructorId={id}
     * Changes status to REJECTED with a reason.
     */
    @PostMapping("/{logId}/reject")
    @PreAuthorize("hasRole('CFI')")
    public ResponseEntity<FlightLogResponse> rejectLog(
            @PathVariable Long logId,
            @RequestParam Long instructorId,
            @RequestBody Map<String, String> body,
            Authentication authentication) {
        enforceCfiOwnership(authentication, instructorId);
        String reason = body.getOrDefault("reason", "No reason provided");
        FlightLogResponse response = flightLogService.rejectLog(logId, instructorId, reason);
        return ResponseEntity.ok(response);
    }

    /**
     * If the authenticated user has ROLE_CFI, enforce that instructorId matches
     * their own user ID.
     */
    private void enforceCfiOwnership(Authentication authentication, Long instructorId) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return;
        }
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Authenticated user not found"));
        if (!user.getId().equals(instructorId)) {
            throw new AccessDeniedException("CFI may only access their own pending logs");
        }
    }
}

