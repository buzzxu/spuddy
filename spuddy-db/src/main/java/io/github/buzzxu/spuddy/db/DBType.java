package io.github.buzzxu.spuddy.db;


import io.github.buzzxu.spuddy.db.dialects.MySQLDialect;
import io.github.buzzxu.spuddy.db.dialects.PostgreSQLDialect;

public enum DBType {
    MYSQL(new MySQLDialect()),
    POSTGRESQL(new PostgreSQLDialect()),
    SQLSERVER(MYSQL.dialect()),
    ORACLE(MYSQL.dialect()),
    SQLITE(MYSQL.dialect()),
    DERBY(MYSQL.dialect()),
    HSQLDB(MYSQL.dialect()),
    DB2(MYSQL.dialect()),
    ;

    private Dialect dialect;

    DBType(Dialect dialect) {
        this.dialect = dialect;
    }

    public Dialect dialect() {
        return dialect;
    }

    public String rewritePageSql(String sql,int number,int size){
        return dialect.page(sql,number,size);
    }

    public static DBType PARSER(String url){
        if(url.startsWith("jdbc:postgresql:")){
            return POSTGRESQL;
        }
        if(url.startsWith("jdbc:mysql:")){
            return MYSQL;
        }
        if(url.startsWith("jdbc:sqlserver:")){
            return SQLSERVER;
        }
        if(url.startsWith("jdbc:oracle:")){
            return ORACLE;
        }
        if(url.startsWith("jdbc:sqlite:")){
            return SQLITE;
        }
        if(url.startsWith("jdbc:hsqldb")){
            return HSQLDB;
        }
        if(url.startsWith("jdbc:derby")){
            return DERBY;
        }
        if(url.startsWith("jdbc:db2")){
            return DB2;
        }
        return MYSQL;
    }
}
