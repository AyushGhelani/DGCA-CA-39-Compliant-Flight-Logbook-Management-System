package com.dgca.flightlogbook.dto;

import com.dgca.flightlogbook.entity.FlightLogStatus;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Read-only response DTO for a flight log entry.
 */
public class FlightLogResponse {

    private Long id;
    private LocalDate dateUtc;
    private String departureAerodrome;
    private String arrivalAerodrome;
    private LocalTime blockOffUtc;
    private LocalTime blockOnUtc;
    private Integer totalFlightTime;
    private Integer picTime;
    private Integer sicTime;
    private Integer dualTime;
    private Integer dayTime;
    private Integer nightTime;
    private Integer instrumentTime;
    private String remarks;
    private FlightLogStatus status;
    private Long studentId;
    private String studentName;
    private Long instructorId;
    private String instructorName;
    private Long aircraftId;
    private String aircraftRegistration;
    private String aircraftType;

    public FlightLogResponse() {}

    // ---- Getters and Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDateUtc() { return dateUtc; }
    public void setDateUtc(LocalDate dateUtc) { this.dateUtc = dateUtc; }

    public String getDepartureAerodrome() { return departureAerodrome; }
    public void setDepartureAerodrome(String departureAerodrome) { this.departureAerodrome = departureAerodrome; }

    public String getArrivalAerodrome() { return arrivalAerodrome; }
    public void setArrivalAerodrome(String arrivalAerodrome) { this.arrivalAerodrome = arrivalAerodrome; }

    public LocalTime getBlockOffUtc() { return blockOffUtc; }
    public void setBlockOffUtc(LocalTime blockOffUtc) { this.blockOffUtc = blockOffUtc; }

    public LocalTime getBlockOnUtc() { return blockOnUtc; }
    public void setBlockOnUtc(LocalTime blockOnUtc) { this.blockOnUtc = blockOnUtc; }

    public Integer getTotalFlightTime() { return totalFlightTime; }
    public void setTotalFlightTime(Integer totalFlightTime) { this.totalFlightTime = totalFlightTime; }

    public Integer getPicTime() { return picTime; }
    public void setPicTime(Integer picTime) { this.picTime = picTime; }

    public Integer getSicTime() { return sicTime; }
    public void setSicTime(Integer sicTime) { this.sicTime = sicTime; }

    public Integer getDualTime() { return dualTime; }
    public void setDualTime(Integer dualTime) { this.dualTime = dualTime; }

    public Integer getDayTime() { return dayTime; }
    public void setDayTime(Integer dayTime) { this.dayTime = dayTime; }

    public Integer getNightTime() { return nightTime; }
    public void setNightTime(Integer nightTime) { this.nightTime = nightTime; }

    public Integer getInstrumentTime() { return instrumentTime; }
    public void setInstrumentTime(Integer instrumentTime) { this.instrumentTime = instrumentTime; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public FlightLogStatus getStatus() { return status; }
    public void setStatus(FlightLogStatus status) { this.status = status; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public Long getInstructorId() { return instructorId; }
    public void setInstructorId(Long instructorId) { this.instructorId = instructorId; }

    public String getInstructorName() { return instructorName; }
    public void setInstructorName(String instructorName) { this.instructorName = instructorName; }

    public Long getAircraftId() { return aircraftId; }
    public void setAircraftId(Long aircraftId) { this.aircraftId = aircraftId; }

    public String getAircraftRegistration() { return aircraftRegistration; }
    public void setAircraftRegistration(String aircraftRegistration) { this.aircraftRegistration = aircraftRegistration; }

    public String getAircraftType() { return aircraftType; }
    public void setAircraftType(String aircraftType) { this.aircraftType = aircraftType; }

    // ---- Builder ----

    public static FlightLogResponseBuilder builder() { return new FlightLogResponseBuilder(); }

    public static class FlightLogResponseBuilder {
        private final FlightLogResponse r = new FlightLogResponse();

        public FlightLogResponseBuilder id(Long v) { r.id = v; return this; }
        public FlightLogResponseBuilder dateUtc(LocalDate v) { r.dateUtc = v; return this; }
        public FlightLogResponseBuilder departureAerodrome(String v) { r.departureAerodrome = v; return this; }
        public FlightLogResponseBuilder arrivalAerodrome(String v) { r.arrivalAerodrome = v; return this; }
        public FlightLogResponseBuilder blockOffUtc(LocalTime v) { r.blockOffUtc = v; return this; }
        public FlightLogResponseBuilder blockOnUtc(LocalTime v) { r.blockOnUtc = v; return this; }
        public FlightLogResponseBuilder totalFlightTime(Integer v) { r.totalFlightTime = v; return this; }
        public FlightLogResponseBuilder picTime(Integer v) { r.picTime = v; return this; }
        public FlightLogResponseBuilder sicTime(Integer v) { r.sicTime = v; return this; }
        public FlightLogResponseBuilder dualTime(Integer v) { r.dualTime = v; return this; }
        public FlightLogResponseBuilder dayTime(Integer v) { r.dayTime = v; return this; }
        public FlightLogResponseBuilder nightTime(Integer v) { r.nightTime = v; return this; }
        public FlightLogResponseBuilder instrumentTime(Integer v) { r.instrumentTime = v; return this; }
        public FlightLogResponseBuilder remarks(String v) { r.remarks = v; return this; }
        public FlightLogResponseBuilder status(FlightLogStatus v) { r.status = v; return this; }
        public FlightLogResponseBuilder studentId(Long v) { r.studentId = v; return this; }
        public FlightLogResponseBuilder studentName(String v) { r.studentName = v; return this; }
        public FlightLogResponseBuilder instructorId(Long v) { r.instructorId = v; return this; }
        public FlightLogResponseBuilder instructorName(String v) { r.instructorName = v; return this; }
        public FlightLogResponseBuilder aircraftId(Long v) { r.aircraftId = v; return this; }
        public FlightLogResponseBuilder aircraftRegistration(String v) { r.aircraftRegistration = v; return this; }
        public FlightLogResponseBuilder aircraftType(String v) { r.aircraftType = v; return this; }

        public FlightLogResponse build() { return r; }
    }
}
