package io.github.buzzxu.spuddy.security.controllers;

import io.github.buzzxu.spuddy.security.AccountService;
import io.github.buzzxu.spuddy.security.UserInfoService;
import io.github.buzzxu.spuddy.security.objects.UserInfo;
import jakarta.annotation.Resource;

/**
 * @author xux
 * @date 2024年12月28日 22:35:47
 */
public abstract class AbstractLoginController<U extends UserInfo> {
    @Resource(type = AccountService.class)
    protected AccountService accountService;
    @Resource(type = UserInfoService.class)
    protected UserInfoService<U> userInfoService;


    public abstract  <U extends UserInfo> U userInfo();
}
