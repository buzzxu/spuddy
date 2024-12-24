package io.github.buzzxu.spuddy.exceptions;

import io.github.buzzxu.spuddy.errors.ErrorCodes;

import java.io.Serial;


/**
 * Created by xux on 2016/4/9.
 */
public class HtmlException extends ApplicationException {

    @Serial
    private static final long serialVersionUID = 6998134799135978720L;

    public HtmlException() {
        super(ErrorCodes.INTERNAL_ERROR.code(), ErrorCodes.INTERNAL_ERROR.message(), ErrorCodes.INTERNAL_ERROR.status());
    }

    public HtmlException(int status) {
        super(ErrorCodes.httpStatus(status));
    }

    public HtmlException(Throwable cause) {
        super(cause);
    }


    public HtmlException(String message, Throwable cause) {
        super(message, cause);
    }

    public HtmlException(String message, int status) {
        super(message, status);
    }
    public HtmlException(String message, Throwable cause, int status) {
        super(message, cause, status);
    }

    public static HtmlException raise(String message, int status) {
        return new HtmlException(message,status);
    }
    public static HtmlException raise(String message, int status, Throwable cause) {
        return new HtmlException(message, cause,status);
    }
    public static HtmlException notFound() {
        return new HtmlException(ErrorCodes.NOT_FOUND.status());
    }

    public int status(){
        return status;
    }
}
