package io.github.buzzxu.spuddy.dal.jdbi;

import org.jdbi.v3.core.statement.PaginationSQLInterceptor;
import org.jdbi.v3.sqlobject.customizer.SqlStatementCustomizingAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @program: 
 * @description:
 * @author: 徐翔
 * @create: 2020-02-29 21:16
 **/
@SqlStatementCustomizingAnnotation(PaginationSQLInterceptor.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER})
public @interface Pagination {
    int number() default 1;
    int size() default 10;
}
