package io.github.buzzxu.spuddy.messaging.redis.handlers;

import io.github.buzzxu.spuddy.messaging.AbstractMessageHandler;
import io.github.buzzxu.spuddy.messaging.MessageRetryException;
import io.github.buzzxu.spuddy.messaging.MessagingException;
import io.github.buzzxu.spuddy.messaging.Subscribe;
import io.github.buzzxu.spuddy.messaging.redis.NameAndAge;
import lombok.extern.slf4j.Slf4j;

import static io.github.buzzxu.spuddy.messaging.redis.RedisMessageTest.TOPIC_JSON;
import static io.github.buzzxu.spuddy.messaging.redis.RedisMessageTest.TOPIC_STRING;

/**
 * @author xux
 * @date 2025年01月22日 20:33:11
 */
@Slf4j
@Subscribe(name=TOPIC_JSON,topic = TOPIC_JSON, group = "test")
public class TestObjectHandler extends AbstractMessageHandler<NameAndAge> {
    @Override
    public void accept(NameAndAge body) throws MessagingException {
        try {
            System.out.println("收到对象:"+body);
        }catch (MessageRetryException ex){
            log.error("发生错误,重新执行",ex);
            throw ex;
        }
    }
}
