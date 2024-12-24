package io.github.buzzxu.spuddy.security.jwt;

import com.auth0.jwt.JWTCreator;
import io.github.buzzxu.spuddy.errors.SecurityException;
import io.github.buzzxu.spuddy.exceptions.ApplicationException;
import io.github.buzzxu.spuddy.objects.Pair;
import io.github.buzzxu.spuddy.redis.Redis;
import io.github.buzzxu.spuddy.security.exceptions.TokenGenerateException;
import io.github.buzzxu.spuddy.security.objects.UserInfo;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.github.buzzxu.spuddy.security.KEYs.USER_TOKEN;

/**
 * @program: shxmao-platform
 * @description:
 * @author: xux
 * @create: 2021-09-01 11:24
 **/
public abstract class AccountService {
    @Resource(type = JwtConfig.class)
    protected JwtConfig jwtConfig;
    @Autowired
    protected Redis redis;
    @Autowired
    protected JWTs jwTs;


    public <U extends UserInfo> Pair<String, U> login(U user, Function<Long,U> function){
        return generatorToken(user.getId(),function);
    }

    protected <U extends UserInfo> Pair<String,U> generatorToken(Long userId, Function<Long,U> function)throws ApplicationException{
        return generatorToken(function.apply(userId));
    }

    public <U extends UserInfo> Pair<String, U> generatorToken(U user) {
        try {
            String token = jwTs.create(user, jwtConsumer(user), jwtSecretSupplier(user));
            if(user.getId() == 0 || !storeToken(user.getId(),user.getType(),token)){
                throw new SecurityException("缓存用户信息失败",401);
            }
            return Pair.of(token,user);
        } catch ( TokenGenerateException e) {
            throw ApplicationException.raise("生成令牌失败，请稍后尝试");
        } catch (SecurityException ex){
            throw ex;
        } catch (ApplicationException ex){
            throw ex;
        } catch (Exception ex){
            throw ApplicationException.raise(ex);
        }
    }


    protected <U extends UserInfo> Supplier<String> jwtSecretSupplier(U userInfo){
        return () -> userInfo.getPsword();
    }

    protected <U extends UserInfo> Consumer<JWTCreator.Builder> jwtConsumer(U userInfo){
        return builder -> {
            builder.withClaim("type",userInfo.getType());
            writeInfoToToken(builder,userInfo);
        };
    }

    protected abstract  <U extends UserInfo> void writeInfoToToken(JWTCreator.Builder builder,U userInfo);



    public boolean storeToken(long userId, int type,String token){
        //存入redis中
        return redis.execute(redis->{
            //设置user的token 设置过期时间与token生成的时间一致
            redis.setex(USER_TOKEN.to(type,userId),jwtConfig.getExpiration(),token);
            return true;
        });
    }


    public void clearUserCache(List<String> userKeys){
        //删除用户缓存
        redis.execute(redis->{
            String[] keys = new String[userKeys.size()];
            redis.del(userKeys.toArray(keys));
            return null;
        });
    }
}
