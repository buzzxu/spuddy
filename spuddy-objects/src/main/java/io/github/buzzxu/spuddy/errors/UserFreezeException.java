package io.github.buzzxu.spuddy.errors;

import java.io.Serial;

import static io.github.buzzxu.spuddy.errors.ErrorCodes.FORBIDDEN;

public class UserFreezeException extends SecurityException {
    @Serial
    private static final long serialVersionUID = -6528073152281360280L;

    public UserFreezeException() {
        super("账户已被冻结,请联系客服或管理人员", FORBIDDEN.status());
    }
}
