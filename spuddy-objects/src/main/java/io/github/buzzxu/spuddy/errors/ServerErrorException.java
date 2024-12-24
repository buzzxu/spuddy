package io.github.buzzxu.spuddy.errors;

import io.github.buzzxu.spuddy.exceptions.ApplicationException;

import java.io.Serial;


/**
 * @program: 
 * @description: 服务异常 禁止向客户端抛出异常信息
 * @author: 徐翔
 * @create: 2018-07-03 09:39
 **/
public class ServerErrorException extends ApplicationException {
    @Serial
    private static final long serialVersionUID = -428983371141527953L;

    public ServerErrorException(String message, Throwable cause) {
        super(  message,cause);
    }
    public ServerErrorException(Throwable cause) {
        super(  cause);
    }
}
