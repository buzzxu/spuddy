package io.github.buzzxu.spuddy.security.exceptions;

import io.github.buzzxu.spuddy.exceptions.ApplicationException;

/**
 * @author xux
 * @date 2024年12月28日 22:34:41
 */
public class ValiCodeException extends ApplicationException {
    public ValiCodeException() {
        super("error-valiCode", "您输入的验证码有误,请重新输入", 3001);
    }
}
