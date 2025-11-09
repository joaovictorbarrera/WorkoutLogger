package org.joaobarrera.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/*
 * Joao Barrera
 * CEN 3024 - Software Development 1
 * November 9, 2025
 * LocalDateTimeDeserializer.java
 */

/**
 * Custom JSON deserializer for LocalDateTime objects used within the Workout Logger application.
 * <p>
 * This class ensures that LocalDateTime values are safely deserialized from JSON strings.
 * If an invalid or blank date string is provided, the deserializer returns null instead of
 * throwing an exception, allowing for more robust JSON parsing.
 */

public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    /**
     * Converts a JSON string into a LocalDateTime object.
     * <p>
     * If the input text is null, blank, or invalid, this method safely returns null
     * instead of throwing a parsing exception.
     *
     * @param p the JSON parser providing the text to deserialize
     * @param ctxt the deserialization context used by Jackson
     * @return a LocalDateTime object if parsing succeeds, or null if invalid
     * @throws IOException if an I/O error occurs during parsing
     */
    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getText();

        if (text == null || text.isBlank()) {
            return null;
        }

        try {
            return LocalDateTime.parse(text);
        } catch (DateTimeParseException e) {
            // return null instead of throwing
            return null;
        }
    }
}
