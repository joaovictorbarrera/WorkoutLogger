package org.joaobarrera;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Workout {
    private Integer workoutID = null; // Assigned when added
    private final String name;
    private final LocalDateTime startDateTime;
    private final Integer duration; // in minutes
    private final Double distance;
    private final UnitType unit;
    private final String notes;

    public Workout(String name, LocalDateTime startDateTime, Integer duration,
                   Double distance, UnitType unit, String notes) {
        this.name = name;
        this.startDateTime = startDateTime;
        this.duration = duration;
        this.distance = distance;
        this.unit = unit;
        this.notes = notes;
    }

    public Workout(Workout other) {
        this.name = other.name;
        this.startDateTime = other.startDateTime;
        this.duration = other.duration;
        this.distance = other.distance;
        this.unit = other.unit;
        this.notes = other.notes;
        this.workoutID = other.getID();
    }

    // Returns a new workout with the distance units converted to the given target
    public Workout convertUnit(UnitType targetUnit) {
        // If already in the target unit, return itself
        if (this.unit == targetUnit) {
            return this;
        }

        double convertedDistance = this.distance;

        if (this.unit == UnitType.MILES && targetUnit == UnitType.KILOMETERS) {
            convertedDistance = this.distance * 1.60934;
        } else if (this.unit == UnitType.KILOMETERS && targetUnit == UnitType.MILES) {
            convertedDistance = this.distance / 1.60934;
        }

        Workout convertedWorkout = new Workout(
                this.name,
                this.startDateTime,
                this.duration,
                convertedDistance,
                targetUnit,
                this.notes
        );

        // Preserve the same Workout ID if it was already assigned
        if (this.workoutID != null) {
            convertedWorkout.setID(this.workoutID);
        }

        return convertedWorkout;
    }

    // Getters and Setters
    public Integer getID() { return workoutID; }
    public void setID(int id) { this.workoutID = id; }

    public String getName() { return name; }
    public LocalDateTime getStartDateTime() { return startDateTime; }
    public Integer getDuration() { return duration; }
    public Double getDistance() { return distance; }
    public UnitType getUnit() { return unit; }
    public String getNotes() { return notes; }

    @Override
    public String toString() {
        return "WorkoutID: " + workoutID +
                ", Name: " + name +
                ", Start: " + startDateTime.format(DateTimeFormatter.ofPattern("MM/dd/yyyy 'at' HH:mm")) +
                ", Duration: " + duration + " minutes" +
                ", Distance: " + String.format("%.2f", distance) +
                " " + unit +
                ", Notes: " + notes;
    }

}
