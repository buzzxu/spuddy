package io.github.buzzxu.spuddy.security.exceptions;


import io.github.buzzxu.spuddy.errors.SecurityException;

import static io.github.buzzxu.spuddy.errors.ErrorCodes.BAD_REQUEST;

/**
 * @author xux
 * @date 2022年08月24日 10:14
 */
public class LoginFailException extends SecurityException {
    public LoginFailException(String message) {
        super(message,BAD_REQUEST.status());
    }
}
