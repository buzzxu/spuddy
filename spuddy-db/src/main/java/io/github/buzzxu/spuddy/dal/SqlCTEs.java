package io.github.buzzxu.spuddy.dal;

import java.util.ArrayList;
import java.util.Arrays;

import static java.util.stream.Collectors.joining;

/**
 * @author xux
 * @date 2024年08月10日 15:55:52
 */
public class SqlCTEs extends ArrayList<SqlCTE> {
    private String sql;

    public SqlCTEs sql(String sql) {
        this.sql = sql;
        return  this;
    }
    public String sql() {
        return "WITH " + this.stream()
                .map(SqlCTE::sql)
                .collect(joining(", ")) + " " + sql;
    }
    public static SqlCTEs ctes(SqlCTE... ctes){
        SqlCTEs sqlCTEs = new SqlCTEs();
        sqlCTEs.addAll(Arrays.asList(ctes));
        return sqlCTEs;
    }

    public String countSql(){
        return this.stream().filter(SqlCTE::count).map(SqlCTE::countSql).findFirst().orElseThrow(()->new IllegalArgumentException("查询失败,未包含主表数量SQL语句"));
    }
}
