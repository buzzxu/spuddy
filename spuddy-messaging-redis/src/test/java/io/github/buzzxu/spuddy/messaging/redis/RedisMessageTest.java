package io.github.buzzxu.spuddy.messaging.redis;

import io.github.buzzxu.spuddy.messaging.Publisher;
import io.github.buzzxu.spuddy.test.TestSpringInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/**
 * @author xux
 * @date 2025年01月22日 9:58:01
 */
@TestPropertySource("classpath:application.properties")
@SpringJUnitConfig(initializers = MessagingRedisSpringInitializer.class)
public class RedisMessageTest {

    public static final String TOPIC_STRING = "topic:test:string";
    public static final String TOPIC_JSON = "topic:test:object";

    @Autowired
    private Publisher publisher;



    @Test
    public void testPublish() throws Exception {
        publisher.publish(TOPIC_STRING, "hello world");
        Thread.sleep(30000);
    }


    @Test
    public void testPublishObj() throws Exception {
        publisher.publish(TOPIC_JSON, new NameAndAge("xux", 18));
        Thread.sleep(30000);
    }


}
