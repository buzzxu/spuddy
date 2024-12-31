package io.github.buzzxu.spuddy.security.boss;



import io.github.buzzxu.spuddy.R;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.lang.ShiroException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;


@RestControllerAdvice
public class ShiroExceptionAdvice {



    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(UNAUTHORIZED)
    public R authenticationException(AuthenticationException e) throws Exception {
        return R.error(UNAUTHORIZED.value(),e.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(UNAUTHORIZED)
    public R defaultErrorHandler(UnauthorizedException e) throws Exception {
        return R.error(UNAUTHORIZED.value(),e.getMessage());
    }

    @ExceptionHandler(ShiroException.class)
    @ResponseStatus(UNAUTHORIZED)
    public R ShiroException(ShiroException e) throws Exception {
        return R.error(UNAUTHORIZED.value(),e.getMessage());
    }

}
