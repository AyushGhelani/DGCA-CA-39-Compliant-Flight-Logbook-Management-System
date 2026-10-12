package com.dgca.flightlogbook.controller;

import com.dgca.flightlogbook.entity.Role;
import com.dgca.flightlogbook.entity.User;
import com.dgca.flightlogbook.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * User lookup controller – provides instructor listing for form dropdowns.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    /**
     * GET /api/users/instructors – Returns all CFI users as {id, fullName}.
     * Requires authentication (any role).
     */
    @GetMapping("/instructors")
    public ResponseEntity<List<Map<String, Object>>> getInstructors() {
        List<Map<String, Object>> instructors = userRepository.findByRole(Role.CFI)
                .stream()
                .map(u -> Map.<String, Object>of("id", u.getId(), "fullName", u.getFullName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(instructors);
    }
}
