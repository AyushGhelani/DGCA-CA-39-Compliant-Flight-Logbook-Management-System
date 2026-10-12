package com.dgca.flightlogbook.ui.model;

public class Instructor {
    private Long id;
    private String fullName;

    public Instructor() {}

    public Instructor(Long id, String fullName) {
        this.id = id;
        this.fullName = fullName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    @Override
    public String toString() {
        return fullName != null ? fullName : ("Instructor #" + id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Instructor that = (Instructor) o;
        return id != null ? id.equals(that.id) : (fullName != null && fullName.equals(that.fullName));
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : (fullName != null ? fullName.hashCode() : 0);
    }
}
