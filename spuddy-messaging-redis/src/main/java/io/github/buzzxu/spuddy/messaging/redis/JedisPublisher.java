package io.github.buzzxu.spuddy.messaging.redis;

import io.github.buzzxu.spuddy.jackson.Jackson;
import io.github.buzzxu.spuddy.messaging.Message;
import io.github.buzzxu.spuddy.messaging.MessagingException;
import io.github.buzzxu.spuddy.messaging.Publisher;
import io.github.buzzxu.spuddy.redis.Redis;
import lombok.RequiredArgsConstructor;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.params.XAddParams;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author xux
 * @date 2025年01月18日 20:52:33
 */
@RequiredArgsConstructor
public class JedisPublisher implements Publisher {
    public static final String KEY = "data";
    private final Redis redis;


    @Override
    public void publish(String topic, String message) throws MessagingException {
        xadd(topic,message);
    }

    @Override
    public void publish(String topic, Object message) throws MessagingException {
        xadd(topic,Jackson.object2Json(message));
    }

    @Override
    public <T> void publishTx(Message<T> message) throws MessagingException {
        publish(message.getTopic(), Jackson.object2Json(message.getData()));
    }

    private void xadd(String topic, String message) {
        redis.execute(redis -> {
            redis.xadd(topic, StreamEntryID.NEW_ENTRY,Map.of(KEY, message));
//            redis.xadd(topic, Map.of(KEY, message), XAddParams.xAddParams().maxLen(50000).approximateTrimming());
            return null;
        });
    }
}
