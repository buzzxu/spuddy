package io.github.buzzxu.spuddy.security;



import io.github.buzzxu.spuddy.security.objects.UserInfo;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author 徐翔
 * @create 2021-08-26 11:02
 **/
public class SecurityUser {
    private static final ThreadLocal<UserInfo> thread = new ThreadLocal<>();


    public static <U extends UserInfo> void set(U user) {
        thread.set(user);
    }

    @SuppressWarnings("unchecked")
    public static <U extends UserInfo> Optional<U> ofNullable() {
        return Optional.ofNullable((U)thread.get());
    }

    @SuppressWarnings("unchecked")
    public static <U extends UserInfo> U of() throws SecurityException {
        return (U) of(()->UserInfo.anonymous());
    }
    public static <U extends UserInfo> U of(Supplier<U> def) throws SecurityException {
        U user;
        try {
            if((user = (U) thread.get()) != null){
                return  user;
            }
        }catch (ClassCastException ex){
            //noting
        }
        return def.get();
    }
    public static void clear() {
        thread.remove();
    }
}
