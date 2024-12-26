package io.github.buzzxu.spuddy.security.exceptions;

/**
 * 重新注册
 */
public class ReRegisterException extends Exception {
    public ReRegisterException() {
    }

    public ReRegisterException(String message) {
        super(message);
    }

    public ReRegisterException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReRegisterException(Throwable cause) {
        super(cause);
    }
}
