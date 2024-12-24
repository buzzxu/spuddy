package io.github.buzzxu.spuddy.redis;


import jakarta.annotation.PreDestroy;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


import java.io.IOException;

/**
 * @program:
 * @description: jedis简单封装
 * @author: xux
 * @created: 2021/04/26 15:58
 */
public class RedisPool extends AbstractRedis{
    protected JedisPool redisPool;
    protected RedisConfig redisConfig;

    public RedisPool(JedisPool redisPool, RedisConfig redisConfig) {
        this.redisPool = redisPool;
        this.redisConfig = redisConfig;
    }



    @Override
    public Jedis getResource() {
        return redisPool.getResource();
    }

    @Override
    @PreDestroy
    public void close() throws IOException {
        redisPool.destroy();
    }
}
