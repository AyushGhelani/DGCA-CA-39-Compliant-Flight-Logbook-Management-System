package com.dgca.flightlogbook.entity;

/**
 * Enum representing user roles within the DGCA Logbook System.
 * <ul>
 *   <li>STUDENT  – Logs flights and submits for certification</li>
 *   <li>CFI      – Chief Flying Instructor; certifies/rejects flight logs</li>
 *   <li>ADMIN    – Generates official CA-39 reports</li>
 * </ul>
 */
public enum Role {
    STUDENT,
    CFI,
    ADMIN
}
