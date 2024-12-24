package io.github.buzzxu.spuddy.jackson.conv.desensitization;


import io.github.buzzxu.spuddy.jackson.conv.JsonConvert;
import io.github.buzzxu.spuddy.util.Replaces;

/**
 * 手机号脱敏
 * @author xux
 * @date 2023年04月02日 16:48:34
 */
public class MobileDesensitization implements JsonConvert<String> {
    @Override
    public String convert(String target) {
        return Replaces.mobile(target);
    }
}
