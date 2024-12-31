package io.github.buzzxu.spuddy.redis;

import com.google.common.base.Strings;
import io.github.buzzxu.spuddy.redis.lock.DistributionLock;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author 徐翔
 * @create 2021-08-25 15:54
 **/
@Configuration("jedisConfigure")
public class JedisConfigure {
    @Bean
    public JedisPoolConfig jedisPoolConfig(RedisConfig redisConfig){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setBlockWhenExhausted(true);
        //最大连接数
        if(redisConfig.getMaxTotal() > 0){
            poolConfig.setMaxTotal(redisConfig.getMaxTotal());
        }
        if(redisConfig.getMaxWaitMillis() > 0){
            poolConfig.setMaxWaitMillis(redisConfig.getMaxWaitMillis());
        }
        //最大空闲连接数,
        if(redisConfig.getMaxIdle()>0){
            poolConfig.setMaxIdle(redisConfig.getMaxIdle());
        }
        if(redisConfig.getMinIdle() > 0){
            poolConfig.setMinIdle(redisConfig.getMinIdle());
        }
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(false);
        poolConfig.setJmxEnabled(false);
        return poolConfig;
    }

    @Bean(destroyMethod = "close",name = "redisPool")
    public JedisPool jedisConnectionFactory(RedisConfig redisConfig, JedisPoolConfig poolConfig) {
        String url = redisConfig.getUrl();
        String[] hostAndPort = url.split(":");
        if(hostAndPort.length != 2){
            throw new IllegalArgumentException("Redis Url error.(as 127.0.0.1:7689)"+url);
        }
        if(!Strings.isNullOrEmpty(redisConfig.getUser())){
            return new JedisPool(poolConfig,hostAndPort[0],Integer.parseInt(hostAndPort[1]),redisConfig.getTimeout(), redisConfig.getUser(),Strings.emptyToNull(redisConfig.getPassword()),redisConfig.getDb());
        }
        return new JedisPool(poolConfig,hostAndPort[0],Integer.parseInt(hostAndPort[1]),redisConfig.getTimeout(), Strings.emptyToNull(redisConfig.getPassword()),redisConfig.getDb());
    }

    @DependsOn("redisPool")
    @Bean(destroyMethod = "close")
    public Redis dbRedis(JedisPool redisPool, RedisConfig redisConfig){
        return new RedisPool(redisPool,redisConfig);
    }

    @DependsOn("redisPool")
    @Bean
    public DistributionLock distributionLock(Redis redisPool){
        return new DistributionLock((RedisPool)redisPool);
    }
}
