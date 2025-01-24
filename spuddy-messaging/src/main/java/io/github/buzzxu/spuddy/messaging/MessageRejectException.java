package io.github.buzzxu.spuddy.messaging;

import lombok.Getter;

/**
 * @program: yuanmai-platform
 * @description:
 * @author: 徐翔
 **/
public class MessageRejectException extends MessagingException {
    @Getter
    private final boolean reject;

    public MessageRejectException(String message) {
        super(message);
        this.reject = true;
    }

    public MessageRejectException(Throwable cause, boolean reject) {
        super(cause);
        this.reject = reject;
    }

    public MessageRejectException(Throwable cause) {
        this(cause, true);
    }

    public MessageRejectException(String message, boolean reject) {
        super(message);
        this.reject = reject;
    }

    public MessageRejectException(String message, Throwable cause, boolean reject) {
        super(message, cause);
        this.reject = reject;
    }

}
