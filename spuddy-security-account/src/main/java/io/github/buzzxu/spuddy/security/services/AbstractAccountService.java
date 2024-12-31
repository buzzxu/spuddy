package io.github.buzzxu.spuddy.security.services;

import com.auth0.jwt.JWTCreator;
import com.google.common.base.Strings;
import io.github.buzzxu.spuddy.errors.SecurityException;
import io.github.buzzxu.spuddy.errors.LockedAccountException;
import io.github.buzzxu.spuddy.exceptions.ApplicationException;
import io.github.buzzxu.spuddy.objects.Pair;
import io.github.buzzxu.spuddy.redis.Redis;
import io.github.buzzxu.spuddy.security.AccountService;
import io.github.buzzxu.spuddy.security.GetMeToken;
import io.github.buzzxu.spuddy.security.UserService;
import io.github.buzzxu.spuddy.security.exceptions.TokenGenerateException;
import io.github.buzzxu.spuddy.security.exceptions.UnknownAccountException;
import io.github.buzzxu.spuddy.security.exceptions.ValiCodeException;
import io.github.buzzxu.spuddy.security.funs.FunctionThrowsSecurity;
import io.github.buzzxu.spuddy.security.jwt.JWTs;
import io.github.buzzxu.spuddy.security.jwt.JwtConfig;
import io.github.buzzxu.spuddy.security.objects.UserInfo;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkArgument;
import static io.github.buzzxu.spuddy.security.KEY.USER_INFO;
import static io.github.buzzxu.spuddy.security.KEY.USER_TOKEN;

/**
 * @author xux
 * @date 2024年12月28日 22:07:19
 */
public abstract class AbstractAccountService implements AccountService {

    protected int userExpire;
    @Resource
    protected UserService userService;
    @Resource
    protected CaptchaService captchaService;
    @Resource(type = JwtConfig.class)
    protected JwtConfig jwtConfig;
    @Resource(type = JWTs.class)
    protected JWTs jwTs;
    @Resource
    protected Redis redis;

    @PostConstruct
    public void init(){
        userExpire = Math.toIntExact(jwtConfig.getExpiration());
    }
    public <U extends UserInfo> Pair<String, U> login(U user, int region,Function<Long,U> function){
        return generatorToken(user.getId(),region,function);
    }

    /**
     * 无需验证码登录
     * @param userName
     * @param password
     * @param function
     * @param <U>
     * @return
     * @throws SecurityException
     */
    @Override
    public <U extends UserInfo> Pair<String, U> login(String userName, String password, int region, String lang, FunctionThrowsSecurity<Long, U> function) throws SecurityException, LockedAccountException {
        checkArgument(!Strings.isNullOrEmpty(userName) && !Strings.isNullOrEmpty(password),"用户名或密码不能为空");
        long userId = userService.verifyPwd(userName,password).orElseThrow(UnknownAccountException::new);
        return generatorToken(userId,region,function);
    }


    /**
     * 登录
     * @param userName
     * @param password
     * @param valiCodeKey
     * @param valiCode
     * @param function
     * @param <U>
     * @return
     * @throws SecurityException
     */
    @Override
    public <U extends UserInfo> Pair<String,U> login(String userName, String password, String valiCodeKey, String valiCode, int region,String lang,FunctionThrowsSecurity<Long,U> function) throws SecurityException, LockedAccountException {
        checkArgument(!Strings.isNullOrEmpty(userName) && !Strings.isNullOrEmpty(password),"用户名或密码不能为空");
        if(!captchaService.check(valiCodeKey,valiCode)){
            throw new ValiCodeException();
        }
        long userId = userService.verifyPwd(userName,password).orElseThrow(UnknownAccountException::new);
        return generatorToken(userId,region,function);
    }

    /**
     * 注销 需要删除Token
     * @param userId
     * @param token
     */
    @Override
    public void logOut(long userId,String token){
        int region = jwTs.region(token);
        //删除token
        redis.execute(redis->{
            String tokenKey = USER_TOKEN.to(userId,region),userKey = USER_INFO.to(userId);
            redis.del(tokenKey,userKey);
            return null;
        });
    }

    @Override
    @Transactional(rollbackFor = {ApplicationException.class,LockedAccountException.class,IllegalArgumentException.class})
    public <U extends UserInfo> Pair<String,U> changePassword(long userId,String oldPassword,String newPassword,FunctionThrowsSecurity<Long,U> function)throws LockedAccountException{
        if(changePassword(userId,oldPassword,newPassword)){
            int region = jwTs.region(GetMeToken.of());
            return generatorToken(userId,region,function);
        }
        throw ApplicationException.notifyUser("修改密码失败");
    }

    @Transactional(rollbackFor = {ApplicationException.class,LockedAccountException.class,IllegalArgumentException.class})
    @Override
    public boolean changePassword(long userId,String oldPassword, String password) throws LockedAccountException {
        checkArgument(!Strings.isNullOrEmpty(oldPassword) && !Strings.isNullOrEmpty(password),"新旧密码不能为空");
        checkArgument(!StringUtils.equals(oldPassword,password),"新密码不能等同于旧密码");
        return userService.changePassword(userId,oldPassword,password,encry-> redis.execute(redis->  {
            //更新密码
            redis.hset(USER_INFO.to(userId),"password",encry);
            return true;
        }));
    }
    protected <U extends UserInfo> Pair<String,U> generatorToken(Long userId,int region, Function<Long,U> function)throws ApplicationException{
        return generatorToken(function.apply(userId),region);
    }
    /**
     * 生成token
     * @param user
     * @param <U>
     * @return
     */
    @Override
    public <U extends UserInfo> Pair<String, U> generatorToken(U user,int region) {
        try {
            String token = jwTs.create(user, jwtConsumer(user,region), jwtSecretSupplier(user));
            if(user.getId() == 0 || !storeToken(user.getId(),token,region)){
                throw new SecurityException("缓存用户信息失败",401);
            }
            return Pair.of(token,user);
        } catch ( TokenGenerateException e) {
            throw ApplicationException.notifyUser("生成令牌失败，请稍后尝试");
        } catch (SecurityException ex){
            throw ex;
        } catch (ApplicationException ex){
            throw ex;
        } catch (Exception ex){
            throw ApplicationException.raise(ex);
        }
    }

    /**
     * 存储Token
     * @param userId
     * @param token
     * @return
     */
    @Override
    public boolean storeToken(long userId, String token,int region){
        //存入redis中
        return redis.execute(redis->{
            //设置user的token 设置过期时间与token生成的时间一致
            redis.setex(USER_TOKEN.to(userId,region),userExpire,token);
            return true;
        });
    }

    protected <U extends UserInfo> Consumer<JWTCreator.Builder> jwtConsumer(U userInfo, int region){
        return builder -> {
            builder.withClaim("type", userInfo.getType());
            builder.withClaim("userName", userInfo.getUserName());
            builder.withClaim("region",region);
            writeInfoToToken(builder,userInfo);
        };
    }
    protected <U extends UserInfo> Supplier<String> jwtSecretSupplier(U userInfo){
        return userInfo::getPassword;
    }

    protected abstract  <U extends UserInfo> void writeInfoToToken(JWTCreator.Builder builder,U userInfo);
}
