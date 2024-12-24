package io.github.buzzxu.spuddy.jackson.conv;


import io.github.buzzxu.spuddy.util.Replaces;

/**
 * @author xux
 * @date 2023年04月02日 21:04:26
 */
public class BlankStrConvert implements JsonConvert<String>{
    @Override
    public String convert(String target) {
        return Replaces.blank(target);
    }
}
