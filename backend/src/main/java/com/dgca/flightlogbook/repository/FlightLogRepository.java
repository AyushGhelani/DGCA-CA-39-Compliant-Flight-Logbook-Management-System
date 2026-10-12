package com.dgca.flightlogbook.repository;

import com.dgca.flightlogbook.entity.FlightLog;
import com.dgca.flightlogbook.entity.FlightLogStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FlightLogRepository extends JpaRepository<FlightLog, Long> {

    /**
     * Fetch all logs for a student with eager-loaded aircraft and instructor,
     * avoiding N+1 queries.
     */
    @Query("SELECT fl FROM FlightLog fl " +
           "JOIN FETCH fl.aircraft " +
           "LEFT JOIN FETCH fl.instructor " +
           "WHERE fl.student.id = :studentId " +
           "ORDER BY fl.dateUtc DESC, fl.blockOffUtc DESC")
    List<FlightLog> findByStudentIdWithDetails(@Param("studentId") Long studentId);

    /**
     * Filter a student's logs by date range and optional status.
     */
    @Query("SELECT fl FROM FlightLog fl " +
           "JOIN FETCH fl.aircraft " +
           "LEFT JOIN FETCH fl.instructor " +
           "WHERE fl.student.id = :studentId " +
           "AND (:fromDate IS NULL OR fl.dateUtc >= :fromDate) " +
           "AND (:toDate IS NULL OR fl.dateUtc <= :toDate) " +
           "AND (:status IS NULL OR fl.status = :status) " +
           "ORDER BY fl.dateUtc DESC")
    List<FlightLog> findByStudentIdFiltered(
            @Param("studentId") Long studentId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("status") FlightLogStatus status);

    /**
     * Logs awaiting certification from a specific instructor.
     */
    @Query("SELECT fl FROM FlightLog fl " +
           "JOIN FETCH fl.aircraft " +
           "JOIN FETCH fl.student " +
           "WHERE fl.instructor.id = :instructorId " +
           "AND fl.status = 'PENDING' " +
           "ORDER BY fl.dateUtc ASC")
    List<FlightLog> findPendingByInstructorId(@Param("instructorId") Long instructorId);

    /**
     * All certified logs for a student – used for CA-39 reporting.
     */
    @Query("SELECT fl FROM FlightLog fl " +
           "JOIN FETCH fl.aircraft " +
           "WHERE fl.student.id = :studentId " +
           "AND fl.status = 'CERTIFIED' " +
           "ORDER BY fl.dateUtc ASC")
    List<FlightLog> findCertifiedByStudentId(@Param("studentId") Long studentId);
}
