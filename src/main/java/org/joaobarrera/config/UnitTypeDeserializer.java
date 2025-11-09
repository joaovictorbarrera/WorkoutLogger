package org.joaobarrera.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.joaobarrera.model.UnitType;

import java.io.IOException;

/*
 * Joao Barrera
 * CEN 3024 - Software Development 1
 * November 9, 2025
 * UnitTypeDeserializer.java
 */

/**
 * Custom JSON deserializer for the UnitType enum used in the Workout Logger application.
 * <p>
 * This class converts JSON strings into UnitType objects. If the input string is null,
 * blank, or does not match any valid UnitType, the method safely returns null instead of
 * throwing an exception.
 */

public class UnitTypeDeserializer extends JsonDeserializer<UnitType> {

    /**
     * Converts a JSON string into a UnitType enum.
     * <p>
     * If the input string is null, blank, or invalid, this method safely returns null.
     *
     * @param p the JSON parser providing the text to deserialize
     * @param ctxt the deserialization context used by Jackson
     * @return a UnitType object corresponding to the input string, or null if invalid
     * @throws IOException if an I/O error occurs during parsing
     */
    @Override
    public UnitType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getText();

        if (text == null) {
            return null;
        }

        try {
            return UnitType.valueOf(text.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            // invalid unit string becomes null
            return null;
        }
    }
}
