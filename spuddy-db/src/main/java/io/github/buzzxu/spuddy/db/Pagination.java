package io.github.buzzxu.spuddy.db;

public final class Pagination {

    public static String rewritePageSql(String sql,int number,int size){
        return DBType.POSTGRESQL.rewritePageSql(sql,number,size);
    }
}
