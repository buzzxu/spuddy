package io.github.buzzxu.spuddy.security.exceptions;

import io.github.buzzxu.spuddy.errors.SecurityException;

@SuppressWarnings("serial")
public class TokenExpiredException extends SecurityException {
    public TokenExpiredException() {
        super("Token失效,需要重新授权", 401);
    }
}
