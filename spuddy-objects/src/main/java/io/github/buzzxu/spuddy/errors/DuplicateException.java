package io.github.buzzxu.spuddy.errors;

import io.github.buzzxu.spuddy.exceptions.ApplicationException;

public class DuplicateException extends ApplicationException {

    public DuplicateException(String message) {
        super(ErrorCodes.BAD_REQUEST.code(), message, 400);
    }

    public DuplicateException(Throwable cause) {
        super(cause);
        this.status(400);
    }

    public DuplicateException(String message, Throwable cause) {
        super(message, cause);
        this.status(400);
    }
}
