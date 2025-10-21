import org.joaobarrera.model.OperationResult;
import org.joaobarrera.model.UnitType;
import org.joaobarrera.model.Workout;
import org.joaobarrera.service.WorkoutManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WorkoutManagerAddTest {
    private WorkoutManager workoutManager;

    @BeforeEach
    void setUp() {
        workoutManager = new WorkoutManager();
    }

    @DisplayName("Should fail to add workout when name is null")
    @Test
    public void addWorkout_ShouldFail_WhenNameIsNull() {
        Workout workout = new Workout(null, LocalDateTime.parse("2025-10-10T12:00"), 10, 10.0, UnitType.KILOMETERS, "");
        OperationResult<List<Workout>> addResult = workoutManager.addWorkout(workout);
        assertFalse(addResult.isSuccess());
        assertTrue(workoutManager.getAllWorkouts().isEmpty());
    }

    @DisplayName("Should fail to add workout when name is blank")
    @Test
    public void addWorkout_ShouldFail_WhenNameIsBlank() {
        Workout workout = new Workout("", LocalDateTime.parse("2025-10-10T12:00"), 10, 10.0, UnitType.KILOMETERS, "");
        OperationResult<List<Workout>> addResult = workoutManager.addWorkout(workout);
        assertFalse(addResult.isSuccess());
        assertTrue(workoutManager.getAllWorkouts().isEmpty());
    }

    @DisplayName("Should fail to add workout when StartDateTime is null")
    @Test
    public void addWorkout_ShouldFail_WhenStartDateTimeIsBad() {
        Workout workout = new Workout("Test", null, 10, 10.0, UnitType.KILOMETERS, "");
        OperationResult<List<Workout>> addResult = workoutManager.addWorkout(workout);
        assertFalse(addResult.isSuccess());
        assertTrue(workoutManager.getAllWorkouts().isEmpty());
    }

    @DisplayName("Should fail to add workout when Duration is null")
    @Test
    public void addWorkout_ShouldFail_WhenDurationIsNull() {
        Workout workout = new Workout("Test", LocalDateTime.parse("2025-10-10T12:00"), null, 10.0, UnitType.KILOMETERS, "");
        OperationResult<List<Workout>> addResult = workoutManager.addWorkout(workout);
        assertFalse(addResult.isSuccess());
        assertTrue(workoutManager.getAllWorkouts().isEmpty());
    }

    @DisplayName("Should fail to add workout when Duration is zero")
    @Test
    public void addWorkout_ShouldFail_WhenDurationIsZero() {
        Workout workout = new Workout("Test", LocalDateTime.parse("2025-10-10T12:00"), 0, 10.0, UnitType.KILOMETERS, "");
        OperationResult<List<Workout>> addResult = workoutManager.addWorkout(workout);
        assertFalse(addResult.isSuccess());
        assertTrue(workoutManager.getAllWorkouts().isEmpty());
    }

    @DisplayName("Should fail to add workout when Duration is negative")
    @Test
    public void addWorkout_ShouldFail_WhenDurationIsNegative() {
        Workout workout = new Workout("Test", LocalDateTime.parse("2025-10-10T12:00"), -10, 10.0, UnitType.KILOMETERS, "");
        OperationResult<List<Workout>> addResult = workoutManager.addWorkout(workout);
        assertFalse(addResult.isSuccess());
        assertTrue(workoutManager.getAllWorkouts().isEmpty());
    }

    @DisplayName("Should successfully add workout when Duration is 1")
    @Test
    public void addWorkout_ShouldSucceed_WhenDurationIsOne() {
        Workout workout = new Workout("Test", LocalDateTime.parse("2025-10-10T12:00"), 1, 10.0, UnitType.KILOMETERS, "");
        OperationResult<List<Workout>> addResult = workoutManager.addWorkout(workout);
        assertTrue(addResult.isSuccess());
        assertEquals(1, workoutManager.getAllWorkouts().size());
    }

    @DisplayName("Should fail to add workout when Distance is null")
    @Test
    public void addWorkout_ShouldFail_WhenDistanceIsNull() {
        Workout workout = new Workout("Test", LocalDateTime.parse("2025-10-10T12:00"), 10, null, UnitType.KILOMETERS, "");
        OperationResult<List<Workout>> addResult = workoutManager.addWorkout(workout);
        assertFalse(addResult.isSuccess());
        assertTrue(workoutManager.getAllWorkouts().isEmpty());
    }

    @DisplayName("Should fail to add workout when Distance is negative")
    @Test
    public void addWorkout_ShouldFail_WhenDistanceIsNegative() {
        Workout workout = new Workout("Test", LocalDateTime.parse("2025-10-10T12:00"), 10, -10.0, UnitType.KILOMETERS, "");
        OperationResult<List<Workout>> addResult = workoutManager.addWorkout(workout);
        assertFalse(addResult.isSuccess());
        assertTrue(workoutManager.getAllWorkouts().isEmpty());
    }

    @DisplayName("Should fail to add workout when Unit is null")
    @Test
    public void addWorkout_ShouldFail_WhenUnitIsNull() {
        Workout workout = new Workout("Test", LocalDateTime.parse("2025-10-10T12:00"), 10, 10.0, null, "");
        OperationResult<List<Workout>> addResult = workoutManager.addWorkout(workout);
        assertFalse(addResult.isSuccess());
        assertTrue(workoutManager.getAllWorkouts().isEmpty());
    }

    @DisplayName("Should fail to add workout when Notes are over 200 characters")
    @Test
    public void addWorkout_ShouldFail_WhenNotesIsBad() {
        // Notes at exactly 201 characters
        String notes = "a".repeat(201);
        Workout workout = new Workout("Test", LocalDateTime.parse("2025-10-10T12:00"), 10, 10.0, UnitType.KILOMETERS, notes);
        OperationResult<List<Workout>> addResult = workoutManager.addWorkout(workout);
        assertFalse(addResult.isSuccess());
        assertTrue(workoutManager.getAllWorkouts().isEmpty());
    }

    @DisplayName("Should succeed to add workout when Notes are exactly 200 characters")
    @Test
    public void addWorkout_ShouldSucceed_WhenNotesAreAtLimit() {
        String notes = "a".repeat(200);
        Workout workout = new Workout("Test", LocalDateTime.parse("2025-10-10T12:00"), 10, 10.0, UnitType.KILOMETERS, notes);
        OperationResult<List<Workout>> addResult = workoutManager.addWorkout(workout);
        assertTrue(addResult.isSuccess());
        assertEquals(1, workoutManager.getAllWorkouts().size());
    }

    @DisplayName("Should succeed when all values are good")
    @Test
    public void addWorkout_ShouldSucceed_WhenAllValuesAreValid() {
        Workout workout = new Workout("Test", LocalDateTime.parse("2025-10-10T12:00"), 10, 10.0, UnitType.KILOMETERS, "");
        OperationResult<List<Workout>> addResult = workoutManager.addWorkout(workout);
        assertTrue(addResult.isSuccess());
        assertEquals(1, workoutManager.getAllWorkouts().size());
    }

    @DisplayName("Data added is not corrupted")
    @Test
    public void addWorkout_DataNotCorrupted() {
        Workout workout = new Workout("Test", LocalDateTime.parse("2025-10-10T12:00"), 10, 10.0, UnitType.KILOMETERS, "some notes");
        OperationResult<List<Workout>> addResult = workoutManager.addWorkout(workout);
        assertTrue(addResult.isSuccess());
        assertEquals(1, workoutManager.getAllWorkouts().size());
        Workout addedWorkout = workoutManager.getAllWorkouts().get(0);
        assertEquals("Test", addedWorkout.getName());
        assertEquals(LocalDateTime.parse("2025-10-10T12:00"), addedWorkout.getStartDateTime());
        assertEquals(10, addedWorkout.getDuration());
        assertEquals(10.0, addedWorkout.getDistance());
        assertEquals(UnitType.KILOMETERS, addedWorkout.getUnit());
        assertEquals("some notes", addedWorkout.getNotes());
    }

    @DisplayName("Able to add multiple workouts")
    @Test
    public void addWorkout_MultipleWorkouts() {
        Workout workout1 = new Workout("Test1", LocalDateTime.parse("2025-10-10T12:00"), 10, 10.0, UnitType.KILOMETERS, "");
        Workout workout2 = new Workout("Test2", LocalDateTime.parse("2025-10-10T12:00"), 10, 10.0, UnitType.KILOMETERS, "");
        Workout workout3 = new Workout("Test3", LocalDateTime.parse("2025-10-10T12:00"), 10, 10.0, UnitType.KILOMETERS, "");
        workoutManager.addWorkout(workout1);
        workoutManager.addWorkout(workout2);
        workoutManager.addWorkout(workout3);
        assertEquals(3, workoutManager.getAllWorkouts().size());
        assertEquals("Test1", workoutManager.getAllWorkouts().get(0).getName());
        assertEquals("Test2", workoutManager.getAllWorkouts().get(1).getName());
        assertEquals("Test3", workoutManager.getAllWorkouts().get(2).getName());
    }

    @DisplayName("Adding a workout does not corrupt other data")
    @Test
    public void addWorkout_DataIntegrity() {
        Workout workout1 = new Workout("Test1", LocalDateTime.parse("2025-10-10T14:00"), 20, 20.0, UnitType.MILES, "some notes");

        workoutManager.addWorkout(workout1);
        assertEquals(1, workoutManager.getAllWorkouts().size());
        assertEquals("Test1", workout1.getName());
        assertEquals(LocalDateTime.parse("2025-10-10T14:00"), workout1.getStartDateTime());
        assertEquals(20, workout1.getDuration());
        assertEquals(20.0, workout1.getDistance());
        assertEquals(UnitType.MILES, workout1.getUnit());
        assertEquals("some notes", workout1.getNotes());

        Workout workout2 = new Workout("Test2", LocalDateTime.parse("2025-10-10T12:00"), 10, 10.0, UnitType.KILOMETERS, "");
        workoutManager.addWorkout(workout2);
        assertEquals(2, workoutManager.getAllWorkouts().size());
        Workout workoutAtFirstIndex = workoutManager.getAllWorkouts().get(0);

        assertEquals("Test1", workoutAtFirstIndex.getName());
        assertEquals(LocalDateTime.parse("2025-10-10T14:00"), workoutAtFirstIndex.getStartDateTime());
        assertEquals(20, workoutAtFirstIndex.getDuration());
        assertEquals(20.0, workoutAtFirstIndex.getDistance());
        assertEquals(UnitType.MILES, workoutAtFirstIndex.getUnit());
        assertEquals("some notes", workoutAtFirstIndex.getNotes());
    }

    @DisplayName("Should allow duplicate workouts")
    @Test
    public void addWorkout_ShouldAllowDuplicates() {
        Workout workout = new Workout("Test", LocalDateTime.parse("2025-10-10T12:00"), 10, 10.0, UnitType.KILOMETERS, "");
        workoutManager.addWorkout(workout);
        workoutManager.addWorkout(workout);
        List<Workout> all = workoutManager.getAllWorkouts();
        assertEquals(2, all.size());
        assertNotEquals(all.get(0).getID(), all.get(1).getID());
    }

    @DisplayName("IDs assigned to workouts should be unique")
    @Test
    public void addWorkout_ShouldAssignUniqueIDs() {
        Workout workout1 = new Workout("Test1", LocalDateTime.now(), 10, 10.0, UnitType.KILOMETERS, "");
        Workout workout2 = new Workout("Test2", LocalDateTime.now(), 10, 10.0, UnitType.KILOMETERS, "");

        workoutManager.addWorkout(workout1);
        workoutManager.addWorkout(workout2);

        int id1 = workoutManager.getAllWorkouts().get(0).getID();
        int id2 = workoutManager.getAllWorkouts().get(1).getID();

        assertNotEquals(id1, id2);
    }
}
