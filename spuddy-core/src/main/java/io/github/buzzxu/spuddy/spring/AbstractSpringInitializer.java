package io.github.buzzxu.spuddy.spring;


import com.google.common.collect.Sets;
import io.github.buzzxu.spuddy.Env;
import io.github.buzzxu.spuddy.internal.PluginLoader;
import org.slf4j.Logger;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.AnnotationConfigRegistry;

import java.util.Arrays;
import java.util.Set;

/**
 * @author 徐翔
 * @create 2021-08-25 15:37
 **/
public abstract class AbstractSpringInitializer<T extends ConfigurableApplicationContext> implements ApplicationContextInitializer<T> {
    protected final Logger logger;

    public AbstractSpringInitializer(Logger logger) {
        this.logger = logger;
    }

    protected void initializer(T applicationContext, Env env){
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getBeanFactory();
        Set<String> existingBeans = Sets.newHashSet(Arrays.asList(beanFactory.getBeanDefinitionNames()));

        beanFactory.registerResolvableDependency(Env.class,env);
        PluginLoader.INSTANCE.plugins.forEach(plugin -> {
            plugin.init(env);
        });
        AnnotationConfigRegistry ctx;
        if(applicationContext instanceof AnnotationConfigRegistry){
            ctx = (AnnotationConfigRegistry)applicationContext;
        }else{
            ctx = new AnnotationConfigApplicationContext((DefaultListableBeanFactory) applicationContext.getBeanFactory());
        }
        Set<String> scannedPackages = Sets.newHashSet();
        for(String basePackage : env.getBasePackages()) {
            if(!scannedPackages.contains(basePackage)) {
                ctx.scan(basePackage);
                scannedPackages.add(basePackage);
            }
        }
        PluginLoader.INSTANCE.plugins.forEach(plugin -> {
            if(!plugin.classes().isEmpty()){
                plugin.classes().stream()
                        .filter(cls -> !existingBeans.contains(cls.getName()))
                        .forEach(ctx::register);
            }
        });
        ctx.register(SpuddyConfigure.class);
        ctx.register(SpringContextHolder.class);
        ctx.register(ApplicationFinished.class);

//        // 移除重复的bean定义
//        for (String beanName : beanFactory.getBeanDefinitionNames()) {
//            if (!existingBeans.contains(beanName)) {
//                beanFactory.removeBeanDefinition(beanName);
//            }
//        }
    }
}
