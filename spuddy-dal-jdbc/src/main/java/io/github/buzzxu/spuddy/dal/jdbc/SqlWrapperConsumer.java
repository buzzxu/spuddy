package io.github.buzzxu.spuddy.dal.jdbc;



import io.github.buzzxu.spuddy.exceptions.ApplicationException;

import java.sql.SQLException;
import java.util.Objects;

/**
 * @program: yuanmai-platform
 * @description:
 * @author: 徐翔
 * @create: 2020-03-15 21:08
 **/
@FunctionalInterface
public interface SqlWrapperConsumer<T> {

    void accept(T target)throws SQLException, ApplicationException;

    default SqlWrapperConsumer<T> andThen(SqlWrapperConsumer<? super T> after) {
        Objects.requireNonNull(after);
        return (t) -> {
            this.accept(t);
            after.accept(t);
        };
    }
}
