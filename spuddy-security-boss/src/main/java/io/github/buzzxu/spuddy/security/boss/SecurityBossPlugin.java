package io.github.buzzxu.spuddy.security.boss;

import com.google.common.collect.Sets;
import io.github.buzzxu.spuddy.Plugin;
import io.github.buzzxu.spuddy.annotations.Named;
import io.github.buzzxu.spuddy.security.boss.configure.BossSecurityConfigure;
import io.github.buzzxu.spuddy.security.boss.controllers.ShiroExceptionController;

import java.util.Set;


/**
 * @author xux
 * @date 2024年12月28日 23:14:55
 */
@Named("security-boss")
public class SecurityBossPlugin implements Plugin {

    @Override
    public Set<Class<?>> classes() {
        Set<Class<?>> classes = Sets.newHashSetWithExpectedSize(3);
        classes.add(ShiroExceptionController.class);
        classes.add(ShiroExceptionAdvice.class);
        classes.add(BossSecurityConfigure.class);
        return classes;
    }



}
