package io.github.buzzxu.spuddy.serialize;

/**
 * @program: 
 * @description: 序列化异常
 * @author: xuxiang
 * @create: 2020-11-25 10:02
 **/
public class SerializerException extends RuntimeException {
    public SerializerException() {
    }

    public SerializerException(String message) {
        super(message);
    }

    public SerializerException(String message, Throwable cause) {
        super(message, cause);
    }

    public SerializerException(Throwable cause) {
        super(cause);
    }
}
