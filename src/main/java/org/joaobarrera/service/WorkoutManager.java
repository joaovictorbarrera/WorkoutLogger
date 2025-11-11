package org.joaobarrera.service;

import jakarta.transaction.Transactional;
import org.joaobarrera.model.OperationResult;
import org.joaobarrera.model.UnitType;
import org.joaobarrera.entity.Workout;
import org.joaobarrera.repository.WorkoutRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/*
 * Joao Barrera
 * CEN 3024 - Software Development 1
 * November 9, 2025
 * WorkoutManager.java
 */

/**
 * The WorkoutManager service class provides core business logic for managing workouts
 * in the Workout Logger application.
 * <p>
 * Responsibilities include connecting to an SQLite database, performing CRUD operations,
 * validating workout data, and converting units between kilometers and miles.
 * <p>
 * All database operations are safely parameterized to prevent SQL injection, and
 * validation ensures that only correct workout data is persisted.
 */
@Service
public class WorkoutManager {
    private final WorkoutRepository workoutRepository;

    /**
     * Constructs a WorkoutManager with the specified WorkoutRepository.
     * <p>
     * The WorkoutRepository provides access to persistent workout data,
     * enabling the manager to perform CRUD operations and other business logic.
     *
     * @param workoutRepository the repository used for database operations
     */
    public WorkoutManager(WorkoutRepository workoutRepository) {
        this.workoutRepository = workoutRepository;
    }

    /**
     * Adds a new workout to the database after validating the data.
     *
     * @param workout the Workout object to add
     * @return OperationResult containing the updated list of workouts and a success/failure message
     */
    @Transactional
    public OperationResult<Workout> addWorkout(Workout workout) {
        try {
            OperationResult<String> validation = validateWorkout(workout);
            if (!validation.success()) {
                return new OperationResult<>(false, null, validation.message());
            }

            Workout saved = workoutRepository.save(workout);
            return new OperationResult<>(true, saved, "Added workout: " + saved.getName());
        } catch (Exception e) {
            return new OperationResult<>(false, null, "Error adding workout: " + e.getMessage());
        }
    }

    /**
     * Retrieves all workouts stored in the database.
     *
     * @return OperationResult containing a list of all workouts and a success/failure message
     */
    public OperationResult<List<Workout>> getAllWorkouts() {
        try {
            List<Workout> workouts = workoutRepository.findAll();
            return new OperationResult<>(true, workouts, "Retrieved all workouts.");
        } catch (Exception e) {
            return new OperationResult<>(false, null, "Error retrieving workouts: " + e.getMessage());
        }
    }

    /**
     * Searches for workouts whose names contain the specified search term (case-insensitive).
     * <p>
     * Returns all workouts if the search term is empty or null.
     *
     * @param searchTerm the name or partial name to search for
     * @return OperationResult containing a list of matching workouts and a success/failure message
     */
    public OperationResult<List<Workout>> getWorkoutsBySearchParameter(String searchTerm) {
        try {
            List<Workout> workouts;
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                workouts = workoutRepository.findAll();
            } else {
                workouts = workoutRepository.findByNameContainingIgnoreCase(searchTerm);
            }
            return new OperationResult<>(true, workouts, "Found " + workouts.size() + " matching workouts.");
        } catch (Exception e) {
            return new OperationResult<>(false, null, "Error searching workouts: " + e.getMessage());
        }
    }


    /**
     * Updates an existing workout identified by the given ID.
     * <p>
     * Validates the workout and updates the database with the new data.
     *
     * @param workoutID the ID of the workout to update
     * @param updatedWorkout the updated workout data
     * @return OperationResult containing the updated list of workouts and a success/failure message
     */
    @Transactional
    public OperationResult<Workout> updateWorkout(Integer workoutID, Workout updatedWorkout) {
        try {
            OperationResult<String> validation = validateWorkout(updatedWorkout);
            if (!validation.success()) return new OperationResult<>(false, null, validation.message());

            Optional<Workout> existingOpt = workoutRepository.findById(workoutID);
            if (existingOpt.isEmpty()) return new OperationResult<>(false, null, "Workout with ID " + workoutID + " not found.");

            Workout existing = existingOpt.get();
            existing.setName(updatedWorkout.getName());
            existing.setDistance(updatedWorkout.getDistance());
            existing.setUnit(updatedWorkout.getUnit());
            existing.setStartDateTime(updatedWorkout.getStartDateTime());
            existing.setDuration(updatedWorkout.getDuration());
            existing.setNotes(updatedWorkout.getNotes());

            Workout saved = workoutRepository.save(existing);
            return new OperationResult<>(true, saved, "Workout " + workoutID + " updated.");
        } catch (Exception e) {
            return new OperationResult<>(false, null, "Error updating workout: " + e.getMessage());
        }
    }

    /**
     * Deletes a workout from the database by its ID.
     *
     * @param workoutID the ID of the workout to delete
     * @return OperationResult containing the updated list of workouts and a success/failure message
     */
    @Transactional
    public OperationResult<Integer> deleteWorkout(Integer workoutID) {
        try {
            if (!workoutRepository.existsById(workoutID)) {
                return new OperationResult<>(false, null, "Workout with ID " + workoutID + " not found.");
            }
            workoutRepository.deleteById(workoutID);
            return new OperationResult<>(true, workoutID, "Deleted workout ID " + workoutID);
        } catch (Exception e) {
            return new OperationResult<>(false, null, "Error deleting workout: " + e.getMessage());
        }
    }

    /**
     * Converts all stored workouts to the specified unit type (kilometers or miles).
     * <p>
     * Updates both the in-memory objects and the database records.
     *
     * @param targetUnit the unit type to convert all workouts to
     * @return OperationResult containing the updated list of workouts and a success/failure message
     */
    @Transactional
    public OperationResult<List<Workout>> convertAllUnits(UnitType targetUnit) {
        if (targetUnit == null) {
            return new OperationResult<>(false, null, "Target unit cannot be null.");
        }

        try {
            List<Workout> workouts = workoutRepository.findAll();
            if (workouts.isEmpty()) return new OperationResult<>(false, null, "No workouts to convert.");

            for (Workout w : workouts) {
                if (w.getUnit() != targetUnit) {
                    double convertedDistance = w.getUnit() == UnitType.KILOMETERS
                            ? w.getDistance() * 0.621371
                            : w.getDistance() / 0.621371;
                    w.setDistance(convertedDistance);
                    w.setUnit(targetUnit);
                }
            }
            workoutRepository.saveAll(workouts);
            return new OperationResult<>(true, workouts, "Converted all workouts to " + targetUnit);
        } catch (Exception e) {
            return new OperationResult<>(false, null, "Error converting workouts: " + e.getMessage());
        }
    }

    // -- Validation methods --
    // All validation methods return an error message if they are invalid

    // Name must be a non-empty string with at most 50 characters
    private String validateName(String name) {
        boolean valid = name != null && !name.trim().isEmpty() && name.length() <= 50;
        return valid ? null : "Workout name cannot be null or empty or longer than 50 characters." ;
    }

    // Start Date Time cannot be null and must be in a valid format
    private String validateStartDateTime(LocalDateTime start) {
        boolean valid = start != null;
        return valid ? null : "Start date/time cannot be null and must be in 'YYYY-MM-DDTHH:MM' format (e.g. 2025-10-04T14:30).";
    }

    // Duration needs to be greater than zero
    private String validateDuration(Integer duration) {
        boolean valid = duration != null && duration >= 1;
        return valid ? null : "Duration must be at least 1 minute.";
    }

    // Distance needs to be non-negative
    private String validateDistance(Double distance) {
        boolean valid = distance != null && distance >= 0;
        return valid ? null : "Distance must be a non-negative number.";
    }

    // Unit cannot be null
    private String validateUnit(UnitType unit) {
        boolean valid = unit != null;
        return valid ? null : "Unit must be either KILOMETERS or MILES.";
    }

    // Notes need to be at most 200 characters
    private String validateNotes(String notes) {
        boolean valid = notes == null || notes.length() <= 200;
        return valid ? null : "Notes cannot exceed 200 characters.";
    }

    // Runs all checks and returns an error message if there's any validation error
    private OperationResult<String> validateWorkout(Workout workout) {
        String error = validateName(workout.getName());
        if (error != null) return new OperationResult<>(false, null, error);

        error = validateStartDateTime(workout.getStartDateTime());
        if (error != null) return new OperationResult<>(false, null, error);

        error = validateDuration(workout.getDuration());
        if (error != null) return new OperationResult<>(false, null, error);

        error = validateDistance(workout.getDistance());
        if (error != null) return new OperationResult<>(false, null, error);

        error = validateUnit(workout.getUnit());
        if (error != null) return new OperationResult<>(false, null, error);

        error = validateNotes(workout.getNotes());
        if (error != null) return new OperationResult<>(false, null, error);

        return new OperationResult<>(true, null, "Workout is valid.");
    }
}