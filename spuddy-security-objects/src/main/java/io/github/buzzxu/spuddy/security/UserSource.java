package io.github.buzzxu.spuddy.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @program: 
 * @description:
 * @author: 徐翔
 * @create: 2020-02-28 12:25
 **/
public enum UserSource {
    UNKNOWN(0),  //未知
    PC(1),
    SUPER(2),  //超管
    BOSS(3),   //boss端
    ADMIN(4),  //管理端
    THIRDPARTY(10),//三方平台
    WXMP(11),    //微信公众号
    WXMINI(12),  //微信小程序
    QQ(13),      //QQ
    ALIPAY(14),  //支付宝
    TAOBAO(15),  //淘宝
    WEIBO(16),   //微博
    MOBILE(30), //移动端
    ANDROID(31),
    IPHONE(32),
    H5(33),
    MAN(99),    //人工
    ;

    private final int val;

    UserSource(int val) {
        this.val = val;
    }

    @JsonCreator
    public static UserSource of(int val) {
        for(UserSource source : values()){
            if(source.val == val){
                return source;
            }
        }
        return PC;
    }
    @JsonValue
    public int val() {
        return val;
    }
}
