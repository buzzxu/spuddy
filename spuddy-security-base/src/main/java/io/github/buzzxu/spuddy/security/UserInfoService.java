package io.github.buzzxu.spuddy.security;



import io.github.buzzxu.spuddy.security.objects.UserInfo;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;



/**
 * @author 徐翔
 * @create 2021-08-26 10:09
 **/
public interface UserInfoService<U extends UserInfo> {

    Integer type(long id);
    Optional<String> password(long userId, int type);

    default Optional<String> openId(long userId){
        return Optional.empty();
    }

    default  U of(long id,Class<U> clazz){
        return of(id,type(id),clazz);
    }
     U of(long id, int type, Class<U> clazz);
     U of(long userId, int type, Supplier<U> supplier, Class<U> clazz);

    default  UserInfo convert(Map<String,String> user, int type, Class<U> clazz){
        return UserInfo.from(user,clazz);
    }
}
