package com.no-namesocial.homework.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;

import java.io.IOException;

/**
 * Converts "null" value in json to null during String deserialization.
 */
public class NullStringJsonDeserializer extends JsonDeserializer<String> {
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String result = StringDeserializer.instance.deserialize(p, ctxt);
        return "null".equalsIgnoreCase(result) ? null : result;
    }
}
