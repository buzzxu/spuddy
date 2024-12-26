package io.github.buzzxu.spuddy.security.shiro.spring;



import io.github.buzzxu.spuddy.security.shiro.JwtRealm;
import io.github.buzzxu.spuddy.security.shiro.StatelessSubjectFactory;
import io.github.buzzxu.spuddy.security.shiro.fliter.JwtAuthenticationFilter;
import jakarta.servlet.Filter;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SubjectFactory;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;


import java.util.Map;


public abstract class ShiroJwtConfigure extends AbstractShiroConfigure {



    @Bean
    public abstract CacheManager shiroRedisCache();

    @Bean("jwtRealm")
    public JwtRealm jwtRealm(){
        return new JwtRealm();
    }

    @DependsOn({"jwtRealm","shiroRedisCache","subjectFactory"})
    @Bean("securityManager")
    public DefaultWebSecurityManager securityManager(@Qualifier("jwtRealm") JwtRealm realm, CacheManager cacheManager, SubjectFactory subjectFactory) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 使用自己的realm
        securityManager.setRealm(realm);
        //注入缓存管理器
        securityManager.setCacheManager(cacheManager);
        securityManager.setSubjectFactory(subjectFactory);
        // 关闭shiro自带的session
        ((DefaultSessionStorageEvaluator)((DefaultSubjectDAO)securityManager.getSubjectDAO()).getSessionStorageEvaluator()).setSessionStorageEnabled(false);
        return securityManager;
    }
    @Bean
    public DefaultWebSubjectFactory subjectFactory(){
        StatelessSubjectFactory subjectFactory = new StatelessSubjectFactory();
        return subjectFactory;
    }
    @Bean
    public DefaultSessionManager sessionManager(){
        DefaultSessionManager sessionManager = new DefaultSessionManager();
        sessionManager.setSessionValidationSchedulerEnabled(false);
        return sessionManager;
    }
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        // 强制使用cglib，防止重复代理和可能引起代理出错的问题
        // https://zhuanlan.zhihu.com/p/29161098
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return defaultAdvisorAutoProxyCreator;
    }
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }


    @Override
    protected void filter(Map<String, Filter> filters) {
        filters.put("jwt", new JwtAuthenticationFilter());
    }

    @Override
    protected void filterMapping(Map<String, String> mapping) {
        mapping.put("/**", "jwt");
    }
}
