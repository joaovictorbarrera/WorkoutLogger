package org.joaobarrera.controller;

import org.joaobarrera.model.OperationResult;
import org.joaobarrera.model.UnitType;
import org.joaobarrera.model.Workout;
import org.joaobarrera.service.WorkoutManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Joao Barrera
 * CEN 3024 - Software Development 1
 * November 9, 2025
 * WorkoutApiController.java
 */

/**
 * The WorkoutApiController class provides REST API endpoints for managing workout data
 * within the Workout Logger application.
 * <p>
 * It handles CRUD operations and database interactions such as creating, updating,
 * deleting, and retrieving workout entries.
 * <p>
 * It also supports unit conversion (kilometers to miles and vice versa) and database
 * connection management, enabling smooth interaction between the frontend interface
 * and backend logic.
 */

@RestController
@RequestMapping("/api/workout")
public class WorkoutApiController {
    private final WorkoutManager workoutManager;

    /**
     * Constructor that initializes the WorkoutApiController with a WorkoutManager instance.
     * <p>
     * The WorkoutManager is used to perform all business logic and database operations
     * related to workout-data management.
     *
     * @param workoutManager the service layer object responsible for handling workout logic
     */
    public WorkoutApiController(WorkoutManager workoutManager) {
        this.workoutManager = workoutManager;
    }

    /**
     * Retrieves the name of the currently connected database in a JSON format.
     * <p>
     * When the database is not connected, the value is null.
     *
     * @return JSON object where the key is 'name' and the value is the database name or null
     */
    @GetMapping("/database/name")
    public ResponseEntity<Map<String, Object>> getDatabaseName() {
        String dbName = workoutManager.getCurrentDatabaseName();

        Map<String, Object> response = new HashMap<>();
        response.put("name", dbName);

        return ResponseEntity.ok(response);
    }

    /**
     * Attempts to connect to a database using the provided file path.
     * <p>
     * If the connection fails, an error message is returned.
     *
     * @param path the file path to the database being connected
     * @return JSON response indicating whether the connection succeeded or failed
     */
    @PostMapping("/database/connect")
    public ResponseEntity<?> connect(@RequestParam String path) {
        try {
            workoutManager.connect(path);
            return ResponseEntity.ok(Map.of("message", "Database connected successfully."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Retrieves all workout records stored in the database.
     * <p>
     * If the operation is unsuccessful, null is returned.
     *
     * @return a list of all stored workouts or null if the operation fails
     */
    @GetMapping("/getAll")
    public List<Workout> getAllWorkouts() {
        OperationResult<List<Workout>> result = workoutManager.getAllWorkouts();

        if (result.success()) return result.data();
        else return null;
    }

    /**
     * Retrieves all workouts that match the provided name or partial name.
     * <p>
     * If no records are found, it returns an empty list.
     * <p>
     * If the operation fails, null is returned.
     *
     * @param name the workout name or partial name used for searching
     * @return a list of matching workout records, or null for error
     */
    @GetMapping("/getByName")
    public List<Workout> getWorkoutByName(@RequestParam("name") String name) {
        OperationResult<List<Workout>> result = workoutManager.getWorkoutsBySearchParameter(name);

        if (result.success()) return result.data();
        else return null;
    }

    /**
     * Creates a new workout record using the data provided in the request body.
     * <p>
     * Returns an HTTP response indicating success or failure.
     * <p>
     * Failures will contain a JSON Response with an error message.
     *
     * @param workout the workout object containing the new workout's details
     * @return HTTP response describing the result of the creation attempt
     */
    @PostMapping("/create")
    public ResponseEntity<?> createWorkout (@RequestBody Workout workout) {
        OperationResult<List<Workout>> result = workoutManager.addWorkout(workout);

        return processResult(result);
    }

    /**
     * Updates an existing workout based on the ID provided inside the workout object.
     * <p>
     * Returns an HTTP response describing the success or failure of the update.
     * <p>
     * Failures will contain a JSON Response with an error message.
     *
     * @param workout the updated workout information, including the existing ID
     * @return HTTP response describing the result of the update attempt
     */
    @PutMapping("/updateByID")
    public ResponseEntity<?> updateWorkout (@RequestBody Workout workout) {
        OperationResult<List<Workout>> result = workoutManager.updateWorkout(workout.getID(), workout);

        return processResult(result);
    }

    /**
     * Deletes a workout from the database using the ID provided in the request body.
     * <p>
     * Returns an HTTP response indicating whether the deletion was successful.
     * <p>
     * Failures will contain a JSON Response with an error message.
     *
     * @param workout the workout object containing the ID of the workout to delete
     * @return HTTP response describing the result of the deletion attempt
     */
    @DeleteMapping("/deleteByID")
    public ResponseEntity<?> deleteWorkout (@RequestBody Workout workout) {
        OperationResult<List<Workout>> result = workoutManager.deleteWorkout(workout.getID());

        return processResult(result);
    }

    /**
     * Converts the unit of measurement for all workouts to the specified type
     * (kilometers or miles).
     * <p>
     * Returns an HTTP response indicating success or failure.
     * <p>
     * Failures will contain a JSON Response with an error message.
     *
     * @param unitType the unit type that all existing workouts should be converted to
     * @return HTTP response describing the result of the conversion
     */
    @PutMapping("/convertUnits")
    public ResponseEntity<?> convertAllUnits (@RequestBody UnitType unitType) {
        OperationResult<List<Workout>> result = workoutManager.convertAllUnits(unitType);

        return processResult(result);
    }

    /**
     * Evaluates the result of an operation and generates a standardized HTTP response.
     * <p>
     * Successful operations return 200 OK; failures return 400 Bad Request.
     * <p>
     * 400 errors will contain a JSON Response indicating an error message in the
     * {error: message} format.
     *
     * @param result the outcome of a workout-related operation
     * @return HTTP response indicating whether the operation succeeded or failed
     */
    private ResponseEntity<?> processResult(OperationResult<List<Workout>> result) {
        if (result.success()) {
            // 200 OK
            return ResponseEntity.ok().build();
        } else {
            // Return 400 Bad Request with error message
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "error", result.message()
                    ));
        }
    }
}