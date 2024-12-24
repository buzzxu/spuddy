package io.github.buzzxu.spuddy.exceptions;

import io.github.buzzxu.spuddy.errors.ErrorCodes;

import java.io.Serial;


/**
 * Created by Administrator on 2016/4/9.
 */
public class JsonException extends ApplicationException {
    @Serial
    private static final long serialVersionUID = -1178003171514631255L;
    protected String requestId;

    protected JsonException() {
        super(ErrorCodes.INTERNAL_ERROR.code(), ErrorCodes.INTERNAL_ERROR.message(), ErrorCodes.INTERNAL_ERROR.status());
    }

    protected JsonException(int status) {
        super(ErrorCodes.httpStatus(status));
    }
    protected JsonException(String message, int status) {
        super(message, status);
        this.status = status;
    }
    protected JsonException(String requestId, String message, int status) {
        super(message, status);
        this.requestId = requestId;
    }

    protected JsonException(Throwable cause, int status) {
        super(cause);
        this.status = status;
        this.code = ErrorCodes.httpStatus(status).code();
    }

    protected JsonException(String message, Throwable cause, int status) {
        super(message, cause, status);
    }

    public JsonException requestId(String requestId){
        this.requestId = requestId;
        return this;
    }

    public static JsonException raise(String message) {
        return new JsonException(message, ErrorCodes.INTERNAL_ERROR.status());
    }
    public static JsonException raise(String message, int status) {
        return new JsonException(message,status);
    }
    public static JsonException raise(String requestId, Throwable cause) {
        return new JsonException(cause, ErrorCodes.INTERNAL_ERROR.status()).requestId(requestId);
    }

    /**
     * RPC调用时异常
     * @param message
     * @return JsonException
     */
    public static JsonException rpc(String message, Throwable thr){
        return raise(message, ErrorCodes.BAD_RPC.status(), thr);
    }
    public static JsonException rpc(Throwable thr){
        return raise(thr.getMessage(), ErrorCodes.BAD_RPC.status());
    }
    /**
     * 数据不存在
     * @param message
     * @return
     */
    public static JsonException notExist(String message){
        return raise(message, ErrorCodes.NOT_FOUND.status());
    }

    public static JsonException raise(Throwable cause) {
        return new JsonException(cause, ErrorCodes.INTERNAL_ERROR.status());
    }

    public static JsonException raise(String message, int status, Throwable cause) {
        return new JsonException(message, cause,status);
    }

    public int status(){
        return status;
    }
    public JsonException status(int status){
        this.status = status;
        return this;
    }
}
