package org.joaobarrera.controller;

import org.joaobarrera.model.OperationResult;
import org.joaobarrera.model.Workout;
import org.joaobarrera.service.WorkoutManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return workoutManager.getAllWorkouts();
    }

    @PostMapping("/WorkoutsCreate")
    public ResponseEntity<?> createWorkout (@RequestBody Workout workout) {
        System.out.println("Received workout: " + workout);

        OperationResult<List<Workout>> result = workoutManager.addWorkout(workout);

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