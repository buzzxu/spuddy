package io.github.buzzxu.spuddy.errors;


/**
 * Created by xux on 2017/4/23.
 */
public enum ErrorCodes implements ErrorCode {

    BAD_REQUEST(400, "请求的参数个数或格式不符合要求"),
    UNAUTHORIZED(401, "无权访问"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "请求的资源不存在"),
    METHOD_NOT_ALLOWED(405, "不允许的请求方法"),
    NOT_ACCEPTABLE(406, "不接受的请求"),
    CONFLICT(409, "资源冲突"),
    UNSUPPORTED_MEDIA_TYPE(415, "不支持的Media Type"),
    INTERNAL_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),
    GATEWAY_TIMEOUT(504, "请求服务超时"),
    COMMOND_ERROR(500, "服务器内部错误"),
    USER_NOTIFY(1100, "通知用户发生错误"),
    BAD_RPC(2000, "RPC调用失败"),
    THIRDPARTY_REQUEST(2100, "三方接口请求失败"),
    THIRDPARTY_RETURN(2101, "三方接口返回异常"),
    THIRDPARTY_SMS(2102, "短信发送失败"),
    TOKEN(10001, "获取Token失败");

    private final int status;

    private final String message;


    ErrorCodes(int status,String message){
        this.status = status;
        this.message = message;
    }

    public static ErrorCodes httpStatus(int httpStatus) {
        for(ErrorCodes errorCode : values()) {
            if(errorCode.status() == httpStatus) {
                return errorCode;
            }
        }
        return INTERNAL_ERROR;
    }

    @Override
    public String code() {
        return this.name();
    }

    @Override
    public int status() {
        return status;
    }

    @Override
    public String message() {
        return message;
    }
}
