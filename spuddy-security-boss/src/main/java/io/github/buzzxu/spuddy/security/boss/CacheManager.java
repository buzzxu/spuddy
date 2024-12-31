package io.github.buzzxu.spuddy.security.boss;

import io.github.buzzxu.spuddy.redis.Redis;
import io.github.buzzxu.spuddy.security.shiro.cache.AbstractRedisCacheManager;
import io.github.buzzxu.spuddy.security.shiro.cache.JedisRedisShiroCache;
import jakarta.annotation.Resource;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import redis.clients.jedis.Jedis;

/**
 * @author xux
 * @date 2024年12月28日 22:58:06
 */
public class CacheManager extends AbstractRedisCacheManager {
    @Resource(type = Redis.class)
    private Redis redis;


    @Override
    public Cache getCache(String name) throws CacheException {
        return new JedisRedisShiroCache<>(redis,valueSerializer,name,3600);
    }
}
