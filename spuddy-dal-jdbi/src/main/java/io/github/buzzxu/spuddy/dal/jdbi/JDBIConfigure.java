package io.github.buzzxu.spuddy.dal.jdbi;



import io.github.buzzxu.spuddy.Env;
import io.github.buzzxu.spuddy.db.JdbcConfig;
import jakarta.annotation.Resource;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.spi.JdbiPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;


import javax.sql.DataSource;
import java.util.List;

/**
 * Created by xux on 2017/4/11.
 */
@DependsOn("dataSourceConfigure")
@Configuration("jdbiConfigure")
public class JDBIConfigure {


    @Resource
    private JdbcConfig mainEnv;

    @DependsOn("mainDataSource")
    @Bean
    public Jdbi dbi(@Autowired(required = false) List<JdbiPlugin> plugins, DataSource dataSource,Env env, Environment environment){
        return new JDBIFactory().build(plugins,dataSource, env,environment,mainEnv,"spuddy-jdbi");
    }
}
