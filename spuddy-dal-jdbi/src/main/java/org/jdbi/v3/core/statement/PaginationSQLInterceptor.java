package org.jdbi.v3.core.statement;


import io.github.buzzxu.spuddy.exceptions.ApplicationException;
import io.github.buzzxu.spuddy.dal.jdbi.JdbiUtil;
import io.github.buzzxu.spuddy.dal.jdbi.Pagination;
import io.github.buzzxu.spuddy.objects.Pager;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.sqlobject.customizer.SqlStatementCustomizer;
import org.jdbi.v3.sqlobject.customizer.SqlStatementCustomizerFactory;
import org.jdbi.v3.sqlobject.customizer.SqlStatementParameterCustomizer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static io.github.buzzxu.spuddy.dal.jdbi.JdbiUtil.DefalutPageineRawSql;

/**
 * @see SqlStatement
 * @program: 
 * @description:
 * @author: 徐翔
 * @create: 2020-02-29 21:14
 **/
public class PaginationSQLInterceptor implements SqlStatementCustomizerFactory {

    @Override
    public SqlStatementCustomizer createForMethod(Annotation annotation, Class<?> sqlObjectType, Method method) {
        Pagination pagination = (Pagination)annotation;
        return (smts)->{
            smts.addCustomizer(new StatementCustomizer() {
                @Override
                public void beforeExecution(PreparedStatement stmt, StatementContext ctx) throws SQLException {
                    pagination(pagination.number(),pagination.size(),smts,stmt,ctx);
                }
            });
        };
    }

    @Override
    public SqlStatementParameterCustomizer createForParameter(Annotation annotation, Class<?> sqlObjectType, Method method, Parameter param, int index, Type paramType) {
        return (smts,obj)->{
            Pager pager = (Pager)obj;
            smts.addCustomizer(new StatementCustomizer() {
                String oldSql;
                @Override
                public void beforeExecution(PreparedStatement stmt, StatementContext ctx) throws SQLException {
                    oldSql = pagination(pager.getPageNumber(),pager.getPageSize(),smts,stmt,ctx);
                }
                @Override
                public void afterExecution(PreparedStatement stmt, StatementContext ctx) throws SQLException {
                    count(oldSql,ctx,pager);
                }
            });
        };
    }

    private String  pagination(int number,int size,SqlStatement<?> smts,PreparedStatement stmt, StatementContext ctx) throws SQLException {
        String oldSql = JdbiUtil.getRawSql(stmt);
        stmt.close();
        String newSql = DefalutPageineRawSql(oldSql,number,size);
        try {
//            ParsedSql parsedSql = ctx.getParsedSql();
//            FieldUtils.writeField(parsedSql,"sql",newSql,true);
            Handle handle = smts.getHandle();
            stmt = handle.getStatementBuilder().create(handle.getConnection(),newSql,ctx);
            smts.getConfig(SqlStatements.class).customize(stmt);
            ctx.setStatement(stmt);
            FieldUtils.writeField(smts,"stmt",stmt,true);
        } catch (IllegalAccessException e) {
            throw  ApplicationException.raise(e);
        }
        return oldSql;
    }
    private void count(String oldSql, StatementContext ctx,Pager pager) throws SQLException {
        String countSql = "select count(*) from (" + oldSql + ") as total";
        try(PreparedStatement stmtCount = ctx.getConnection().prepareStatement(countSql);ResultSet rs = stmtCount.executeQuery()){
            int totalCount = 0;
            if (rs.next()) {
                totalCount = rs.getInt(1);
            }
            pager.setTotalRow(totalCount);
            int totalPage = totalCount / pager.getPageSize() + ((totalCount % pager.getPageSize() == 0) ? 0 : 1);
            pager.setTotalPage(totalPage);
        }
    }
}
