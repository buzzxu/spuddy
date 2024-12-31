package io.github.buzzxu.spuddy.security.jwt.simple;

import io.github.buzzxu.spuddy.errors.SecurityException;
import io.github.buzzxu.spuddy.security.annotation.RequiresUser;
import io.github.buzzxu.spuddy.security.jwt.handler.RequireUserHandler;
import io.github.buzzxu.spuddy.security.objects.UserInfo;
import io.github.buzzxu.spuddy.security.UserInfoService;

/**
 * @author 徐翔
 * @create 2021-08-26 10:35
 **/
public abstract class RequiresGuestXHandler<U extends UserInfo> extends RequireUserHandler<U> {

    public RequiresGuestXHandler(UserInfoService<U> userInfoService) {
        super(userInfoService);
    }

    @Override
    public boolean requires(String token) throws SecurityException {
        allowGuest(token);
        return true;
    }
    public boolean requires(String token, RequiresUser requiresUser) throws SecurityException{
        if (requiresUser != null){
            requiresUser(token);
            return true;
        }
        allowGuest(token);
        return true;
    }

}
