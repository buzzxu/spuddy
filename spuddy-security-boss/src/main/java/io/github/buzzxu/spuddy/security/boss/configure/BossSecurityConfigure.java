package io.github.buzzxu.spuddy.security.boss.configure;

import io.github.buzzxu.spuddy.security.services.CaptchaService;
import io.github.buzzxu.spuddy.security.shiro.spring.ShiroJwtConfigure;
import org.apache.shiro.cache.CacheManager;
import org.springframework.context.annotation.*;

/**
 * @author xux
 * @date 2024年12月28日 23:17:31
 */
@Configuration
public class BossSecurityConfigure extends ShiroJwtConfigure {



    @Override
    public CacheManager shiroRedisCache() {
        return new io.github.buzzxu.spuddy.security.boss.CacheManager();
    }
    @Bean
    public CaptchaService captchaService(){
        return new CaptchaService();
    }
}