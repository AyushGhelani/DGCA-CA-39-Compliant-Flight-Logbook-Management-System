package com.dgca.flightlogbook.dto;

import java.util.List;

/**
 * CA-39 summary report DTO grouping total hours by aircraft type, day/night, and PIC/Dual.
 */
public class CA39ReportResponse {

    private Long studentId;
    private String studentName;
    private String licenseNumber;
    private Integer totalFlightMinutes;
    private Integer totalPicMinutes;
    private Integer totalSicMinutes;
    private Integer totalDualMinutes;
    private Integer totalDayMinutes;
    private Integer totalNightMinutes;
    private Integer totalInstrumentMinutes;
    private List<AircraftTypeSummary> byAircraftType;

    public CA39ReportResponse() {}

    // ---- Getters and Setters ----

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public Integer getTotalFlightMinutes() { return totalFlightMinutes; }
    public void setTotalFlightMinutes(Integer totalFlightMinutes) { this.totalFlightMinutes = totalFlightMinutes; }

    public Integer getTotalPicMinutes() { return totalPicMinutes; }
    public void setTotalPicMinutes(Integer totalPicMinutes) { this.totalPicMinutes = totalPicMinutes; }

    public Integer getTotalSicMinutes() { return totalSicMinutes; }
    public void setTotalSicMinutes(Integer totalSicMinutes) { this.totalSicMinutes = totalSicMinutes; }

    public Integer getTotalDualMinutes() { return totalDualMinutes; }
    public void setTotalDualMinutes(Integer totalDualMinutes) { this.totalDualMinutes = totalDualMinutes; }

    public Integer getTotalDayMinutes() { return totalDayMinutes; }
    public void setTotalDayMinutes(Integer totalDayMinutes) { this.totalDayMinutes = totalDayMinutes; }

    public Integer getTotalNightMinutes() { return totalNightMinutes; }
    public void setTotalNightMinutes(Integer totalNightMinutes) { this.totalNightMinutes = totalNightMinutes; }

    public Integer getTotalInstrumentMinutes() { return totalInstrumentMinutes; }
    public void setTotalInstrumentMinutes(Integer totalInstrumentMinutes) { this.totalInstrumentMinutes = totalInstrumentMinutes; }

    public List<AircraftTypeSummary> getByAircraftType() { return byAircraftType; }
    public void setByAircraftType(List<AircraftTypeSummary> byAircraftType) { this.byAircraftType = byAircraftType; }

    // ---- Builder ----

    public static CA39ReportResponseBuilder builder() { return new CA39ReportResponseBuilder(); }

    public static class CA39ReportResponseBuilder {
        private final CA39ReportResponse r = new CA39ReportResponse();

        public CA39ReportResponseBuilder studentId(Long v) { r.studentId = v; return this; }
        public CA39ReportResponseBuilder studentName(String v) { r.studentName = v; return this; }
        public CA39ReportResponseBuilder licenseNumber(String v) { r.licenseNumber = v; return this; }
        public CA39ReportResponseBuilder totalFlightMinutes(Integer v) { r.totalFlightMinutes = v; return this; }
        public CA39ReportResponseBuilder totalPicMinutes(Integer v) { r.totalPicMinutes = v; return this; }
        public CA39ReportResponseBuilder totalSicMinutes(Integer v) { r.totalSicMinutes = v; return this; }
        public CA39ReportResponseBuilder totalDualMinutes(Integer v) { r.totalDualMinutes = v; return this; }
        public CA39ReportResponseBuilder totalDayMinutes(Integer v) { r.totalDayMinutes = v; return this; }
        public CA39ReportResponseBuilder totalNightMinutes(Integer v) { r.totalNightMinutes = v; return this; }
        public CA39ReportResponseBuilder totalInstrumentMinutes(Integer v) { r.totalInstrumentMinutes = v; return this; }
        public CA39ReportResponseBuilder byAircraftType(List<AircraftTypeSummary> v) { r.byAircraftType = v; return this; }

        public CA39ReportResponse build() { return r; }
    }

    // ---- Inner Class: AircraftTypeSummary ----

    public static class AircraftTypeSummary {
        private String aircraftType;
        private String engineType;
        private Integer totalMinutes;
        private Integer picMinutes;
        private Integer sicMinutes;
        private Integer dualMinutes;
        private Integer dayMinutes;
        private Integer nightMinutes;
        private Integer instrumentMinutes;
        private Integer numberOfFlights;

        public AircraftTypeSummary() {}

        public String getAircraftType() { return aircraftType; }
        public void setAircraftType(String aircraftType) { this.aircraftType = aircraftType; }

        public String getEngineType() { return engineType; }
        public void setEngineType(String engineType) { this.engineType = engineType; }

        public Integer getTotalMinutes() { return totalMinutes; }
        public void setTotalMinutes(Integer totalMinutes) { this.totalMinutes = totalMinutes; }

        public Integer getPicMinutes() { return picMinutes; }
        public void setPicMinutes(Integer picMinutes) { this.picMinutes = picMinutes; }

        public Integer getSicMinutes() { return sicMinutes; }
        public void setSicMinutes(Integer sicMinutes) { this.sicMinutes = sicMinutes; }

        public Integer getDualMinutes() { return dualMinutes; }
        public void setDualMinutes(Integer dualMinutes) { this.dualMinutes = dualMinutes; }

        public Integer getDayMinutes() { return dayMinutes; }
        public void setDayMinutes(Integer dayMinutes) { this.dayMinutes = dayMinutes; }

        public Integer getNightMinutes() { return nightMinutes; }
        public void setNightMinutes(Integer nightMinutes) { this.nightMinutes = nightMinutes; }

        public Integer getInstrumentMinutes() { return instrumentMinutes; }
        public void setInstrumentMinutes(Integer instrumentMinutes) { this.instrumentMinutes = instrumentMinutes; }

        public Integer getNumberOfFlights() { return numberOfFlights; }
        public void setNumberOfFlights(Integer numberOfFlights) { this.numberOfFlights = numberOfFlights; }

        // ---- Builder ----

        public static AircraftTypeSummaryBuilder builder() { return new AircraftTypeSummaryBuilder(); }

        public static class AircraftTypeSummaryBuilder {
            private final AircraftTypeSummary s = new AircraftTypeSummary();

            public AircraftTypeSummaryBuilder aircraftType(String v) { s.aircraftType = v; return this; }
            public AircraftTypeSummaryBuilder engineType(String v) { s.engineType = v; return this; }
            public AircraftTypeSummaryBuilder totalMinutes(Integer v) { s.totalMinutes = v; return this; }
            public AircraftTypeSummaryBuilder picMinutes(Integer v) { s.picMinutes = v; return this; }
            public AircraftTypeSummaryBuilder sicMinutes(Integer v) { s.sicMinutes = v; return this; }
            public AircraftTypeSummaryBuilder dualMinutes(Integer v) { s.dualMinutes = v; return this; }
            public AircraftTypeSummaryBuilder dayMinutes(Integer v) { s.dayMinutes = v; return this; }
            public AircraftTypeSummaryBuilder nightMinutes(Integer v) { s.nightMinutes = v; return this; }
            public AircraftTypeSummaryBuilder instrumentMinutes(Integer v) { s.instrumentMinutes = v; return this; }
            public AircraftTypeSummaryBuilder numberOfFlights(Integer v) { s.numberOfFlights = v; return this; }

            public AircraftTypeSummary build() { return s; }
        }
    }
}
