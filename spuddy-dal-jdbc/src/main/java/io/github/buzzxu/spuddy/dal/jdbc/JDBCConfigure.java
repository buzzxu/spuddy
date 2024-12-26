package io.github.buzzxu.spuddy.dal.jdbc;

import org.apache.commons.dbutils.QueryRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;


@DependsOn("dataSourceConfigure")
@Configuration("jdbcConfigure")
public class JDBCConfigure {


    @DependsOn("mainDataSource")
    @Bean(name = "mainDataSourceProxy")
    public TransactionAwareDataSourceProxy transactionAwareDataSourceProxy(DataSource mainDataSource){
        return new TransactionAwareDataSourceProxy(mainDataSource);
    }

    @DependsOn("mainDataSourceProxy")
    @Bean(name = "qr")
    public QueryRunner queryRunner(@Autowired TransactionAwareDataSourceProxy mainDataSourceProxy){
        return new QueryRunner(mainDataSourceProxy);
    }
    @DependsOn("qr")
    @Bean(name = "jdbcer")
    public Jdbcer jdbcer(QueryRunner queryRunner){
        return new Jdbcer(queryRunner);
    }
}
