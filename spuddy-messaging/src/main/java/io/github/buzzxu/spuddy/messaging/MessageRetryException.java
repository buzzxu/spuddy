package io.github.buzzxu.spuddy.messaging;

/**
 * @author xux
 * @date 2022年08月19日 22:15
 */
public class MessageRetryException extends MessageRejectException{
    public MessageRetryException(Throwable cause) {
        super(cause,false);
    }
}
