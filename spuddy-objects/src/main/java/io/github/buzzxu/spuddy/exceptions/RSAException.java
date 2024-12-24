package io.github.buzzxu.spuddy.exceptions;

import java.io.Serial;

/**
 * @program: 
 * @description:
 * @author: 徐翔
 * @create: 2020-01-20 12:35
 **/
public class RSAException extends Exception {
    @Serial
    private static final long serialVersionUID = -8676019429832607902L;

    public RSAException(Throwable cause) {
        super(cause);
    }
}
