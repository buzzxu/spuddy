package io.github.buzzxu.spuddy.util;

import com.google.common.base.Strings;

import static com.google.common.base.Preconditions.checkArgument;
import static io.github.buzzxu.spuddy.util.Files.content;

/**
 * @author xux
 * @date 2018/6/27 上午10:36
 */
public class Scripts {

    /**
     * return redis script String
     *
     * @param path
     * @return
     */
    public static String getScript(String path) {
        checkArgument(!Strings.isNullOrEmpty(path),"param `path` not be null");
        return content("scripts/"+path.trim());
    }

    public static byte[] getScript(byte[] path) {
        return getScript("scripts/" + path).getBytes();
    }
}
