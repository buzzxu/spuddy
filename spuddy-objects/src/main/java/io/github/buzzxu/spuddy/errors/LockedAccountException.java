package io.github.buzzxu.spuddy.errors;

import java.io.Serial;

/**
 * @author xux
 * @date 2018/5/23 下午3:51
 */
public class LockedAccountException extends Exception {
    @Serial
    private static final long serialVersionUID = 6066986323367447825L;

    public LockedAccountException() {
        super("您的账户已被禁用,请联系客服");
    }
    public LockedAccountException(String msg) {
        super(msg);
    }

}
