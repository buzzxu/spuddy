package io.github.buzzxu.spuddy.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.google.common.base.Strings;

import java.io.IOException;

import static org.apache.commons.text.StringEscapeUtils.unescapeHtml4;

public class UnescapeHtmlDeserializer extends IgnoreEscapeContextualDeserializer {
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        var value = p.getText();
        return !Strings.isNullOrEmpty(value)  ? unescapeHtml4(value) : value;
    }
}
