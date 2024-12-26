package io.github.buzzxu.spuddy.security.shiro;

import org.apache.shiro.authc.AuthenticationToken;

import java.io.Serial;

/**
 * @author xux
 */
public class JwtToken implements AuthenticationToken {

    @Serial
    private static final long serialVersionUID = -3380725665263790759L;
    private String principal;
    private final String token;

    public JwtToken(String token) {
        this.token = token;
    }

    public JwtToken(String principal, String token) {
        this.principal = principal;
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
