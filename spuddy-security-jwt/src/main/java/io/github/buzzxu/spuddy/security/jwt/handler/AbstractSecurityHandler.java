package io.github.buzzxu.spuddy.security.jwt.handler;

import com.google.common.base.Strings;
import io.github.buzzxu.spuddy.errors.SecurityException;
import io.github.buzzxu.spuddy.errors.NotFoundException;
import io.github.buzzxu.spuddy.security.GetMeToken;
import io.github.buzzxu.spuddy.security.UserService;
import io.github.buzzxu.spuddy.security.exceptions.TokenAuthException;
import io.github.buzzxu.spuddy.security.exceptions.UnknownAccountException;
import io.github.buzzxu.spuddy.security.handler.SecurityUserHandler;
import io.github.buzzxu.spuddy.security.jwt.JWTs;
import io.github.buzzxu.spuddy.security.jwt.JwtConfig;
import io.github.buzzxu.spuddy.security.objects.PrivilegeInfo;
import io.github.buzzxu.spuddy.security.objects.UserInfo;
import jakarta.annotation.Resource;


/**
 * @author xux
 * @date 2018/6/12 上午9:40
 */
public abstract class AbstractSecurityHandler<U extends UserInfo> implements SecurityUserHandler<U> {
    @Resource
    protected UserService userService;
    @Resource(type = JWTs.class)
    protected JWTs jwTs;
    @Resource
    protected JwtConfig jwtConfig;
    @Override
    public U auth(Object principal,Object credentials) throws SecurityException {
        String token;
        if(credentials == null || Strings.isNullOrEmpty((token = (String) credentials))){
            throw new TokenAuthException("无法获取令牌,请先登陆之后再访问");
        }
        long id = id(token);
        if (id > 0 && test(id, token)) {
            U userInfo = getUser(id, jwTs.parse(token, "type", Integer.class)).orElseThrow(UnknownAccountException::new);
            //使用密码作为密钥
            if (jwTs.verify(token,userInfo.getPassword())) {
                GetMeToken.set(token);
                return userInfo;
            }
        }
        throw new TokenAuthException("令牌验证失败,无权限用户禁止访问").token(token);
    }

    @Override
    public PrivilegeInfo pvgInfo(Long userId) {
        if (userId == null || userId <= 0) {
            throw new NotFoundException("无法获取用户[" + userId + "] Pvg信息");
        }
        return userService.pvgInfo(userId).orElseThrow(() -> new NotFoundException("无法获取用户[" + userId + "] Pvg信息"));
    }

    @Override
    public Long id(String token) {
        return jwTs.id(token);
    }
}
