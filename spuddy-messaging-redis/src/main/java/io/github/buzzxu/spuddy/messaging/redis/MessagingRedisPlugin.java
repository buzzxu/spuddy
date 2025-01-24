package io.github.buzzxu.spuddy.messaging.redis;

import com.google.common.collect.Sets;
import io.github.buzzxu.spuddy.Env;
import io.github.buzzxu.spuddy.Plugin;
import io.github.buzzxu.spuddy.annotations.Named;
import io.github.buzzxu.spuddy.errors.PluginException;
import io.github.buzzxu.spuddy.messaging.AbstractMessagingPlugin;
import io.github.buzzxu.spuddy.messaging.MessageConfigure;

import java.util.Collections;
import java.util.Set;

/**
 * @author xux
 * @date 2025年01月18日 20:49:52
 */
@Named("messaging-redis")
public class MessagingRedisPlugin extends AbstractMessagingPlugin implements Plugin {


    @Override
    public void init(Env env) throws PluginException {
        enable = env.getApplicationContext().getEnvironment().getProperty("redis.enable", Boolean.class,true);
        super.init(env);
    }

    @Override
    public Set<Class<?>> classes() {
        if (enable) {
            Set<Class<?>> classes = Sets.newHashSetWithExpectedSize(5);
            classes.add(MessageConfigure.class);
            classes.add(JedisPublisher.class);
            classes.add(JedisSubscribes.class);
            return classes;
        }
        logger.warn("Messaging-redis plugin disable.");
        return Collections.emptySet();
    }
}
