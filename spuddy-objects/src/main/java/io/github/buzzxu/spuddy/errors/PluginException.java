package io.github.buzzxu.spuddy.errors;

import java.io.Serial;

/**
 * @author xux
 * @date 2018/4/23 上午10:16
 */
public class PluginException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -5440319754898370630L;

    public PluginException() {
    }

    public PluginException(String message) {
        super(message);
    }

    public PluginException(String message, Throwable cause) {
        super(message, cause);
    }

    public PluginException(Throwable cause) {
        super(cause);
    }
}
