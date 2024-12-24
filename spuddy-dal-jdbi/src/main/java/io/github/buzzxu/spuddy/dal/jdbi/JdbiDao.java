package io.github.buzzxu.spuddy.dal.jdbi;

import com.google.common.collect.Lists;
import io.github.buzzxu.spuddy.dal.BaseDao;
import io.github.buzzxu.spuddy.dal.SqlCTEs;
import io.github.buzzxu.spuddy.objects.Pager;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.result.ResultIterable;
import org.jdbi.v3.core.statement.Query;
import org.jdbi.v3.sqlobject.SqlObject;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static io.github.buzzxu.spuddy.dal.jdbi.JdbiUtil.DefalutPageineRawSql;

/**
 * @program: 
 * @description:
 * @author: 徐翔
 * @create: 2020-03-01 20:44
 **/
public interface JdbiDao extends BaseDao,JdbiSteam,SqlObject {


    @Override
    default boolean exists(String table, String column, Object arg){
        return withHandle(handle -> handle.select("SELECT EXISTS (SELECT * FROM "+table+" WHERE "+column+" = ?)",arg).mapTo(boolean.class).one());
    }

    @Override
    default boolean exists(String table, Map<String, Object> params) {
        checkArgument(params != null && !params.isEmpty(),"column is null");
        return withHandle(handle -> {
            String sql = "SELECT EXISTS (SELECT * FROM "+table+" WHERE ";
            List<Object> args = Lists.newArrayListWithCapacity(params.size());
            for(Map.Entry<String,Object> entry : params.entrySet()){
                sql += entry.getKey() + " = ? AND ";
                args.add(entry.getValue());
            }
            sql = sql.substring(0,sql.length() - 4) + ")";
            return  handle.select(sql,args.toArray()).mapTo(Boolean.class).one();
        });
    }

    @Override
    default <T> List<T> paginate(String sql, Map<String,Object> params, Pager<T> pager, Class<T> clazz){
        return paginate(sql,query -> query.bindMap(params),pager,clazz);
    }
    @Override
    default <T> List<T> paginate(String sql, Object params, Pager<T> pager, Class<T> clazz){
        return paginate(sql,query -> query.bindBean(params),pager,clazz);
    }
    @Override
    default List<Map<String, Object>> paginate(String sql, Map<String, Object> params, Pager<Map<String, Object>> pager){
        return paginate(sql,query -> query.bindMap(params),pager);
    }
    @Override
    default List<Map<String, Object>> paginate(String sql, Object params, Pager<Map<String, Object>> pager){
        return paginate(sql,query -> query.bindBean(params),pager);
    }


    default <T> List<T> pageToBean(String sql, Map<String,Object> params, Pager<T> pager, Class<T> clazz){
        return pageToBean(sql,query -> query.bindMap(params),pager,clazz);
    }


    default <T> List<T> pageToBean(String sql, Consumer<Query> consumer, Pager<T> pager, Class<T> clazz){
        return withHandle(handle -> {
            paginate(handle,sql,pager,consumer).ifPresent(query -> {
                pager.setData(query.mapToBean(clazz).list());
            });
            return pager.getData();
        });
    }
    default List<Map<String, Object>> paginate(String sql, Consumer<Query> consumer, Pager<Map<String, Object>> pager){
        return withHandle(handle -> {
            paginate(handle,sql,pager,consumer).ifPresent(query -> {
                pager.setData(query.mapToMap().list());
            });
            return pager.getData();
        });
    }

    default <T> Pager<T> paginate(String sql,  int pageNumber, int pageSize, Consumer<Query> consumer, Class<T> clazz){
        Pager<T> pager = new Pager<>(pageNumber, pageSize);
        paginate(sql,consumer,pager,clazz);
        return pager;
    }




    default <T> List<T> paginate(String sql, Consumer<Query> consumer, Pager<T> pager, Class<T> clazz){
        return withHandle(handle -> {
            paginate(handle,sql,pager,consumer).ifPresent(query -> {
                pager.setData(query.mapTo(clazz).list());
            });
            return pager.getData();
        });
    }

    default <T> Pager<T> paginate(String sql, int pageNumber, int pageSize, Map<String,Object> params, Class<T> clazz){
        return paginate(sql,pageNumber,pageSize,params,query-> query.mapTo(clazz) );
    }
    default <T> Pager<T> paginate(String sql, int pageNumber, int pageSize, Map<String,Object> params, Function<Query, ResultIterable<T>> function){
        return paginate(sql,pageNumber,pageSize,query -> query.bindMap(params),function);
    }
    default <T> Pager<T> paginate(String sql, int pageNumber, int pageSize, Object params, Function<Query, ResultIterable<T>> function){
        return paginate(sql,pageNumber,pageSize,query -> query.bindBean(params),function);
    }
    default <T> Pager<T> paginate(String sql, int pageNumber, int pageSize,  Consumer<Query> consumer,  Function<Query, ResultIterable<T>> function){
        return withHandle(handle -> paginate(handle,sql,pageNumber,pageSize,consumer,function));
    }


    default <T> Pager<T> paginate(Handle handle, String sql, int pageNumber, int pageSize, Map<String,Object> params, Function<Query, ResultIterable<T>> function){
        return paginate(handle, sql, new Pager<>(pageNumber,pageSize), params, function);
    }
    default <T> Pager<T> paginate(Handle handle, String sql, Pager<T> pager, Map<String,Object> params, Function<Query, ResultIterable<T>> function){
        checkNotNull(function);
        paginate(handle,sql,pager,q-> q.bindMap(params)).ifPresent(query -> pager.setData(function.apply(query).collect(Collectors.toList())));
        return pager;
    }
    default <T> Pager<T> paginate(Handle handle, String sql, int pageNumber, int pageSize, Consumer<Query> consumer, Function<Query, ResultIterable<T>> function){
        checkNotNull(function);
        return paginate(handle,sql,new Pager<>(pageNumber,pageSize),consumer,function);
    }
    default <T> Pager<T> paginate(Handle handle, String sql, Pager<T> pager, Consumer<Query> consumer, Function<Query, ResultIterable<T>> function){
        checkNotNull(function);
        paginate(handle,sql,pager,consumer).ifPresent(query -> pager.setData(function.apply(query).collect(Collectors.toList())));
        return pager;
    }

    default <T> Pager<T> paginateλ(SqlCTEs ctes, int pageNumber, int pageSize,Consumer<Query> consumer,Function<Query, Stream<T>> function){
        return paginateλ(ctes.sql(),ctes.countSql(),pageNumber,pageSize,consumer,function);
    }
    default <T> Pager<T> paginateλ(String sql, int pageNumber, int pageSize,Consumer<Query> consumer,Function<Query, Stream<T>> function){
        return withHandle(handle -> λ(handle,sql,pageNumber,pageSize,consumer,function));
    }
    default <T> Pager<T> paginateλ(String sql, String sqlCount,int pageNumber, int pageSize,Consumer<Query> consumer,Function<Query, Stream<T>> function){
        return withHandle(handle -> λ(handle,sql,sqlCount,new Pager<>(pageNumber,pageSize),consumer,function));
    }


    default <T> Pager<T> λ(Handle handle, String sql, int pageNumber, int pageSize, Consumer<Query> consumer, Function<Query, Stream<T>> function){
        return λ(handle, sql, new Pager<>(pageNumber,pageSize), consumer, function);
    }
    default <T> Pager<T> λ(String sql, int pageNumber, int pageSize, Object params,Function<Query, Stream<T>> function){
        return λ(sql,pageNumber,pageSize,query -> query.bindBean(params),function);
    }
    default <T> Pager<T> λ(String sql, int pageNumber, int pageSize, Map<String,Object> params,Function<Query, Stream<T>> function){
        return λ(sql,pageNumber,pageSize,query -> query.bindMap(params),function);
    }
    default <T> Pager<T> λ(String sql, int pageNumber, int pageSize, Consumer<Query> consumer, Function<Query, Stream<T>> function){
        return withHandle(handle -> λ(handle,sql,pageNumber,pageSize,consumer,function));
    }
    default <T> Pager<T> λ(Handle handle,String sql, Pager<T> pager, Consumer<Query> consumer, Function<Query, Stream<T>> function){
        checkNotNull(function);
        paginate(handle,sql,pager,consumer).ifPresent(query -> pager.setData(function.apply(query).collect(Collectors.toList())));
        return pager;
    }
    default <T> Pager<T> λ(Handle handle,SqlCTEs ctes, Pager<T> pager, Consumer<Query> consumer, Function<Query, Stream<T>> function){
        return λ(handle,ctes.sql(),ctes.countSql(),pager,consumer,function);
    }
    default <T> Pager<T> λ(Handle handle,String sql,String countSql, Pager<T> pager, Consumer<Query> consumer, Function<Query, Stream<T>> function){
        checkNotNull(function);
        paginateλ(handle,sql,countSql,pager,consumer).ifPresent(query -> pager.setData(function.apply(query).collect(Collectors.toList())));
        return pager;
    }
    default <T> Pager<T> λ(Handle handle,SqlCTEs ctes,String countSql, Pager<T> pager, Consumer<Query> consumer, Function<Query, Stream<T>> function){
        checkNotNull(function);
        paginateλ(handle,ctes,countSql,pager,consumer).ifPresent(query -> pager.setData(function.apply(query).collect(Collectors.toList())));
        return pager;
    }

    default <T> Optional<Query> paginate(Handle handle, String sql,  Pager<T> pager, Consumer<Query> consumer){
        return paginate(handle,sql, "select count(*) from (" + sql + ") as total", pager, consumer);
    }
    default <T> Optional<Query> paginateλ(Handle handle, SqlCTEs ctes,String countSql, Pager<T> pager, Consumer<Query> consumer){
        return paginateλ(handle,ctes.sql(),countSql,pager,consumer);
    }
    default <T> Optional<Query> paginateλ(Handle handle, SqlCTEs ctes, Pager<T> pager, Consumer<Query> consumer){
        return paginateλ(handle,ctes.sql(),ctes.countSql(),pager,consumer);
    }

    default <T> Optional<Query> paginate(Handle handle, String sql,String countSql,  Pager<T> pager, Consumer<Query> consumer){
        //count
        int totalCount = 0;
        if (pager.isCount()){
            Query query = handle.createQuery(countSql);
            consumer.accept(query);
            totalCount = query.mapTo(Integer.class).one();
            pager.setTotalRow(totalCount);
        }
        if (pager.isCount() && totalCount ==0 ){
            pager.setData(Collections.emptyList());
            return Optional.empty();
        }
        if (totalCount > 0) {
            pager.setTotalPage(totalCount / pager.getPageSize() + ((totalCount % pager.getPageSize() == 0) ? 0 : 1));
        }
        String newSql = pageineRawSql(sql,pager);
        Query query = handle.createQuery(newSql);
        consumer.accept(query);
        return Optional.of(query);
    }
    default <T> Optional<Query> paginateλ(Handle handle, String sql,String countSql,  Pager<T> pager, Consumer<Query> consumer){
        //count
        int totalCount = 0;
        if (pager.isCount()){
            Query query = handle.createQuery(countSql);
            consumer.accept(query);
            totalCount = query.mapTo(Integer.class).one();
            pager.setTotalRow(totalCount);
        }
        if (pager.isCount() && totalCount ==0 ){
            pager.setData(Collections.emptyList());
            return Optional.empty();
        }
        if (totalCount > 0) {
            pager.setTotalPage(totalCount / pager.getPageSize() + ((totalCount % pager.getPageSize() == 0) ? 0 : 1));
        }
        Query query = handle.createQuery(sql);
        consumer.accept(query);
        return Optional.of(query);
    }
    /**
     * 默认分页语句
     * @param sql
     * @param pager
     * @param <T>
     * @return
     */
    default <T> String pageineRawSql(String sql,Pager<T> pager){
        return DefalutPageineRawSql(sql,pager);
    }





    @Deprecated
    default <T> Optional<Query> paginate(String select, String sqlExceptSelect,Pager<T> pager, Consumer<Query> consumer,Handle handle){
        //count
        int totalCount = 0;
        if (pager.isCount()){
            String countSql = "select count(*) from "+ sqlExceptSelect;
            Query query = handle.createQuery(countSql);
            consumer.accept(query);
            totalCount = query.mapTo(Integer.class).one();
            pager.setTotalRow(totalCount);
        }
        if (pager.isCount() && totalCount ==0 ){
            return Optional.empty();
        }
        if (totalCount > 0) {
            pager.setTotalPage(totalCount / pager.getPageSize() + ((totalCount % pager.getPageSize() == 0) ? 0 : 1));
        }
        String newSql = pageineRawSql(select + sqlExceptSelect,pager);
        Query query = handle.createQuery(newSql);
        consumer.accept(query);
        return Optional.of(query);
    }
}
