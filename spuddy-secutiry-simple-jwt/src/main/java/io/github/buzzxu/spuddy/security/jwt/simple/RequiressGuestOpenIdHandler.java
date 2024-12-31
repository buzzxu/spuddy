package io.github.buzzxu.spuddy.security.jwt.simple;

import com.google.common.base.Strings;
import io.github.buzzxu.spuddy.security.objects.UserInfo;
import io.github.buzzxu.spuddy.security.UserInfoService;

import java.util.Optional;

/**
 * @program: shxmao-platform
 * @description:
 * @author: xux
 * @create: 2021-09-01 10:36
 **/
public abstract class RequiressGuestOpenIdHandler <U extends UserInfo> extends RequiresGuestXHandler<U>{

    public RequiressGuestOpenIdHandler(UserInfoService<U> userInfoService) {
        super(userInfoService);
    }

    @Override
    protected String jwtSecretKey(Optional<U> userInfo, int type) {
        U user = userInfo.get();
        String openId = user.getOpenId();
        if(Strings.isNullOrEmpty(openId)){
            openId = userInfoService.openId(user.getId()).get();
        }
        return openId;
    }
}
