package io.github.buzzxu.spuddy.messaging;

/**
 * @author xux
 * @date 2025年01月20日 13:05:58
 */
public class MessagingAckException extends MessagingException {

    public MessagingAckException(String message) {
        super(message);
    }

    public MessagingAckException(Throwable cause) {
        super(cause);
    }
}

