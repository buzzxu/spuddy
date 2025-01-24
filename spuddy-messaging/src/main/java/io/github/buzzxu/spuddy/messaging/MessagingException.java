package io.github.buzzxu.spuddy.messaging;

import java.io.Serial;

/**
 * @program: yuanmai-platform
 * @description:
 * @author: 徐翔
 **/
public class MessagingException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -5974531867329488141L;

    public MessagingException() {
        super();
    }

    public MessagingException(String message) {
        super(message);
    }

    public MessagingException(Throwable cause) {
        super(cause);
    }

    public MessagingException(String message, Throwable cause) {
        super(message, cause);
    }
    public MessagingException(Throwable cause, String message, Object... args) {
        super(message.formatted(args), cause);
    }
}
