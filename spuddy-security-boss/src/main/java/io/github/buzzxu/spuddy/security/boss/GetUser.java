package io.github.buzzxu.spuddy.security.boss;


import io.github.buzzxu.spuddy.security.objects.UserInfo;
import org.apache.shiro.SecurityUtils;

/**
 * @program:
 * @description:
 * @author: 徐翔
 * @create: 2019-12-30 00:33
 **/
public class GetUser {

    public static UserInfo get(){
        return (UserInfo) SecurityUtils.getSubject().getPrincipal();
    }

    public static <U extends UserInfo> U of(){
        return (U) SecurityUtils.getSubject().getPrincipal();
    }
}
