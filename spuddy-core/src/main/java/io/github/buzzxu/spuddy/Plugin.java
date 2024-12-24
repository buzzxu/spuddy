package io.github.buzzxu.spuddy;


import io.github.buzzxu.spuddy.errors.PluginException;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author 徐翔
 * @create 2021-08-25 15:36
 **/
public interface Plugin {
    default <T extends ConfigurableApplicationContext>void before(T applicationContext)throws PluginException {
        //noting to do
    }

    default void init(Env env)throws PluginException {
        //noting to do
    }

    default boolean skip(){
        return false;
    }

    default Set<Class<?>> classes(){
        return Collections.EMPTY_SET;
    }
    default Map<Class,Object> beans(){return Collections.EMPTY_MAP;}

    default void after(Env env)throws PluginException{
        //noting to do
    }

    default void destroy(){
        //noting to do
    }
}
