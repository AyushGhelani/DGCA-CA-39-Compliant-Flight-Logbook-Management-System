package com.dgca.flightlogbook.ui.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class FlightLog {
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
    private String status;
    private Long studentId;
    private String studentName;
    private Long instructorId;
    private String instructorName;
    private Long aircraftId;
    private String aircraftRegistration;
    private String aircraftType;

    public FlightLog() {}

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

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

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

    public String getRoute() {
        return (departureAerodrome != null ? departureAerodrome : "") + " -> " + (arrivalAerodrome != null ? arrivalAerodrome : "");
    }
}
