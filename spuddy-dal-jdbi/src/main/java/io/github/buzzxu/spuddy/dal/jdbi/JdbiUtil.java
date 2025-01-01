package io.github.buzzxu.spuddy.dal.jdbi;

import com.google.common.collect.Sets;
import io.github.buzzxu.spuddy.objects.Pager;
import io.github.buzzxu.spuddy.db.Pagination;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.sql.PreparedStatement;
import java.util.Set;

public class JdbiUtil {
    private static final Set<Handle> TRANSACTIONAL_HANDLES = Sets.newHashSetWithExpectedSize(5);

    private JdbiUtil() {
        throw new UnsupportedOperationException("utility class");
    }

    /**
     * Obtain a Handle instance, either the transactionally bound one if we are in a transaction,
     * or a new one otherwise.
     * @param jdbi the Jdbi instance from which to obtain the handle
     *
     * @return the Handle instance
     */
    public static Handle handle(Jdbi jdbi) {
        Handle bound = (Handle) TransactionSynchronizationManager.getResource(jdbi);
        if (bound == null) {
            bound = jdbi.open();
            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                TransactionSynchronizationManager.bindResource(jdbi, bound);
                TransactionSynchronizationManager.registerSynchronization(new Adapter(jdbi, bound));
                TRANSACTIONAL_HANDLES.add(bound);
            }
        }
        return bound;
    }

    /**
     * Close a handle if it is not transactionally bound, otherwise no-op
     * @param handle the handle to consider closing
     */
    public static void closeIfNeeded(Handle handle) {
        if (!TRANSACTIONAL_HANDLES.contains(handle)) {
            handle.close();
        }
    }

    private static class Adapter implements TransactionSynchronization {
        private final Jdbi db;
        private final Handle handle;

        Adapter(Jdbi db, Handle handle) {
            this.db = db;
            this.handle = handle;
        }

        @Override
        public void resume() {
            TransactionSynchronizationManager.bindResource(db, handle);
        }

        @Override
        public void suspend() {
            TransactionSynchronizationManager.unbindResource(db);
        }

        @Override
        public void beforeCompletion() {
            TRANSACTIONAL_HANDLES.remove(handle);
            TransactionSynchronizationManager.unbindResource(db);
        }
    }

    public static String getRawSql(PreparedStatement stmt){
        return stmt.toString();
    }

    public static <T> String DefalutPageineRawSql(String sql, Pager<T> pager){
        return DefalutPageineRawSql(sql,pager.getPageNumber(),pager.getPageSize());
    }

    public static String DefalutPageineRawSql(String sql,int number,int size){
        return Pagination.rewritePageSql(sql,number,size);
    }
}
