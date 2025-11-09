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

/*
 * Joao Barrera
 * CEN 3024 - Software Development 1
 * November 9, 2025,
 * WorkoutManagerConvertTest.java
 * This class uses unit testing to validate the convertUnits() functionality.
 */
public class WorkoutManagerConvertTest {

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
    @DisplayName("Should fail to convert units when no workouts exist")
    void convertAllUnits_ShouldFail_WhenNoWorkouts() {
        OperationResult<List<Workout>> result = workoutManager.convertAllUnits(UnitType.MILES);
        assertFalse(result.success());
        assertNull(result.data());
    }

    @Test
    @DisplayName("Should successfully convert workouts with mixed units to MILES")
    void convertAllUnits_ShouldConvertWorkoutsWithMixedUnitsToMiles() {
        // Add workouts with mixed units
        workoutManager.addWorkout(new Workout(null, "Run", LocalDateTime.parse("2025-10-10T08:00"), 30, 5.0, UnitType.KILOMETERS, ""));
        workoutManager.addWorkout(new Workout(null, "Walk", LocalDateTime.parse("2025-10-11T10:00"), 45, 3.0, UnitType.MILES, ""));

        OperationResult<List<Workout>> result = workoutManager.convertAllUnits(UnitType.MILES);

        assertTrue(result.success());
        assertNotNull(result.data());
        assertEquals(2, result.data().size());

        Workout firstWorkout = result.data().get(0);
        assertEquals(UnitType.MILES, firstWorkout.getUnit());
        // 5 km to miles
        assertEquals(3.10686, firstWorkout.getDistance(), 0.0001);

        Workout secondWorkout = result.data().get(1);
        assertEquals(UnitType.MILES, secondWorkout.getUnit());
        assertEquals(3.0, secondWorkout.getDistance());
    }

    @Test
    @DisplayName("Should successfully convert workouts with mixed units to KILOMETERS")
    void convertAllUnits_ShouldConvertWorkoutsWithMixedUnitsToKilometers() {
        // Add workouts with mixed units
        workoutManager.addWorkout(new Workout(null, "Run", LocalDateTime.parse("2025-10-10T08:00"), 30, 3.0, UnitType.MILES, ""));
        workoutManager.addWorkout(new Workout(null, "Walk", LocalDateTime.parse("2025-10-11T10:00"), 45, 5.0, UnitType.KILOMETERS, ""));

        OperationResult<List<Workout>> result = workoutManager.convertAllUnits(UnitType.KILOMETERS);

        assertTrue(result.success());
        assertNotNull(result.data());
        assertEquals(2, result.data().size());

        Workout firstWorkout = result.data().get(0);
        assertEquals(UnitType.KILOMETERS, firstWorkout.getUnit());
        // 3 miles to km
        assertEquals(4.82802, firstWorkout.getDistance(), 0.0001);

        Workout secondWorkout = result.data().get(1);
        assertEquals(UnitType.KILOMETERS, secondWorkout.getUnit());
        assertEquals(5.0, secondWorkout.getDistance());
    }


    @Test
    @DisplayName("Should convert all workouts from MILES to KILOMETERS")
    void convertAllUnits_ShouldConvertMilesToKilometers() {
        workoutManager.addWorkout(new Workout(null, "Cycle", LocalDateTime.parse("2025-10-12T07:30"), 60, 10.0, UnitType.MILES, ""));
        workoutManager.addWorkout(new Workout(null, "Jog", LocalDateTime.parse("2025-10-13T09:00"), 20, 4.0, UnitType.MILES, ""));

        OperationResult<List<Workout>> result = workoutManager.convertAllUnits(UnitType.KILOMETERS);

        assertTrue(result.success());
        assertNotNull(result.data());
        assertEquals(2, result.data().size());

        Workout firstWorkout = result.data().get(0);
        assertEquals(UnitType.KILOMETERS, firstWorkout.getUnit());
        // 10 miles to km
        assertEquals(16.0934, firstWorkout.getDistance(), 0.0001);

        Workout secondWorkout = result.data().get(1);
        assertEquals(UnitType.KILOMETERS, secondWorkout.getUnit());
        // 4 miles to km
        assertEquals(6.4374, secondWorkout.getDistance(), 0.0001);
    }

    @Test
    @DisplayName("Should convert all workouts from KILOMETERS to MILES")
    void convertAllUnits_ShouldConvertKilometersToMiles() {
        workoutManager.addWorkout(new Workout(null, "Run", LocalDateTime.parse("2025-10-12T07:30"), 30, 5.0, UnitType.KILOMETERS, ""));
        workoutManager.addWorkout(new Workout(null, "Walk", LocalDateTime.parse("2025-10-13T09:00"), 45, 8.0, UnitType.KILOMETERS, ""));

        OperationResult<List<Workout>> result = workoutManager.convertAllUnits(UnitType.MILES);

        assertTrue(result.success());
        assertNotNull(result.data());
        assertEquals(2, result.data().size());

        Workout firstWorkout = result.data().get(0);
        assertEquals(UnitType.MILES, firstWorkout.getUnit());
        // 5 km to miles
        assertEquals(3.10686, firstWorkout.getDistance(), 0.0001);

        Workout secondWorkout = result.data().get(1);
        assertEquals(UnitType.MILES, secondWorkout.getUnit());
        // 8 km to miles
        assertEquals(4.97097, secondWorkout.getDistance(), 0.0001);
    }

    @Test
    @DisplayName("Should convert 2 workouts and retain all other data through multiple conversions")
    void convertAllUnits_ShouldRetainOtherDataIntegrity() {
        Workout w1 = new Workout(null, "Run", LocalDateTime.parse("2025-10-12T07:30"), 30, 5.0, UnitType.MILES, "Morning run");
        Workout w2 = new Workout(null, "Walk", LocalDateTime.parse("2025-10-13T09:00"), 45, 3.0, UnitType.KILOMETERS, "Evening walk");

        workoutManager.addWorkout(w1);
        workoutManager.addWorkout(w2);

        // Convert to KILOMETERS
        OperationResult<List<Workout>> resultKm = workoutManager.convertAllUnits(UnitType.KILOMETERS);
        assertTrue(resultKm.success());

        // Verify Data Integrity
        List<Workout> convertedToKm = resultKm.data();
        assertEquals(2, convertedToKm.size());

        Workout c1Km = convertedToKm.get(0);
        assertEquals(w1.getName(), c1Km.getName());
        assertEquals(w1.getStartDateTime(), c1Km.getStartDateTime());
        assertEquals(w1.getDuration(), c1Km.getDuration());
        assertEquals(w1.getNotes(), c1Km.getNotes());

        Workout c2Km = convertedToKm.get(1);
        assertEquals(w2.getName(), c2Km.getName());
        assertEquals(w2.getStartDateTime(), c2Km.getStartDateTime());
        assertEquals(w2.getDuration(), c2Km.getDuration());
        assertEquals(w2.getNotes(), c2Km.getNotes());

        // Convert back to MILES
        OperationResult<List<Workout>> resultMiles = workoutManager.convertAllUnits(UnitType.MILES);
        assertTrue(resultMiles.success());

        // Verify Data Integrity
        List<Workout> convertedToMiles = resultMiles.data();
        assertEquals(2, convertedToMiles.size());

        Workout c1Miles = convertedToMiles.get(0);
        assertEquals(w1.getName(), c1Miles.getName());
        assertEquals(w1.getStartDateTime(), c1Miles.getStartDateTime());
        assertEquals(w1.getDuration(), c1Miles.getDuration());
        assertEquals(w1.getNotes(), c1Miles.getNotes());

        Workout c2Miles = convertedToMiles.get(1);
        assertEquals(w2.getName(), c2Miles.getName());
        assertEquals(w2.getStartDateTime(), c2Miles.getStartDateTime());
        assertEquals(w2.getDuration(), c2Miles.getDuration());
        assertEquals(w2.getNotes(), c2Miles.getNotes());
    }
}
