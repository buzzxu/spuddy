package io.github.buzzxu.spuddy.db;



import io.github.buzzxu.spuddy.Plugin;
import io.github.buzzxu.spuddy.annotations.Named;

import java.util.Set;

/**
 * Created by xuxiang on 2016/10/25.
 */
@Named("dataSource")
public class DataSourcePlugin implements Plugin {

    @Override
    public Set<Class<?>> classes() {
        return Set.of(JdbcConfig.class,HikariCPConfig.class,DataSourceConfigure.class);
    }

}
