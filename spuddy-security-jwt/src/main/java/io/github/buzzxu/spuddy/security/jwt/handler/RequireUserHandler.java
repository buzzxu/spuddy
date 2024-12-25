package io.github.buzzxu.spuddy.security.jwt.handler;

import com.google.common.base.Strings;
import io.github.buzzxu.spuddy.redis.Redis;
import io.github.buzzxu.spuddy.security.GetMeToken;
import io.github.buzzxu.spuddy.security.SecurityUser;
import io.github.buzzxu.spuddy.security.exceptions.TokenAuthException;
import io.github.buzzxu.spuddy.security.handler.RequiresHandler;
import io.github.buzzxu.spuddy.security.jwt.JWTs;
import io.github.buzzxu.spuddy.security.objects.UserInfo;
import io.github.buzzxu.spuddy.security.services.UserInfoService;
import io.github.buzzxu.spuddy.errors.SecurityException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static io.github.buzzxu.spuddy.security.KEY.USER_TOKEN;


/**
 * @author 徐翔
 * @create 2021-08-26 10:06
 **/
public abstract class RequireUserHandler<U extends UserInfo> implements RequiresHandler<U> {

    @Autowired
    protected JWTs jwts;
    @Autowired
    protected Redis redis;

    protected UserInfoService userInfoService;

    protected abstract boolean identity(String token);

    protected abstract String jwtSecretKey(Optional<U> userInfo, int type);

    protected boolean verify(Long id, String token, Optional<U> userInfo,int type){
        return userInfo.isPresent() && jwts.verify(token, jwtSecretKey(userInfo,type));
    }

    public RequireUserHandler(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    /**
     * 验证Token
     * @param userId
     * @param token
     * @return
     * @throws SecurityException
     */
    @Override
    public  boolean test(long userId,String token)throws SecurityException{
        //判断token中的身份是否和请求地址所需要的身份相符
        if(!identity(token)){
            throw new SecurityException("您的身份受限,无法继续操作",401);
        }
        int type = type(token);
        return redis.execute(redis->{
            //key 为 user:type:userId形式
            String key = USER_TOKEN.to(type,userId);
            if(redis.exists(key)){
                //如果redis中存的token一样，则返回true
                return StringUtils.equals(token,redis.get(key));
            }
            return false;
        });
    }






    /**
     * 根据token获取用户ID
     * @param token
     * @return
     */
    protected Long id(String token){
        if(Strings.isNullOrEmpty(token)){
            throw new TokenAuthException("验证身份失败,请先登陆之后再访问");
        }
        Long id = jwts.id(token);
        if (id <= 0 || !test(id, token)) {
            throw new TokenAuthException("验证失败,无权限用户禁止访问");
        }
        return id;
    }

    /**
     * 获取用户类型 1=代理商 2= 商家 3=消费者
     * @param token
     * @return
     */
    protected int type(String token){
        if(Strings.isNullOrEmpty(token)){
            throw new TokenAuthException("验证身份失败,请先登陆之后再访问");
        }
        return jwts.parse(token,"type",Integer.class);
    }

    public abstract boolean requires(String token) throws SecurityException;

    /**
     * 允许游客
     * @param token
     */
    protected void allowGuest(String token){
        if(!Strings.isNullOrEmpty(token)){
            long id = id(token);
            int type = type(token);
            Optional<U> userInfo = getUser(id,type);
            if(verify(id,token,userInfo,type)){
                SecurityUser.set(userInfo.get());
                GetMeToken.set(token);
            }
        }else{
            SecurityUser.set(anonymous());
        }
    }

    /**
     * 必须用户登录
     * @param token
     */
    protected void requiresUser(String token){
        long id = id(token);
        if(test(id,token)){
            int type = type(token);
            Optional<U> userInfo = getUser(id,type);
            if(verify(id,token,userInfo,type)){
                SecurityUser.set(userInfo.get());
                GetMeToken.set(token);
                return ;
            }
        }
        throw new TokenAuthException("令牌验证失败,无权限用户禁止访问").token(token);
    }

}
