package io.github.buzzxu.spuddy.errors;

import io.github.buzzxu.spuddy.exceptions.ApplicationException;

import static io.github.buzzxu.spuddy.errors.ErrorCodes.FORBIDDEN;

/**
 * Created by xux on 2017/4/23.
 */
public class ForbiddenException extends ApplicationException {


    private static final long serialVersionUID = 1091854160502554429L;

    public ForbiddenException(String message) {
        super(FORBIDDEN.code(), message, FORBIDDEN.status());
    }
}
