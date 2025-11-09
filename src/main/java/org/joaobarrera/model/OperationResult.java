package org.joaobarrera.model;

/*
 * Joao Barrera
 * CEN 3024 - Software Development 1
 * November 9, 2025
 * OperationResult.java
 */

/**
 * Generic record representing the result of an operation in the Workout Logger application.
 * <p>
 * Encapsulates whether the operation was successful, the data returned by the operation,
 * and an optional message describing the result or any errors.
 */

public record OperationResult<T>(boolean success, T data, String message) {
    /**
     * Returns a string representation of the operation result.
     * <p>
     * Includes the success status, data, and message.
     *
     * @return a string describing the operation result
     */
    @Override
    public String toString() {
        return "Success: " + success +
                ", Data: " + data +
                ", Message: " + message;
    }
}
