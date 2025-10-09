package org.joaobarrera;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

class CLIApp {
    private final WorkoutManager workoutManager;
    private final Scanner scanner;

    public CLIApp(WorkoutManager manager) {
        this.workoutManager = manager;
        this.scanner = new Scanner(System.in);
    }

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

    private Workout getWorkoutInput() {
        String name;
        boolean valid;
        do {
            System.out.print("Enter name: ");
            name = scanner.nextLine();
            String error = workoutManager.validateName(name);
            valid = error == null;
            if (!valid) System.out.println(error);
        } while (!valid);

        LocalDateTime startDateTime;
        do {
            System.out.print("Enter date/time (YYYY-MM-DDTHH:MM): ");
            startDateTime = parseDateTime(scanner.nextLine());
            String error = workoutManager.validateStartDateTime(startDateTime);
            valid = error == null;
            if (!valid) System.out.println(error);
        } while (!valid);

        Integer duration;
        do {
            System.out.print("Enter duration (minutes): ");
            duration = parseDuration(scanner.nextLine());
            String error = workoutManager.validateDuration(duration);
            valid = error == null;
            if (!valid) System.out.println(error);
        } while (!valid);

        Double distance;
        do {
            System.out.print("Enter distance: ");
            distance = parseDistance(scanner.nextLine());
            String error = workoutManager.validateDistance(distance);
            valid = error == null;
            if (!valid) System.out.println(error);
        } while (!valid);

        UnitType unit;
        do {
            System.out.print("Enter unit (KILOMETERS/MILES): ");
            unit = parseUnit(scanner.nextLine());
            String error = workoutManager.validateUnit(unit);
            valid = error == null;
            if (!valid) System.out.println(error);
        } while (!valid);

        String notes;
        do {
            System.out.print("Enter notes (optional, max 200 chars): ");
            notes = scanner.nextLine();
            String error = workoutManager.validateNotes(notes);
            valid = error == null;
            if (!valid) System.out.println(error);
        } while (!valid);

        return new Workout(name, startDateTime, duration, distance, unit, notes);
    }

    private Integer getWorkoutIDInput() {
        int workoutID = Integer.parseInt(scanner.nextLine());
        if (workoutID == -1) return -1;

        String error = workoutManager.validateID(workoutID);
        if (error != null) {
            System.out.println(error);
            return null;
        }

        if (!workoutManager.IDExists(workoutID)) {
            System.out.println("Workout ID does not exist.");
            return null;
        }
        return workoutID;
    }

    private LocalDateTime parseDateTime(String input) {
        try {
            return LocalDateTime.parse(input);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private Integer parseDuration(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double parseDistance(String input) {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private UnitType parseUnit(String input) {
        try {
            return UnitType.valueOf(input.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }

    private boolean handleAddWorkout() {
        Workout workout = getWorkoutInput();
        OperationResult<Workout> addOperationResult = workoutManager.addWorkout(workout);
        System.out.println(addOperationResult.getMessage());
        return addOperationResult.isSuccess();
    }

    private boolean handleEditWorkout() {
        Integer workoutID = null;
        do {
            try {
                System.out.print("Enter workout ID to edit (-1 to cancel): ");
                workoutID = getWorkoutIDInput();
                if (workoutID != null && workoutID == -1) {
                    System.out.println("Aborted.");
                    return false;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric Workout ID.");
            }
        } while (workoutID == null);

        Workout updatedWorkout = getWorkoutInput();
        OperationResult<Workout> updateOperationResult = workoutManager.updateWorkout(workoutID, updatedWorkout);
        System.out.println(updateOperationResult.getMessage());
        return updateOperationResult.isSuccess();
    }

    private boolean handleDeleteWorkout() {
        Integer workoutID = null;
        do {
            try {
                System.out.print("Enter workout ID to delete (-1 to cancel): ");
                workoutID = getWorkoutIDInput();
                if (workoutID != null && workoutID == -1) {
                    System.out.println("Aborted.");
                    return false;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric Workout ID.");
            }
        } while (workoutID == null);

        OperationResult<Workout> deleteOperationResult = workoutManager.deleteWorkout(workoutID);
        System.out.println(deleteOperationResult.getMessage());
        return deleteOperationResult.isSuccess();
    }

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

        for (Workout importedWorkout : importOperationResult.getData()) {
            OperationResult<Workout> addOperationResult = workoutManager.addWorkout(importedWorkout);
            if (!addOperationResult.isSuccess()) {
                System.out.println(addOperationResult.getMessage());
            }
        }

        System.out.println(importOperationResult.getMessage());
        return importOperationResult.getData();
    }

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

    private void handleViewWorkouts() {
        List<Workout> all = workoutManager.getAllWorkouts();
        if (all.isEmpty()) {
            System.out.println("No workouts found.");
        } else {
            all.forEach(System.out::println);
        }
    }

    private List<Workout> handleFilterWorkouts() {
        System.out.print("Enter search term: ");
        String term = scanner.nextLine();
        OperationResult<List<Workout>> searchOperationResult = workoutManager.getWorkoutsBySearchParameter(term);
        System.out.println(searchOperationResult.getMessage());
        searchOperationResult.getData().forEach(System.out::println);

        return searchOperationResult.getData();
    }
}
