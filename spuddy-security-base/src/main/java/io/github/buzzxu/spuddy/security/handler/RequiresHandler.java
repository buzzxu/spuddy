package io.github.buzzxu.spuddy.security.handler;


import io.github.buzzxu.spuddy.errors.SecurityException;
import io.github.buzzxu.spuddy.security.objects.UserInfo;

import java.util.Optional;

/**
 * @author 徐翔
 * @create 2021-08-26 10:01
 **/
public interface RequiresHandler<U extends UserInfo>  {

    String KEY = "Authorization";
    /**
     * 获取用户信息
     * @param userId
     * @param type
     * @return
     * @throws SecurityException
     */
    Optional<U> getUser(long userId, int type)throws SecurityException;

    /**
     * 验证Token
     * @param userId
     * @param token
     * @return
     * @throws SecurityException
     */
    boolean test(long userId,String token)throws SecurityException;

    default U anonymous(){
        return (U) UserInfo.anonymous();
    }

    /**
     * 获取用户ID
     * @param token
     * @return
     */
    default Long id(String token){
        throw new UnsupportedOperationException();
    }
}
