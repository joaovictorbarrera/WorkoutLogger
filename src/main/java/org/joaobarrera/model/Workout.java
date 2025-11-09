package org.joaobarrera.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.joaobarrera.config.LocalDateTimeDeserializer;
import org.joaobarrera.config.UnitTypeDeserializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*
 * Joao Barrera
 * CEN 3024 - Software Development 1
 * November 9, 2025
 * Workout.java
 */

/**
 * Represents a single workout record in the Workout Logger application.
 * <p>
 * Stores details such as the workout name, start time, duration, distance,
 * unit of measurement (kilometers or miles), and optional notes.
 * <p>
 * Used for creating, updating, displaying, and persisting workout information.
 */

public class Workout {
    private Integer id = null;
    private String name;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime startDateTime;

    @JsonDeserialize(using = UnitTypeDeserializer.class)
    private UnitType unit;

    private Integer duration; // in minutes
    private Double distance;
    private String notes;

    /**
     * Default constructor required for JSON deserialization.
     */
    public Workout () {}

    /**
     * Constructs a new Workout with all fields specified.
     * <p>
     * Used to create workout records with complete information for storage
     * or transfer between client and server.
     *
     * @param id the unique identifier for this workout
     * @param name the name of the workout
     * @param startDateTime the starting date and time of the workout
     * @param duration the duration of the workout in minutes
     * @param distance the distance covered in the workout
     * @param unit the unit type of the distance (KILOMETERS or MILES)
     * @param notes optional notes for the workout
     */
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

    /**
     * Returns the ID of the workout.
     * <p>
     * The ID is assigned when the workout is added to the database.
     *
     * @return the workout ID
     */
    public Integer getID() { return id; }

    /**
     * Sets the ID of the workout.
     * <p>
     * Typically assigned automatically when adding a workout to the database.
     *
     * @param id the workout ID to set
     */
    public void setID(Integer id) { this.id = id; }

    /**
     * Returns the name of the workout.
     *
     * @return the workout name
     */
    public String getName() { return name; }

    /**
     * Sets the name of the workout.
     *
     * @param name the workout name to set
     */
    public void setName(String name) { this.name = name; }

    /**
     * Returns the start date and time of the workout.
     *
     * @return the workout start date and time
     */
    public LocalDateTime getStartDateTime() { return startDateTime; }

    /**
     * Sets the start date and time of the workout.
     *
     * @param startDateTime the start date and time to set
     */
    public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }

    /**
     * Returns the duration of the workout in minutes.
     *
     * @return workout duration in minutes
     */
    public Integer getDuration() { return duration; }

    /**
     * Sets the duration of the workout in minutes.
     *
     * @param duration duration in minutes
     */
    public void setDuration(Integer duration) { this.duration = duration; }

    /**
     * Returns the distance covered in the workout.
     *
     * @return workout distance
     */
    public Double getDistance() { return distance; }

    /**
     * Sets the distance covered in the workout.
     *
     * @param distance the distance to set
     */
    public void setDistance(Double distance) { this.distance = distance; }

    /**
     * Returns the unit type of the workout (kilometers or miles).
     *
     * @return the unit type
     */
    public UnitType getUnit() { return unit; }

    /**
     * Sets the unit type of the workout (kilometers or miles).
     *
     * @param unit the unit type to set
     */
    public void setUnit(UnitType unit) { this.unit = unit; }

    /**
     * Returns optional notes associated with the workout.
     *
     * @return the workout notes
     */
    public String getNotes() { return notes; }

    /**
     * Sets optional notes for the workout.
     *
     * @param notes the notes to set
     */
    public void setNotes(String notes) { this.notes = notes; }


    /**
     * Returns a string representation of the workout.
     * <p>
     * Includes the ID, name, formatted start date and time, duration, distance with unit, and notes.
     * <p>
     * Useful for debugging, logging, or displaying workout information in a readable format.
     *
     * @return formatted string describing the workout
     */
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