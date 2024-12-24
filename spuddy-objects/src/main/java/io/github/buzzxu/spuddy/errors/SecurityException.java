package io.github.buzzxu.spuddy.errors;


import java.io.Serial;

/**
 * @author xux
 * @date 2018/5/22 下午2:41
 */
public class SecurityException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1282005026550835297L;
    private final int status;
    public SecurityException(String message, int status) {
        super(message);
        this.status = status;
    }

    public SecurityException(Throwable cause, int status) {
        super(cause);
        this.status = status;
    }

    public int status() {
        return status;
    }
}
