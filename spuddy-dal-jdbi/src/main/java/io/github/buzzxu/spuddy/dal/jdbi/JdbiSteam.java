package io.github.buzzxu.spuddy.dal.jdbi;


import io.github.buzzxu.spuddy.func.Func;
import io.github.buzzxu.spuddy.objects.Pair;
import io.github.buzzxu.spuddy.objects.Tuple3;
import org.jdbi.v3.core.mapper.RowMapperFactory;
import org.jdbi.v3.core.mapper.reflect.FieldMapper;
import org.jdbi.v3.core.result.LinkedHashMapRowReducer;
import org.jdbi.v3.core.result.RowReducer;
import org.jdbi.v3.core.result.RowView;
import org.jdbi.v3.core.statement.Query;

import java.util.List;
import java.util.stream.Stream;


public interface JdbiSteam {


    default <ID,R> Stream<R> reduceRows(Query query, Tuple3<String,Class<ID>,Class<R>> primary, List<Tuple3<String,Class<?>, Func.Consumer2<R,RowView>>> items, Pair<String, Class<?>>... fileds){
        return reduceRows(query, (LinkedHashMapRowReducer<ID, R>) (container, rowView) -> {
            R obj = container.computeIfAbsent(rowView.getColumn(primary.getT1(),primary.getT2()),id -> rowView.getRow(primary.getT3()));
            for (Tuple3<String,Class<?>, Func.Consumer2<R,RowView>> item: items){
                if(rowView.getColumn(item.getT1(),item.getT2()) != null){
                    item.getT3().accept(obj,rowView);
                }
            }
        }, Stream.of(fileds).map(filed -> FieldMapper.factory(filed.getValue(), filed.getKey())).toArray(RowMapperFactory[]::new));
    }

    default <ID,R> Stream<R> reduceRows(Query query, LinkedHashMapRowReducer<ID, R> reducer, Pair<String,Class<?>>... fileds){
        return reduceRows(query,reducer,Stream.of(fileds).map(filed-> FieldMapper.factory(filed.getValue(),filed.getKey())).toArray(RowMapperFactory[]::new));
    }

    default <ID,R> Stream<R> reduceRows(Query query, RowReducer<ID, R> reducer, RowMapperFactory... factories){
        for (RowMapperFactory factory : factories){
            query.registerRowMapper(factory);
        }
        return query.reduceRows(reducer);
    }
}
