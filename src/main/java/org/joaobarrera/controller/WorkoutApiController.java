package org.joaobarrera.controller;

import org.joaobarrera.model.OperationResult;
import org.joaobarrera.model.UnitType;
import org.joaobarrera.model.Workout;
import org.joaobarrera.service.WorkoutManager;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class WorkoutApiController {

    private final WorkoutManager workoutManager;

    public WorkoutApiController(WorkoutManager workoutManager) {
        this.workoutManager = workoutManager;
    }

    @GetMapping("/WorkoutsGet")
    public List<Workout> getAllWorkouts() {
        OperationResult<List<Workout>> result = workoutManager.getWorkoutsBySearchParameter("");

        if (result.isSuccess()) return result.getData();
        else return null;
    }

    @GetMapping("/WorkoutsGetByName")
    public List<Workout> getWorkoutByName(@RequestParam("name") String name) {
        OperationResult<List<Workout>> result = workoutManager.getWorkoutsBySearchParameter(name);

        if (result.isSuccess()) return result.getData();
        else return null;
    }

    @PostMapping("/WorkoutsCreate")
    public ResponseEntity<?> createWorkout (@RequestBody Workout workout) {
        OperationResult<List<Workout>> result = workoutManager.addWorkout(workout);

        return processResult(result);
    }

    @PutMapping("/WorkoutsUpdateByID")
    public ResponseEntity<?> updateWorkout (@RequestBody Workout workout) {
        OperationResult<List<Workout>> result = workoutManager.updateWorkout(workout.getID(), workout);

        return processResult(result);
    }

    @DeleteMapping("/WorkoutsDeleteByID")
    public ResponseEntity<?> deleteWorkout (@RequestBody Workout workout) {
        OperationResult<List<Workout>> result = workoutManager.deleteWorkout(workout.getID());

        return processResult(result);
    }

    @PutMapping("/ConvertUnits")
    public ResponseEntity<?> convertAllUnits (@RequestBody UnitType unitType) {
        OperationResult<List<Workout>> result = workoutManager.convertAllUnits(unitType);

        return processResult(result);
    }

    @PostMapping("/ImportWorkouts")
    public ResponseEntity<?> importWorkouts(@RequestParam("file") MultipartFile file) {
        try {
            // Save the uploaded file to a temporary location
            Path tempFile = Files.createTempFile("workouts-", ".csv");
            file.transferTo(tempFile);

            OperationResult<List<Workout>> result = workoutManager.importFromFile(tempFile.toString());

            // Delete the temporary file
            Files.deleteIfExists(tempFile);

            return processResult(result);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Unexpected Error Importing Workouts",
                    "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/ExportWorkouts")
    public ResponseEntity<?> exportWorkouts() {
        Path tempFile = null;
        try {
            // Create a temp file with .csv suffix
            tempFile = Files.createTempFile("workouts-", ".csv");

            // Export workouts to the temp file
            OperationResult<String> result = workoutManager.exportToFile(tempFile.toString());

            if (!result.isSuccess()) {
                return ResponseEntity.badRequest().body(result.getMessage());
            }

            byte[] fileContent = Files.readAllBytes(tempFile);

            // Set headers for download
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"workouts.csv\"")
                    .header(HttpHeaders.CONTENT_TYPE, "text/csv")
                    .body(fileContent);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Unexpected Error Exporting Workouts",
                    "message", e.getMessage()
            ));
        } finally {
            // Delete temp file
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException ignored) {}
            }
        }
    }

    public ResponseEntity<?> processResult(OperationResult<List<Workout>> result) {
        if (result.isSuccess()) {
            // 200 OK
            return ResponseEntity.ok().build();
        } else {
            // Return 400 Bad Request with error message
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "error", "Validation failed",
                            "message", result.getMessage()
                    ));
        }
    }
}