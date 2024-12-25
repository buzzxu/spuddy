package io.github.buzzxu.spuddy.dal;



import io.github.buzzxu.spuddy.objects.Pager;

import java.util.List;
import java.util.Map;

/**
 * @program: 
 * @description:
 * @author: 徐翔
 * @create: 2020-03-01 21:23
 **/
public interface BaseDao {

    default boolean exists(String table,String column,Object arg){
        throw new UnsupportedOperationException();
    }
    default boolean exists(String table,Map<String,Object> params){
        throw new UnsupportedOperationException();
    }
    default <T> List<T> paginate(String sql, Map<String,Object> params, Pager<T> pager, Class<T> clazz){
        throw new UnsupportedOperationException();
    }

    default <T> List<T> paginate(String sql, Object params, Pager<T> pager, Class<T> clazz){
        throw new UnsupportedOperationException();
    }
    default List<Map<String,Object>> paginate(String sql, Map<String,Object> params, Pager<Map<String,Object>> pager){
        throw new UnsupportedOperationException();
    }
    default List<Map<String, Object>> paginate(String sql, Object params, Pager<Map<String, Object>> pager){
        throw new UnsupportedOperationException();
    }
}
