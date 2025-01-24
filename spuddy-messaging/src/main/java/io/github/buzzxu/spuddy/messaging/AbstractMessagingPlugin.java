package io.github.buzzxu.spuddy.messaging;

import com.google.common.base.Strings;
import io.github.buzzxu.spuddy.Env;
import io.github.buzzxu.spuddy.Plugin;
import io.github.buzzxu.spuddy.errors.PluginException;
import org.slf4j.Logger;

/**
 * @author xux
 * @date 2025年01月18日 16:24:01
 */
public abstract class AbstractMessagingPlugin implements Plugin {
    protected String[] skip;
    protected Logger logger;
    protected boolean enable;
    @Override
    public void init(Env env) throws PluginException {
        logger = env.logger();
        String skips = env.getApplicationContext().getEnvironment().getProperty("messaging.subscribe.skip", String.class,null);
        skip = Strings.isNullOrEmpty(skips) ? null : (skips.indexOf(",") > 0 ? skips.split(",") : new String[]{skips});
    }


}
