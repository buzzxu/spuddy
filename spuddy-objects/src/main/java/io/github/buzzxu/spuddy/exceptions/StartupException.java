package io.github.buzzxu.spuddy.exceptions;


import java.io.Serial;

/**
 * Created by xux on 2016/9/4.
 */
public class StartupException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -1257927030939769384L;

    public StartupException() {
    }

    public StartupException(String message) {
        super(message);
    }

    public StartupException(String message, Throwable cause) {
        super(message, cause);
    }

    public StartupException(Throwable cause) {
        super(cause);
    }

    public StartupException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
