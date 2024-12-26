package io.github.buzzxu.spuddy.security.exceptions;

import io.github.buzzxu.spuddy.errors.SecurityException;

@SuppressWarnings("serial")
public class TokenAuthException extends SecurityException {
    private String token;

    public TokenAuthException() {
        super("非法Token不受信任", 401);
    }

    public TokenAuthException(String message) {
        super(message, 401);
    }

    public TokenAuthException token(String token) {
        this.token = token;
        return this;
    }

    public String getToken() {
        return token;
    }
}
