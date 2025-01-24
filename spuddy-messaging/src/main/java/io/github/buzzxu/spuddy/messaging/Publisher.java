package io.github.buzzxu.spuddy.messaging;

import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author xux
 * @date 2025年01月18日 20:54:22
 */
public interface Publisher {

    default void publish(String topic, String message) throws MessagingException {
        throw new UnsupportedOperationException();
    }

    default void publish(String topic, Object message) throws MessagingException {
        throw new UnsupportedOperationException();
    }
    default <T> void  publish(Message<T> message) throws MessagingException{
        throw new UnsupportedOperationException();
    }

    default <T> void publishTx(Message<T> message) throws MessagingException {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                publish(message);
            }
        });
    }

    default <T> void publishTx(String topic, Object message) throws MessagingException {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                publish(topic, message);
            }
        });
    }

}
