package org.joaobarrera.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.joaobarrera.config.LocalDateTimeDeserializer;
import org.joaobarrera.config.UnitTypeDeserializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Workout {
    private Integer id = null; // Assigned when added
    private String name;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime startDateTime;

    @JsonDeserialize(using = UnitTypeDeserializer.class)
    private UnitType unit;

    private Integer duration; // in minutes
    private Double distance;
    private String notes;

    // Deserializer needs default constructor
    public Workout () {}

    public Workout(Integer id, String name, LocalDateTime startDateTime, Integer duration,
                   Double distance, UnitType unit, String notes) {
        this.id = id;
        this.name = name;
        this.startDateTime = startDateTime;
        this.duration = duration;
        this.distance = distance;
        this.unit = unit;
        this.notes = notes;
    }

    public Workout(Workout other) {
        this.id = other.id;
        this.name = other.name;
        this.startDateTime = other.startDateTime;
        this.duration = other.duration;
        this.distance = other.distance;
        this.unit = other.unit;
        this.notes = other.notes;
    }

    // Getters and Setters
    public Integer getID() { return id; }
    public void setID(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getStartDateTime() { return startDateTime; }
    public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }

    public UnitType getUnit() { return unit; }
    public void setUnit(UnitType unit) { this.unit = unit; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return "id: " + id +
                ", Name: " + name +
                ", Start: " + (startDateTime == null ? "null" : startDateTime.format(DateTimeFormatter.ofPattern("MM/dd/yyyy 'at' HH:mm"))) +
                ", Duration: " + duration + " minutes" +
                ", Distance: " + String.format("%.2f", distance) +
                " " + unit +
                ", Notes: " + notes;
    }

}