package io.github.buzzxu.spuddy.messaging;

/**
 * @author xux
 * @date 2025年01月18日 15:35:53
 */
public interface MessageHandler<M> {
    default void accept(M body) throws MessagingException {
        throw new UnsupportedOperationException();
    }

    default void accept(String data) throws MessagingException {
        throw new UnsupportedOperationException();
    }
}
