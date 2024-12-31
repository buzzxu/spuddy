package io.github.buzzxu.spuddy.security.boss.controllers.requests;

import lombok.Getter;
import lombok.Setter;

/**
 * @program:
 * @description:
 * @author: 徐翔
 * @create: 2019-12-22 17:39
 **/
@Getter @Setter
public class LoginUserPwdRequest {
    /**
     * 用户名
     */
    private String userName;
    /**
     * 密码
     */
    private String password;
    /**
     * 验证码KEY
     */
    private String key;
    /**
     * 验证码
     */
    private String valiCode;

    private Integer region;

    /**
     * 语言
     */
    private String lang;
}
