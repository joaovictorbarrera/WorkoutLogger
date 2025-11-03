import org.joaobarrera.model.OperationResult;
import org.joaobarrera.model.UnitType;
import org.joaobarrera.model.Workout;
import org.joaobarrera.service.WorkoutManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WorkoutManagerDeleteTest {

    private WorkoutManager workoutManager;

    private static final String DB_PATH = "src/test/resources/databases/test.db";

    @BeforeEach
    void setUp() throws SQLException {
        // Initialize manager
        workoutManager = new WorkoutManager();

        // Ensure test DB file exists
        File dbFile = new File(DB_PATH);
        if (!dbFile.exists()) {
            throw new IllegalStateException("Test database not found: " + DB_PATH);
        }

        // Connect to test DB
        workoutManager.connect(DB_PATH);

        // Clear all rows in Workout table
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM Workout");
        }
    }

    @Test
    @DisplayName("Should successfully delete workout with matching ID")
    void deleteWorkout_SuccessfullyDeletesMatchingID() {
        Workout workout = new Workout(null, "Run", LocalDateTime.now(), 30, 5.0, UnitType.KILOMETERS, "");
        workoutManager.addWorkout(workout);
        int idToDelete = workoutManager.getAllWorkouts().getData().get(0).getID();

        OperationResult<List<Workout>> deleteResult = workoutManager.deleteWorkout(idToDelete);

        assertTrue(deleteResult.isSuccess());
        assertEquals(0, workoutManager.getAllWorkouts().getData().size());
    }

    @Test
    @DisplayName("Should fail to delete when ID does not exist")
    void deleteWorkout_FailsWhenIDDoesNotExist() {
        Workout workout = new Workout(null, "Run", LocalDateTime.now(), 30, 5.0, UnitType.KILOMETERS, "");
        workoutManager.addWorkout(workout);

        OperationResult<List<Workout>> deleteResult = workoutManager.deleteWorkout(9999);

        assertFalse(deleteResult.isSuccess());
        assertEquals(1, workoutManager.getAllWorkouts().getData().size());
    }

    @Test
    @DisplayName("Should fail to delete when workout list is empty")
    void deleteWorkout_FailsWhenListIsEmpty() {
        OperationResult<List<Workout>> deleteResult = workoutManager.deleteWorkout(1);
        assertFalse(deleteResult.isSuccess());
    }

    @Test
    @DisplayName("Should only delete workout with matching ID")
    void deleteWorkout_OnlyDeletesMatchingWorkout() {
        Workout workout1 = new Workout(null, "Run", LocalDateTime.now(), 30, 5.0, UnitType.KILOMETERS, "");
        Workout workout2 = new Workout(null, "Bike", LocalDateTime.now(), 60, 20.0, UnitType.KILOMETERS, "");

        workoutManager.addWorkout(workout1);
        workoutManager.addWorkout(workout2);

        int idToDelete = workoutManager.getAllWorkouts().getData().get(0).getID();

        OperationResult<List<Workout>> deleteResult = workoutManager.deleteWorkout(idToDelete);

        assertTrue(deleteResult.isSuccess());
        assertEquals(1, workoutManager.getAllWorkouts().getData().size());
        assertEquals("Bike", workoutManager.getAllWorkouts().getData().get(0).getName());
    }

    @Test
    @DisplayName("Should delete correct workout when multiple with same data")
    void deleteWorkout_DeletesCorrectWorkoutWithSameData() {
        Workout workout1 = new Workout(null, "Run", LocalDateTime.now(), 30, 5.0, UnitType.KILOMETERS, "");
        Workout workout2 = new Workout(null, "Run", LocalDateTime.now(), 30, 5.0, UnitType.KILOMETERS, "");

        workoutManager.addWorkout(workout1);
        workoutManager.addWorkout(workout2);

        int idToNotDelete = workoutManager.getAllWorkouts().getData().get(0).getID();

        // delete second "Run"
        int idToDelete = workoutManager.getAllWorkouts().getData().get(1).getID();
        OperationResult<List<Workout>> deleteResult = workoutManager.deleteWorkout(idToDelete);

        assertTrue(deleteResult.isSuccess());
        assertEquals(1, workoutManager.getAllWorkouts().getData().size());

        // Confirm first one still exists
        assertEquals(idToNotDelete, workoutManager.getAllWorkouts().getData().get(0).getID());
    }

    @DisplayName("Should delete middle workout and preserve data integrity of others")
    @Test
    void deleteWorkout_IntegrityPreserved() {
        Workout w1 = new Workout(null, "Workout1", LocalDateTime.parse("2025-10-10T10:00"), 30, 5.0, UnitType.KILOMETERS, "Morning run");
        Workout w2 = new Workout(null, "Workout2", LocalDateTime.parse("2025-10-11T11:00"), 45, 10.0, UnitType.KILOMETERS, "Mid-day cycle");
        Workout w3 = new Workout(null, "Workout3", LocalDateTime.parse("2025-10-12T12:00"), 60, 15.0, UnitType.KILOMETERS, "Evening swim");

        workoutManager.addWorkout(w1);
        workoutManager.addWorkout(w2);
        workoutManager.addWorkout(w3);

        // Grab the ID of the workout in the middle (w2) and delete it
        int idToDelete = workoutManager.getAllWorkouts().getData().get(1).getID();
        OperationResult<List<Workout>> deleteResult = workoutManager.deleteWorkout(idToDelete);
        assertTrue(deleteResult.isSuccess());

        // Check list size
        List<Workout> remainingWorkouts = workoutManager.getAllWorkouts().getData();
        assertEquals(2, remainingWorkouts.size());

        // Verify w1 is still intact
        Workout remaining1 = remainingWorkouts.get(0);
        assertEquals("Workout1", remaining1.getName());
        assertEquals(LocalDateTime.parse("2025-10-10T10:00"), remaining1.getStartDateTime());
        assertEquals(30, remaining1.getDuration());
        assertEquals(5.0, remaining1.getDistance());
        assertEquals(UnitType.KILOMETERS, remaining1.getUnit());
        assertEquals("Morning run", remaining1.getNotes());

        // Verify w3 is still intact
        Workout remaining2 = remainingWorkouts.get(1);
        assertEquals("Workout3", remaining2.getName());
        assertEquals(LocalDateTime.parse("2025-10-12T12:00"), remaining2.getStartDateTime());
        assertEquals(60, remaining2.getDuration());
        assertEquals(15.0, remaining2.getDistance());
        assertEquals(UnitType.KILOMETERS, remaining2.getUnit());
        assertEquals("Evening swim", remaining2.getNotes());

        // Delete w1
        idToDelete = workoutManager.getAllWorkouts().getData().get(0).getID();
        deleteResult = workoutManager.deleteWorkout(idToDelete);
        assertTrue(deleteResult.isSuccess());

        // Verify w3 is still intact
        Workout remaining = remainingWorkouts.get(1);
        assertEquals("Workout3", remaining.getName());
        assertEquals(LocalDateTime.parse("2025-10-12T12:00"), remaining.getStartDateTime());
        assertEquals(60, remaining.getDuration());
        assertEquals(15.0, remaining.getDistance());
        assertEquals(UnitType.KILOMETERS, remaining.getUnit());
        assertEquals("Evening swim", remaining.getNotes());
    }

    @DisplayName("Should fail when trying to delete with zero or negative ID")
    @Test
    void deleteWorkout_InvalidIDValues() {
        OperationResult<List<Workout>> resultZero = workoutManager.deleteWorkout(0);
        OperationResult<List<Workout>> resultNegative = workoutManager.deleteWorkout(-1);

        assertFalse(resultZero.isSuccess());
        assertFalse(resultNegative.isSuccess());
    }

}
