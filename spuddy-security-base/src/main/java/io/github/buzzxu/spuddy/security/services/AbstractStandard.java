package io.github.buzzxu.spuddy.security.services;


import io.github.buzzxu.spuddy.dal.jdbc.Jdbcer;
import io.github.buzzxu.spuddy.db.SqlException;
import io.github.buzzxu.spuddy.i18n.I18n;
import jakarta.annotation.Resource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.joining;

/**
 * @author xux
 * @date 2018/5/25 下午5:14
 */
public abstract class AbstractStandard {

    @Resource
    protected QueryRunner qr;
    @Resource
    protected Jdbcer jdbcer;

    protected void checkId(String sql, Integer... ids) throws SqlException {
        long count;
        try {
            if(ids.length ==1){
                checkArgument(ids[0] != 0,"请传入合法的ID,不能传入0");
                count = qr.query(sql + " =?",new ScalarHandler<>(1),ids[0]);
            }else{
                checkArgument(Arrays.stream(ids).noneMatch(x -> x == 0),"请传入合法的ID,不能传入0");
                sql += " IN (";
                for (int id : ids) {
                    sql +=id+",";
                }
                sql = sql.substring(0,sql.length()-1);
                sql+=")";
                count = qr.query(sql,new ScalarHandler<>(1));
            }
            checkArgument( ids.length == count,"传入的ID有误，请检查");
        }catch (SQLException ex){
            throw new SqlException(ex);
        }

    }

    protected String i18n(String key){
        return I18n.use("i18n/security").get(key);
    }
    protected String i18n(String key, Object... arguments){
        return I18n.use("i18n/security").format(key,arguments);
    }
    protected String whereIds(List<Integer> ids){
        String sql = "";
        if(ids.size() ==1){
            sql+="="+ids.get(0);
        }else {
            sql += " IN " + ids.stream().map(Object::toString).collect(joining(",","(",")"));

        }
        return sql;
    }
}
