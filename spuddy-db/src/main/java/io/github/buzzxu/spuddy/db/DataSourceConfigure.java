package io.github.buzzxu.spuddy.db;

import com.google.common.base.Strings;
import com.zaxxer.hikari.HikariDataSource;
import io.github.buzzxu.spuddy.Env;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;


import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by xuxiang on 2016/10/24.
 */
@Configuration("dataSourceConfigure")
@Order(Ordered.HIGHEST_PRECEDENCE+100000)
@DependsOn("mainEnv")
@EnableTransactionManagement(proxyTargetClass = true)
public class DataSourceConfigure {

    @Autowired
    private JdbcConfig mainEnv;
    @Autowired
    private Env env;


    @Primary
    @Bean(destroyMethod = "close", name = "mainDataSource")
    public DataSource hikariDataSource(HikariCPConfig hikariCPEnv) {
        env.logger().info("Using datasource HikariCP ");
        System.setProperty("dsType","hikari");
        HikariDataSource ds = new HikariDataSource();
        if(Strings.isNullOrEmpty(mainEnv.getDriverClassName())){
            ds.setDriverClassName(mainEnv.getDriverClassName());
        }
        ds.setJdbcUrl(mainEnv.getUrl());
        ds.setUsername(mainEnv.getUsername());
        ds.setPassword(mainEnv.getPassword());
        ds.setAutoCommit(mainEnv.isAutoCommit());
        ds.setReadOnly(mainEnv.isReadOnly());
        if(!Strings.isNullOrEmpty(hikariCPEnv.getConnectionTestQuery())){
            ds.setConnectionTestQuery(hikariCPEnv.getConnectionTestQuery());
        }
        if(!Strings.isNullOrEmpty(mainEnv.getConnectionInitSql())){
            ds.setConnectionInitSql(mainEnv.getConnectionInitSql());
        }else if(!Strings.isNullOrEmpty(hikariCPEnv.getInitSql())){
            ds.setConnectionInitSql(hikariCPEnv.getInitSql());
        }
        if(!Strings.isNullOrEmpty(hikariCPEnv.getPoolName())){
            ds.setPoolName(hikariCPEnv.getPoolName());
        }
        if(!Strings.isNullOrEmpty(hikariCPEnv.getCatalog())){
            ds.setCatalog(hikariCPEnv.getCatalog());
        }
        if(!Strings.isNullOrEmpty(hikariCPEnv.getTransactionIsolation())){
            ds.setTransactionIsolation(hikariCPEnv.getTransactionIsolation());
        }
        if(hikariCPEnv.getLeakDetectionThreshold() !=0){
            ds.setLeakDetectionThreshold(hikariCPEnv.getLeakDetectionThreshold());
        }
        ds.setConnectionTimeout(hikariCPEnv.getConnectionTimeout());
        ds.setValidationTimeout(hikariCPEnv.getValidationTimeout());
        ds.setMaximumPoolSize(hikariCPEnv.getMaxPoolSize());
        ds.setMaxLifetime(hikariCPEnv.getMaxLifetime());
        ds.setMinimumIdle(hikariCPEnv.getMinimumIdle());
        ds.setIdleTimeout(hikariCPEnv.getIdleTimeout());

        if(mainEnv.getDbType().equals(DBType.MYSQL)){
            ds.addDataSourceProperty("cachePrepStmts", "true");
            ds.addDataSourceProperty("useServerPrepStmts", "true");
            ds.addDataSourceProperty("prepStmtCacheSize", "256");
            ds.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        }
        if(mainEnv.getDbType().equals(DBType.POSTGRESQL)){
            if(mainEnv.isReadOnly()){
                ds.addDataSourceProperty("readOnly", "true");
            }
            ds.setConnectionTimeout(0);
            ds.addDataSourceProperty("prepareThreshold", "3");
            ds.addDataSourceProperty("preparedStatementCacheQueries", "128");
            ds.addDataSourceProperty("preparedStatementCacheSizeMiB", "4");
        }
        return ds;
    }




    @DependsOn("mainDataSource")
    @Primary
    @Bean
    public PlatformTransactionManager transactionManager( DataSource mainDataSource) {
        return new DataSourceTransactionManager(mainDataSource);
    }



}
