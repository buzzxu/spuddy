package io.github.buzzxu.spuddy.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.google.common.base.Strings;

import java.io.IOException;

import static org.owasp.encoder.Encode.forHtml;

public class EscapeHtmlDeserializer extends IgnoreEscapeContextualDeserializer {
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        var value = p.getText();
        return !Strings.isNullOrEmpty(value) ? forHtml(value) : value;
    }

}
