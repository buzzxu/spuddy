package io.github.buzzxu.spuddy.jackson.deserializer;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import io.github.buzzxu.spuddy.jackson.annotation.JsonIgnoreEscape;

public abstract class IgnoreEscapeContextualDeserializer extends JsonDeserializer<String> implements ContextualDeserializer {

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        if(property != null && property.getMember().hasAnnotation(JsonIgnoreEscape.class)){
            return StringDeserializer.instance;
        }
        return this;
    }
}
