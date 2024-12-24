package io.github.buzzxu.spuddy.util;


import java.awt.*;

/**
 * @program: 
 * @description:
 * @author: 徐翔
 * @create: 2018-08-15 21:40
 **/
public class Colors {


    /**
     * RGB 转 颜色代码
     * @param r
     * @param g
     * @param b
     * @return
     */
    public static String toHex(int r, int g, int b) {
        return "#" + toBrowserHexValue(r) + toBrowserHexValue(g)
                + toBrowserHexValue(b);
    }

    /**
     * 颜色代码 转rgb
     * @param colorStr
     * @return
     */
    public static Color hex2Rgb(String colorStr) {
        return new Color(
                Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
                Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
                Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) );
    }

    private static String toBrowserHexValue(int number) {
        StringBuilder builder = new StringBuilder(
                Integer.toHexString(number & 0xff));
        while (builder.length() < 2) {
            builder.append("0");
        }
        return builder.toString().toUpperCase();
    }


}
