package io.github.buzzxu.spuddy.security.boss.controllers;


import io.github.buzzxu.spuddy.R;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.lang.ShiroException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * @author 徐翔
 * @program poster-project
 * @description ${DESCRIPTION}
 * @create 2018-11-23 14:26
 * @see
 **/
@RestController
public class ShiroExceptionController {

    @RequestMapping("/boss/error")
    @ResponseBody
    public Object error(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");
        if(exception != null){
            Throwable cause = exception.getCause();
            if (cause instanceof AuthenticationException) {
                response.setStatus(FORBIDDEN.value());
                return new ResponseEntity(FORBIDDEN);
            }else if (cause instanceof UnauthenticatedException) {
                response.setStatus(UNAUTHORIZED.value());
                return new ResponseEntity(UNAUTHORIZED);
            }else if (cause instanceof ShiroException) {
                response.setStatus(FORBIDDEN.value());
                return new ResponseEntity(FORBIDDEN);
            }
        }
        return R.error((Integer)request.getAttribute("javax.servlet.error.status_code"),(String)request.getAttribute("javax.servlet.error.message"));
    }
}
