package io.github.buzzxu.spuddy.security.exceptions;

import io.github.buzzxu.spuddy.errors.SecurityException;
@SuppressWarnings("serial")
public class TokenGenerateException extends SecurityException {
    public TokenGenerateException() {
        super("生成Token失败",401);
    }

    public TokenGenerateException(Throwable cause) {
        super(cause, 401);
    }
}
