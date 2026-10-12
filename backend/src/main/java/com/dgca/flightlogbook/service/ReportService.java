package com.dgca.flightlogbook.service;

import com.dgca.flightlogbook.dto.CA39ReportResponse;
import com.dgca.flightlogbook.entity.FlightLog;
import com.dgca.flightlogbook.entity.User;
import com.dgca.flightlogbook.exception.ResourceNotFoundException;
import com.dgca.flightlogbook.repository.FlightLogRepository;
import com.dgca.flightlogbook.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service generating DGCA CA-39 summary reports.
 * Aggregates certified flight hours by aircraft type, capacity, and day/night splits.
 */
@Service
public class ReportService {

    @Autowired
    private FlightLogRepository flightLogRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Generate a CA-39 summary report for a specific student.
     * Only CERTIFIED logs are included.
     */
    @Transactional(readOnly = true)
    public CA39ReportResponse generateCA39Summary(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student not found with id: " + studentId));

        List<FlightLog> certifiedLogs = flightLogRepository.findCertifiedByStudentId(studentId);

        // Overall totals
        int totalFlight = 0, totalPic = 0, totalSic = 0, totalDual = 0;
        int totalDay = 0, totalNight = 0, totalInstrument = 0;

        for (FlightLog log : certifiedLogs) {
            totalFlight += log.getTotalFlightTime();
            totalPic += log.getPicTime();
            totalSic += log.getSicTime();
            totalDual += log.getDualTime();
            totalDay += log.getDayTime();
            totalNight += log.getNightTime();
            totalInstrument += log.getInstrumentTime();
        }

        // Group by aircraft type designator
        Map<String, List<FlightLog>> byType = certifiedLogs.stream()
                .collect(Collectors.groupingBy(
                        log -> log.getAircraft().getTypeDesignator()));

        List<CA39ReportResponse.AircraftTypeSummary> typeSummaries = byType.entrySet().stream()
                .map(entry -> {
                    String type = entry.getKey();
                    List<FlightLog> logs = entry.getValue();
                    String engineType = logs.get(0).getAircraft().getEngineType().name();

                    return CA39ReportResponse.AircraftTypeSummary.builder()
                            .aircraftType(type)
                            .engineType(engineType)
                            .totalMinutes(logs.stream().mapToInt(FlightLog::getTotalFlightTime).sum())
                            .picMinutes(logs.stream().mapToInt(FlightLog::getPicTime).sum())
                            .sicMinutes(logs.stream().mapToInt(FlightLog::getSicTime).sum())
                            .dualMinutes(logs.stream().mapToInt(FlightLog::getDualTime).sum())
                            .dayMinutes(logs.stream().mapToInt(FlightLog::getDayTime).sum())
                            .nightMinutes(logs.stream().mapToInt(FlightLog::getNightTime).sum())
                            .instrumentMinutes(logs.stream().mapToInt(FlightLog::getInstrumentTime).sum())
                            .numberOfFlights(logs.size())
                            .build();
                })
                .collect(Collectors.toList());

        return CA39ReportResponse.builder()
                .studentId(student.getId())
                .studentName(student.getFullName())
                .licenseNumber(student.getLicenseNumber())
                .totalFlightMinutes(totalFlight)
                .totalPicMinutes(totalPic)
                .totalSicMinutes(totalSic)
                .totalDualMinutes(totalDual)
                .totalDayMinutes(totalDay)
                .totalNightMinutes(totalNight)
                .totalInstrumentMinutes(totalInstrument)
                .byAircraftType(typeSummaries)
                .build();
    }
}
