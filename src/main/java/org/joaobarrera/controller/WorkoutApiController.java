package org.joaobarrera.controller;

import org.joaobarrera.model.OperationResult;
import org.joaobarrera.model.UnitType;
import org.joaobarrera.entity.Workout;
import org.joaobarrera.service.WorkoutManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger log = LoggerFactory.getLogger(WorkoutApiController.class);

    private final WorkoutManager workoutManager;

    public WorkoutApiController(WorkoutManager workoutManager) {
        this.workoutManager = workoutManager;
    }

    @GetMapping("/getAll")
    public List<Workout> getAllWorkouts() {
        long start = System.currentTimeMillis();
        log.info("action=GET_ALL_WORKOUTS");

        OperationResult<List<Workout>> result = workoutManager.getAllWorkouts();

        long duration = System.currentTimeMillis() - start;

        if (result.success()) {
            log.info("action=GET_ALL_WORKOUTS status=SUCCESS count={} duration={}ms",
                    result.data().size(), duration);
            return result.data();
        } else {
            log.error("action=GET_ALL_WORKOUTS status=FAIL duration={}ms message={}",
                    duration, result.message());
            return null;
        }
    }

    @GetMapping("/getByName")
    public List<Workout> getWorkoutByName(@RequestParam("name") String name) {
        long start = System.currentTimeMillis();
        log.info("action=GET_WORKOUT_BY_NAME query={}", name);

        OperationResult<List<Workout>> result = workoutManager.getWorkoutsBySearchParameter(name);

        long duration = System.currentTimeMillis() - start;

        if (result.success()) {
            log.info("action=GET_WORKOUT_BY_NAME status=SUCCESS count={} duration={}ms",
                    result.data().size(), duration);
            return result.data();
        } else {
            log.error("action=GET_WORKOUT_BY_NAME status=FAIL query={} duration={}ms message={}",
                    name, duration, result.message());
            return null;
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createWorkout(@RequestBody Workout workout) {
        long start = System.currentTimeMillis();
        log.info("action=CREATE_WORKOUT payload={}", workout);

        OperationResult<Workout> result = workoutManager.addWorkout(workout);

        long duration = System.currentTimeMillis() - start;

        if (result.success()) {
            log.info("action=CREATE_WORKOUT status=SUCCESS id={} duration={}ms",
                    result.data().getID(), duration);
        } else {
            log.error("action=CREATE_WORKOUT status=FAIL duration={}ms message={}",
                    duration, result.message());
        }

        return processResult(result);
    }

    @PutMapping("/updateByID")
    public ResponseEntity<?> updateWorkout(@RequestBody Workout workout) {
        long start = System.currentTimeMillis();
        log.info("action=UPDATE_WORKOUT id={} payload={}", workout.getID(), workout);

        OperationResult<Workout> result = workoutManager.updateWorkout(workout.getID(), workout);

        long duration = System.currentTimeMillis() - start;

        if (result.success()) {
            log.info("action=UPDATE_WORKOUT status=SUCCESS id={} duration={}ms",
                    workout.getID(), duration);
        } else {
            log.error("action=UPDATE_WORKOUT status=FAIL id={} duration={}ms message={}",
                    workout.getID(), duration, result.message());
        }

        return processResult(result);
    }

    @DeleteMapping("/deleteByID")
    public ResponseEntity<?> deleteWorkout(@RequestBody Workout workout) {
        long start = System.currentTimeMillis();
        log.info("action=DELETE_WORKOUT id={}", workout.getID());

        OperationResult<Integer> result = workoutManager.deleteWorkout(workout.getID());

        long duration = System.currentTimeMillis() - start;

        if (result.success()) {
            log.info("action=DELETE_WORKOUT status=SUCCESS id={} duration={}ms",
                    workout.getID(), duration);
        } else {
            log.error("action=DELETE_WORKOUT status=FAIL id={} duration={}ms message={}",
                    workout.getID(), duration, result.message());
        }

        return processResult(result);
    }

    @PutMapping("/convertUnits")
    public ResponseEntity<?> convertAllUnits(@RequestBody UnitType unitType) {
        long start = System.currentTimeMillis();
        log.info("action=CONVERT_UNITS targetUnit={}", unitType);

        OperationResult<List<Workout>> result = workoutManager.convertAllUnits(unitType);

        long duration = System.currentTimeMillis() - start;

        if (result.success()) {
            log.info("action=CONVERT_UNITS status=SUCCESS duration={}ms", duration);
        } else {
            log.error("action=CONVERT_UNITS status=FAIL duration={}ms message={}",
                    duration, result.message());
        }

        return processResult(result);
    }

    private ResponseEntity<?> processResult(OperationResult<?> result) {
        if (result.success()) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", result.message()));
        }
    }
}