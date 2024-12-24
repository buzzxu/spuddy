package io.github.buzzxu.spuddy.jackson.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import io.github.buzzxu.spuddy.jackson.conv.JsonConvert;

import java.lang.annotation.*;

/**
 * @author xux
 * @date 2023年04月02日 16:46:30
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
@Documented
public @interface JsonPropertyConvert {
    @SuppressWarnings("all")
    Class<? extends JsonConvert<?>> using();
}
