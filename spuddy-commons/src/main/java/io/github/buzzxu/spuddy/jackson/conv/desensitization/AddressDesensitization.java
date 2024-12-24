package io.github.buzzxu.spuddy.jackson.conv.desensitization;


import io.github.buzzxu.spuddy.jackson.conv.JsonConvert;
import io.github.buzzxu.spuddy.util.Replaces;

/**
 * @author xux
 * @date 2023年04月02日 21:05:56
 */
public class AddressDesensitization implements JsonConvert<String> {
    @Override
    public String convert(String target) {
        return Replaces.address(target);
    }
}
