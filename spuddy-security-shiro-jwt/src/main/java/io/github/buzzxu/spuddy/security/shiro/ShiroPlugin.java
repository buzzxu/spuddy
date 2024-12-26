package io.github.buzzxu.spuddy.security.shiro;



import io.github.buzzxu.spuddy.Plugin;
import io.github.buzzxu.spuddy.annotations.Named;

import java.util.Set;

/**
 * @author xux
 * @date 2018/6/13 下午2:41
 */
@Named("security-shiro")
public class ShiroPlugin implements Plugin {

    @Override
    public Set<Class<?>> classes() {
        return Set.of(ShiroConfig.class);
    }
}
