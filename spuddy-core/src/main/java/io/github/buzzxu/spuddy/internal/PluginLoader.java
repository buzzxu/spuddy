package io.github.buzzxu.spuddy.internal;

import io.github.buzzxu.spuddy.Plugin;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.util.List;

/**
 * @author xux
 * @date 2024年12月23日 21:02:43
 */
public class PluginLoader {
    public final static PluginLoader INSTANCE = new PluginLoader();
    public List<Plugin> plugins =  SpringFactoriesLoader.loadFactories(Plugin.class,Thread.currentThread().getContextClassLoader());
}
