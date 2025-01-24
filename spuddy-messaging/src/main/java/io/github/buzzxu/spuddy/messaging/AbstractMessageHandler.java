package io.github.buzzxu.spuddy.messaging;

import io.github.buzzxu.spuddy.jackson.Jackson;
import io.github.buzzxu.spuddy.serialize.SerializerException;
import org.springframework.core.GenericTypeResolver;

/**
 * @author xux
 * @date 2025年01月18日 15:45:20
 */
public abstract class AbstractMessageHandler<M> implements MessageHandler<M> {
    protected final Class<M> clazz;

    public AbstractMessageHandler() {
        Class<?>[] genericTypes = GenericTypeResolver.resolveTypeArguments(
                getClass(), MessageHandler.class);
        if (genericTypes == null || genericTypes.length == 0) {
            throw new IllegalArgumentException("Cannot resolve generic type for handler");
        }
        clazz = (Class<M>) genericTypes[0];
    }

    @Override
    public void accept(String data) throws MessagingException {
        try {
            accept(Jackson.json2Object(data,clazz));
        } catch (SerializerException ex) {
            throw new MessageRejectException(ex.getMessage(), ex, true);
        }
    }
}
