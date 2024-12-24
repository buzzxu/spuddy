package io.github.buzzxu.spuddy.security.services;



import io.github.buzzxu.spuddy.security.objects.UserInfo;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;



/**
 * @author 徐翔
 * @create 2021-08-26 10:09
 **/
public interface UserInfoService {

    Optional<String> password(long userId, int type);

    default Optional<String> openId(long userId){
        return Optional.empty();
    }

    <U extends UserInfo> U of(long id, int type, Class<U> clazz);
    <U extends UserInfo> U of(long userId, int type, Supplier<U> supplier, Class<U> clazz);

    default <U extends UserInfo> UserInfo convert(Map<String,String> user, int type, Class<U> clazz){
        return UserInfo.from(user,clazz);
    }
}
