package io.github.buzzxu.spuddy.security.exceptions;

import io.github.buzzxu.spuddy.errors.SecurityException;

/**
 * @author 徐翔
 * @program yuanmai-platform
 * @description ${DESCRIPTION}
 * @create 2018-11-29 11:33
 * @see
 **/
@SuppressWarnings("serial")
public class UnknownAccountException extends SecurityException {
    public UnknownAccountException() {
        super("未知账户,无法获取用户信息,登录失败", 400);
    }

    public UnknownAccountException(long userId) {
        super(String.format("未知账户,无法获取用户信息,登录失败.[code=%d]", userId), 400);
    }
}
