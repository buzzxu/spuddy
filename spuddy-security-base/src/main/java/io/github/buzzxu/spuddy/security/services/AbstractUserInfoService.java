package io.github.buzzxu.spuddy.security.services;

import io.github.buzzxu.spuddy.errors.SecurityException;
import io.github.buzzxu.spuddy.redis.Redis;
import io.github.buzzxu.spuddy.security.UserInfoService;
import io.github.buzzxu.spuddy.security.UserService;
import io.github.buzzxu.spuddy.security.objects.UserInfo;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;


import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static io.github.buzzxu.spuddy.security.KEY.USER_INFO;


/**
 * @author 徐翔
 * @create 2021-08-26 10:31
 **/
@RequiredArgsConstructor
public abstract class AbstractUserInfoService<U extends UserInfo> implements UserInfoService<U> {
    protected final Class<U> clazz;
    @Resource
    protected Redis redis;
    protected long expireUserTime;
    @Value("${jwt.expiration:7}")
    protected int expiration;
    @Resource(type = UserService.class)
    protected UserService userService;
    @PostConstruct
    public void init(){
        this.expireUserTime = Duration.ofDays(expiration).toSeconds();
    }

    @Override
    public Integer type(long id) {
        return userService.type(id).orElseThrow(()-> new SecurityException("无法获取身份信息",401));
    }

    @Override
    public  U of(long id, int type, Class<U> clazz) {
        return of(id,type,()->load(id,type),clazz);
    }

    @Override
    public U of(long userId, int type, Supplier<U> supplier, Class<U> clazz) {
        if(userId ==0){
            throw new SecurityException("无法获取身份信息",401);
        }
        Optional<UserInfo> user = redis.execute(redis->{
            String key = USER_INFO.to(userId);
            if(redis.exists(key)){
                Map<String,String> data = redis.hgetAll(key);
                if(!data.containsKey("type")) {
                    redis.del(key);
                    return Optional.empty();
                }
                return Optional.of(convert(data,Integer.parseInt(data.get("type")),clazz));
            }else{
                return Optional.empty();
            }
        });
        return (U)user.or(()->{
            //查询
            UserInfo userInfo = supplier.get();
            if(userInfo != null){
                String key$info = USER_INFO.to(userId);
                redis.execute(redis->{
                    //保存用户信息
                    redis.hmset(key$info,userInfo.map());
                    //设置过期时间
                    redis.expire(key$info,expireUserTime);
                    return true;
                });
                return Optional.of(userInfo);
            }else{
                throw new SecurityException("无法获取身份信息",401);
            }
        }).get();
    }

    @Override
    public Optional<String> password(long userId, int type) {
        U userInfo = of(userId,type,clazz);
        return userInfo != null ? Optional.of(userInfo.getPassword()) : Optional.empty();
    }

    protected abstract U load(long userId, int type) throws SecurityException;
}
