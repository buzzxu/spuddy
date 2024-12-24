package io.github.buzzxu.spuddy.dal.jdbi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import io.github.buzzxu.spuddy.Env;
import io.github.buzzxu.spuddy.db.JdbcConfig;
import io.github.buzzxu.spuddy.exceptions.StartupException;
import io.github.buzzxu.spuddy.jackson.Jackson;
import io.github.buzzxu.spuddy.objects.IdName;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.apache.commons.lang3.StringUtils;
import org.jdbi.v3.core.ConnectionFactory;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.argument.Arguments;
import org.jdbi.v3.core.argument.NullArgument;
import org.jdbi.v3.core.config.ConfigRegistry;
import org.jdbi.v3.core.generic.GenericType;
import org.jdbi.v3.core.spi.JdbiPlugin;
import org.jdbi.v3.core.statement.ColonPrefixSqlParser;
import org.jdbi.v3.core.statement.SqlStatements;
import org.jdbi.v3.core.statement.TemplateEngine;
import org.jdbi.v3.jackson2.Jackson2Config;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by xux on 2017/4/16.
 */
public class JDBIFactory {



    protected Optional<TimeZone> databaseTimeZone() {
        return Optional.empty();
    }

    public Jdbi build(List<JdbiPlugin> plugins, DataSource dataSource, Env env,Environment environment, JdbcConfig mainEnv, String name) {
        dynamicClass(env,mainEnv);
        final Jdbi jdbi = Jdbi.create(new ConnectionFactory() {
            @Override
            public Connection openConnection() throws SQLException {
                return DataSourceUtils.getConnection(dataSource);
            }

            @Override
            public void closeConnection(Connection conn) throws SQLException {
                DataSourceUtils.releaseConnection(conn, dataSource);
            }
        });
        jdbi.getConfig(Arguments.class).setUntypedNullArgument(new NullArgument(Types.NULL));
        jdbi.getConfig(SqlStatements.class).setUnusedBindingAllowed(true);


        jdbi.setSqlParser(new ColonPrefixSqlParser());
        if (environment.getProperty("db.logger.enabled", Boolean.class,false)) {
            jdbi.setSqlLogger(new Slf4jSqlLogger(environment.getProperty("db.logger.name") != null ? LoggerFactory.getLogger(environment.getProperty("db.logger.name", String.class)) : env.logger()));
        }
        if (mainEnv.isAutoCommit()) {
            final TemplateEngine original = jdbi.getConfig(SqlStatements.class).getTemplateEngine();
            jdbi.setTemplateEngine(new NamePrependingTemplateEngine(original));
        }
        jdbi.registerColumnMapper(OptionalDouble.class, (rs, col, ctx) -> {
            final double value = rs.getDouble(col);
            return rs.wasNull() ? OptionalDouble.empty() : OptionalDouble.of(value);
        });
        jdbi.registerColumnMapper(OptionalInt.class, (rs, col, ctx) -> {
            final int value = rs.getInt(col);
            return rs.wasNull() ? OptionalInt.empty() : OptionalInt.of(value);
        });
        jdbi.registerColumnMapper(OptionalLong.class,(rs,col,ctx)->{
            final int value = rs.getInt(col);
            return rs.wasNull() ? OptionalLong.empty() : OptionalLong.of(value);
        });
        customizeJdbi(jdbi);

        jdbi.installPlugins();
        if (plugins != null ) {
            plugins.forEach(jdbi::installPlugin);
        }
        jdbi.getConfig(Jackson2Config.class).setMapper(Jackson.of());

        return jdbi;
    }

    private void customizeJdbi(Jdbi jdbi){
        jdbi.registerColumnMapper(new GenericType<List<IdName>>() {
        }, (r, columnNumber, ctx) -> {
            String val = r.getString(columnNumber);
            return Strings.isNullOrEmpty(val) ? Collections.emptyList() : Jackson.json2List(val, IdName.class);
        });
        jdbi.registerColumnMapper(new GenericType<List<Map<String, Object>>>() {
        }, (r, columnNumber, ctx) -> {
            String val = r.getString(columnNumber);
            return Strings.isNullOrEmpty(val) ? null : Jackson.json2Object(val, Jackson.buildCollectionType(List.class, Map.class));
        });
        jdbi.registerColumnMapper(new GenericType<Map<String, Object>>() {
        }, (r, columnNumber, ctx) -> {
            String val = r.getString(columnNumber);
            return Strings.isNullOrEmpty(val) ? Collections.EMPTY_MAP : Jackson.json2Map(val);
        });
        jdbi.registerColumnMapper(new GenericType<List<String>>() {
        }, (r, columnNumber, ctx) -> {
            String val = r.getString(columnNumber);
            return Strings.isNullOrEmpty(val) ? Collections.EMPTY_LIST : Jackson.isJSON(val) ? Jackson.json2Object(val, new TypeReference<List<String>>() {
            }) : Splitter.on(",").splitToList(val);
        });
        jdbi.registerColumnMapper(new GenericType<List<Integer>>() {
        }, (r, columnNumber, ctx) -> {
            String val = r.getString(columnNumber);
            return Strings.isNullOrEmpty(val) ? Collections.EMPTY_LIST :  Jackson.isJSON(val) ? Jackson.json2Object(val, new TypeReference<List<Integer>>() {
            }) : Splitter.on(",").splitToStream(val).map(v-> Integer.valueOf(v)).collect(Collectors.toList());
        });
        jdbi.registerColumnMapper(new GenericType<List<Long>>() {
        }, (r, columnNumber, ctx) -> {
            String val = r.getString(columnNumber);
            return Strings.isNullOrEmpty(val) ? Collections.EMPTY_LIST : Jackson.isJSON(val) ? Jackson.json2Object(val, new TypeReference<List<Long>>() {
            }): Splitter.on(",").splitToStream(val).map(v-> Long.valueOf(v)).collect(Collectors.toList());
        });

        jdbi.registerColumnMapper(new GenericType<String[]>(){}, (r, columnNumber, ctx) -> {
            String val = r.getString(columnNumber);
            return Strings.isNullOrEmpty(val) ? null : Jackson.isJSON(val) ? Jackson.json2Object(val,String[].class) :  Splitter.on(",").splitToStream(val).toArray(String[]::new );
        });
        jdbi.registerColumnMapper(new GenericType<Integer[]>(){},(r, columnNumber, ctx) -> {
            String val = r.getString(columnNumber);
            return Strings.isNullOrEmpty(val) ? null : Jackson.isJSON(val) ? Jackson.json2Object(val,Integer[].class) :Splitter.on(",").splitToStream(val).map(v-> Long.valueOf(v)).toArray(Integer[]::new);
        });
        jdbi.registerColumnMapper(new GenericType<Long[]>(){},(r, columnNumber, ctx) -> {
            String val = r.getString(columnNumber);
            return Strings.isNullOrEmpty(val) ? null : Jackson.isJSON(val) ? Jackson.json2Object(val,Long[].class) :Splitter.on(",").splitToStream(val).map(v-> Long.valueOf(v)).toArray(Long[]::new);
        });

        jdbi.registerArgument(new AbstractArgumentFactory<List<IdName>>(Types.VARCHAR) {
            @Override
            protected Argument build(List<IdName> val, ConfigRegistry config) {
                return (position, statement, ctx) -> statement.setString(position, Jackson.object2Json(val));
            }
        });
        jdbi.registerArgument(new AbstractArgumentFactory<List<Map<String, Object>>>(Types.VARCHAR) {
            @Override
            protected Argument build(List<Map<String, Object>> val, ConfigRegistry config) {
                return (position, statement, ctx) -> statement.setString(position, Jackson.object2Json(val));
            }
        });
        jdbi.registerArgument(new AbstractArgumentFactory<Map<String, Object>>(Types.VARCHAR) {
            @Override
            protected Argument build(Map<String, Object> val, ConfigRegistry config) {
                return (position, statement, ctx) -> statement.setString(position, Jackson.object2Json(val));
            }
        });
        jdbi.registerArgument(new AbstractArgumentFactory<int[]>(Types.VARCHAR) {
            @Override
            protected Argument build(int[] value, ConfigRegistry config) {
                return (position, statement, ctx) -> statement.setString(position, Jackson.object2Json(value));
            }
        });
        jdbi.registerArgument(new AbstractArgumentFactory<Integer[]>(Types.VARCHAR) {
            @Override
            protected Argument build(Integer[] value, ConfigRegistry config) {
                return (position, statement, ctx) -> statement.setString(position,Jackson.object2Json(value));
            }
        });
        jdbi.registerArgument(new AbstractArgumentFactory<Long[]>(Types.VARCHAR) {
            @Override
            protected Argument build(Long[] value, ConfigRegistry config) {
                return (position, statement, ctx) -> statement.setString(position,Jackson.object2Json(value));
            }
        });
        jdbi.registerArgument(new AbstractArgumentFactory<String[]>(Types.VARCHAR) {
            @Override
            protected Argument build(String[] value, ConfigRegistry config) {
                return (position, statement, ctx) -> statement.setString(position, Jackson.object2Json(value));
            }
        });
        jdbi.registerArgument(new AbstractArgumentFactory<List<Integer>>(Types.VARCHAR) {
            @Override
            protected Argument build(List<Integer> value, ConfigRegistry config) {
                return (position, statement, ctx) -> statement.setString(position,Jackson.object2Json(value));
            }
        });
        jdbi.registerArgument(new AbstractArgumentFactory<List<Long>>(Types.VARCHAR) {
            @Override
            protected Argument build(List<Long> value, ConfigRegistry config) {
                return (position, statement, ctx) -> statement.setString(position, Jackson.object2Json(value));
            }
        });

        jdbi.registerArgument(new AbstractArgumentFactory<List<String>>(Types.VARCHAR) {
            @Override
            protected Argument build(List<String> value, ConfigRegistry config) {
                return (position, statement, ctx) -> statement.setString(position,Jackson.object2Json(value));
            }
        });
    }
    private void dynamicClass(Env env,JdbcConfig mainEnv){
        try {
            env.logger().info("JDBI Pagination bytecode write");
            env.logger().info("Database is {}",mainEnv.getDbType().name());
            String dsType = System.getProperty("dsType","hikari");
            ClassPool cp = ClassPool.getDefault();
            cp.insertClassPath(new ClassClassPath(this.getClass()));
            String cls = "io.github.buzzxu.spuddy.dal.jdbi.JdbiUtil";
            CtClass cc=cp.get(cls);
            CtMethod cm=cc.getDeclaredMethod("getRawSql");
            String body;
            switch (mainEnv.getDbType()){
                case MYSQL:{
                    body = """
                                {
                                    Object obj = org.apache.commons.lang3.reflect.FieldUtils.readField($1,"delegate",true);
                                    if (obj instanceof com.mysql.cj.jdbc.ClientPreparedStatement){
                                        com.mysql.cj.jdbc.ClientPreparedStatement ps = (com.mysql.cj.jdbc.ClientPreparedStatement)obj;
                                        return ((com.mysql.cj.PreparedQuery)org.apache.commons.lang3.reflect.FieldUtils.readField(ps,"query",true)).asSql();
                                    }else{
                                        com.mysql.cj.jdbc.ServerPreparedStatement ps = (com.mysql.cj.jdbc.ServerPreparedStatement)obj;
                                        return ((com.mysql.cj.PreparedQuery)ps.getQuery()).asSql();
                                    }
                                }
                                """;
                    break;
                }
                case POSTGRESQL: {
                    body = """
                            {
                                Object obj = org.apache.commons.lang3.reflect.FieldUtils.readField($1, "delegate", true);
                                if (obj instanceof org.postgresql.jdbc.PgPreparedStatement) {
                                    org.postgresql.jdbc.PgPreparedStatement ps = (org.postgresql.jdbc.PgPreparedStatement) obj;
                                    return ps.toString();
                                } else {
                                    throw new UnsupportedOperationException("Unsupported PreparedStatement type for PostgreSQL");
                                }
                            }
                            """;
                    break;
                }
                default:
                    body = "return ((com.mysql.cj.PreparedQuery)((com.mysql.cj.jdbc.ServerPreparedStatement) org.apache.commons.lang3.reflect.FieldUtils.readField($1,\"delegate\",true)).getQuery()).asSql();";
            }
            cm.setBody(body);
            cc.toClass();
            env.logger().info("Override getRawSql method of {} using javassit",cls);
        }catch (Exception ex){
            throw new StartupException(ex);
        }

    }
}
