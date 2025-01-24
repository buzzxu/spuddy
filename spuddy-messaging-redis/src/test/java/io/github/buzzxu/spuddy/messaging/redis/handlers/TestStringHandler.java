package io.github.buzzxu.spuddy.messaging.redis.handlers;

import io.github.buzzxu.spuddy.messaging.AbstractMessageHandler;
import io.github.buzzxu.spuddy.messaging.MessageRetryException;
import io.github.buzzxu.spuddy.messaging.MessagingException;
import io.github.buzzxu.spuddy.messaging.Subscribe;
import lombok.extern.slf4j.Slf4j;

import static io.github.buzzxu.spuddy.messaging.redis.RedisMessageTest.TOPIC_STRING;

/**
 * @author xux
 * @date 2025年01月22日 10:22:46
 */
@Slf4j
@Subscribe(name=TOPIC_STRING,topic = TOPIC_STRING, group = "test")
public class TestStringHandler extends AbstractMessageHandler<String> {

    @Override
    public void accept(String data) throws MessagingException {
        try {
            System.out.println("收到消息:"+data);
            throw new IllegalArgumentException("测试重拾");
        }catch (IllegalArgumentException ex){
            log.error("发生错误,重新执行",ex);
            throw new MessageRetryException(ex);
        }
    }
}
