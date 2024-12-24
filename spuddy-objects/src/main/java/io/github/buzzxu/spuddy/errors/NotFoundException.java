package io.github.buzzxu.spuddy.errors;



import io.github.buzzxu.spuddy.exceptions.ApplicationException;

import java.io.Serial;

import static io.github.buzzxu.spuddy.errors.ErrorCodes.NOT_FOUND;

/**
 * @author xux
 * @date 2018/5/23 上午10:26
 */
public class NotFoundException extends ApplicationException {
    @Serial
    private static final long serialVersionUID = 4366110066207363601L;

    public NotFoundException(String message) {
        super(NOT_FOUND.code(), message, NOT_FOUND.status());
    }
}
