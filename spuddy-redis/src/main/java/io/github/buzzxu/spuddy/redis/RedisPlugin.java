package io.github.buzzxu.spuddy.redis;



import io.github.buzzxu.spuddy.Plugin;

import java.util.Set;

/**
 * @author 徐翔
 * @create 2021-08-25 15:52
 **/
public class RedisPlugin implements Plugin {

    @Override
    public Set<Class<?>> classes() {
        return Set.of(RedisConfig.class,JedisConfigure.class);
    }
}
