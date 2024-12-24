package io.github.buzzxu.spuddy.security;

/**
 * @author 徐翔
 * @create 2021-08-26 10:10
 **/
public enum KEYs {
    USER_INFO("user:%d:%d"),       //用户信息
    USER_TOKEN("token:%d:%d"),     //token
    ;
    private final String key;

    KEYs(String key) {
        this.key = key;
    }

    public String to(Object... args) {
        return String.format(this.key, args);
    }
}
