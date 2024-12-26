package io.github.buzzxu.spuddy.dal.jdbc.handlers;

import com.google.common.collect.Maps;
import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author xux
 * @date 2022年04月28日 15:02
 */
public class Map2BeanHander<T> implements ResultSetHandler<T> {
    private Class<? extends T> type;
    private final XBeanProcessor convert;

    public Map2BeanHander(String prefix,Class<? extends T> type) {
        this.type = type;
        this.convert = new XBeanProcessor(Maps.newHashMapWithExpectedSize(3),prefix);
    }

    @Override
    public T handle(ResultSet rs) throws SQLException {
        return rs.next() ? this.convert.toBean(rs, this.type) : null;
    }
}
