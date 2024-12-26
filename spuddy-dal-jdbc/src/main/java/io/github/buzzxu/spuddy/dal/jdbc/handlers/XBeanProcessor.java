package io.github.buzzxu.spuddy.dal.jdbc.handlers;

import org.apache.commons.dbutils.BeanProcessor;
import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyDescriptor;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

/**
 * @author xux
 * @date 2022年04月28日 0:08
 */
public class XBeanProcessor extends BeanProcessor {
    private String prefix;

    public XBeanProcessor(Map<String, String> columnToPropertyOverrides, String prefix) {
        super(columnToPropertyOverrides);
        this.prefix = prefix;
    }

    @Override
    protected int[] mapColumnsToProperties(ResultSetMetaData rsmd, PropertyDescriptor[] props) throws SQLException {
        int cols = rsmd.getColumnCount();
        int[] columnToProperty = new int[cols + 1];
        Arrays.fill(columnToProperty, PROPERTY_NOT_FOUND);

        for (int col = 1; col <= cols; col++) {
            String columnName = rsmd.getColumnLabel(col);
            if (null == columnName || columnName.isEmpty()) {
                columnName = rsmd.getColumnName(col);
            }
            final String generousColumnName = StringUtils.replace(columnName,prefix, "");
            for (int i = 0; i < props.length; i++) {
                final String propName = props[i].getName();
                if (columnName.equalsIgnoreCase(propName) ||
                        generousColumnName.equalsIgnoreCase(propName))  {
                    columnToProperty[col] = i;
                    break;
                }
            }
        }

        return columnToProperty;
    }
}
