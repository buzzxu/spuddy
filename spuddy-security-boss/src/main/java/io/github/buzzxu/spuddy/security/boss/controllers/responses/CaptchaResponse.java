package io.github.buzzxu.spuddy.security.boss.controllers.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @program:
 * @description:
 * @author: 徐翔
 * @create: 2019-12-30 00:11
 **/
@AllArgsConstructor
@Getter
@Setter
public class CaptchaResponse {

    private String key;

    private String base64;
}
