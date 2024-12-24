package io.github.buzzxu.spuddy.errors;

import io.github.buzzxu.spuddy.exceptions.ApplicationException;

import static io.github.buzzxu.spuddy.errors.ErrorCodes.USER_NOTIFY;

/**
 * 必须通知用户
 */
public class NofityUserException extends ApplicationException {
    private static final long serialVersionUID = 3948494399365039921L;

    public NofityUserException(String message) {
        super(USER_NOTIFY.code(),message,USER_NOTIFY.status());
    }
}
