package com.dgca.flightlogbook.controller;

import com.dgca.flightlogbook.entity.Aircraft;
import com.dgca.flightlogbook.service.AircraftService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Aircraft management controller.
 * GET is public; POST requires ADMIN role.
 */
@RestController
@RequestMapping("/api/aircraft")
public class AircraftController {

    @Autowired
    private AircraftService aircraftService;

    /**
     * POST /api/aircraft – Add a new aircraft (ADMIN only).
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Aircraft> createAircraft(@Valid @RequestBody Aircraft aircraft) {
        Aircraft saved = aircraftService.createAircraft(aircraft);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * GET /api/aircraft – List all aircraft.
     */
    @GetMapping
    public ResponseEntity<List<Aircraft>> getAllAircraft() {
        return ResponseEntity.ok(aircraftService.getAllAircraft());
    }

    /**
     * GET /api/aircraft/{id} – Get aircraft by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Aircraft> getAircraftById(@PathVariable Long id) {
        return ResponseEntity.ok(aircraftService.getById(id));
    }
}
