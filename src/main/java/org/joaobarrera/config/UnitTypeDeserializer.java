package org.joaobarrera.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.joaobarrera.model.UnitType;

import java.io.IOException;

public class UnitTypeDeserializer extends JsonDeserializer<UnitType> {

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
