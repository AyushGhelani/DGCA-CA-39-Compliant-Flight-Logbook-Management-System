package com.dgca.flightlogbook.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO for creating/updating a flight log entry.
 * Business validations (capacity split, day/night split) are enforced in the service layer.
 */
public class FlightLogRequest {

    @NotNull(message = "Flight date (UTC) is required")
    private LocalDate dateUtc;

    @NotBlank(message = "Departure aerodrome ICAO code is required")
    @Size(min = 4, max = 4, message = "Departure aerodrome must be a 4-letter ICAO code")
    private String departureAerodrome;

    @NotBlank(message = "Arrival aerodrome ICAO code is required")
    @Size(min = 4, max = 4, message = "Arrival aerodrome must be a 4-letter ICAO code")
    private String arrivalAerodrome;

    @NotNull(message = "Block off time (UTC) is required")
    private LocalTime blockOffUtc;

    @NotNull(message = "Block on time (UTC) is required")
    private LocalTime blockOnUtc;

    @NotNull(message = "Aircraft ID is required")
    private Long aircraftId;

    @NotNull(message = "Student (pilot) user ID is required")
    private Long studentId;

    private Long instructorId;

    @NotNull(message = "PIC time (minutes) is required")
    @Min(0)
    private Integer picTime;

    @Min(0)
    private Integer sicTime = 0;

    @Min(0)
    private Integer dualTime = 0;

    @NotNull(message = "Day time (minutes) is required")
    @Min(0)
    private Integer dayTime;

    @Min(0)
    private Integer nightTime = 0;

    @Min(0)
    private Integer instrumentTime = 0;

    @Size(max = 500)
    private String remarks;

    public FlightLogRequest() {}

    // ---- Getters and Setters ----

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

    public Long getAircraftId() { return aircraftId; }
    public void setAircraftId(Long aircraftId) { this.aircraftId = aircraftId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getInstructorId() { return instructorId; }
    public void setInstructorId(Long instructorId) { this.instructorId = instructorId; }

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
}
