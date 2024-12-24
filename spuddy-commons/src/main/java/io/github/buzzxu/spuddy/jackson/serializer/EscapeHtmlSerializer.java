package io.github.buzzxu.spuddy.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.common.base.Strings;

import java.io.IOException;

import static org.owasp.encoder.Encode.forHtml;


public class EscapeHtmlSerializer extends IgnoreEscapeContextualSerializer {

    @Override
    public Class<String> handledType() {
        return String.class;
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (!Strings.isNullOrEmpty(value)) {
            value = forHtml((value));
        }
        gen.writeString(value);
    }
}
