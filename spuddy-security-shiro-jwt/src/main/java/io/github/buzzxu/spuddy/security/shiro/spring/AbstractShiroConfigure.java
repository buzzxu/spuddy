package io.github.buzzxu.spuddy.security.shiro.spring;

import com.google.common.base.Strings;
import io.github.buzzxu.spuddy.security.shiro.ShiroConfig;
import jakarta.servlet.Filter;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;


import java.util.Map;

/**
 * @author xux
 * @date 2018/6/13 下午2:12
 */
public abstract class AbstractShiroConfigure {


    @Bean
    public ShiroFilterFactoryBean shirFilter(ShiroConfig shiroConfig, DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        // 必须设置 SecurityManager
        shiroFilter.setSecurityManager(securityManager);
        filter(shiroFilter.getFilters());
        // 拦截器
        shiroConfig.getFilterMapping().forEach((k,v)->shiroFilter.getFilterChainDefinitionMap().put(k,v));
        filterMapping(shiroFilter.getFilterChainDefinitionMap());
        if(!Strings.isNullOrEmpty(shiroConfig.getLoginUrl())){
            shiroFilter.setLoginUrl(shiroConfig.getLoginUrl());
        }
        if(!Strings.isNullOrEmpty(shiroConfig.getSuccessUrl())){
            shiroFilter.setSuccessUrl(shiroConfig.getSuccessUrl());
        }
        if(!Strings.isNullOrEmpty(shiroConfig.getUnauthorizedUrl())){
            shiroFilter.setUnauthorizedUrl(shiroConfig.getUnauthorizedUrl());
        }
        return shiroFilter;
    }

    protected abstract void filter(Map<String, Filter> filters);

    protected abstract void filterMapping(Map<String, String> mapping);

}
