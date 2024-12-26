package io.github.buzzxu.spuddy.dal.jdbc;

import com.google.common.collect.Lists;
import io.github.buzzxu.spuddy.dal.jdbc.handlers.Map2BeanHander;
import io.github.buzzxu.spuddy.exceptions.ApplicationException;
import io.github.buzzxu.spuddy.objects.Pager;
import io.github.buzzxu.spuddy.util.Pagination;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.*;
import org.apache.commons.lang3.ArrayUtils;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author xux
 * @date 2022年04月27日 21:05
 */
@Slf4j
public class Jdbcer {

    private QueryRunner qr;

    public Jdbcer(QueryRunner qr) {
        this.qr = qr;
    }

    public void insert(String sql, Object... params) {
        checkArgument(params != null && params.length > 0 ,"请设置参数");
        try {
            qr.insert(sql,rs -> null,params);
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
    }

    public Long insertForKey(String sql, Object... params) {
        checkArgument(params != null && params.length > 0 ,"请设置参数");
        try {
            return qr.insert(sql,rs -> {
                if(rs.next()){
                    return rs.getLong(1);
                }
                return null;
            },params);
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
    }
    public void insertBatch(String sql, Object[]... params) {
        checkArgument(params != null && params.length > 0 ,"请设置参数");
        try {
            qr.insertBatch(sql,rs -> null,params);
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
    }



    private ScalarHandler scalarHandler = new ScalarHandler() {
        @Override
        public Object handle(ResultSet rs) throws SQLException {
            Object obj = super.handle(rs);
            if (obj instanceof BigInteger) {
                return ((BigInteger) obj).longValue();
            }
            return obj;
        }
    };

    public long count(String sql, Object... params) {
        Number num = 0;
        try {
            if (params == null) {
                num = (Number) qr.query(sql, scalarHandler);
            } else {
                num = (Number) qr.query(sql, scalarHandler, params);
            }
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
        return (num != null) ? num.longValue() : -1;
    }

    /**
     * 执行sql语句
     *
     * @param sql
     *            sql语句
     * @return 受影响的行数
     */
    public int update(String sql) {
        return update(sql, (Object) null);
    }

    /**
     * 单条修改记录
     *
     * @param sql
     *            sql语句
     * @param param
     *            参数
     * @return 受影响的行数
     */
    public int update(String sql, Object param) {
        return update(sql, new Object[] { param });
    }

    /**
     * 单条修改记录
     *
     * @param sql
     *            sql语句
     * @param params
     *            参数数组
     * @return 受影响的行数
     */
    public int update(String sql, Object... params) {
        int affectedRows = 0;
        try {
            if (params == null) {
                affectedRows = qr.update(sql);
            } else {
                affectedRows = qr.update(sql, params);
            }
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
        return affectedRows;
    }

    /**
     * 批量修改记录
     *
     * @param sql
     *            sql语句
     * @param params
     *            二维参数数组
     * @return 受影响的行数的数组
     */
    public int[] batchUpdate(String sql, Object[][] params) {
        int[] affectedRows = new int[0];
        try {
            affectedRows = qr.batch(sql, params);
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
        return affectedRows;
    }

    /**
     * 执行查询，将每行的结果保存到一个Map对象中，然后将所有Map对象保存到List中
     *
     * @param sql
     *            sql语句
     * @return 查询结果
     */
    public List<Map<String, Object>> find(String sql) {
        return find(sql, (Object) null);
    }

    /**
     * 执行查询，将每行的结果保存到一个Map对象中，然后将所有Map对象保存到List中
     *
     * @param sql
     *            sql语句
     * @param param
     *            参数
     * @return 查询结果
     */
    public List<Map<String, Object>> find(String sql, Object param) {
        return find(sql, new Object[] { param });
    }

    /**
     * 执行查询，将每行的结果保存到一个Map对象中，然后将所有Map对象保存到List中
     *
     * @param sql
     *            sql语句
     * @param params
     *            参数数组
     * @return 查询结果
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> findPage(String sql, int page, int count, Object... params) {
        sql = sql + " LIMIT ?,?";
        List<Map<String, Object>> list;
        try {
            if (params == null) {
                list = qr.query(sql, new MapListHandler(), page, count);
            } else {
                list = qr.query(sql, new MapListHandler(), ArrayUtils.addAll(
                        params, page, count));
            }
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
        return list;
    }

    /**
     * 执行查询，将每行的结果保存到一个Map对象中，然后将所有Map对象保存到List中
     *
     * @param sql
     *            sql语句
     * @param params
     *            参数数组
     * @return 查询结果
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> find(String sql, Object... params) {
        List<Map<String, Object>> list = Lists.newArrayListWithCapacity(5);
        try {
            if (params == null) {
                list =  qr.query(sql, new MapListHandler());
            } else {
                list =  qr.query(sql, new MapListHandler(), params);
            }
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
        return list;
    }
    public <T> List<T> find(String sql, ResultSetHandler<List<T>> rs,Object... params) {
        List<T> list;
        try {
            if (params == null) {
                list = qr.query(sql, rs);
            } else {
                list = qr.query(sql, rs, params);
            }
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
        return list;
    }
    /**
     * 执行查询，将每行的结果保存到Bean中，然后将所有Bean保存到List中
     *
     * @param entityClass
     *            类名
     * @param sql
     *            sql语句
     * @return 查询结果
     */
    public <T> List<T> find(Class<T> entityClass, String sql) {
        return find(entityClass, sql, null);
    }

    /**
     * 执行查询，将每行的结果保存到Bean中，然后将所有Bean保存到List中
     *
     * @param entityClass
     *            类名
     * @param sql
     *            sql语句
     * @param param
     *            参数
     * @return 查询结果
     */
    public <T> List<T> find(Class<T> entityClass, String sql, Object param) {
        return find(entityClass, sql, new Object[] { param });
    }

    /**
     * 执行查询，将每行的结果保存到Bean中，然后将所有Bean保存到List中
     *
     * @param entityClass
     *            类名
     * @param sql
     *            sql语句
     * @param params
     *            参数数组
     * @return 查询结果
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> find(Class<T> entityClass, String sql, Object[] params) {
        List<T> list = Lists.newArrayListWithCapacity(3);
        try {
            if (params == null) {
                list = (List<T>) qr.query(sql, new BeanListHandler(entityClass));
            } else {
                list = (List<T>) qr.query(sql, new BeanListHandler(entityClass), params);
            }
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
        return list;
    }

    /**
     * 查询出结果集中的第一条记录，并封装成对象
     *
     * @param entityClass
     *            类名
     * @param sql
     *            sql语句
     * @return 对象
     */
    public <T> T findFirst(Class<T> entityClass, String sql) {
        return findFirst(entityClass, sql, (Object[]) null);
    }

    /**
     * 查询出结果集中的第一条记录，并封装成对象
     *
     * @param entityClass
     *            类名
     * @param sql
     *            sql语句
     * @param params
     *            参数数组
     * @return 对象
     */
    @SuppressWarnings("unchecked")
    public <T> T findFirst(Class<T> entityClass, String sql, Object... params) {
        return findFirst(sql,new BeanHandler<>(entityClass),params);
    }

    public <T> T findFirst(String prefix,Class<T> entityClass, String sql, Object... params) {
        return findFirst(sql,new Map2BeanHander<>(prefix,entityClass),params);
    }
    public <T> T findFirst(String sql, ResultSetHandler<T> rs, Object... params) {
        Object object;
        try {
            if (params == null) {
                object = qr.query(sql, rs);
            } else {
                object = qr.query(sql, rs, params);
            }
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
        return (T) object;
    }
    /**
     * 查询出结果集中的第一条记录，并封装成Map对象
     *
     * @param sql
     *            sql语句
     * @return 封装为Map的对象
     */
    public Map<String, Object> findFirst(String sql) {
        return findFirst(sql, (Object) null);
    }

    /**
     * 查询出结果集中的第一条记录，并封装成Map对象
     *
     * @param sql
     *            sql语句
     * @param param
     *            参数
     * @return 封装为Map的对象
     */
    public Map<String, Object> findFirst(String sql, Object param) {
        return findFirst(sql, new Object[] { param });
    }

    /**
     * 查询出结果集中的第一条记录，并封装成Map对象
     *
     * @param sql
     *            sql语句
     * @param params
     *            参数数组
     * @return 封装为Map的对象
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> findFirst(String sql, Object... params) {
        Map<String, Object> map = null;
        try {
            if (params == null) {
                map = qr.query(sql, new MapHandler());
            } else {
                map = qr.query(sql, new MapHandler(), params);
            }
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
        return map;
    }

    /**
     * 查询某一条记录，并将指定列的数据转换为Object
     *
     * @param sql
     *            sql语句
     * @param params
     *            列名
     * @return 结果对象
     */
    public Object findBy(String sql, String params) {
        return findBy(sql, params, (Object) null);
    }

    /**
     * 查询某一条记录，并将指定列的数据转换为Object
     *
     * @param sql
     *            sql语句
     * @param columnName
     *            列名
     * @param param
     *            参数
     * @return 结果对象
     */
    public Object findBy(String sql, String columnName, Object param) {
        return findBy(sql, columnName, new Object[] { param });
    }

    /**
     * 查询某一条记录，并将指定列的数据转换为Object
     *
     * @param sql
     *            sql语句
     * @param columnName
     *            列名
     * @param params
     *            参数数组
     * @return 结果对象
     */
    public Object findBy(String sql, String columnName, Object... params) {
        Object object;
        try {
            if (params == null) {
                object = qr.query(sql, new ScalarHandler(columnName));
            } else {
                object = qr.query(sql, new ScalarHandler(columnName), params);
            }
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
        return object;
    }

    /**
     * 查询某一条记录，并将指定列的数据转换为Object
     *
     * @param sql
     *            sql语句
     * @param columnIndex
     *            列索引
     * @return 结果对象
     */
    public Object findBy(String sql, int columnIndex) {
        return findBy(sql, columnIndex, (Object) null);
    }

    /**
     * 查询某一条记录，并将指定列的数据转换为Object
     *
     * @param sql
     *            sql语句
     * @param columnIndex
     *            列索引
     * @param param
     *            参数
     * @return 结果对象
     */
    public Object findBy(String sql, int columnIndex, Object param) {
        return findBy(sql, columnIndex, new Object[] { param });
    }

    /**
     * 查询某一条记录，并将指定列的数据转换为Object
     *
     * @param sql
     *            sql语句
     * @param columnIndex
     *            列索引
     * @param params
     *            参数数组
     * @return 结果对象
     */
    public Object findBy(String sql, int columnIndex, Object... params) {
        Object object;
        try {
            if (params == null) {
                object = qr.query(sql, new ScalarHandler(columnIndex));
            } else {
                object = qr.query(sql, new ScalarHandler(columnIndex), params);
            }
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
        return object;
    }

    /**
     *
     * @param <T>分页查询
     * @param beanClass
     * @param sql
     * @param pageNumber
     * @param pageSize
     * @param params
     * @return
     */
    public <T> List<T> findPage(Class<T> beanClass, String sql, int pageNumber, int pageSize, Object... params) {
        if (pageNumber <= 1) {
            pageNumber = 0;
        }
        return query(beanClass, sql + Pagination.rewritePageSql(sql,pageNumber,pageSize) , params);
    }



    public Pager<Map<String,Object>> paginate(String sql, int pageNumber, int pageSize, Object... params){
        Pager<Map<String,Object>> pager = new Pager<>(pageNumber, pageSize);
        paginate(sql, pager, params);
        return pager;
    }

    public  List<Map<String,Object>> paginate(String sql, Pager<Map<String,Object>> pager, Object... params){
        int totalCount = 0;
        if (pager.isCount()){
            String countSql = "select count(*) from (" + sql + ") as total";
            totalCount = Math.toIntExact(count(countSql,params));
            pager.setTotalRow(totalCount);
        }
        if (pager.isCount() && totalCount ==0 ){
            pager.setData(Collections.emptyList());
            return pager.getData();
        }
        if (totalCount > 0) {
            pager.setTotalPage(totalCount / pager.getPageSize() + ((totalCount % pager.getPageSize() == 0) ? 0 : 1));
        }
        String newSql = Pagination.rewritePageSql(sql,pager.getPageNumber(),pager.getPageSize());
        try {
            List<Map<String,Object>> data = qr.query(newSql, new MapListHandler(), params);
            pager.setData(data);
            return data;
        }catch (SQLException ex){
            throw ApplicationException.raise(ex);
        }
    }

    public <T> Pager<T> paginate(String sql, int pageNumber, int pageSize, Class<T> clazz,Object... params){
        Pager<T> pager = new Pager<>(pageNumber, pageSize);
        paginate(sql, pager, clazz, params);
        return pager;
    }
    public <T> List<T> paginate(String sql, Pager<T> pager, Class<T> clazz, Object... params){
        return paginate(sql,pager,clazz,new BeanListHandler(
                clazz),params);
    }
    public <T> List<T> paginate(String sql, Pager<T> pager, Class<T> clazz, ResultSetHandler<List<T>> beanHandler, Object... params){
        int totalCount = 0;
        if (pager.isCount()){
            String countSql = "select count(*) from (" + sql + ") as total";
            totalCount = Math.toIntExact(count(countSql,params));
            pager.setTotalRow(totalCount);
        }
        if (pager.isCount() && totalCount ==0 ){
            pager.setData(Collections.emptyList());
            return pager.getData();
        }
        if (totalCount > 0) {
            pager.setTotalPage(totalCount / pager.getPageSize() + ((totalCount % pager.getPageSize() == 0) ? 0 : 1));
        }
        String newSql = Pagination.rewritePageSql(sql,pager.getPageNumber(),pager.getPageSize());
        try {
            List<T> data = qr.query(newSql, isPrimitive(clazz) ? columnListHandler : beanHandler, params);
            pager.setData(data);
            return data;
        }catch (SQLException ex){
            throw ApplicationException.raise(ex);
        }
    }
    public <T> List<T> query(Class<T> beanClass, String sql, Object... params) {
        try {
            return (List<T>) qr.query(sql, isPrimitive(beanClass) ? columnListHandler : new BeanListHandler(
                    beanClass), params);
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
    }

    private List<Class<?>> PrimitiveClasses = new ArrayList<>() {
        {
            add(Long.class);
            add(Integer.class);
            add(String.class);
            add(java.util.Date.class);
            add(java.sql.Date.class);
            add(java.sql.Timestamp.class);
        }
    };
    // 返回单一列时用到的handler
    private final static ColumnListHandler columnListHandler = new ColumnListHandler() {
        @Override
        protected Object handleRow(ResultSet rs) throws SQLException {
            Object obj = super.handleRow(rs);
            if (obj instanceof BigInteger) {
                return ((BigInteger) obj).longValue();
            }
            return obj;
        }

    };

    // 判断是否为原始类型
    private boolean isPrimitive(Class<?> cls) {
        return cls.isPrimitive() || PrimitiveClasses.contains(cls);
    }

}
