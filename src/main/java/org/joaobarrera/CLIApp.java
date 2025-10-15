package org.joaobarrera;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

// Main class for CLI interaction
// handles all user input/output and delegates logic to WorkoutManager
public class CLIApp {
    private final WorkoutManager workoutManager;
    private final Scanner scanner;

    public CLIApp(WorkoutManager manager) {
        this.workoutManager = manager;
        this.scanner = new Scanner(System.in);
    }

    // Main menu loop: continuously prompt the user until they choose to exit
    public void run() {
        while (true) {
            int choice = displayMenuAndGetChoice();
            switch (choice) {
                case 1 -> handleImportFromFile();
                case 2 -> handleViewWorkouts();
                case 3 -> handleFilterWorkouts();
                case 4 -> handleAddWorkout();
                case 5 -> handleEditWorkout();
                case 6 -> handleDeleteWorkout();
                case 7 -> handleConvertUnits();
                case 8 -> handleExportToFile();
                case 9 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid choice");
            }
        }
    }

    // Prints menu and gets user’s numeric choice
    private int displayMenuAndGetChoice() {
        System.out.println("\n--- Workout Manager Menu ---");
        System.out.println("1. Import from file");
        System.out.println("2. View workouts");
        System.out.println("3. Search workouts");
        System.out.println("4. Add workout");
        System.out.println("5. Edit workout");
        System.out.println("6. Delete workout");
        System.out.println("7. Convert units");
        System.out.println("8. Export File");
        System.out.println("9. Exit");

        Integer choice;
        do {
            System.out.print("Enter choice: ");
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                choice = null;
                System.out.println("Invalid input. Please enter a numeric choice.");
            }
        } while (choice == null);

        return choice;
    }

    // Prompts user for all workout fields, validates each, and returns a Workout object
    // Returns null if user intentionally canceled by entering -1
    private Workout getWorkoutInput() {
        System.out.println("\nEnter workout details (type -1 at any time to cancel)");

        String name = promptAndValidate("Enter name: ",
                s -> s,
                workoutManager::validateName);
        if (name == null) return null;

        LocalDateTime startDateTime = promptAndValidate(
                "Enter date/time (YYYY-MM-DDTHH:MM): ",
                this::parseDateTime,
                workoutManager::validateStartDateTime);
        if (startDateTime == null) return null;

        Integer duration = promptAndValidate(
                "Enter duration (minutes): ",
                this::parseDuration,
                workoutManager::validateDuration);
        if (duration == null) return null;

        Double distance = promptAndValidate(
                "Enter distance: ",
                this::parseDistance,
                workoutManager::validateDistance);
        if (distance == null) return null;

        UnitType unit = promptAndValidate(
                "Enter unit (KILOMETERS/MILES): ",
                this::parseUnit,
                workoutManager::validateUnit);
        if (unit == null) return null;

        String notes = promptAndValidate(
                "Enter notes (optional, max 200 chars): ",
                s -> s,
                workoutManager::validateNotes);
        if (notes == null) return null;

        return new Workout(name, startDateTime, duration, distance, unit, notes);
    }

    // Generic prompt+validate loop — applies a parser and validator, repeats until valid or cancelled
    private <T> T promptAndValidate(String prompt, Function<String, T> parser, Function<T, String> validator) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            // Allow user to cancel input
            if (input.equals("-1")) {
                return null;
            }

            T value = parser.apply(input);
            String error = validator.apply(value);

            if (error == null) return value;
            System.out.println(error);
        }
    }

    // Asks user for a valid workout ID that exists in memory
    private Integer getWorkoutIDInput() {
        while (true) {
            System.out.print("Enter workout ID to edit (-1 to cancel): ");
            String input = scanner.nextLine().trim();

            if (input.equals("-1")) return -1; // cancel
            int workoutID;
            try {
                workoutID = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric Workout ID.");
                continue;
            }

            String error = workoutManager.validateID(workoutID);
            if (error != null) {
                System.out.println(error);
                continue;
            }

            if (!workoutManager.IDExists(workoutID)) {
                System.out.println("There are no records with Workout ID " + workoutID);
                continue;
            }
            return workoutID;
        }
    }

    // Parses date/time string into LocalDateTime
    // Returns null if it is unable to parse user input
    private LocalDateTime parseDateTime(String input) {
        try {
            return LocalDateTime.parse(input);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    // Parses duration string into Integer
    // Returns null if it is unable to parse user input
    private Integer parseDuration(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // Parses distance string into Double
    // Returns null if it is unable to parse user input
    private Double parseDistance(String input) {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // Parses and converts unit string into UnitType
    // Returns null if it is unable to parse user input
    private UnitType parseUnit(String input) {
        try {
            return UnitType.valueOf(input.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }

    // Adds a new workout based on user input
    private boolean handleAddWorkout() {
        Workout workout = getWorkoutInput();
        if (workout == null) {
            System.out.println("Aborted.");
            return false;
        }
        OperationResult<List<Workout>> addOperationResult = workoutManager.addWorkout(workout);
        System.out.println(addOperationResult.getMessage());
        return addOperationResult.isSuccess();
    }

    // Edits an existing workout by Workout ID based on user input
    private boolean handleEditWorkout() {
        Integer workoutID = getWorkoutIDInput();
        if (workoutID == -1) {
            System.out.println("Aborted.");
            return false;
        }

        Workout updatedWorkout = getWorkoutInput();
        if (updatedWorkout == null) {
            System.out.println("Aborted.");
            return false;
        }
        OperationResult<List<Workout>> updateOperationResult = workoutManager.updateWorkout(workoutID, updatedWorkout);
        System.out.println(updateOperationResult.getMessage());
        return updateOperationResult.isSuccess();
    }

    // Deletes a workout by ID based on user input
    private boolean handleDeleteWorkout() {
        Integer workoutID = getWorkoutIDInput();
        if (workoutID == -1) {
            System.out.println("Aborted.");
            return false;
        }

        OperationResult<List<Workout>> deleteOperationResult = workoutManager.deleteWorkout(workoutID);
        System.out.println(deleteOperationResult.getMessage());
        return deleteOperationResult.isSuccess();
    }

    // Imports workouts based on file path given by user and adds them to the list
    private List<Workout> handleImportFromFile() {
        OperationResult<List<Workout>> importOperationResult;
        do {
            System.out.print("Enter file path to import (-1 to cancel): ");
            String path = scanner.nextLine();
            if (path.equals("-1")) {
                System.out.println("Aborted.");
                return null;
            }

            importOperationResult = workoutManager.importFromFile(path);

            if (!importOperationResult.isSuccess()) System.out.println(importOperationResult.getMessage());
        } while (!importOperationResult.isSuccess());

        System.out.println(importOperationResult.getMessage());
        return importOperationResult.getData();
    }

    // Exports workouts to a file based on a file path given by the user
    private String handleExportToFile() {
        OperationResult<String> exportOperationResult;
        do {
            System.out.print("Enter file path to export workouts (-1 to cancel): ");
            String filePath = scanner.nextLine();
            if (filePath.equals("-1")) {
                System.out.println("Aborted.");
                return null;
            }

            exportOperationResult = workoutManager.exportToFile(filePath);

            if (!exportOperationResult.isSuccess()) {
                System.out.println(exportOperationResult.getMessage());
            }
        } while (!exportOperationResult.isSuccess());

        System.out.println(exportOperationResult.getMessage());
        return exportOperationResult.getData();
    }

    // Converts all workouts to a given specified unit (KILOMETERS or MILES)
    private List<Workout> handleConvertUnits() {
        UnitType unitTarget;
        boolean valid;
        do {
            System.out.print("Enter target unit (KILOMETERS/MILES): ");
            unitTarget = parseUnit(scanner.nextLine());
            String error = workoutManager.validateUnit(unitTarget);
            if (error != null) System.out.println(error);
            valid = error == null;
        } while (!valid);

        OperationResult<List<Workout>> convertOperationResult = workoutManager.convertAllUnits(unitTarget);
        System.out.println(convertOperationResult.getMessage());
        return convertOperationResult.getData();
    }

    // Displays all workouts in the system
    private void handleViewWorkouts() {
        List<Workout> all = workoutManager.getAllWorkouts();
        if (all.isEmpty()) {
            System.out.println("No workouts found.");
        } else {
            all.forEach(System.out::println);
        }
    }

    // Searches workouts by partial or total name
    private List<Workout> handleFilterWorkouts() {
        System.out.print("Enter search term: ");
        String term = scanner.nextLine();
        OperationResult<List<Workout>> searchOperationResult = workoutManager.getWorkoutsBySearchParameter(term);
        System.out.println(searchOperationResult.getMessage());
        searchOperationResult.getData().forEach(System.out::println);

        return searchOperationResult.getData();
    }
}
