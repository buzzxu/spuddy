package io.github.buzzxu.spuddy.messaging.redis;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.github.buzzxu.spuddy.Env;
import io.github.buzzxu.spuddy.messaging.MessageHandler;
import io.github.buzzxu.spuddy.messaging.Subscribe;
import io.github.buzzxu.spuddy.redis.Redis;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author xux
 * @date 2025年01月21日 18:01:44
 */
@RequiredArgsConstructor
public class JedisSubscribes {
    private final Env env;
    private final Redis redis;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final ApplicationContext applicationContext;
    private ThreadPoolExecutor executor;
    private List<JedisConumser> handlers;
    @PostConstruct
    public synchronized void init() {
        Map<String, Object> subscribes =  applicationContext.getBeansWithAnnotation(Subscribe.class);
        if(!subscribes.isEmpty()){
            handlers = Lists.newArrayListWithCapacity(subscribes.size());
            executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(subscribes.size());
            for(Map.Entry<String,Object> entry : subscribes.entrySet()){
                Subscribe subscribe = entry.getValue().getClass().getAnnotation(Subscribe.class);
                if(entry.getValue() instanceof MessageHandler<?> messageHandler){
                    checkArgument(!Strings.isNullOrEmpty(subscribe.name()),"%s @Subscribe name is null", entry.getKey());
                    checkArgument(!Strings.isNullOrEmpty(subscribe.topic()),"%s @Subscribe topic is null", entry.getKey());
                    checkArgument(!Strings.isNullOrEmpty(subscribe.group()),"%s @Subscribe group is null", entry.getKey());
                    String xconsumer = subscribe.name()+"-"+env.getAppId();
                    JedisConumser conumser = new JedisConumser(redis, messageHandler,subscribe.topic(), subscribe.group(), xconsumer).createGroup();
                    handlers.add(conumser);
                    executor.submit(conumser);
                }
            }
            //处理pending消息
            startPendingMessageProcessor();
        }
    }

    private void startPendingMessageProcessor() {
        scheduler.scheduleAtFixedRate(() -> {
            handlers.forEach(JedisConumser::startPendingMessageProcessor);
        }, 0, 5, TimeUnit.MINUTES);
    }

    @PreDestroy
    public void destroy() {
        if(executor != null){
            executor.shutdown();
        }
        scheduler.shutdownNow();

    }
}
