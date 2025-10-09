package org.joaobarrera;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.*;

// Handles creating, updating, deleting, searching, importing, and exporting workouts.
// Stores all workouts in memory and provides validation and file handling utilities.
class WorkoutManager {
    private final List<Workout> workouts = new ArrayList<>();
    private int nextID = 1;

    public WorkoutManager() {}

    // Checks if a given workout ID exists
    public boolean IDExists(int workoutID) {
        return workouts.stream().anyMatch(w -> w.getID() == workoutID);
    }

    // Adds a new workout after validating it and generating a unique ID
    public OperationResult<Workout> addWorkout(Workout workout) {
        if (isWorkoutValid(workout)) {
            do {
                workout.setID(nextID++);
            } while (IDExists(workout.getID()));

            workouts.add(workout);
            return new OperationResult<>(true, workout, "Added workout: " + workout.getName());
        }
        return new OperationResult<>(false, null, "Workout " + workout.getName() + " is invalid.");
    }

    // Retrieves the full list of workouts
    public List<Workout> getAllWorkouts() { return workouts; }

    // Returns all workouts whose names contain the given search term (case-insensitive).
    // Always succeeds, even if no results are found. Returns a full list if the search-term is an empty string.
    public OperationResult<List<Workout>> getWorkoutsBySearchParameter(String searchTerm) {
        List<Workout> results = new ArrayList<>();
        for (Workout w : workouts) {
            if (w.getName().toLowerCase().contains(searchTerm.toLowerCase())) results.add(w);
        }
        return new OperationResult<>(true, results, "Search for \"" + searchTerm + "\" returned " + results.size() + " results");
    }

    // Updates a workout in place by matching its ID and replacing it with the specified workout data
    public OperationResult<Workout> updateWorkout(int WorkoutID, Workout updatedWorkout) {
        if (!isWorkoutValid(updatedWorkout)) {
            return new OperationResult<>(false, null, "Workout " + updatedWorkout.getName() + " is invalid.");
        }

        for (int i = 0; i < workouts.size(); i++) {
            if (workouts.get(i).getID() == WorkoutID) {
                updatedWorkout.setID(WorkoutID);
                workouts.set(i, updatedWorkout);
                return new OperationResult<>(true, updatedWorkout, "Updated workout ID: " + WorkoutID);
            }
        }
        return new OperationResult<>(false, null, "Workout ID " + WorkoutID + " does not exist.");
    }

    // Deletes all occurrences of a workout with a matching ID
    public OperationResult<Workout> deleteWorkout(int WorkoutID) {
        Workout lastDeleted = null;

        for (int i = 0; i < workouts.size(); i++) {
            if (workouts.get(i).getID() == WorkoutID) {
                lastDeleted = workouts.remove(i);
                i--;
            }
        }

        if (lastDeleted != null) {
            return new OperationResult<>(true, lastDeleted, "Deleted workout with ID: " + WorkoutID);
        }


        return new OperationResult<>(
                false,
                null,
                "Workout ID " + WorkoutID + " does not exist."
        );
    }

    // Converts all workouts to the specified unit type
    public OperationResult<List<Workout>> convertAllUnits(UnitType targetUnit) {
        if (workouts.isEmpty()) {
            return new OperationResult<>(false, null, "No workouts to convert.");
        }

        workouts.replaceAll(workout -> workout.convertUnit(targetUnit));

        return new OperationResult<>(true, workouts, "Converted all workouts to " + targetUnit);

    }

    // Imports workouts from a file. Expects fields to be comma-separated.
    public OperationResult<List<Workout>> importFromFile(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return new OperationResult<>(false, null, "Invalid file path: null or blank.");
        }

        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            return new OperationResult<>(false, null, "File not found or not a regular file: " + filePath);
        }

        List<Workout> importedWorkouts = new ArrayList<>();
        int lineNumber = 0;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                lineNumber++;
                String line = scanner.nextLine().trim();

                // skip over empty lines
                if (line.isEmpty()) continue;

                String[] parts = line.split(",", -1);
                if (parts.length < 6) {
                    return new OperationResult<>(false, null, "Error on line " + lineNumber + ": expected 6 fields but found " + parts.length);
                }

                try {
                    String name = parts[0].trim();
                    LocalDateTime startDateTime = LocalDateTime.parse(parts[1].trim());
                    Integer duration = Integer.parseInt(parts[2].trim());
                    Double distance = Double.parseDouble(parts[3].trim());
                    UnitType unit = UnitType.valueOf(parts[4].trim().toUpperCase());
                    String notes = parts[5].trim();

                    String error = validateLine(name, startDateTime, duration, distance, unit, notes);
                    if (error != null) {
                        return new OperationResult<>(false, null, "Error on line " + lineNumber + ": " + error);
                    }

                    Workout workout = new Workout(name, startDateTime, duration, distance, unit, notes);
                    importedWorkouts.add(workout);
                } catch (Exception e) {
                    return new OperationResult<>(false, null, "Error on line " + lineNumber + ": unexpected parsing error: " + e.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            return new OperationResult<>(false, null, "File not found: " + e.getMessage());
        }

        return new OperationResult<>(true, importedWorkouts, "Imported " + importedWorkouts.size() + " workouts from " + filePath);
    }

    // Exports all workouts to a .txt or .csv file
    public OperationResult<String> exportToFile(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return new OperationResult<>(false, null, "Invalid file path.");
        }

        String lowerPath = filePath.toLowerCase();
        if (!(lowerPath.endsWith(".txt") || lowerPath.endsWith(".csv"))) {
            return new OperationResult<>(false, null, "Invalid file extension. Only .txt or .csv allowed.");
        }

        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            return new OperationResult<>(false, null, "Parent directory does not exist.");
        }

        try (FileWriter writer = new FileWriter(filePath)) {
            for (Workout w : workouts) {
                // Line format: name,date,duration,distance,unit,notes
                writer.write(String.format("%s,%s,%d,%f,%s,%s%n",
                        w.getName(),
                        w.getStartDateTime(),
                        w.getDuration(),
                        w.getDistance(),
                        w.getUnit().name(),
                        w.getNotes()
                ));
            }

            return new OperationResult<>(true, null, "Exported " + workouts.size() + " workouts to " + filePath);

        } catch (IOException e) {
            return new OperationResult<>(false, null, "Error writing to file: " + e.getMessage());
        }
    }

    // -Validation methods-
    // Returns a validation error message if invalid, or null if valid
    public String validateID(Integer workoutID) {
        boolean valid = workoutID != null && workoutID > 0;
        return valid ? null : "Workout ID cannot be null and must be greater than 0." ;
    }

    public String validateName(String name) {
        boolean valid = name != null && !name.trim().isEmpty();
        return valid ? null : "Workout name cannot be null or empty." ;
    }

    public String validateStartDateTime(LocalDateTime start) {
        boolean valid = start != null;
        return valid ? null : "Start date/time cannot be null and must be in YYYY-MM-DDTHH:MM format (e.g. 2025-10-04T14:30).";
    }

    public String validateDuration(Integer duration) {
        boolean valid = duration != null && duration >= 1;
        return valid ? null : "Duration must be at least 1 minute.";
    }

    public String validateDistance(Double distance) {
        boolean valid = distance != null && distance >= 0;
        return valid ? null : "Distance must be a non-negative number.";
    }

    public String validateUnit(UnitType unit) {
        boolean valid = unit != null;
        return valid ? null : "Unit must be either KILOMETERS or MILES.";
    }

    public String validateNotes(String notes) {
        boolean valid = notes == null || notes.length() <= 200;
        return valid ? null : "Notes cannot exceed 200 characters.";
    }

    private String validateLine(String name, LocalDateTime startDateTime, Integer duration,
                                Double distance, UnitType unit, String notes) {

        String error = validateName(name);
        if (error != null) return error;

        error = validateStartDateTime(startDateTime);
        if (error != null) return error;

        error = validateDuration(duration);
        if (error != null) return error;

        error = validateDistance(distance);
        if (error != null) return error;

        error = validateUnit(unit);
        if (error != null) return error;

        error = validateNotes(notes);
        return error;
    }

    // Returns true if all fields in a workout are valid
    // Does not evaluate workout ID
    private boolean isWorkoutValid(Workout workout) {
        return (
                validateName(workout.getName()) == null &&
                validateStartDateTime(workout.getStartDateTime()) == null &&
                validateDuration(workout.getDuration()) == null &&
                validateDistance(workout.getDistance()) == null &&
                validateUnit(workout.getUnit()) == null &&
                validateNotes(workout.getNotes()) == null
        );
    }
}
