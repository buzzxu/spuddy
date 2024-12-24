package io.github.buzzxu.spuddy.security.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import io.github.buzzxu.spuddy.util.Converts;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author 徐翔
 * @create 2021-08-26 10:00
 **/
@Getter
@Setter
@ToString
public class UserInfo {
    protected Long id;
    protected String name;
    protected int type;
    protected String phone;
    @JsonIgnore
    private String psword;
    @JsonIgnore
    protected String salt;
    protected String openId;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
    private static UserInfo ANONYMOUS = new UserInfo();

    public boolean isAnonymous() {
        return this == ANONYMOUS;
    }

    public static UserInfo anonymous() {
        return ANONYMOUS;
    }

    public Map<String,String> map(){
        Map<String,String> hash = Converts.map(this);
        if(!Strings.isNullOrEmpty(psword)){
            hash.put("psword",getPsword());
        }
        return hash;
    }

    public static <T extends UserInfo> T from(Map<String,String> map, Class<T> clazz){
        T bean = Converts.bean(map,clazz);
        if(map.containsKey("psword")){
            bean.setPsword(map.get("psword"));
        }
        return bean;
    }
}

