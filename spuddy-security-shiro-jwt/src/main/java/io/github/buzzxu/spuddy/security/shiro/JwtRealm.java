package io.github.buzzxu.spuddy.security.shiro;


import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author xux
 * @date 2018/6/13 下午12:43
 */
public class JwtRealm extends SecurityRealm {

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class getAuthenticationTokenClass() {
        return JwtToken.class;
    }

    @Override
    public boolean isAuthenticationCachingEnabled() {
        return false;
    }

    @Override
    public boolean isAuthorizationCachingEnabled() {
        //鉴权信息
        return true;
    }
}
