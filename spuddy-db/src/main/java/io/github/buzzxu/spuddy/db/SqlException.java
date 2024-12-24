package io.github.buzzxu.spuddy.db;

import java.io.Serial;

public class SqlException extends Exception {

    @Serial
    private static final long serialVersionUID = 5580168382995650662L;

    public SqlException() {
    }

    public SqlException(String message) {
        super(message);
    }

    public SqlException(String message, Throwable cause) {
        super(message, cause);
    }

    public SqlException(Throwable cause) {
        super(cause);
    }
}
