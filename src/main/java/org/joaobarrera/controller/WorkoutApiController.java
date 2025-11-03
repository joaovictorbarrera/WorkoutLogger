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

@RestController
@RequestMapping("/api/workout")
public class WorkoutApiController {

    private final WorkoutManager workoutManager;

    public WorkoutApiController(WorkoutManager workoutManager) {
        this.workoutManager = workoutManager;
    }

    @GetMapping("/database/name")
    public ResponseEntity<?> getDatabaseName() {
        String dbName = workoutManager.getCurrentDatabaseName();

        if (dbName == null) return ResponseEntity.badRequest().body(Map.of("error", "No database connected."));

        return ResponseEntity.ok(Map.of("name", dbName));
    }

    @PostMapping("/database/connect")
    public ResponseEntity<?> connect(@RequestParam String path) {
        try {
            workoutManager.connect(path);
            return ResponseEntity.ok(Map.of("message", "Database connected successfully."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/getAll")
    public List<Workout> getAllWorkouts() {
        OperationResult<List<Workout>> result = workoutManager.getAllWorkouts();

        if (result.isSuccess()) return result.getData();
        else return null;
    }

    @GetMapping("/getByName")
    public List<Workout> getWorkoutByName(@RequestParam("name") String name) {
        OperationResult<List<Workout>> result = workoutManager.getWorkoutsBySearchParameter(name);

        if (result.isSuccess()) return result.getData();
        else return null;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createWorkout (@RequestBody Workout workout) {
        OperationResult<List<Workout>> result = workoutManager.addWorkout(workout);

        return processResult(result);
    }

    @PutMapping("/updateByID")
    public ResponseEntity<?> updateWorkout (@RequestBody Workout workout) {
        OperationResult<List<Workout>> result = workoutManager.updateWorkout(workout.getID(), workout);

        return processResult(result);
    }

    @DeleteMapping("/deleteByID")
    public ResponseEntity<?> deleteWorkout (@RequestBody Workout workout) {
        OperationResult<List<Workout>> result = workoutManager.deleteWorkout(workout.getID());

        return processResult(result);
    }

    @PutMapping("/convertUnits")
    public ResponseEntity<?> convertAllUnits (@RequestBody UnitType unitType) {
        OperationResult<List<Workout>> result = workoutManager.convertAllUnits(unitType);

        return processResult(result);
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
                            "error", result.getMessage()
                    ));
        }
    }
}