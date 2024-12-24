package io.github.buzzxu.spuddy.spring;


import io.github.buzzxu.spuddy.Env;
import io.github.buzzxu.spuddy.internal.PluginLoader;
import org.slf4j.Logger;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.AnnotationConfigRegistry;

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
        applicationContext.getBeanFactory().registerResolvableDependency(Env.class,env);
        PluginLoader.INSTANCE.plugins.forEach(plugin -> {
            plugin.init(env);
        });
        AnnotationConfigRegistry ctx;
        if(applicationContext instanceof AnnotationConfigRegistry){
            ctx = (AnnotationConfigRegistry)applicationContext;
        }else{
            ctx = new AnnotationConfigApplicationContext((DefaultListableBeanFactory) applicationContext.getBeanFactory());
        }
        ctx.scan(env.getBasePackages());
        PluginLoader.INSTANCE.plugins.forEach(plugin -> {
            if(!plugin.classes().isEmpty()){
                ctx.register(plugin.classes().stream().toArray(Class[]::new));
            }
        });
        ctx.register(SpuddyConfigure.class);
        ctx.register(SpringContextHolder.class);
        ctx.register(ApplicationFinished.class);
    }
}
