package io.github.buzzxu.spuddy.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;

/**
 * 账户类型
 *
 * @author 徐翔
 * @since 2021-10-23 17:49
 **/
public enum AccountType {
    UNKNOWN(0,"未知"),
    COMPANY(1,"企业"),
    PERSON(2,"个人"),
    PLATFORM(99,"平台"),
    ;

    private int type;
    private String text;

    AccountType(int type, String text) {
        this.type = type;
        this.text = text;
    }

    @JsonCreator
    public static AccountType of(int type){
        for (AccountType val : values()){
            if(val.type == type){
                return val;
            }
        }
        return UNKNOWN;
    }

    @JsonValue
    public int type() {
        return type;
    }

    public String text() {
        return text;
    }

    public static List<AccountType> allλ(){
        return List.of(PERSON,COMPANY);
    }
    public static List<Pair<Integer,String>> all(){
        return allλ().stream().map(v-> Pair.of(v.type,v.text)).toList();
    }
}
