package io.github.buzzxu.spuddy.security;

import java.nio.charset.StandardCharsets;

public enum KEY {


    USER_INFO("user:%d"),
    USER_TOKEN("token:%d:%d"), //tolen:用户ID:登录区域
    USER_ROLE("user:role:%d"),
    USER_PERM("user:perm:%d"),
    USER_PERM_TYPE("user:perm:%d:%d"),
    ROLE_PERM("role:perm:%d"),
    ROLE_PERM_TYPE("role:perm:%d:%d"),
    USER_PVG("user:pvg:%d"),
    USER_AUTH("shiro:authorization:%d"),
    ;
    private final String key;

    KEY(String key) {
        this.key = key;
    }


    public String to(Object... args) {
        return String.format(key, args);
    }

    public byte[] bytes(Object... args) {
        return to(args).getBytes(StandardCharsets.UTF_8);
    }
}
