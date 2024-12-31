package io.github.buzzxu.spuddy.security.boss.controllers.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

/**
 * @program:
 * @description: 登录成功返回
 * @author: 徐翔
 * @create: 2019-12-22 17:39
 **/
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {
    /**
     * 用户名
     */
    private String userName;
    /**
     * 授权TOKEN
     */
    private String token;
    /**
     * 是否首次登录
     */
    private boolean firstLogin;
}
