package com.dgca.flightlogbook.ui.model;

import java.util.List;

public class CA39Report {
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

    public CA39Report() {}

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
    }
}
