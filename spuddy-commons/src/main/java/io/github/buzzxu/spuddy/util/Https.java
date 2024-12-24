package io.github.buzzxu.spuddy.util;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author xux
 * @date 2022年12月07日 10:46:50
 */
@Slf4j
public class Https {
    private static final String DISPOSITION_FORMAT = "attachment; filename=\"%s\"; filename*=utf-8''%s";
    public static String filename(String filename){
        if(!Strings.isNullOrEmpty(filename)){
            try {
                filename = URLEncoder.encode(filename, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.error("不支持的编码",e);
            }
        }
        return String.format(DISPOSITION_FORMAT, filename, filename);
    }
}
