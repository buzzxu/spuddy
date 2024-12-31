package io.github.buzzxu.spuddy.redis;


import com.google.common.base.Strings;
import io.github.buzzxu.spuddy.exceptions.ApplicationException;
import io.github.buzzxu.spuddy.jackson.Jackson;
import io.github.buzzxu.spuddy.serialize.SerializerException;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * @program:
 * @description: jedis简单封装
 * @author: xux
 * @created: 2021/04/26 15:58
 */
@Slf4j
public class RedisPool extends AbstractRedis{
    protected JedisPool redisPool;
    protected RedisConfig redisConfig;

    public RedisPool(JedisPool redisPool, RedisConfig redisConfig) {
        this.redisPool = redisPool;
        this.redisConfig = redisConfig;
    }

    public <T> List<T> gets(String key, long second, Supplier<List<T>> supplier, Class<T> clazz) {
        List<T> r;
        try {
            r = execute(redis-> {
                String data = redis.get(key);
                List<T> result;
                if (Strings.isNullOrEmpty(data)) {
                    try {
                        if(supplier != null){
                            result = supplier.get();
                            if (result != null && !result.isEmpty()) {
                                redis.setex(key, second, Jackson.object2Json(result));
                                return result;
                            }
                        }
                        return Collections.emptyList();
                    }catch (Exception ex){
                        throw ApplicationException.raise(ex);
                    }
                }
                try {
                    return Jackson.json2List(data, clazz);
                } catch (SerializerException ex) {
                    //如果出现异常需要删除问题数据 重新获取
                    redis.del(key);
                    throw ex;
                }
            });
        }catch (SerializerException ex){
            //需要重新获取
            log.warn("redis serialize error, again retry! key: {}",key,ex);
            return Collections.emptyList();
        }
        return r;
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
