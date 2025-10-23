package org.joaobarrera.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

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
