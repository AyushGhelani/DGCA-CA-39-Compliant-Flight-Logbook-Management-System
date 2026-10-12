package com.dgca.flightlogbook.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * FlightLog entity – the core transaction entity capturing a single sortie.
 * Business Rules (DGCA CA-39):
 *   total_flight_time = block_on_utc - block_off_utc (computed, in minutes)
 *   pic_time + sic_time + dual_time == total_flight_time
 *   day_time + night_time == total_flight_time
 */
@Entity
@Table(name = "flight_logs", indexes = {
        @Index(name = "idx_student_date", columnList = "student_id, dateUtc"),
        @Index(name = "idx_instructor_status", columnList = "instructor_id, status")
})
public class FlightLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private LocalDate dateUtc;

    @NotBlank
    @Size(max = 4)
    @Column(nullable = false, length = 4)
    private String departureAerodrome;

    @NotBlank
    @Size(max = 4)
    @Column(nullable = false, length = 4)
    private String arrivalAerodrome;

    @NotNull
    @Column(nullable = false)
    private LocalTime blockOffUtc;

    @NotNull
    @Column(nullable = false)
    private LocalTime blockOnUtc;

    @Column(nullable = false)
    private Integer totalFlightTime;

    @Column(nullable = false)
    private Integer picTime;

    @Column(nullable = false)
    private Integer sicTime = 0;

    @Column(nullable = false)
    private Integer dualTime = 0;

    @Column(nullable = false)
    private Integer dayTime;

    @Column(nullable = false)
    private Integer nightTime = 0;

    @Column(nullable = false)
    private Integer instrumentTime = 0;

    @Size(max = 500)
    @Column(length = 500)
    private String remarks;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private FlightLogStatus status = FlightLogStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id")
    private User instructor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aircraft_id", nullable = false)
    private Aircraft aircraft;

    public FlightLog() {}

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

    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }

    public User getInstructor() { return instructor; }
    public void setInstructor(User instructor) { this.instructor = instructor; }

    public Aircraft getAircraft() { return aircraft; }
    public void setAircraft(Aircraft aircraft) { this.aircraft = aircraft; }

    // ---- Builder ----

    public static FlightLogBuilder builder() { return new FlightLogBuilder(); }

    public static class FlightLogBuilder {
        private final FlightLog log = new FlightLog();

        public FlightLogBuilder id(Long id) { log.id = id; return this; }
        public FlightLogBuilder dateUtc(LocalDate dateUtc) { log.dateUtc = dateUtc; return this; }
        public FlightLogBuilder departureAerodrome(String v) { log.departureAerodrome = v; return this; }
        public FlightLogBuilder arrivalAerodrome(String v) { log.arrivalAerodrome = v; return this; }
        public FlightLogBuilder blockOffUtc(LocalTime v) { log.blockOffUtc = v; return this; }
        public FlightLogBuilder blockOnUtc(LocalTime v) { log.blockOnUtc = v; return this; }
        public FlightLogBuilder totalFlightTime(Integer v) { log.totalFlightTime = v; return this; }
        public FlightLogBuilder picTime(Integer v) { log.picTime = v; return this; }
        public FlightLogBuilder sicTime(Integer v) { log.sicTime = v; return this; }
        public FlightLogBuilder dualTime(Integer v) { log.dualTime = v; return this; }
        public FlightLogBuilder dayTime(Integer v) { log.dayTime = v; return this; }
        public FlightLogBuilder nightTime(Integer v) { log.nightTime = v; return this; }
        public FlightLogBuilder instrumentTime(Integer v) { log.instrumentTime = v; return this; }
        public FlightLogBuilder remarks(String v) { log.remarks = v; return this; }
        public FlightLogBuilder status(FlightLogStatus v) { log.status = v; return this; }
        public FlightLogBuilder student(User v) { log.student = v; return this; }
        public FlightLogBuilder instructor(User v) { log.instructor = v; return this; }
        public FlightLogBuilder aircraft(Aircraft v) { log.aircraft = v; return this; }

        public FlightLog build() { return log; }
    }
}
