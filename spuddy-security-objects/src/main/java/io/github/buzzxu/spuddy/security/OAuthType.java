package io.github.buzzxu.spuddy.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author XuXiang
 * @version 0.0.1
 * @date 2019-03-14 20:52
 **/
public enum OAuthType {

    UNKNOWN(0, "UNKNOW", "未知"),
    WEIXIN(1, "wechat", "微信公众号"),
    WEIXIN_MINIAPP(2, "wechat_miniapp", "微信小程序"),
    QQ(3, "qq", "QQ"),
    ALIPAY(4, "alipay", "支付宝"),
    TAOBAO(5, "taobao", "淘宝"),
    WEIBO(6, "weibo", "微博"),
    JPUSH(7, "jpush", "极光推送"),
    APPLE(8,"apple","苹果"),
    ALLINPAY(9, "allinpay", "通联通商云"),
    ALLINPAY_SUB(901, "allinpay_sub", "通联子账户"),
    WEIXIN_MOBILE(11, "wechat_mobile", "微信移动端"),
    WEIXIN_H5(12, "wechat_h5", "微信H5"),
    YUNXIN(13, "yunxin", "网易云信"),
    ;

    private final int type;
    private final String name;
    private final String text;

    OAuthType(int type, String name, String text) {
        this.type = type;
        this.name = name;
        this.text = text;
    }

    @JsonCreator
    public static OAuthType of(int type){
        for(OAuthType val : values()){
            if(val.type == type){
                return val;
            }
        }
        return UNKNOWN;
    }
    public static OAuthType of(UserSource source){
       switch (source){
           case WXMP:
           case WXMINI:
               return WEIXIN;
           case WEIBO:
               return WEIBO;
           case ALIPAY:
                return ALIPAY;
           case TAOBAO:
                return TAOBAO;
           case QQ:
               return QQ;
           default:
               return UNKNOWN;

       }
    }

    @JsonValue
    public int type(){
        return type;
    }
}
