import org.joaobarrera.Main;
import org.joaobarrera.model.OperationResult;
import org.joaobarrera.model.UnitType;
import org.joaobarrera.entity.Workout;
import org.joaobarrera.repository.WorkoutRepository;
import org.joaobarrera.service.WorkoutManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Joao Barrera
 * CEN 3024 - Software Development 1
 * November 9, 2025,
 * WorkoutManagerUpdateTest.java
 * This class uses unit testing to validate the updateWorkout() functionality.
 */
@SpringBootTest(classes = Main.class)
@ActiveProfiles("test")
public class WorkoutManagerUpdateTest {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    
    @Autowired
    private WorkoutManager workoutManager;

    @Autowired
    private WorkoutRepository workoutRepository;

    @BeforeEach
    void setup() {
        // Wipe all records before each test
        workoutRepository.deleteAll();
    }

    @DisplayName("Should fail to update workout when name is null")
    @Test
    public void updateWorkout_ShouldFail_WhenNameIsNull() {
        Workout workout = new Workout(null, null, LocalDateTime.parse("2025-10-10T12:00"), 10, 10.0, UnitType.KILOMETERS, "");
        workoutManager.addWorkout(new Workout(null, "Initial", LocalDateTime.parse("2025-10-10T12:00"), 10, 10.0, UnitType.KILOMETERS, ""));

        int idToUpdate = workoutManager.getAllWorkouts().data().get(0).getID();
        
        OperationResult<Workout> result = workoutManager.updateWorkout(idToUpdate, workout);
        assertFalse(result.success());
    }

    @DisplayName("Should fail to update workout when name is blank")
    @Test
    public void updateWorkout_ShouldFail_WhenNameIsBlank() {
        Workout workout = new Workout(null, "", LocalDateTime.parse("2025-10-10T12:00"), 10, 10.0, UnitType.KILOMETERS, "");
        workoutManager.addWorkout(new Workout(null, "Initial", LocalDateTime.parse("2025-10-10T12:00"), 10, 10.0, UnitType.KILOMETERS, ""));

        int idToUpdate = workoutManager.getAllWorkouts().data().get(0).getID();
        
        OperationResult<Workout> result = workoutManager.updateWorkout(idToUpdate, workout);
        assertFalse(result.success());
    }

    @DisplayName("Should succeed to update workout when name is exactly 50 characters")
    @Test
    public void updateWorkout_ShouldSucceed_WhenNameIs50Chars() {
        workoutManager.addWorkout(new Workout(null, "Initial", LocalDateTime.parse("2025-10-10T12:00"), 10, 10.0, UnitType.KILOMETERS, ""));

        String name50 = "A".repeat(50);
        Workout updatedWorkout = new Workout(null, name50, LocalDateTime.parse("2025-10-10T12:00"), 10, 10.0, UnitType.KILOMETERS, "");

        int idToUpdate = workoutManager.getAllWorkouts().data().get(0).getID();
        OperationResult<Workout> result = workoutManager.updateWorkout(idToUpdate, updatedWorkout);
        
        assertTrue(result.success());
        assertEquals(name50, workoutManager.getAllWorkouts().data().get(0).getName());
    }

    @DisplayName("Should fail to update workout when name exceeds 50 characters")
    @Test
    public void updateWorkout_ShouldFail_WhenNameExceeds50Chars() {
        workoutManager.addWorkout(new Workout(null, "Initial", LocalDateTime.parse("2025-10-10T12:00"), 10, 10.0, UnitType.KILOMETERS, ""));

        String name51 = "A".repeat(51);
        Workout updatedWorkout = new Workout(null, name51, LocalDateTime.parse("2025-10-10T12:00"), 10, 10.0, UnitType.KILOMETERS, "");

        int idToUpdate = workoutManager.getAllWorkouts().data().get(0).getID();
        OperationResult<Workout> result = workoutManager.updateWorkout(idToUpdate, updatedWorkout);
        assertFalse(result.success());
        assertEquals("Initial", workoutManager.getAllWorkouts().data().get(0).getName());
    }

    @DisplayName("Should fail to update workout when StartDateTime is null")
    @Test
    public void updateWorkout_ShouldFail_WhenStartDateTimeIsNull() {
        Workout workout = new Workout(null, "Test", null, 10, 10.0, UnitType.KILOMETERS, "");
        workoutManager.addWorkout(new Workout(null, "Initial", LocalDateTime.parse("2025-10-10T12:00"), 10, 10.0, UnitType.KILOMETERS, ""));
        
        int idToUpdate = workoutManager.getAllWorkouts().data().get(0).getID();
        OperationResult<Workout> result = workoutManager.updateWorkout(idToUpdate, workout);
        assertFalse(result.success());
    }

    @DisplayName("Should fail to update workout when Duration is null")
    @Test
    public void updateWorkout_ShouldFail_WhenDurationIsNull() {
        Workout workout = new Workout(null, "Test", LocalDateTime.parse("2025-10-10T12:00"), null, 10.0, UnitType.KILOMETERS, "");
        workoutManager.addWorkout(new Workout(null, "Initial", LocalDateTime.parse("2025-10-10T12:00"), 10, 10.0, UnitType.KILOMETERS, ""));
        
        int idToUpdate = workoutManager.getAllWorkouts().data().get(0).getID();
        OperationResult<Workout> result = workoutManager.updateWorkout(idToUpdate, workout);
        assertFalse(result.success());
    }

    @DisplayName("Should fail to update workout when Duration is zero")
    @Test
    public void updateWorkout_ShouldFail_WhenDurationIsZero() {
        Workout workout = new Workout(null, "Test", LocalDateTime.parse("2025-10-10T12:00"), 0, 10.0, UnitType.KILOMETERS, "");
        workoutManager.addWorkout(new Workout(null, "Initial", LocalDateTime.parse("2025-10-10T12:00"), 10, 10.0, UnitType.KILOMETERS, ""));

        int idToUpdate = workoutManager.getAllWorkouts().data().get(0).getID();
        OperationResult<Workout> result = workoutManager.updateWorkout(idToUpdate, workout);
        assertFalse(result.success());
    }

    @DisplayName("Should fail to update workout when Duration is negative")
    @Test
    public void updateWorkout_ShouldFail_WhenDurationIsNegative() {
        Workout workout = new Workout(null, "Test", LocalDateTime.parse("2025-10-10T12:00"), -10, 10.0, UnitType.KILOMETERS, "");
        workoutManager.addWorkout(new Workout(null, "Initial", LocalDateTime.parse("2025-10-10T12:00"), 10, 10.0, UnitType.KILOMETERS, ""));

        int idToUpdate = workoutManager.getAllWorkouts().data().get(0).getID();
        OperationResult<Workout> result = workoutManager.updateWorkout(idToUpdate, workout);
        assertFalse(result.success());
    }

    @DisplayName("Should succeed to update workout when Duration is 1")
    @Test
    public void updateWorkout_ShouldSucceed_WhenDurationIsOne() {
        workoutManager.addWorkout(new Workout(null, "Initial", LocalDateTime.parse("2025-10-10T12:00"), 10, 10.0, UnitType.KILOMETERS, ""));
        Workout workout = new Workout(null, "Test", LocalDateTime.parse("2025-10-10T12:00"), 1, 10.0, UnitType.KILOMETERS, "");

        int idToUpdate = workoutManager.getAllWorkouts().data().get(0).getID();
        OperationResult<Workout> result = workoutManager.updateWorkout(idToUpdate, workout);
        assertTrue(result.success());
    }

    @DisplayName("Should fail to update workout when Distance is null")
    @Test
    public void updateWorkout_ShouldFail_WhenDistanceIsNull() {
        workoutManager.addWorkout(new Workout(null, "Initial", LocalDateTime.parse("2025-10-10T12:00"), 10, 10.0, UnitType.KILOMETERS, ""));
        Workout workout = new Workout(null, "Test", LocalDateTime.parse("2025-10-10T12:00"), 10, null, UnitType.KILOMETERS, "");

        int idToUpdate = workoutManager.getAllWorkouts().data().get(0).getID();
        OperationResult<Workout> result = workoutManager.updateWorkout(idToUpdate, workout);
        assertFalse(result.success());
    }

    @DisplayName("Should fail to update workout when Distance is negative")
    @Test
    public void updateWorkout_ShouldFail_WhenDistanceIsNegative() {
        workoutManager.addWorkout(new Workout(null, "Initial", LocalDateTime.parse("2025-10-10T12:00"), 10, 10.0, UnitType.KILOMETERS, ""));
        Workout workout = new Workout(null, "Test", LocalDateTime.parse("2025-10-10T12:00"), 10, -10.0, UnitType.KILOMETERS, "");

        int idToUpdate = workoutManager.getAllWorkouts().data().get(0).getID();
        OperationResult<Workout> result = workoutManager.updateWorkout(idToUpdate, workout);
        assertFalse(result.success());
    }

    @DisplayName("Should fail to update workout when Unit is null")
    @Test
    public void updateWorkout_ShouldFail_WhenUnitIsNull() {
        workoutManager.addWorkout(new Workout(null, "Initial", LocalDateTime.parse("2025-10-10T12:00"), 10, 10.0, UnitType.KILOMETERS, ""));
        Workout workout = new Workout(null, "Test", LocalDateTime.parse("2025-10-10T12:00"), 10, 10.0, null, "");

        int idToUpdate = workoutManager.getAllWorkouts().data().get(0).getID();
        OperationResult<Workout> result = workoutManager.updateWorkout(idToUpdate, workout);
        assertFalse(result.success());
    }

    @DisplayName("Should fail to update workout when Notes are over 200 characters")
    @Test
    public void updateWorkout_ShouldFail_WhenNotesIsTooLong() {
        workoutManager.addWorkout(new Workout(null, "Initial", LocalDateTime.parse("2025-10-10T12:00"), 10, 10.0, UnitType.KILOMETERS, ""));
        String notes = "a".repeat(201);
        Workout workout = new Workout(null, "Test", LocalDateTime.parse("2025-10-10T12:00"), 10, 10.0, UnitType.KILOMETERS, notes);

        int idToUpdate = workoutManager.getAllWorkouts().data().get(0).getID();
        OperationResult<Workout> result = workoutManager.updateWorkout(idToUpdate, workout);
        assertFalse(result.success());
    }

    @DisplayName("Should succeed to update workout when Notes are exactly 200 characters")
    @Test
    public void updateWorkout_ShouldSucceed_WhenNotesAreAtLimit() {
        workoutManager.addWorkout(new Workout(null, "Initial", LocalDateTime.parse("2025-10-10T12:00"), 10, 10.0, UnitType.KILOMETERS, ""));
        String notes = "a".repeat(200);
        Workout workout = new Workout(null, "Test", LocalDateTime.parse("2025-10-10T12:00"), 10, 10.0, UnitType.KILOMETERS, notes);

        int idToUpdate = workoutManager.getAllWorkouts().data().get(0).getID();
        OperationResult<Workout> result = workoutManager.updateWorkout(idToUpdate, workout);
        assertTrue(result.success());
    }


    @Test
    @DisplayName("Should successfully update an existing workout with valid data")
    void updateWorkout_ShouldSucceed_WithValidData() {
        // Adds 'Run' Workout
        Workout original = new Workout(null, "Run", LocalDateTime.now(), 30, 5.0, UnitType.KILOMETERS, "Morning run");
        workoutManager.addWorkout(original);

        // Updates 'Run' to 'Run Updated'
        int idToUpdate = workoutManager.getAllWorkouts().data().get(0).getID();
        Workout updatedWorkout = new Workout(null, "Run Updated", LocalDateTime.now().plusDays(1), 45, 10.0, UnitType.MILES, "Evening run");
        OperationResult<Workout> result = workoutManager.updateWorkout(idToUpdate, updatedWorkout);

        assertTrue(result.success());
        List<Workout> workouts = workoutManager.getAllWorkouts().data();
        assertEquals(1, workouts.size());

        Workout stored = workouts.get(0);

        System.out.println(stored.getStartDateTime());
        System.out.println(updatedWorkout.getStartDateTime());

        assertEquals(idToUpdate, stored.getID());
        assertEquals("Run Updated", stored.getName());
        assertEquals(
                updatedWorkout.getStartDateTime().format(formatter),
                stored.getStartDateTime().format(formatter)
        );
        assertEquals(45, stored.getDuration());
        assertEquals(10.0, stored.getDistance());
        assertEquals(UnitType.MILES, stored.getUnit());
        assertEquals("Evening run", stored.getNotes());

        // Ensure the original reference is not mutated
        assertNotEquals(original.getName(), stored.getName());
    }

    @Test
    @DisplayName("Should fail update if WorkoutID does not exist")
    void updateWorkout_ShouldFail_WithInvalidID() {
        Workout workout = new Workout(null, "Run", LocalDateTime.now(), 30, 5.0, UnitType.KILOMETERS, "");
        workoutManager.addWorkout(workout);

        int invalidId = 9999;
        OperationResult<Workout> result = workoutManager.updateWorkout(invalidId, workout);

        assertFalse(result.success());
    }

    @Test
    @DisplayName("Should not mutate original stored workout instance")
    void updateWorkout_ShouldNotMutateOriginalWorkout() {
        Workout original = new Workout(null, "Run", LocalDateTime.now(), 30, 5.0, UnitType.KILOMETERS, "");
        workoutManager.addWorkout(original);

        Workout storedOriginal = workoutManager.getAllWorkouts().data().get(0);

        int idToUpdate = workoutManager.getAllWorkouts().data().get(0).getID();
        Workout updated = new Workout(null, "Updated Run", LocalDateTime.now().plusHours(2), 40, 6.0, UnitType.MILES, "Updated notes");
        workoutManager.updateWorkout(idToUpdate, updated);

        Workout storedUpdated = workoutManager.getAllWorkouts().data().get(0);
        assertNotEquals(storedUpdated, storedOriginal);
    }

    @Test
    @DisplayName("Should update one workout and leave the other workout unchanged")
    void updateWorkout_ShouldNotAffectOtherWorkouts() {
        // Add two distinct workouts
        Workout workout1 = new Workout(null, "Run", LocalDateTime.parse("2025-10-10T06:30"), 30, 5.0, UnitType.KILOMETERS, "Morning run");
        Workout workout2 = new Workout(null, "Bike", LocalDateTime.parse("2025-10-11T07:00"), 60, 20.0, UnitType.MILES, "Evening bike");

        workoutManager.addWorkout(workout1);
        workoutManager.addWorkout(workout2);

        List<Workout> workoutsBeforeUpdate = workoutManager.getAllWorkouts().data();
        int idToUpdate = workoutsBeforeUpdate.get(0).getID();

        // Perform update on workout1
        Workout updatedWorkout = new Workout(null, "Run Updated", LocalDateTime.parse("2025-10-12T06:30"), 45, 10.0, UnitType.KILOMETERS, "Longer run");
        OperationResult<Workout> result = workoutManager.updateWorkout(idToUpdate, updatedWorkout);
        assertTrue(result.success());

        List<Workout> workoutsAfterUpdate = workoutManager.getAllWorkouts().data();

        // Check that workout2 is NOT affected and all fields remain the same
        Workout otherWorkout = workoutsAfterUpdate.get(1);

        assertEquals("Bike", otherWorkout.getName());
        assertEquals(LocalDateTime.parse("2025-10-11T07:00"), otherWorkout.getStartDateTime());
        assertEquals(60, otherWorkout.getDuration());
        assertEquals(20.0, otherWorkout.getDistance());
        assertEquals(UnitType.MILES, otherWorkout.getUnit());
        assertEquals("Evening bike", otherWorkout.getNotes());
    }

    @DisplayName("Should successfully update a workout with itself (no changes)")
    @Test
    void updateWorkout_NoChangesWithSameObject() {
        Workout workout = new Workout(null, "Morning Run", LocalDateTime.parse("2025-10-10T07:00"), 30, 5.0, UnitType.KILOMETERS, "Nice run");
        workoutManager.addWorkout(workout);

        int idToUpdate = workoutManager.getAllWorkouts().data().get(0).getID();

        // Update with the exact same object (no changes)
        OperationResult<Workout> updateResult = workoutManager.updateWorkout(idToUpdate, workout);

        assertTrue(updateResult.success());

        // Confirm the workout data is still intact
        Workout updatedWorkout = workoutManager.getAllWorkouts().data().get(0);
        assertEquals("Morning Run", updatedWorkout.getName());
        assertEquals(LocalDateTime.parse("2025-10-10T07:00"), updatedWorkout.getStartDateTime());
        assertEquals(30, updatedWorkout.getDuration());
        assertEquals(5.0, updatedWorkout.getDistance());
        assertEquals(UnitType.KILOMETERS, updatedWorkout.getUnit());
        assertEquals("Nice run", updatedWorkout.getNotes());
    }


    @DisplayName("Should fail when trying to update with zero or negative ID")
    @Test
    void updateWorkout_InvalidIDValues() {
        Workout updatedWorkout = new Workout(null, "Test", LocalDateTime.parse("2025-10-10T10:00"), 30, 5.0, UnitType.KILOMETERS, "Test notes");

        OperationResult<Workout> resultZero = workoutManager.updateWorkout(0, updatedWorkout);
        OperationResult<Workout> resultNegative = workoutManager.updateWorkout(-1, updatedWorkout);

        assertFalse(resultZero.success());
        assertFalse(resultNegative.success());
    }

}
