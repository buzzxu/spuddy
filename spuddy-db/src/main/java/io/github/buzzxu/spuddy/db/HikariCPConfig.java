package io.github.buzzxu.spuddy.db;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

/**
 * Created by xuxiang on 2016/10/24.
 */
@Getter
@Component("hikariCPEnv")
public class HikariCPConfig {
    private final String poolName;
    private final String catalog;
    private final String transactionIsolation;
    private final int leakDetectionThreshold;
    private final int maxPoolSize;
    private final int minimumIdle;
    private final long idleTimeout;
    private final long maxLifetime;
    private final String initSql;
    private final long connectionTimeout;
    private final long validationTimeout;
    private final String connectionTestQuery;

    public HikariCPConfig(
            @Value("${db.hikari.testQuery:SELECT 1}") String connectionTestQuery
            ,@Value("${db.hikari.poolName:HikariCP}") String poolName
            ,@Value("${db.hikari.catalog:}") String catalog
            ,@Value("${db.hikari.transactionIsolation:}") String transactionIsolation
            ,@Value("${db.hikari.leakDetectionThreshold:0}") int leakDetectionThreshold
            ,@Value("${db.hikari.max-lifetime:900000}") int maxLifetime //28770000
            ,@Value("${db.hikari.maximum-pool-size:30}") int maxPoolSize
            ,@Value("${db.hikari.minimum-idle:10}") int minimumIdle
            ,@Value("${db.hikari.idle-timeout:30000}") long idleTimeout
            ,@Value("${db.hikari.connection-init-sql:}") String initSql
            ,@Value("${db.hikari.connection-timeout:10000}") long connectionTimeout
            ,@Value("${db.hikari.validation-timeout:5000}") long validationTimeout) {
        this.poolName = poolName;
        this.catalog = catalog;
        this.transactionIsolation = transactionIsolation;
        this.leakDetectionThreshold = leakDetectionThreshold;
        this.maxPoolSize = maxPoolSize;
        this.minimumIdle = minimumIdle;
        this.idleTimeout = idleTimeout;
        this.maxLifetime = maxLifetime;
        this.initSql = initSql;
        this.connectionTimeout  = connectionTimeout;
        this.validationTimeout = validationTimeout;
        this.connectionTestQuery = connectionTestQuery;
    }
}
