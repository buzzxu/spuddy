package io.github.buzzxu.spuddy.exceptions;

import io.github.buzzxu.spuddy.errors.ErrorCode;
import io.github.buzzxu.spuddy.errors.ErrorCodes;
import io.github.buzzxu.spuddy.errors.NofityUserException;
import io.github.buzzxu.spuddy.errors.NotFoundException;

import java.io.Serial;
import java.sql.SQLException;

/**
 * Created by xux on 2017/4/23.
 */
public class ApplicationException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 5585426712416537603L;


    protected String code;
    protected int status;
    protected Object target;

    public ApplicationException(String code, String message, int status) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public ApplicationException(Throwable cause) {
        super(cause);
        this.status = ErrorCodes.INTERNAL_ERROR.status();
        this.code = ErrorCodes.INTERNAL_ERROR.code();
    }

    public ApplicationException(ErrorCodes err) {
        super(err.message());
        this.status = err.status();
        this.code = err.code();
    }

    public ApplicationException(String message, int status) {
        super(message);
        this.status = status;
        this.code = ErrorCodes.httpStatus(status).code();
    }

    public ApplicationException(ErrorCode errorCode, Throwable cause) {
        this(errorCode, errorCode.message(), cause);
    }

    public ApplicationException(String message, Throwable cause, int status) {
        super(message, cause);
        this.status = status;
        this.code = ErrorCodes.httpStatus(status).code();
    }

    public ApplicationException(String message, Throwable cause) {
        this(ErrorCodes.INTERNAL_ERROR, message, cause);
    }

    private ApplicationException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.code = errorCode.code();
        this.status = errorCode.status();
    }

    private ApplicationException(ErrorCode errorCode) {
        this(errorCode.code(),errorCode.message(),errorCode.status());
    }
    private ApplicationException(String code,String message, int status,Throwable cause) {
        super(message, cause);
        this.code = code;
        this.status = status;
    }

    public static ApplicationException raise(String message) {
        return new ApplicationException(ErrorCodes.INTERNAL_ERROR.code(), message, ErrorCodes.INTERNAL_ERROR.status());
    }
    public static ApplicationException raise(String message,Object... args) {
        return new ApplicationException(ErrorCodes.BAD_REQUEST.code(), String.format(message, args), ErrorCodes.BAD_REQUEST.status());
    }
    public static IllegalArgumentException argument(String message,Object... args){
        return new IllegalArgumentException(String.format(message, args));
    }

    public static ApplicationException raise(String code,String message,int status) {
        return new ApplicationException(code,message,status);
    }
    public static ApplicationException raise(Throwable cause) {
        return new ApplicationException(ErrorCodes.INTERNAL_ERROR, cause);
    }
    public static ApplicationException raise(String message,ErrorCode errorCode,Throwable cause) {
        return new ApplicationException(errorCode.code(),message,errorCode.status(),cause);
    }
    public static ApplicationException raise(String message,Throwable cause) {
        return new ApplicationException(message,cause);
    }
    public static ApplicationException raise(Throwable cause,String message,Object args) {
        return new ApplicationException(message.formatted(args),cause);
    }
    public static ApplicationException raise(String code,String message,int status, Throwable cause) {
        return new ApplicationException(code,message,status, cause);
    }
    public static ApplicationException raise(ErrorCode errorCode) {
        return new ApplicationException(errorCode);
    }

    public static ApplicationException raise(SQLException ex){
        return new io.github.buzzxu.spuddy.exceptions.SQLException("数据库操作异常:"+ex.getMessage(),ex);
    }


    /**
     * 通知用户
     * @param message
     * @return
     */
    public static NofityUserException notifyUser(String message){
        return new NofityUserException(message);
    }

    public static NofityUserException notifyUser(String message,Object... args){
        return notifyUser(String.format(message, args));
    }
    /**
     * RPC调用时异常
     * @param message
     * @return ApplicationException
     */
    public static ApplicationException rpc(String code,String message,Throwable thr){
        return raise(code, message, ErrorCodes.BAD_RPC.status(), thr);
    }
    public static ApplicationException rpc(Throwable thr){
        return raise(ErrorCodes.BAD_RPC.code(), thr.getMessage(), ErrorCodes.BAD_RPC.status());
    }
    /**
     * 数据不存在
     * @param message
     * @return
     */
    public static ApplicationException notFound(String message){
        return new NotFoundException(message);
    }
    public static ApplicationException notFound(String message,Object... args){
        return notFound(String.format(message,args));
    }


    public int status() {
        return status;
    }

    public String code() {
        return code;
    }

    public Object target() {
        return target;
    }

    public ApplicationException status(int status) {
        this.status = status;
        return this;
    }

    public ApplicationException target(Object target) {
        this.target = target;
        return this;
    }
}
