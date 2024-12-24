package io.github.buzzxu.spuddy.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author 徐翔
 * @create 2021-08-25 15:47
 **/
@Getter
@Setter
@Component
public class RedisConfig {
    private String url;
    private String user;
    private String password;
    private int db = 0;
    private int maxTotal = 500;
    private long maxWaitMillis = 5000;
    private int maxIdle = 200;
    private int minIdle = 100;
    private int timeout = 6000;

    public RedisConfig(@Value("${redis.url}") String url
            , @Value("${redis.user:}") String user
            , @Value("${redis.password}") String password
            , @Value("${redis.database:0}") int db
            , @Value("${redis.pool.max-total:500}") int maxTotal
            , @Value("${redis.pool.max-wait:6}") long maxWaitMillis
            , @Value("${redis.pool.max-idle:2000}") int maxIdle
            , @Value("${redis.pool.min-idle:500}") int minIdle
            , @Value("${redis.timeout:6000}") int timeout) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.db = db;
        this.maxTotal = maxTotal;
        this.maxWaitMillis = maxWaitMillis;
        this.maxIdle = maxIdle;
        this.minIdle = minIdle;
        this.timeout = timeout;
    }
}
