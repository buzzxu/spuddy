package io.github.buzzxu.spuddy.security;

import io.github.buzzxu.spuddy.errors.LockedAccountException;
import io.github.buzzxu.spuddy.exceptions.ApplicationException;
import io.github.buzzxu.spuddy.objects.Pair;
import io.github.buzzxu.spuddy.security.funs.FunctionThrowsSecurity;
import io.github.buzzxu.spuddy.security.objects.UserInfo;

/**
 * @author xux
 * @date 2024年12月28日 22:04:00
 */
public interface AccountService {
    String KEY_VALICODE_QUICK_REGISTER = "valicode:register:quick:%s";


    /**
     * 用户名密码登录
     * @param userName
     * @param password
     * @param region
     * @param function
     * @return
     * @param <U>
     * @throws SecurityException
     */
    default <U extends UserInfo> Pair<String,U> login(String userName, String password, int region,FunctionThrowsSecurity<Long,U> function) throws SecurityException,LockedAccountException{
        return login(userName, password, region, "zh_CN",function);
    }

    /**
     * 用户名密码登录
     * @param userName
     * @param password
     * @param function
     * @param <U>
     * @return
     * @throws SecurityException
     */
    <U extends UserInfo> Pair<String,U> login(String userName, String password, int region,String lang,FunctionThrowsSecurity<Long,U> function) throws SecurityException,LockedAccountException;


    /**
     * 登录 验证码
     * @param userName
     * @param password
     * @param valiCodeKey
     * @param valiCode
     * @param function
     * @param <U>
     * @return
     * @throws SecurityException
     */
    <U extends UserInfo> Pair<String,U> login(String userName, String password, String valiCodeKey, String valiCode, int region,String lang,FunctionThrowsSecurity<Long,U> function) throws SecurityException, LockedAccountException ;


    /**
     * 注销 需要删除Token
     * @param userId
     */
    default void logOut(long userId){
        String token = GetMeToken.of();
        logOut(userId,token);
    }

    /**
     * 删除 注销token
     * @param userId
     * @param token
     */
    void logOut(long userId,String token);


    /**
     * 修改密码
     * @param userId
     * @param oldPassword
     * @param newPassword
     * @param function
     * @param <U>
     * @return
     * @throws LockedAccountException
     */
    <U extends UserInfo> Pair<String,U> changePassword(long userId, String oldPassword, String newPassword, FunctionThrowsSecurity<Long,U> function)throws LockedAccountException;

    /**
     * 修改密码
     * @param userId
     * @param oldPassword
     * @param password
     * @return
     * @throws LockedAccountException
     */
    boolean changePassword(long userId,String oldPassword, String password) throws LockedAccountException;
    /**
     * 生成token
     * @param userId
     * @param function
     * @param <U>
     * @return
     * @throws ApplicationException
     */
    default <U extends UserInfo> Pair<String,U> generatorToken(Long userId, int region,FunctionThrowsSecurity<Long,U> function)throws ApplicationException {
        return generatorToken(function.apply(userId),region);
    }

    /**
     * 生成token
     * @param user
     * @param <U>
     * @return
     */
    <U extends UserInfo> Pair<String,U> generatorToken(U user,int region);

    /**
     * 存储Token
     * @param userId
     * @param token
     * @return
     */
    boolean storeToken(long userId,String token,int region);
}
