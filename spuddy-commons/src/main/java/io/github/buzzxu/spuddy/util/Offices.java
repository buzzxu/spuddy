package io.github.buzzxu.spuddy.util;

import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * Office 操作
 */
public class Offices {

    /**
     * 预览
     * @param src
     * @return
     */
    public static String view(String src)  {
//        return "https://view.officeapps.live.com/op/view.aspx?src="+URLCoder.encode(src);
        return "https://view.officeapps.live.com/op/view.aspx?src="+ URLEncoder.encode(src, Charset.defaultCharset());
    }
}
