package io.github.buzzxu.spuddy.security.handler;

import io.github.buzzxu.spuddy.errors.SecurityException;
import io.github.buzzxu.spuddy.errors.LockedAccountException;
import io.github.buzzxu.spuddy.security.objects.PrivilegeInfo;
import io.github.buzzxu.spuddy.security.objects.UserInfo;

/**
 * @author xux
 */
public interface SecurityUserHandler<U extends UserInfo> extends RequiresHandler<U> {

    String KEY = "Authorization";

    /**
     * 验证用户信息
     * @param principal
     * @param credentials
     * @return
     * @throws SecurityException
     * @throws LockedAccountException
     */
    U auth(Object principal,Object credentials) throws SecurityException, LockedAccountException;


    PrivilegeInfo pvgInfo(Long userId);


}
