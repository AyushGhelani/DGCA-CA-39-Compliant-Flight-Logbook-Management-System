package com.dgca.flightlogbook.service;

import com.dgca.flightlogbook.entity.Aircraft;
import com.dgca.flightlogbook.exception.BusinessValidationException;
import com.dgca.flightlogbook.exception.ResourceNotFoundException;
import com.dgca.flightlogbook.repository.AircraftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for aircraft CRUD operations.
 */
@Service
public class AircraftService {

    @Autowired
    private AircraftRepository aircraftRepository;

    @Transactional
    public Aircraft createAircraft(Aircraft aircraft) {
        if (aircraftRepository.existsByRegistrationTail(aircraft.getRegistrationTail())) {
            throw new BusinessValidationException(
                    "Aircraft with registration '" + aircraft.getRegistrationTail() + "' already exists");
        }
        return aircraftRepository.save(aircraft);
    }

    @Transactional(readOnly = true)
    public List<Aircraft> getAllAircraft() {
        return aircraftRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Aircraft getById(Long id) {
        return aircraftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aircraft not found with id: " + id));
    }
}
