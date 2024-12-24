package io.github.buzzxu.spuddy.util;


import com.google.common.base.Strings;
import com.google.re2j.Pattern;

/**
 * @author xux
 * @date 2018/5/23 上午10:38
 */
public abstract class Patterns {

    /**
     * 正则表达式：固定电话
     */
    private static final Pattern PHONE = Pattern.compile("0\\d{2,3}-\\d{7,8}|\\(?0\\d{2,3}[)-]?\\d{7,8}|\\(?0\\d{2,3}[)-]*\\d{7,8}");
    /**
     * 正则表达式：验证手机号
     * ^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[01235678]|18[0-9]|19[0-9])\d{8}$
     * ^((13[0-9])|(14[5-9])|(15([0-3]|[5-9]))|(16[6-7])|(17[1-8])|(18[0-9])|(19[1|3])|(19[5|6])|(19[8|9]))\d{8}$
     */
    private static final Pattern MOBILE = Pattern.compile("^(13[0-9]|14[579]|15[0-3,5-9]|16[2-7]|17[01235678]|18[0-9]|19[0-9])\\d{8}$");
    /**
     * 正则表达式：验证邮箱
     */
    private static final Pattern EMAILL = Pattern.compile("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
    /**
     * 正则表达式：验证用户名
     */
    private static final Pattern USERNAME = Pattern.compile("^([a-zA-Z]|[0-9])\\w{4,17}$");
    private static final Pattern USERNAME_CN = Pattern.compile("^[(a-zA-Z0-9\u4e00-\u9fa5){1}_#]{2,20}$");
    /**
     * 正则表达式：验证密码
     */
    private static final Pattern PASSWORD = Pattern.compile("^[a-zA-Z0-9]{6,16}$");
    /**
     * 正则表达式：验证密码 纯数字或者纯字母，不通过 ^\S*(?=\S{6,})(?=\S*\d)(?=\S*[A-Z])(?=\S*[a-z])(?=\S*[!@#$%^&*? ])\S*$
     * 强密码正则，最少6位，包括至少1个大写字母，1个小写字母，1个数字，1个特殊字符 /^.*(?=.{6,})(?=.*\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[!@#$%^&*? ]).*$/
     */
    private static final java.util.regex.Pattern PASSWORD_STRONG = java.util.regex.Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?!.*\\s).{6,16}$");
    /**
     * 正则表达式：验证汉字
     * old ^[\u4e00-\u9fa5]*$
     */
    private static final java.util.regex.Pattern CHINESE = java.util.regex.Pattern.compile("^(?:[\\u3400-\\u4DB5\\u4E00-\\u9FEA\\uFA0E\\uFA0F\\uFA11\\uFA13\\uFA14\\uFA1F\\uFA21\\uFA23\\uFA24\\uFA27-\\uFA29]|[\\uD840-\\uD868\\uD86A-\\uD86C\\uD86F-\\uD872\\uD874-\\uD879][\\uDC00-\\uDFFF]|\\uD869[\\uDC00-\\uDED6\\uDF00-\\uDFFF]|\\uD86D[\\uDC00-\\uDF34\\uDF40-\\uDFFF]|\\uD86E[\\uDC00-\\uDC1D\\uDC20-\\uDFFF]|\\uD873[\\uDC00-\\uDEA1\\uDEB0-\\uDFFF]|\\uD87A[\\uDC00-\\uDFE0])+$");
    private static final java.util.regex.Pattern NAME_CHINESE = java.util.regex.Pattern.compile("^(?:[\\u4e00-\\u9fa5·]{2,16})$");
    private static final Pattern NAME_ENGLISH = Pattern.compile("(^[a-zA-Z]{1}[a-zA-Z\\s]{0,20}[a-zA-Z]{1}$)");
    /**
     * 正则表达式：验证身份证
     */
    private static final Pattern ID_CARD = Pattern.compile("^(^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$)|(^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])((\\d{4})|\\d{3}[Xx])$)$");
    /**
     * 正则表达式：验证身份证 15位
     */
    private static final Pattern ID_CARD_15 = Pattern.compile("^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$");
    /**
     * 正则表达式：验证身份证 18位
     */
    private static final Pattern ID_CARD_18 = Pattern.compile("^[1-9]\\d{5}(?:18|19|20)\\d{2}(?:0[1-9]|10|11|12)(?:0[1-9]|[1-2]\\d|30|31)\\d{3}[\\dXx]$");
    /**
     * 护照
     */
    private static final Pattern PASSPORT = Pattern.compile("(^[EeKkGgDdSsPpHh]\\d{8}$)|(^(([Ee][a-fA-F])|([DdSsPp][Ee])|([Kk][Jj])|([Mm][Aa])|(1[45]))\\d{7}$)");
    /**
     * 正则表达式：验证URL   ^(((ht|f)tps?):\/\/)?[\w-]+(\.[\w-]+)+([\w.,@?^=%&:/~+#-]*[\w@?^=%&/~+#-])?$
     */
    private static final Pattern URL = Pattern.compile("^((ht|f)tps?:\\/\\/)?[\\w-]+(\\.[\\w-]+)+:\\d{1,5}\\/?$");
    /**
     * 正则表达式：验证IP地址  (25[0-5]|2[0-4]\d|[0-1]\d{2}|[1-9]?\d)
     */
    private static final Pattern IP = Pattern.compile("^((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)$");

    /**
     * 正则表达式：是否整数正则表达式
     */
    private static final Pattern NUMERIC = Pattern.compile("^[-]?\\d+[.]?\\d*$");
    /**
     * 正则表达式：是否整数正则表达式（可带正负号）
     */
    private static final Pattern NUMBERSIGN = Pattern.compile("^[+-]?[0-9]+$");

    /**
     * 正则表达式：是否是浮点数
     */
    private static final Pattern DECIMAL = Pattern.compile("^[+-]?[0-9]+$");

    /**
     * 正则表达式：是否是浮点数正则表达式(可带正负号)   ^[+-]?[0-9]+[.]?[0-9]+$
     */
    private static final Pattern DECIMALSIGN = Pattern.compile("^[+-]?\\d+(\\.\\d+)?$");
    /**
     * 正则表达式：是否为纯字符
     */
    private static final Pattern LETTER = Pattern.compile("^[A-Za-z]+$");
    /**
     * 数字或字符
     */
    private static final Pattern ALPHANUMERIC = Pattern.compile("^[0-9a-zA-Z]+$");
    /**
     * 经度
     */
    private static final Pattern LONGITUDE = Pattern.compile("^(\\-|\\+)?(((\\d|[1-9]\\d|1[0-7]\\d|0{1,3})\\.\\d{0,6})|(\\d|[1-9]\\d|1[0-7]\\d|0{1,3})|180\\.0{0,6}|180)$");
    /**
     * 纬度
     */
    private static final Pattern LATITUDE = Pattern.compile("^(\\-|\\+)?([0-8]?\\d{1}\\.\\d{0,6}|90\\.0{0,6}|[0-8]?\\d{1}|90)$");
    /**
     * 车牌号
     */
    private static final Pattern CAR = Pattern.compile("^(?:[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领 A-Z]{1}[A-HJ-NP-Z]{1}(?:(?:[0-9]{5}[DF])|(?:[DF](?:[A-HJ-NP-Z0-9])[0-9]{4})))|(?:[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领 A-Z]{1}[A-Z]{1}[A-HJ-NP-Z0-9]{4}[A-HJ-NP-Z0-9 挂学警港澳]{1})$");
    /**
     * 统一社会信用代码   ^([0-9ABCDEFGHJKLMNPQRTUWXY]{2})(\d{6})([0-9ABCDEFGHJKLMNPQRTUWXY]{9})([0-9ABCDEFGHJKLMNPQRTUWXY])$
     */
    private static final Pattern ORG = Pattern.compile("^[0-9A-HJ-NPQRTUWXY]{2}\\d{6}[0-9A-HJ-NPQRTUWXY]{10}$");
    private static final java.util.regex.Pattern NAME = java.util.regex.Pattern.compile("(^[\\u4e00-\\u9fa5]{1}[\\u4e00-\\u9fa5\\.·。]{0,8}[\\u4e00-\\u9fa5]{1}$)|(^[a-zA-Z]{1}[a-zA-Z\\s]{0,8}[a-zA-Z]{1}$)");
    /**
     * 抖音地址 截取视频ID
     */
    private static final java.util.regex.Pattern DOUYIN_VIDEO = java.util.regex.Pattern.compile("(?<=video\\/).*(?=\\/)");

    public static final Pattern SPECIAL = Pattern.compile("\\pP|\\pS\\pZ\\pC|\\s+");

    public static final Pattern FILENAME = Pattern.compile("^[\u4e00-\u9fa5a-zA-Z0-9\\-]+$");
    /**
     * 验证手机号
     * @param val
     * @return
     */
    public static boolean mobile(String val){
        return !Strings.isNullOrEmpty(val) && MOBILE.matcher(val).matches();
    }

    /**
     * 固定电话
     * @param val
     * @return
     */
    public static boolean phone(String val){
        return !Strings.isNullOrEmpty(val) && PHONE.matcher(val).matches();
    }
    /**
     * 验证邮箱
     * @param val
     * @return
     */
    public static boolean email(String val){
        return !Strings.isNullOrEmpty(val) && EMAILL.matcher(val).matches();
    }

    /**
     * 验证用户名
     *
     * @param val
     * @return
     */
    public static boolean userName(String val) {
        return !Strings.isNullOrEmpty(val) && USERNAME.matcher(val).matches();
    }

    public static boolean userNameф(String val) {
        return !Strings.isNullOrEmpty(val) && USERNAME_CN.matcher(val).matches();
    }

    /**
     * 验证密码
     *
     * @param val
     * @return
     */
    public static boolean password(String val) {
        return !Strings.isNullOrEmpty(val) && PASSWORD.matcher(val).matches();
    }

    /**
     * 验证密码 纯数字或者纯字母，不通过
     * @param val
     * @return
     */
    public static boolean passwordф(String val){
        return PASSWORD_STRONG.matcher(val).matches();
    }
    /**
     * 验证汉字
     * @param val
     * @return
     */
    public static boolean chinese(String val){
        return CHINESE.matcher(val).matches();
    }

    /**
     * 中文姓名
     * @param val
     * @return
     */
    public static boolean name_cn(String val){
        return !Strings.isNullOrEmpty(val) && NAME_CHINESE.matcher(val).matches();
    }

    /**
     * 英文姓名
     * @param val
     * @return
     */
    public static boolean name_en(String val){
        return !Strings.isNullOrEmpty(val) && NAME_ENGLISH.matcher(val).matches();
    }
    /**
     * 验证身份证
     * @param val
     * @return
     */
    public static boolean idCard(String val){
        return !Strings.isNullOrEmpty(val) && ID_CARD.matcher(val).matches();
    }

    public static boolean idCard15(String val){
        return !Strings.isNullOrEmpty(val) && ID_CARD_15.matcher(val).matches();
    }

    public static boolean idCard18(String val){
        return !Strings.isNullOrEmpty(val) && ID_CARD_18.matcher(val).matches();
    }

    /**
     * 护照
     * @param val
     * @return
     */
    public static boolean passport(String val){
        return !Strings.isNullOrEmpty(val) && PASSPORT.matcher(val).matches();
    }
    /**
     * 验证URL
     * @param val
     * @return
     */
    public static boolean url(String val){
        return !Strings.isNullOrEmpty(val) && URL.matcher(val).matches();
    }

    /**
     * 验证IP地址
     * @param val
     * @return
     */
    public static boolean ip(String val){
        return !Strings.isNullOrEmpty(val) && IP.matcher(val).matches();
    }

    /**
     * 是否是浮点数
     * @param val
     * @return
     */
    public static boolean decimal(String val){
        return !Strings.isNullOrEmpty(val) && DECIMAL.matcher(val).matches();
    }
    /**
     * 是否是浮点数（可带正负号）
     * @param val
     * @return
     */
    public static boolean decimalSign(String val){
        return !Strings.isNullOrEmpty(val) && DECIMALSIGN.matcher(val).matches();
    }

    /**
     * 是否整数（可带正负号）
     * @param val
     * @return
     */
    public static boolean numberSign(String val){
        return !Strings.isNullOrEmpty(val) && NUMBERSIGN.matcher(val).matches();
    }

    /**
     * 是否整数
     * @param val
     * @return
     */
    public static boolean numeric(String val){
        return !Strings.isNullOrEmpty(val) && NUMERIC.matcher(val).matches();
    }

    /**
     * 是否为纯字符
     *
     * @param val
     * @return
     */
    public static boolean letter(String val) {
        return !Strings.isNullOrEmpty(val) && LETTER.matcher(val).matches();
    }

    /**
     * 是否全是数字或字符
     *
     * @param val
     * @return
     */
    public static boolean alphanumeric(String val) {

        return !Strings.isNullOrEmpty(val) && ALPHANUMERIC.matcher(val).matches();
    }

    /**
     * 经度
     *
     * @param val
     * @return
     */
    public static boolean longitude(String val) {
        return !Strings.isNullOrEmpty(val) && LONGITUDE.matcher(val).matches();
    }

    /**
     * 纬度
     * @param val
     * @return
     */
    public static boolean latitude(String val){
        return !Strings.isNullOrEmpty(val) && LATITUDE.matcher(val).matches();
    }

    /**
     * 车牌
     * @param val
     * @return
     */
    public static boolean car(String val){
        return !Strings.isNullOrEmpty(val) && CAR.matcher(val).matches();
    }

    /**
     * 统一社会信用代码
     * @param val
     * @return
     */
    public static boolean org(String val){
        return !Strings.isNullOrEmpty(val) && ORG.matcher(val).matches();
    }

    /**
     * 姓名
     * @param val
     * @return
     */
    public static boolean name(String val){
        return !Strings.isNullOrEmpty(val) && NAME.matcher(val).matches();
    }

    /**
     * 特殊字符
     * @param val
     * @return
     */
    public static boolean special(String val){
        return !Strings.isNullOrEmpty(val) && SPECIAL.matcher(val).find();
    }

    /**
     * 文件名称
     * @param val
     * @return
     */
    public static boolean fileName(String val){
        return !Strings.isNullOrEmpty(val) && FILENAME.matcher(val).matches();
    }
}
