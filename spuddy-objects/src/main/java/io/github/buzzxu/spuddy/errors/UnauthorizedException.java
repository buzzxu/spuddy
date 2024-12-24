package io.github.buzzxu.spuddy.errors;



import java.io.Serial;

import static io.github.buzzxu.spuddy.errors.ErrorCodes.UNAUTHORIZED;

/**
 * Created by xux on 2017/4/23.
 */
public class UnauthorizedException extends SecurityException {

    @Serial
    private static final long serialVersionUID = -8783424299350560280L;


    public UnauthorizedException(String message) {
        super(message, UNAUTHORIZED.status());
    }
}
