package io.github.buzzxu.spuddy.util;

import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * @program: 
 * @description:
 * @author: xuxiang
 * @create: 2020-04-17 22:25
 **/
public final class Replaces {
    private static final Pattern MOBILE = Pattern.compile("(\\d{3})\\d{4}(\\d{4})");
    private static final Pattern NAME = Pattern.compile("(?<=[\\u4e00-\\u9fa5]).*(?=[\\u4e00-\\u9fa5])");
    private static final Pattern BLANK = Pattern.compile("[ `~!@#$%^&*()+=|{}':;',/\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]");

    private static final Pattern NON_NUMBER = Pattern.compile("\\D");
    public static String mobile(String mobile){
        if(Patterns.mobile(mobile)){
            return MOBILE.matcher(mobile).replaceAll("$1****$2").strip();
        }
        return mobile;
    }
    public static String name(String name){
        if(Patterns.name_cn(name)){
            return NAME.matcher(name).replaceAll("*").strip();
        }
        return name;
    }
    public static String nameф(String name) {
        if (!Strings.isNullOrEmpty(name)) {
            char[] chars = name.toCharArray();
            for (int i = 1; i < chars.length; i++) {
                chars[i] = '*';
            }
            return String.valueOf(chars);
        }
        return "";
    }
    /**
     * 特殊字符替换为空白
     * @param str
     * @return
     */
    public static String blank(String str){
        if (Strings.isNullOrEmpty(str)) {
            return str;
        }
        return BLANK.matcher(str).replaceAll("").strip();
    }

    public static String idNumber(String idNumber){
        if (!Strings.isNullOrEmpty(idNumber)) {
            if (idNumber.length() == 15){
                idNumber = idNumber.replaceAll("(\\w{6})\\w*(\\w{3})", "$1******$2");
            }
            if (idNumber.length() == 18){
                idNumber = idNumber.replaceAll("(\\w{6})\\w*(\\w{3})", "$1*********$2");
            }
        }
        return idNumber;
    }
    public static String idNumberん(String idNumber){
        if (!Strings.isNullOrEmpty(idNumber)) {
            return StringUtils.left(idNumber, 6).concat(StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(idNumber, 3), StringUtils.length(idNumber), "*"), "******"));
        }
        return idNumber;
    }
    public static String address(String address){
        if (!Strings.isNullOrEmpty(address)) {
            return StringUtils.left(address, 3).concat(StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(address, address.length()-11), StringUtils.length(address), "*"), "***"));
        }
        return address;
    }

    public static String number(String var){
        if(Strings.isNullOrEmpty(var)){
            return "";
        }
        return  NON_NUMBER.matcher(var).replaceAll("").strip();
    }

    public static String padWithZeros(int number,int length){
        return String.format("%0"+length+"d",number);
    }
}
