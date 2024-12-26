package io.github.buzzxu.spuddy.security.exceptions;

/**
 * @author: xux
 * @description: 密码为NULL异常
 * @date: 2021/7/27 12:21 上午
 */
public class PasswordNullException extends IllegalArgumentException {
    public PasswordNullException() {
        super("密码未设置,请先修改密码");
    }
}
