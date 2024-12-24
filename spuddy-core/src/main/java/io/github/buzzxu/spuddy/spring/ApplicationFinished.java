package io.github.buzzxu.spuddy.spring;


import io.github.buzzxu.spuddy.Env;
import io.github.buzzxu.spuddy.internal.PluginLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.SmartApplicationListener;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author 徐翔
 * @create 2021-08-25 15:39
 **/
public class ApplicationFinished implements SmartApplicationListener {
    private volatile AtomicBoolean flag=new AtomicBoolean(false);
    @Autowired
    private Env env;


    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> event) {
        return event == ContextRefreshedEvent.class;
    }

    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        return true;
    }


    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (!flag.compareAndSet(false,true)) return;
        PluginLoader.INSTANCE.plugins.forEach(plugin -> {
            plugin.after(env);
        });
    }
}
