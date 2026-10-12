package com.dgca.flightlogbook.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Aircraft entity – stores aircraft type and tail registration per DGCA requirements.
 */
@Entity
@Table(name = "aircraft", uniqueConstraints = {
        @UniqueConstraint(columnNames = "registrationTail")
})
public class Aircraft {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, unique = true, length = 20)
    private String registrationTail;

    @NotBlank
    @Size(max = 30)
    @Column(nullable = false, length = 30)
    private String typeDesignator;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private EngineType engineType;

    @OneToMany(mappedBy = "aircraft", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<FlightLog> flightLogs = new ArrayList<>();

    public Aircraft() {}

    public Aircraft(Long id, String registrationTail, String typeDesignator, EngineType engineType) {
        this.id = id;
        this.registrationTail = registrationTail;
        this.typeDesignator = typeDesignator;
        this.engineType = engineType;
    }

    // ---- Getters and Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRegistrationTail() { return registrationTail; }
    public void setRegistrationTail(String registrationTail) { this.registrationTail = registrationTail; }

    public String getTypeDesignator() { return typeDesignator; }
    public void setTypeDesignator(String typeDesignator) { this.typeDesignator = typeDesignator; }

    public EngineType getEngineType() { return engineType; }
    public void setEngineType(EngineType engineType) { this.engineType = engineType; }

    public List<FlightLog> getFlightLogs() { return flightLogs; }
    public void setFlightLogs(List<FlightLog> flightLogs) { this.flightLogs = flightLogs; }
}
