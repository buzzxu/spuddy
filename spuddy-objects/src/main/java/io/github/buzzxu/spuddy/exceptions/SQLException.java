package io.github.buzzxu.spuddy.exceptions;

public class SQLException extends ApplicationException{
    public SQLException(String message, Throwable cause) {
        super(message, cause, 500);
    }
}
