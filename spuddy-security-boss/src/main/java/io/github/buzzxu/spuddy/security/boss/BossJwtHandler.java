package io.github.buzzxu.spuddy.security.boss;

import io.github.buzzxu.spuddy.redis.Redis;
import io.github.buzzxu.spuddy.security.UserInfoService;
import io.github.buzzxu.spuddy.security.exceptions.TokenAuthException;
import io.github.buzzxu.spuddy.security.jwt.JWTs;
import io.github.buzzxu.spuddy.security.jwt.handler.AbstractSecurityHandler;
import io.github.buzzxu.spuddy.security.objects.UserInfo;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;

import static io.github.buzzxu.spuddy.security.KEY.USER_TOKEN;

/**
 * @author xux
 * @date 2024年12月28日 22:43:28
 */
public abstract class BossJwtHandler <U extends UserInfo> extends AbstractSecurityHandler<U> {

    @Resource
    protected Redis redis;
    @Resource
    protected JWTs jwTs;
    @Resource
    protected UserInfoService<U> userInfoService;


    @Override
    public boolean test(long userId, String token) throws SecurityException {
        if(!tokenIdentity(token)){
            throw new TokenAuthException("所持令牌身份限制,无法继续操作");
        }
        int region = jwTs.region(token);
        return redis.execute(redis->{
            String key = USER_TOKEN.to(userId,region);
            if(redis.exists(key)){
                return StringUtils.equals(token,redis.get(key));
            }
            return false;
        });
    }

    @Override
    public Long id(String token) {
        return super.id(token);
    }

    /**
     * 验证token
     * @param token
     * @return false 表示失败
     * @throws TokenAuthException
     */
    protected abstract boolean tokenIdentity(String token) throws TokenAuthException;
}
