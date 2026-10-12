package com.dgca.flightlogbook.controller;

import com.dgca.flightlogbook.dto.CA39ReportResponse;
import com.dgca.flightlogbook.entity.User;
import com.dgca.flightlogbook.repository.UserRepository;
import com.dgca.flightlogbook.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Reporting controller – generates DGCA CA-39 formatted reports.
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserRepository userRepository;

    /**
     * GET /api/reports/ca39-summary/{studentId}
     * Generates structured reports grouping total hours by aircraft type,
     * day/night, and PIC/Dual to match the DGCA CA-39 submission format.
     * STUDENT may only view their own report.
     */
    @GetMapping("/ca39-summary/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CFI', 'STUDENT')")
    public ResponseEntity<CA39ReportResponse> getCA39Summary(
            @PathVariable Long studentId,
            Authentication authentication) {
        // Enforce student ownership
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isCfi = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CFI"));
        if (!isAdmin && !isCfi) {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new AccessDeniedException("Authenticated user not found"));
            if (!user.getId().equals(studentId)) {
                throw new AccessDeniedException("Students may only view their own CA-39 report");
            }
        }
        CA39ReportResponse report = reportService.generateCA39Summary(studentId);
        return ResponseEntity.ok(report);
    }
}

