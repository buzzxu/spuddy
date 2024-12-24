package io.github.buzzxu.spuddy.jackson;


import io.github.buzzxu.spuddy.serialize.SerializerException;

import java.io.Serial;

/**
 * Created by xuxiang on 2016/11/16.
 */
public class JacksonException extends SerializerException {
    @Serial
    private static final long serialVersionUID = -1429957548045759075L;

    public JacksonException() {
        super();
    }

    public JacksonException(String message) {
        super(message);
    }

    public JacksonException(String message, Throwable cause) {
        super(message, cause);
    }

    public JacksonException(Throwable cause) {
        super(cause);
    }
}
