package io.github.buzzxu.spuddy.security.shiro.cache;


import io.github.buzzxu.spuddy.security.shiro.serializer.ObjectSerializer;
import io.github.buzzxu.spuddy.security.shiro.serializer.RedisSerializer;
import jakarta.annotation.PostConstruct;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.CacheManager;



public abstract class AbstractRedisCacheManager implements CacheManager{

    protected String cacheKeyPrefix ;
    protected RedisSerializer<SimpleAuthorizationInfo> valueSerializer;


    @PostConstruct
    protected void init(){
        this.valueSerializer = new ObjectSerializer<>(SimpleAuthorizationInfo.class);
    }

}
