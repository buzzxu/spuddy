package io.github.buzzxu.spuddy.db;


import io.github.buzzxu.spuddy.exceptions.StartupException;
import javassist.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by xuxiang on 2016/10/24.
 */
@Getter
@Component("mainEnv")
public class JdbcConfig {
    private final String driverClassName;
    private final String url;
    private final String username;
    private final String password;
    private final String connectionInitSql;

    private final String cachePrepStmts;
    private final int prepStmtCacheSize;
    private final int prepStmtCacheSqlLimit;
    private final String validationQuery;
    private final boolean autoCommit;
    private final boolean readOnly;

    private final DBType dbType;

    public JdbcConfig(@Value("${db.connection.driver-class-name:com.mysql.cj.jdbc.Driver}") String driverClassName
            , @Value("${db.connection.url:}") String url
            , @Value("${db.connection.username:}") String username
            , @Value("${db.connection.password:}") String password
            , @Value("${db.connection.initSql:}") String connectionInitSql
            , @Value("${db.connection.cachePrepStmts:true}") String cachePrepStmts
            , @Value("${db.connection.prepStmtCacheSize:250}") int prepStmtCacheSize
            , @Value("${db.connection.prepStmtCacheSqlLimit:2048}") int prepStmtCacheSqlLimit
            , @Value("${db.connection.autoCommit:true}") boolean autoCommit
            , @Value("${db.connection.readOnly:false}") boolean readOnly
    ) {

        this.driverClassName = driverClassName;
        this.url = url;
        this.dbType = DBType.PARSER(url);
        this.username = username;
        this.password = password;
        this.connectionInitSql = connectionInitSql;
        this.cachePrepStmts = cachePrepStmts;
        this.prepStmtCacheSize = prepStmtCacheSize;
        this.prepStmtCacheSqlLimit = prepStmtCacheSqlLimit;
        this.validationQuery = switch (dbType){
            case ORACLE -> "SELECT 1 FROM DUAL";
            case DB2 -> "select 1 from sysibm.sysdummy1";
            case HSQLDB -> "select 1 from INFORMATION_SCHEMA.SYSTEM_USERS";
            case DERBY -> "values 1";
            default -> "SELECT 1";
        };
        this.autoCommit = autoCommit;
        this.readOnly = readOnly;
        //重写实现分页sql
        rewritePageSql(url);
    }




    private static void rewritePageSql(String url){
        try {
            ClassPool cp = ClassPool.getDefault();
            cp.insertClassPath(new ClassClassPath(JdbcConfig.class));
            CtClass cc=cp.get("io.github.buzzxu.spuddy.util.Pagination");
            cc.addField(CtField.make( "private static final io.github.buzzxu.spuddy.db.DBType dbType;",cc),CtField.Initializer.byExpr("io.github.buzzxu.spuddy.db.DBType.PARSER(\""+url+"\");"));
            CtMethod cm=cc.getDeclaredMethod("rewritePageSql");
            cm.setBody("return dbType.rewritePageSql($$);");
            cc.toClass();
        }catch (Exception ex){
            throw new StartupException(ex);
        }
    }
}
