package io.github.buzzxu.spuddy.security.jwt;

import com.google.common.base.Strings;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import static io.github.buzzxu.spuddy.security.handler.RequiresHandler.KEY;


/**
 * @author 徐翔
 * @create 2021-08-26 9:58
 **/
public class GetToken {

    public static String of(HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader(KEY);
        // 先从Header里面获取
        if(Strings.isNullOrEmpty(token)){
            // 获取不到再从Parameter中拿
            token = httpRequest.getParameter(KEY);
            // 还是获取不到再从Cookie中拿
            if(Strings.isNullOrEmpty(token)){
                Cookie[] cookies = httpRequest.getCookies();
                if(cookies != null){
                    for (Cookie cookie : cookies) {
                        if(KEY.equals(cookie.getName())){
                            token = cookie.getValue();
                            break;
                        }
                    }
                }
            }
        }
        return token;
    }
}
