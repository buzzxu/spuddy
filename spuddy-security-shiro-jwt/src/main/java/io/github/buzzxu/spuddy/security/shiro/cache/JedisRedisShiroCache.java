package io.github.buzzxu.spuddy.security.shiro.cache;


import io.github.buzzxu.spuddy.redis.Redis;
import io.github.buzzxu.spuddy.security.shiro.serializer.RedisSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.CacheException;
import redis.clients.jedis.Jedis;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Math.toIntExact;

/**
 * @author xux
 * @date 2018/6/12 上午11:05
 */
@Slf4j
public class JedisRedisShiroCache<K,V> extends AbstractShiroCache<K,V> {

    private final Redis redis;
    @SuppressWarnings("rawtypes")
    public JedisRedisShiroCache(Redis redis,RedisSerializer valueSerializer, String prefix, long expire) {
        super(valueSerializer,prefix,expire);
        this.redis = redis;
    }

    @SuppressWarnings("unchecked")
    @Override
    public V get(K k) throws CacheException {
        log.debug("get key [{}]",k);
        Object key = hashKey(k);
        return redis.execute(r->{
            String rawValue = r.get(key.toString());
            if (rawValue == null) {
                return null;
            }
            return valueSerializer.deserialize(rawValue);
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public V put(K k, V v) throws CacheException {
        if (k == null) {
            log.warn("Saving a null key is meaningless, return value directly without call Redis.");
            return v;
        }
        log.debug("put key [{}]",k);
        return redis.execute(r->{
            Object key = hashKey(k);
            r.setex(key.toString(), toIntExact(expire),valueSerializer.serialize(v));
            return v;
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public V remove(K k) throws CacheException {
        if (k == null) {
            return null;
        }
        log.debug("remove key [{}]",k);
        Object key = hashKey(k);
        Optional<String>  rawValue = redis.rmget(key.toString());
        return rawValue.map(valueSerializer::deserialize).orElse(null);
    }
    @SuppressWarnings("unchecked")
    @Override
    public void clear() throws CacheException {
        log.debug("clear shiro redis cache");
        redis.del(this.keyPrefix+"*");
    }


    @Override
    public int size() {
        return redis.size(this.keyPrefix+"*");
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<K> keys() {
        return Collections.emptySet();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<V> values() {
        return redis.values(this.keyPrefix+"*").stream().map(valueSerializer::deserialize).collect(Collectors.toList());
    }


}
