package org.joaobarrera.service;

import org.joaobarrera.model.OperationResult;
import org.joaobarrera.model.UnitType;
import org.joaobarrera.model.Workout;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

/*
 * Joao Barrera
 * CEN 3024 - Software Development 1
 * November 9, 2025
 * WorkoutManager.java
 */

/**
 * The WorkoutManager service class provides core business logic for managing workouts
 * in the Workout Logger application.
 * <p>
 * Responsibilities include connecting to an SQLite database, performing CRUD operations,
 * validating workout data, and converting units between kilometers and miles.
 * <p>
 * All database operations are safely parameterized to prevent SQL injection, and
 * validation ensures that only correct workout data is persisted.
 */
@Service
public class WorkoutManager {
    private Connection connection = null;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    /**
     * Default constructor.
     * <p>
     * Initializes the WorkoutManager service.
     */
    public WorkoutManager() { }

    /**
     * Connects to the specified SQLite database file.
     * <p>
     * Validates that the file exists, the Workout table is present,
     * and the schema is valid. Closes any existing connection before replacing it.
     *
     * @param dbFilePath path to the SQLite database file
     * @throws SQLException if a database access error occurs
     */
    public void connect(String dbFilePath) throws SQLException {
        // Validate the file exists
        File dbFile = new File(dbFilePath);
        if (!dbFile.exists()) {
            throw new IllegalArgumentException("Database file does not exist: " + dbFilePath);
        }

        // Attempt the connection
        String url = "jdbc:sqlite:" + dbFilePath;
        Connection newConnection = DriverManager.getConnection(url);

        // Check if the database has a Workout table
        if (!isWorkoutTablePresent(newConnection)) {
            throw new IllegalStateException("Workout table not found in the database.");
        }

        // Check if the Workout table has all required columns
        if (!isWorkoutTableValid(newConnection)) {
            throw new IllegalStateException("Workout table schema is invalid.");
        }

        // if a connection already exists, close it
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }

        // Replace connection
        connection = newConnection;
    }

    /**
     * Returns the current database file path if connected.
     * <p>
     * If no connection exists or the connection is closed, returns null.
     *
     * @return the full SQLite database file path or null if not connected
     */
    public String getCurrentDatabaseName() {
        try {
            if (connection == null || connection.isClosed()) return null;
            DatabaseMetaData meta = connection.getMetaData();
            String url = meta.getURL();
            if (url != null && url.startsWith("jdbc:sqlite:")) {
                return url.substring("jdbc:sqlite:".length());
            }
            return url;
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Adds a new workout to the database after validating the data.
     *
     * @param workout the Workout object to add
     * @return OperationResult containing the updated list of workouts and a success/failure message
     */
    public OperationResult<List<Workout>> addWorkout(Workout workout) {
        // Validate the database connection
        OperationResult<?> result = validateDatabaseConnected();
        if (!result.success()) return new OperationResult<>(false, null, result.message());

        // Validate given Workout Data
        OperationResult<String> validationResult = validateWorkout(workout);
        if (!validationResult.success()) {
            return new OperationResult<>(false, null, "Workout " + workout.getName() + " has validation errors: " + validationResult.message());
        }

        // Safely injects workout data into the SQL INSERT statement and runs it with the given data
        String sql = "INSERT INTO Workout (name, distance, unit, startDateTime, duration, notes) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            injectWorkoutIntoSQLStatement(workout, stmt);
            stmt.executeUpdate();
        } catch (SQLException e) {
            return new OperationResult<>(false, null, "Error adding workout: " + e.getMessage());
        }

        return new OperationResult<>(true, getAllWorkouts().data(), "Added workout: " + workout.getName());
    }

    /**
     * Retrieves all workouts stored in the database.
     *
     * @return OperationResult containing a list of all workouts and a success/failure message
     */
    public OperationResult<List<Workout>> getAllWorkouts() {
        // Validates the database connection is good
        OperationResult<?> result = validateDatabaseConnected();
        if (!result.success()) return new OperationResult<>(false, null, result.message());

        String sql = "SELECT * FROM Workout";
        List<Workout> workouts = new ArrayList<>();

        // Runs a SQL SELECT for ALL and converts all rows and adds to the list
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            addAllRowsToWorkoutList(rs, workouts);
        } catch (SQLException e) {
            return new OperationResult<>(false, null, "Error retrieving all workouts: " + e.getMessage());
        }

        return new OperationResult<>(true, workouts, "Retrieved all workouts.");
    }

    /**
     * Searches for workouts whose names contain the specified search term (case-insensitive).
     * <p>
     * Returns all workouts if the search term is empty or null.
     *
     * @param searchTerm the name or partial name to search for
     * @return OperationResult containing a list of matching workouts and a success/failure message
     */
    public OperationResult<List<Workout>> getWorkoutsBySearchParameter(String searchTerm) {
        // Validates if the database connection is good
        OperationResult<?> result = validateDatabaseConnected();
        if (!result.success()) return new OperationResult<>(false, null, result.message());

        List<Workout> workouts = new ArrayList<>();
        String sql;

        // Decides whether to retrieve all for an empty search term
        // or if SQL needs to actually do a search
        boolean hasSearch = searchTerm != null && !searchTerm.trim().isEmpty();
        if (hasSearch) {
            sql = "SELECT * FROM Workout WHERE LOWER(name) LIKE ?";
        } else {
            sql = "SELECT * FROM Workout";
        }

        // Injects searchTerm into LIKE field then runs SQL SELECT
        // where the lower-cased name matches the search term
        // Converts all rows and adds to the list
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            if (hasSearch) stmt.setString(1, "%" + searchTerm.toLowerCase() + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                addAllRowsToWorkoutList(rs, workouts);
            }
        } catch (SQLException e) {
            return new OperationResult<>(false, null, "Error searching workouts: " + e.getMessage());
        }

        return new OperationResult<>(true, workouts, "Found " + workouts.size() + " matching workouts.");
    }

    /**
     * Updates an existing workout identified by the given ID.
     * <p>
     * Validates the workout and updates the database with the new data.
     *
     * @param workoutID the ID of the workout to update
     * @param updatedWorkout the updated workout data
     * @return OperationResult containing the updated list of workouts and a success/failure message
     */
    public OperationResult<List<Workout>> updateWorkout(Integer workoutID, Workout updatedWorkout) {
        // Validates database connection is good
        OperationResult<?> result = validateDatabaseConnected();
        if (!result.success()) return new OperationResult<>(false, null, result.message());

        // Validate given Workout ID
        OperationResult<Boolean> idCheck = IDExists(workoutID);
        if (!idCheck.success()) return new OperationResult<>(false, null, idCheck.message());
        if (!idCheck.data()) return new OperationResult<>(false, null, "Workout with ID " + workoutID + " not found.");

        // Validate given Workout data
        OperationResult<String> validationResult = validateWorkout(updatedWorkout);
        if (!validationResult.success()) {
            return new OperationResult<>(false, null, "Workout " + updatedWorkout.getName() + " has validation errors: " + validationResult.message());
        }

        // Injects new data into SQL UPDATE statement where the ID matches the given ID
        String sql = "UPDATE Workout SET name = ?, distance = ?, unit = ?, startDateTime = ?, duration = ?, notes = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            injectWorkoutIntoSQLStatement(updatedWorkout, stmt);
            stmt.setInt(7, workoutID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            return new OperationResult<>(false, null, "Error updating workout: " + e.getMessage());
        }

        return new OperationResult<>(true, getAllWorkouts().data(), "Workout " + workoutID + " updated.");
    }

    /**
     * Deletes a workout from the database by its ID.
     *
     * @param workoutID the ID of the workout to delete
     * @return OperationResult containing the updated list of workouts and a success/failure message
     */
    public OperationResult<List<Workout>> deleteWorkout(Integer workoutID) {
        // Validates database connection is good
        OperationResult<?> result = validateDatabaseConnected();
        if (!result.success()) return new OperationResult<>(false, null, result.message());

        // Validate given Workout ID
        OperationResult<Boolean> idCheck = IDExists(workoutID);
        if (!idCheck.success()) return new OperationResult<>(false, null, idCheck.message());
        if (!idCheck.data()) return new OperationResult<>(false, null, "Workout with ID " + workoutID + " not found.");

        // Runs SQL DELETE where the ID equals the given ID
        String sql = "DELETE FROM Workout WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, workoutID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            return new OperationResult<>(false, null, "Error deleting workout: " + e.getMessage());
        }

        return new OperationResult<>(true, getAllWorkouts().data(), "Deleted workout ID " + workoutID);
    }

    /**
     * Converts all stored workouts to the specified unit type (kilometers or miles).
     * <p>
     * Updates both the in-memory objects and the database records.
     *
     * @param targetUnit the unit type to convert all workouts to
     * @return OperationResult containing the updated list of workouts and a success/failure message
     */
    public OperationResult<List<Workout>> convertAllUnits(UnitType targetUnit) {
        // Validate if the database connection is good
        OperationResult<?> result = validateDatabaseConnected();
        if (!result.success()) return new OperationResult<>(false, null, result.message());

        // Validate targetUnit is valid
        if (targetUnit == null)
            return new OperationResult<>(false, null, "Target unit cannot be null.");

        // Validate there are workouts to convert
        List<Workout> workouts = getAllWorkouts().data();
        if (workouts == null || workouts.isEmpty())
            return new OperationResult<>(false, null, "No workouts to convert.");

        // Converts all to target unit, then runs SQL UPDATE on ID with new values
        for (Workout w : workouts) {
            if (w.getUnit() != targetUnit) {
                double convertedDistance = w.getUnit() == UnitType.KILOMETERS
                        ? w.getDistance() * 0.621371   // km → miles
                        : w.getDistance() / 0.621371;  // miles → km
                w.setDistance(convertedDistance);
                w.setUnit(targetUnit);

                String sql = "UPDATE Workout SET distance = ?, unit = ? WHERE id = ?";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setDouble(1, w.getDistance());
                    stmt.setString(2, w.getUnit().name());
                    stmt.setInt(3, w.getID());
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    return new OperationResult<>(false, null, "Error converting workout ID " + w.getID() + ": " + e.getMessage());
                }
            }
        }

        return new OperationResult<>(true, getAllWorkouts().data(), "Converted all workouts to " + targetUnit);
    }

    // -- Validation methods --
    // All validation methods return an error message if they are invalid

    // ID must be positive integer
    private String validateID(Integer workoutID) {
        boolean valid = workoutID != null && workoutID > 0;
        return valid ? null : "Workout ID cannot be null and must be greater than 0." ;
    }

    // Name must be a non-empty string with at most 50 characters
    private String validateName(String name) {
        boolean valid = name != null && !name.trim().isEmpty() && name.length() <= 50;
        return valid ? null : "Workout name cannot be null or empty or longer than 50 characters." ;
    }

    // Start Date Time cannot be null and must be in a valid format
    private String validateStartDateTime(LocalDateTime start) {
        boolean valid = start != null;
        return valid ? null : "Start date/time cannot be null and must be in 'YYYY-MM-DDTHH:MM' format (e.g. 2025-10-04T14:30).";
    }

    // Duration needs to be greater than zero
    private String validateDuration(Integer duration) {
        boolean valid = duration != null && duration >= 1;
        return valid ? null : "Duration must be at least 1 minute.";
    }

    // Distance needs to be non-negative
    private String validateDistance(Double distance) {
        boolean valid = distance != null && distance >= 0;
        return valid ? null : "Distance must be a non-negative number.";
    }

    // Unit cannot be null
    private String validateUnit(UnitType unit) {
        boolean valid = unit != null;
        return valid ? null : "Unit must be either KILOMETERS or MILES.";
    }

    // Notes need to be at most 200 characters
    private String validateNotes(String notes) {
        boolean valid = notes == null || notes.length() <= 200;
        return valid ? null : "Notes cannot exceed 200 characters.";
    }

    // Runs all checks and returns an error message if there's any validation error
    private OperationResult<String> validateWorkout(Workout workout) {
        String error = validateName(workout.getName());
        if (error != null) return new OperationResult<>(false, null, error);

        error = validateStartDateTime(workout.getStartDateTime());
        if (error != null) return new OperationResult<>(false, null, error);

        error = validateDuration(workout.getDuration());
        if (error != null) return new OperationResult<>(false, null, error);

        error = validateDistance(workout.getDistance());
        if (error != null) return new OperationResult<>(false, null, error);

        error = validateUnit(workout.getUnit());
        if (error != null) return new OperationResult<>(false, null, error);

        error = validateNotes(workout.getNotes());
        if (error != null) return new OperationResult<>(false, null, error);

        return new OperationResult<>(true, null, "Workout is valid.");
    }

    // Check if the database connection is not null and not closed, returns an error message if it's not connected
    private OperationResult<?> validateDatabaseConnected() {
        try {
            if (connection == null || connection.isClosed()) {
                return new OperationResult<>(false, null, "Not connected to database.");
            }
        } catch (Exception e) {
            return new OperationResult<>(false, null, "Error checking if database is connected " + e.getMessage());
        }

        return new OperationResult<>(true, null, "Database is connected.");
    }

    // Check if a table exists
    private boolean isWorkoutTablePresent(Connection connection) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        try (ResultSet rs = meta.getTables(null, null, "Workout", new String[]{"TABLE"})) {
            return rs.next();
        }
    }

    // Check if the Workout table has the correct schema
    private boolean isWorkoutTableValid(Connection connection) throws SQLException {
        String sql = "PRAGMA table_info(Workout)";
        boolean idFound = false, nameFound = false, distanceFound = false, unitFound = false,
                startDateTimeFound = false, durationFound = false, notesFound = false;

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String column = rs.getString("name");
                switch (column) {
                    case "id" -> idFound = true;
                    case "name" -> nameFound = true;
                    case "distance" -> distanceFound = true;
                    case "unit" -> unitFound = true;
                    case "startDateTime" -> startDateTimeFound = true;
                    case "duration" -> durationFound = true;
                    case "notes" -> notesFound = true;
                }
            }
        }
        return idFound && nameFound && distanceFound && unitFound && startDateTimeFound && durationFound && notesFound;
    }

    // -- Database Helper Methods --

    // Checks if a given workout ID exists
    private OperationResult<Boolean> IDExists(Integer workoutID) {
        OperationResult<?> result = validateDatabaseConnected();
        if (!result.success()) return new OperationResult<>(false, null, result.message());

        String validateIDResult = validateID(workoutID);
        if (validateIDResult != null) return new OperationResult<>(false, null, validateIDResult);

        String sql = "SELECT COUNT(*) FROM Workout WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, workoutID);
            try (ResultSet rs = stmt.executeQuery()) {
                boolean exists = rs.next() && rs.getInt(1) > 0;
                return new OperationResult<>(true, exists, exists ? "ID exists." : "ID does not exist.");
            }
        } catch (SQLException e) {
            return new OperationResult<>(false, null, "Error checking ID existence: " + e.getMessage());
        }
    }

    // Receives a resultSet with workout rows (SQL)
    // Converts each row to a Workout Object and adds to the list
    private void addAllRowsToWorkoutList(ResultSet rs, List<Workout> workouts) throws SQLException {
        while (rs.next()) {
            Workout w = new Workout(
                    rs.getInt("id"),
                    rs.getString("name"),
                    LocalDateTime.parse(rs.getString("startDateTime"), formatter),
                    rs.getInt("duration"),
                    rs.getDouble("distance"),
                    UnitType.valueOf(rs.getString("unit")),
                    rs.getString("notes")
            );
            workouts.add(w);
        }
    }

    // Safely Injects Workout data into SQL statement
    private void injectWorkoutIntoSQLStatement(Workout workout, PreparedStatement stmt) throws SQLException {
        stmt.setString(1, workout.getName());
        stmt.setDouble(2, workout.getDistance());
        stmt.setString(3, workout.getUnit().name());
        stmt.setString(4, workout.getStartDateTime().format(formatter));
        stmt.setInt(5, workout.getDuration());
        stmt.setString(6, workout.getNotes());
    }
}
