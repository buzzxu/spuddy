package io.github.buzzxu.spuddy.messaging.redis;

import io.github.buzzxu.spuddy.test.TestSpringInitializer;

/**
 * @author xux
 * @date 2025年01月22日 10:36:37
 */
public class MessagingRedisSpringInitializer extends TestSpringInitializer {
    public MessagingRedisSpringInitializer() {
        super(RedisMessageTest.class);
    }
}
