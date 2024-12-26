package io.github.buzzxu.spuddy.security.shiro.cache;


import io.github.buzzxu.spuddy.redis.Redis;
import io.github.buzzxu.spuddy.security.shiro.serializer.RedisSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.CacheException;

import java.util.Collection;
import java.util.Set;

@Slf4j
public class RedisShiroCache<K,V> extends AbstractShiroCache<K,V> {

    private final Redis redis;

    public RedisShiroCache(Redis redis,RedisSerializer valueSerializer, String keyPrefix, long expire) {
        super(valueSerializer, keyPrefix, expire);
        this.redis = redis;
    }

    @Override
    public V get(K k) throws CacheException {
       return null;
    }

    @Override
    public V put(K k, V v) throws CacheException {
        return null;
    }

    @Override
    public V remove(K k) throws CacheException {
        return null;
    }

    @Override
    public void clear() throws CacheException {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Set<K> keys() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }
}
