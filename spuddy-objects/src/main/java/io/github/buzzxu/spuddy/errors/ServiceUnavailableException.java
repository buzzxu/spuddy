package io.github.buzzxu.spuddy.errors;

import io.github.buzzxu.spuddy.exceptions.ApplicationException;

import static io.github.buzzxu.spuddy.errors.ErrorCodes.SERVICE_UNAVAILABLE;

/**
 * Created by xux on 2017/4/23.
 */
public class ServiceUnavailableException extends ApplicationException {


    private static final long serialVersionUID = 1244376675324590537L;

    public ServiceUnavailableException(String message){
        super(SERVICE_UNAVAILABLE.code(), " 服务不可用: " + message,SERVICE_UNAVAILABLE.status());
    }
}
