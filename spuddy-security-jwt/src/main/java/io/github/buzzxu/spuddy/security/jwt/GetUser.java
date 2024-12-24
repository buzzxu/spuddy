package io.github.buzzxu.spuddy.security.jwt;

import io.github.buzzxu.spuddy.errors.SecurityException;
import io.github.buzzxu.spuddy.security.objects.UserInfo;

import java.util.Optional;

/**
 * @author 徐翔
 * @create 2021-08-26 10:03
 **/
public class GetUser {
    private static final ThreadLocal<UserInfo> thread = new ThreadLocal<>();

    public static <U extends UserInfo> void set(U user){
        thread.set(user);
    }

    public static <U extends UserInfo> Optional<U> ofNullable(){
        return Optional.ofNullable((U)thread.get());
    }

    public static <U extends UserInfo> U of() throws SecurityException{
        UserInfo user;
        if((user = thread.get()) != null){
            return (U) user;
        }
        throw new SecurityException("无法获取用户信息",401);
    }

    public static void clear() {
        thread.remove();
    }
}
