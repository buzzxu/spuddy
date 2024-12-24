package io.github.buzzxu.spuddy.jackson.serializer;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.github.buzzxu.spuddy.jackson.annotation.JsonIgnoreEscape;

public abstract class IgnoreEscapeContextualSerializer extends JsonSerializer<String> implements ContextualSerializer {


    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        if(property == null){
            return prov.findNullValueSerializer(null);
        }
        if(property.getMember().hasAnnotation(JsonIgnoreEscape.class)){
            return ToStringSerializer.instance;
        }
        return this;
    }
}
