package io.github.buzzxu.spuddy.dal.jdbi;

import org.jdbi.v3.core.statement.SqlLogger;
import org.jdbi.v3.core.statement.StatementContext;
import org.slf4j.Logger;

import java.time.Duration;

/**
 * @program:
 * @description:
 * @author: xuxiang
 * @create: 2020-10-20 21:51
 **/
public class Slf4jSqlLogger implements SqlLogger {

    private final Logger logger;

    public Slf4jSqlLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void logBeforeExecution(StatementContext context) {
        if (context.getStatement() != null) {
            logger.info("sql: {}", JdbiUtil.getRawSql(context.getStatement()));
        }
    }

    @Override
    public void logAfterExecution(StatementContext context) {
        Duration duration = Duration.between(context.getExecutionMoment(), context.getCompletionMoment());
        logger.info("耗时: {} ms, {} s", duration.toMillis(), duration.getSeconds());
    }
}
