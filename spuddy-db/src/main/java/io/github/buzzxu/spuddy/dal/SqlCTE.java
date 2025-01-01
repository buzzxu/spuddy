package io.github.buzzxu.spuddy.dal;


import io.github.buzzxu.spuddy.db.Pagination;
import lombok.Getter;
import lombok.Setter;

/**
 * @author xux
 * @date 2024年08月10日 15:39:58
 */
@Getter @Setter
public final class SqlCTE {

    private String name;
    private String sql;
    private String _sql;
    private boolean count;

    private SqlCTE(String name,String sql,boolean count){
        this.name = name;
        this.sql = sql;
        this.count = count;
    }

    public static SqlCTE cte(String name,String sql){
        return new SqlCTE(name,sql,false);
    }
    public static SqlCTE cte(String name,String sql,int number,int size){
        return new SqlCTE(name, Pagination.rewritePageSql(sql,number,size),true).countSql(sql);
    }

    SqlCTE countSql(String _sql){
        this._sql = _sql;
        return this;
    }
    public String sql(){
        return "  %s AS (%s)".formatted(name,sql);
    }
    public boolean count(){
        return count;
    }
    public String countSql(){
        return "select count(*) from (" + _sql + ") as total";
    }
}
