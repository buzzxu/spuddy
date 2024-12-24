package io.github.buzzxu.spuddy.security.jwt;



import io.github.buzzxu.spuddy.Plugin;

import java.util.Set;

/**
 * @program: shxmao-platform
 * @description:
 * @author: xux
 * @create: 2021-09-01 15:06
 **/
public class JWTPlugin implements Plugin {
    @Override
    public Set<Class<?>> classes() {
        return Set.of(JWTs.class,JwtConfig.class);
    }
}
