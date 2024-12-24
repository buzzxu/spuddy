package io.github.buzzxu.spuddy.security.jwt.simple;

import com.google.common.base.Strings;
import io.github.buzzxu.spuddy.errors.SecurityException;
import io.github.buzzxu.spuddy.security.jwt.handler.RequireUserHandler;
import io.github.buzzxu.spuddy.security.objects.UserInfo;
import io.github.buzzxu.spuddy.security.services.UserInfoService;

import java.util.Optional;

/**
 * @program: shxmao-platform
 * @description:
 * @author: xux
 * @create: 2021-09-26 10:58
 **/
public abstract class RequiresUserPasswordHandler <U extends UserInfo> extends RequireUserHandler<U> {

    public RequiresUserPasswordHandler(UserInfoService userInfoService) {
        super(userInfoService);
    }

    @Override
    protected String jwtSecretKey(Optional<U> userInfo, int type) {
        U user = userInfo.get();
        String password = user.getPsword();
        if(Strings.isNullOrEmpty(password)){
            password = userInfoService.password(user.getId(),type).get();
        }
        return password;
    }

    @Override
    public boolean requires(String token) throws SecurityException {
        requiresUser(token);
        return true;
    }
}
