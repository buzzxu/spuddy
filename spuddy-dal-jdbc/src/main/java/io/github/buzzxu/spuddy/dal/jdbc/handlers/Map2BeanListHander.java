package io.github.buzzxu.spuddy.dal.jdbc.handlers;

import com.google.common.collect.Maps;
import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author xux
 * @date 2022年04月27日 23:47
 */
public class Map2BeanListHander<T> implements ResultSetHandler<List<T>> {
    private Class<? extends T> type;
    private final XBeanProcessor convert;

    public Map2BeanListHander(String prefix,Class<? extends T> type) {
        this.type = type;
        this.convert = new XBeanProcessor(Maps.newHashMapWithExpectedSize(3),prefix);
    }

    @Override
    public List<T> handle(ResultSet rs) throws SQLException {
        return convert.toBeanList(rs,type);
    }




}
