package com.dgca.flightlogbook.ui.model;

public class Aircraft {
    private Long id;
    private String registrationTail;
    private String typeDesignator;
    private String engineType;

    public Aircraft() {}

    public Aircraft(Long id, String registrationTail, String typeDesignator, String engineType) {
        this.id = id;
        this.registrationTail = registrationTail;
        this.typeDesignator = typeDesignator;
        this.engineType = engineType;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRegistrationTail() { return registrationTail; }
    public void setRegistrationTail(String registrationTail) { this.registrationTail = registrationTail; }

    public String getTypeDesignator() { return typeDesignator; }
    public void setTypeDesignator(String typeDesignator) { this.typeDesignator = typeDesignator; }

    public String getEngineType() { return engineType; }
    public void setEngineType(String engineType) { this.engineType = engineType; }

    @Override
    public String toString() {
        String friendly = getFriendlyName(typeDesignator);
        if (friendly != null) {
            return registrationTail + " - " + friendly + " (" + engineType + ")";
        }
        return registrationTail + " (" + typeDesignator + " - " + engineType + ")";
    }

    private String getFriendlyName(String code) {
        if (code == null) return null;
        switch (code.trim().toUpperCase()) {
            case "C150": return "Cessna 150";
            case "C172": return "Cessna 172";
            case "A320": return "Airbus A320";
            case "A350": return "Airbus A350";
            case "P32": return "Pilatus P32";
            case "PC12": return "Pilatus PC-12";
            case "PA28": return "Piper PA-28";
            case "B737": return "Boeing 737";
            default: return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Aircraft aircraft = (Aircraft) o;
        return id != null ? id.equals(aircraft.id) : (registrationTail != null && registrationTail.equals(aircraft.registrationTail));
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : (registrationTail != null ? registrationTail.hashCode() : 0);
    }
}
