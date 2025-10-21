import org.joaobarrera.model.OperationResult;
import org.joaobarrera.model.UnitType;
import org.joaobarrera.model.Workout;
import org.joaobarrera.service.WorkoutManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WorkoutManagerImportTest {

    private WorkoutManager workoutManager;

    @BeforeEach
    void setUp() {
        workoutManager = new WorkoutManager();
    }

    private String getResourcePath(String resourceName) throws URISyntaxException {
        URL resource = getClass().getClassLoader().getResource("import-tests/" + resourceName);
        assertNotNull(resource, "Test resource not found: " + resourceName);
        return Paths.get(resource.toURI()).toString();
    }

    @Test
    @DisplayName("Should successfully import from a valid file")
    void importFromFile_ShouldSucceed_WithValidFile() throws URISyntaxException {
        String filePath = getResourcePath("20-valid-workouts.txt");
        OperationResult<List<Workout>> result = workoutManager.importFromFile(filePath);

        assertTrue(result.isSuccess());
        assertEquals(20, workoutManager.getAllWorkouts().size());
    }

    @Test
    @DisplayName("Should fail to import from malformed file")
    void importFromFile_ShouldFail_WithMalformedFile() throws URISyntaxException {
        String filePath = getResourcePath("malformed-workouts.txt");
        OperationResult<List<Workout>> result = workoutManager.importFromFile(filePath);

        assertFalse(result.isSuccess());
        assertEquals(0, workoutManager.getAllWorkouts().size());
    }

    @Test
    @DisplayName("Should import nothing from empty file and succeed")
    void importFromFile_ShouldSucceed_WithEmptyFile() throws URISyntaxException {
        String filePath = getResourcePath("empty-file.txt");
        OperationResult<List<Workout>> result = workoutManager.importFromFile(filePath);

        assertTrue(result.isSuccess());
        assertEquals(0, workoutManager.getAllWorkouts().size());
    }

    @Test
    @DisplayName("Should fail when file path is null")
    void importFromFile_ShouldFail_WithNullPath() {
        OperationResult<List<Workout>> result = workoutManager.importFromFile(null);
        assertFalse(result.isSuccess());
        assertEquals(0, workoutManager.getAllWorkouts().size());
    }

    @Test
    @DisplayName("Should fail when file path is blank")
    void importFromFile_ShouldFail_WithBlankPath() {
        OperationResult<List<Workout>> result = workoutManager.importFromFile("   ");
        assertFalse(result.isSuccess());
        assertEquals(0, workoutManager.getAllWorkouts().size());
    }

    @Test
    @DisplayName("Should fail when file does not exist")
    void importFromFile_ShouldFail_WhenFileNotFound() {
        OperationResult<List<Workout>> result = workoutManager.importFromFile("nonexistent.txt");
        assertFalse(result.isSuccess());
        assertEquals(0, workoutManager.getAllWorkouts().size());
    }

    @Test
    @DisplayName("Should ignore empty lines and whitespace in import file")
    void importFromFile_ShouldIgnoreEmptyLines() throws URISyntaxException {
        String filePath = getResourcePath("15-valid-workouts-with-empty-lines.txt"); // create this file with empty lines sprinkled in
        OperationResult<List<Workout>> result = workoutManager.importFromFile(filePath);

        assertTrue(result.isSuccess());
        assertEquals(15, workoutManager.getAllWorkouts().size());
    }

    @Test
    @DisplayName("Should import all workouts including duplicates")
    void importFromFile_ShouldImportDuplicates() throws URISyntaxException {
        String filePath = getResourcePath("duplicate-workouts.txt"); // see file content below
        OperationResult<List<Workout>> result = workoutManager.importFromFile(filePath);

        assertTrue(result.isSuccess());
        assertEquals(3, workoutManager.getAllWorkouts().size());

        Workout w1 = workoutManager.getAllWorkouts().get(0);
        Workout w2 = workoutManager.getAllWorkouts().get(1);
        Workout w3 = workoutManager.getAllWorkouts().get(2);

        assertEquals(w1.getName(), w2.getName());
        assertEquals(w2.getName(), w3.getName());

        assertEquals(w1.getStartDateTime(), w2.getStartDateTime());
        assertEquals(w2.getStartDateTime(), w3.getStartDateTime());

        assertEquals(w1.getDuration(), w2.getDuration());
        assertEquals(w2.getDuration(), w3.getDuration());

        assertEquals(w1.getDistance(), w2.getDistance());
        assertEquals(w2.getDistance(), w3.getDistance());

        assertEquals(w1.getUnit(), w2.getUnit());
        assertEquals(w2.getUnit(), w3.getUnit());

        assertEquals(w1.getNotes(), w2.getNotes());
        assertEquals(w2.getNotes(), w3.getNotes());
    }

    @Test
    @DisplayName("Should import 3 distinct workouts with full data integrity")
    void importFromFile_ShouldImportThreeDistinctWorkouts_WithDataIntegrity() throws Exception {
        String filePath = getResourcePath("3-distinct-workouts.txt");
        OperationResult<List<Workout>> result = workoutManager.importFromFile(filePath);

        assertTrue(result.isSuccess());
        List<Workout> imported = workoutManager.getAllWorkouts();
        assertEquals(3, imported.size());

        Workout w1 = imported.get(0);
        assertEquals("Run", w1.getName());
        assertEquals(LocalDateTime.parse("2025-10-10T07:00"), w1.getStartDateTime());
        assertEquals(30, w1.getDuration());
        assertEquals(5.0, w1.getDistance());
        assertEquals(UnitType.KILOMETERS, w1.getUnit());
        assertEquals("Morning jog", w1.getNotes());

        Workout w2 = imported.get(1);
        assertEquals("Bike", w2.getName());
        assertEquals(LocalDateTime.parse("2025-10-11T12:00"), w2.getStartDateTime());
        assertEquals(60, w2.getDuration());
        assertEquals(20.0, w2.getDistance());
        assertEquals(UnitType.KILOMETERS, w2.getUnit());
        assertEquals("Lunch ride", w2.getNotes());

        Workout w3 = imported.get(2);
        assertEquals("Swim", w3.getName());
        assertEquals(LocalDateTime.parse("2025-10-12T18:00"), w3.getStartDateTime());
        assertEquals(45, w3.getDuration());
        assertEquals(1.5, w3.getDistance());
        assertEquals(UnitType.KILOMETERS, w3.getUnit());
        assertEquals("Evening swim", w3.getNotes());
    }

    @Test
    @DisplayName("Should add 2 workouts manually, import 3 more, and verify total 5 with data integrity")
    void addThreeThenImportThree_ShouldHaveSixDistinctWorkouts() throws Exception {
        // Add 2 manually
        Workout m1 = new Workout("Walk", LocalDateTime.parse("2025-10-08T08:00"), 20, 2.0, UnitType.KILOMETERS, "Morning walk");
        Workout m2 = new Workout("Yoga", LocalDateTime.parse("2025-10-09T09:30"), 40, 0.0, UnitType.KILOMETERS, "Stretching session");

        workoutManager.addWorkout(m1);
        workoutManager.addWorkout(m2);

        // Import 3 workouts from file
        String filePath = getResourcePath("3-distinct-workouts.txt");
        OperationResult<List<Workout>> importResult = workoutManager.importFromFile(filePath);

        assertTrue(importResult.isSuccess());

        List<Workout> allWorkouts = workoutManager.getAllWorkouts();
        assertEquals(5, allWorkouts.size());

        // Verify manually added
        Workout w1 = allWorkouts.get(0);
        assertEquals("Walk", w1.getName());
        assertEquals(LocalDateTime.parse("2025-10-08T08:00"), w1.getStartDateTime());
        assertEquals(20, w1.getDuration());
        assertEquals(2.0, w1.getDistance());
        assertEquals(UnitType.KILOMETERS, w1.getUnit());
        assertEquals("Morning walk", w1.getNotes());

        Workout w2 = allWorkouts.get(1);
        assertEquals("Yoga", w2.getName());
        assertEquals(LocalDateTime.parse("2025-10-09T09:30"), w2.getStartDateTime());
        assertEquals(40, w2.getDuration());
        assertEquals(0.0, w2.getDistance());
        assertEquals(UnitType.KILOMETERS, w2.getUnit());
        assertEquals("Stretching session", w2.getNotes());

        // Verify imported
        Workout w3 = allWorkouts.get(2);
        assertEquals("Run", w3.getName());
        assertEquals(LocalDateTime.parse("2025-10-10T07:00"), w3.getStartDateTime());
        assertEquals(30, w3.getDuration());
        assertEquals(5.0, w3.getDistance());
        assertEquals(UnitType.KILOMETERS, w3.getUnit());
        assertEquals("Morning jog", w3.getNotes());

        Workout w4 = allWorkouts.get(3);
        assertEquals("Bike", w4.getName());
        assertEquals(LocalDateTime.parse("2025-10-11T12:00"), w4.getStartDateTime());
        assertEquals(60, w4.getDuration());
        assertEquals(20.0, w4.getDistance());
        assertEquals(UnitType.KILOMETERS, w4.getUnit());
        assertEquals("Lunch ride", w4.getNotes());

        Workout w5 = allWorkouts.get(4);
        assertEquals("Swim", w5.getName());
        assertEquals(LocalDateTime.parse("2025-10-12T18:00"), w5.getStartDateTime());
        assertEquals(45, w5.getDuration());
        assertEquals(1.5, w5.getDistance());
        assertEquals(UnitType.KILOMETERS, w5.getUnit());
        assertEquals("Evening swim", w5.getNotes());
    }

    @Test
    @DisplayName("Import 20 valid workouts from CSV file and verify data integrity")
    void importFromFile_ShouldImport20ValidWorkoutsAndVerifyData() throws Exception {
        String filePath = getResourcePath("20-valid-workouts.csv");
        OperationResult<List<Workout>> result = workoutManager.importFromFile(filePath);

        assertTrue(result.isSuccess());
        List<Workout> imported = workoutManager.getAllWorkouts();
        assertEquals(20, imported.size());

        // Verify first workout
        Workout w1 = imported.get(0);
        assertEquals("Morning Run", w1.getName());
        assertEquals(LocalDateTime.parse("2025-10-07T06:30"), w1.getStartDateTime());
        assertEquals(45, w1.getDuration());
        assertEquals(5.0, w1.getDistance());
        assertEquals(UnitType.KILOMETERS, w1.getUnit());
        assertEquals("Felt great", w1.getNotes());

        // Verify fourth workout
        Workout w4 = imported.get(3);
        assertEquals("Hill Sprints", w4.getName());
        assertEquals(LocalDateTime.parse("2025-10-06T07:00"), w4.getStartDateTime());
        assertEquals(20, w4.getDuration());
        assertEquals(1.2, w4.getDistance());
        assertEquals(UnitType.MILES, w4.getUnit());
        assertEquals("Intense sprint session", w4.getNotes());

        // Verify last workout
        Workout w20 = imported.get(19);
        assertEquals("Group Ride", w20.getName());
        assertEquals(LocalDateTime.parse("2025-09-25T07:00"), w20.getStartDateTime());
        assertEquals(120, w20.getDuration());
        assertEquals(40.0, w20.getDistance());
        assertEquals(UnitType.KILOMETERS, w20.getUnit());
        assertEquals("Cycling group training session", w20.getNotes());
    }
}
