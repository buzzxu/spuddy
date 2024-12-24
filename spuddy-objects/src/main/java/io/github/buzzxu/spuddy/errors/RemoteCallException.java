package io.github.buzzxu.spuddy.errors;

import io.github.buzzxu.spuddy.exceptions.ApplicationException;

/**
 * Created by xux on 2017/4/23.
 */
public class RemoteCallException extends ApplicationException {

    private static final long serialVersionUID = 2637732694873125447L;
    private ErrorMessage originError;

    public RemoteCallException(ErrorMessage error, int httpStatus) {
        super(error.getCode(),"调用远程服务异常, cause: " + error.getMessage(), httpStatus);
    }
}
