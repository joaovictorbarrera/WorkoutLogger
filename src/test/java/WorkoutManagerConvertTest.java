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

public class WorkoutManagerConvertTest {

    private WorkoutManager workoutManager;

    @BeforeEach
    void setUp() {
        workoutManager = new WorkoutManager();
    }

    @Test
    @DisplayName("Should fail to convert units when no workouts exist")
    void convertAllUnits_ShouldFail_WhenNoWorkouts() {
        OperationResult<List<Workout>> result = workoutManager.convertAllUnits(UnitType.MILES);
        assertFalse(result.isSuccess());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("Should successfully convert workouts with mixed units to MILES")
    void convertAllUnits_ShouldConvertWorkoutsWithMixedUnitsToMiles() {
        // Add workouts with mixed units
        workoutManager.addWorkout(new Workout("Run", LocalDateTime.parse("2025-10-10T08:00"), 30, 5.0, UnitType.KILOMETERS, ""));
        workoutManager.addWorkout(new Workout("Walk", LocalDateTime.parse("2025-10-11T10:00"), 45, 3.0, UnitType.MILES, ""));

        OperationResult<List<Workout>> result = workoutManager.convertAllUnits(UnitType.MILES);

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(2, result.getData().size());

        Workout firstWorkout = result.getData().get(0);
        assertEquals(UnitType.MILES, firstWorkout.getUnit());
        // 5 km to miles
        assertEquals(3.10686, firstWorkout.getDistance(), 0.0001);

        Workout secondWorkout = result.getData().get(1);
        assertEquals(UnitType.MILES, secondWorkout.getUnit());
        assertEquals(3.0, secondWorkout.getDistance());
    }

    @Test
    @DisplayName("Should successfully convert workouts with mixed units to KILOMETERS")
    void convertAllUnits_ShouldConvertWorkoutsWithMixedUnitsToKilometers() {
        // Add workouts with mixed units
        workoutManager.addWorkout(new Workout("Run", LocalDateTime.parse("2025-10-10T08:00"), 30, 3.0, UnitType.MILES, ""));
        workoutManager.addWorkout(new Workout("Walk", LocalDateTime.parse("2025-10-11T10:00"), 45, 5.0, UnitType.KILOMETERS, ""));

        OperationResult<List<Workout>> result = workoutManager.convertAllUnits(UnitType.KILOMETERS);

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(2, result.getData().size());

        Workout firstWorkout = result.getData().get(0);
        assertEquals(UnitType.KILOMETERS, firstWorkout.getUnit());
        // 3 miles to km
        assertEquals(4.82802, firstWorkout.getDistance(), 0.0001);

        Workout secondWorkout = result.getData().get(1);
        assertEquals(UnitType.KILOMETERS, secondWorkout.getUnit());
        assertEquals(5.0, secondWorkout.getDistance());
    }


    @Test
    @DisplayName("Should convert all workouts from MILES to KILOMETERS")
    void convertAllUnits_ShouldConvertMilesToKilometers() {
        workoutManager.addWorkout(new Workout("Cycle", LocalDateTime.parse("2025-10-12T07:30"), 60, 10.0, UnitType.MILES, ""));
        workoutManager.addWorkout(new Workout("Jog", LocalDateTime.parse("2025-10-13T09:00"), 20, 4.0, UnitType.MILES, ""));

        OperationResult<List<Workout>> result = workoutManager.convertAllUnits(UnitType.KILOMETERS);

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(2, result.getData().size());

        Workout firstWorkout = result.getData().get(0);
        assertEquals(UnitType.KILOMETERS, firstWorkout.getUnit());
        // 10 miles to km
        assertEquals(16.0934, firstWorkout.getDistance(), 0.0001);

        Workout secondWorkout = result.getData().get(1);
        assertEquals(UnitType.KILOMETERS, secondWorkout.getUnit());
        // 4 miles to km
        assertEquals(6.4374, secondWorkout.getDistance(), 0.0001);
    }

    @Test
    @DisplayName("Should convert all workouts from KILOMETERS to MILES")
    void convertAllUnits_ShouldConvertKilometersToMiles() {
        workoutManager.addWorkout(new Workout("Run", LocalDateTime.parse("2025-10-12T07:30"), 30, 5.0, UnitType.KILOMETERS, ""));
        workoutManager.addWorkout(new Workout("Walk", LocalDateTime.parse("2025-10-13T09:00"), 45, 8.0, UnitType.KILOMETERS, ""));

        OperationResult<List<Workout>> result = workoutManager.convertAllUnits(UnitType.MILES);

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(2, result.getData().size());

        Workout firstWorkout = result.getData().get(0);
        assertEquals(UnitType.MILES, firstWorkout.getUnit());
        // 5 km to miles
        assertEquals(3.10686, firstWorkout.getDistance(), 0.0001);

        Workout secondWorkout = result.getData().get(1);
        assertEquals(UnitType.MILES, secondWorkout.getUnit());
        // 8 km to miles
        assertEquals(4.97097, secondWorkout.getDistance(), 0.0001);
    }

    @Test
    @DisplayName("Should convert 2 workouts and retain all other data through multiple conversions")
    void convertAllUnits_ShouldRetainOtherDataIntegrity() {
        Workout w1 = new Workout("Run", LocalDateTime.parse("2025-10-12T07:30"), 30, 5.0, UnitType.MILES, "Morning run");
        Workout w2 = new Workout("Walk", LocalDateTime.parse("2025-10-13T09:00"), 45, 3.0, UnitType.KILOMETERS, "Evening walk");

        workoutManager.addWorkout(w1);
        workoutManager.addWorkout(w2);

        // Convert to KILOMETERS
        OperationResult<List<Workout>> resultKm = workoutManager.convertAllUnits(UnitType.KILOMETERS);
        assertTrue(resultKm.isSuccess());

        // Verify Data Integrity
        List<Workout> convertedToKm = resultKm.getData();
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
        assertTrue(resultMiles.isSuccess());

        // Verify Data Integrity
        List<Workout> convertedToMiles = resultMiles.getData();
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

    @Test
    @DisplayName("Should not change stored workout reference when converting to same unit")
    void convertAllUnits_ShouldNotChangeReference_WhenUnitIsSame() {
        Workout workout = new Workout("Run", LocalDateTime.parse("2025-10-12T07:30"), 30, 5.0, UnitType.KILOMETERS, "Morning run");
        workoutManager.addWorkout(workout);

        // Store original reference
        Workout original = workoutManager.getAllWorkouts().get(0);

        OperationResult<List<Workout>> result = workoutManager.convertAllUnits(UnitType.KILOMETERS);

        assertTrue(result.isSuccess());
        List<Workout> converted = workoutManager.getAllWorkouts();
        assertEquals(1, converted.size());

        Workout afterConvert = workoutManager.getAllWorkouts().get(0);

        // The reference should be the same because no conversion needed
        assertSame(original, afterConvert);
    }

    @Test
    @DisplayName("Should change stored workout reference when converting to different unit")
    void convertAllUnits_ShouldChangeReference_WhenUnitIsDifferent() {
        Workout workout = new Workout("Run", LocalDateTime.parse("2025-10-12T07:30"), 30, 5.0, UnitType.KILOMETERS, "Morning run");
        workoutManager.addWorkout(workout);

        // Store original reference
        Workout original = workoutManager.getAllWorkouts().get(0);

        OperationResult<List<Workout>> result = workoutManager.convertAllUnits(UnitType.MILES);

        assertTrue(result.isSuccess());
        List<Workout> converted = workoutManager.getAllWorkouts();
        assertEquals(1, converted.size());

        Workout afterConvert = workoutManager.getAllWorkouts().get(0);

        // The reference should be different because a new workout object is created on conversion
        assertNotSame(original, afterConvert);
    }

}
