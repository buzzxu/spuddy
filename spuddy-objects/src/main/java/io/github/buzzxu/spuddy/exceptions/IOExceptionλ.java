package io.github.buzzxu.spuddy.exceptions;

/**
 * @author xux
 * @date 2024年06月04日 18:02:50
 */
public class IOExceptionλ extends RuntimeException{
    public IOExceptionλ() {
    }

    public IOExceptionλ(String message) {
        super(message);
    }

    public IOExceptionλ(String message, Throwable cause) {
        super(message, cause);
    }

    public IOExceptionλ(Throwable cause) {
        super(cause);
    }

    public IOExceptionλ(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
