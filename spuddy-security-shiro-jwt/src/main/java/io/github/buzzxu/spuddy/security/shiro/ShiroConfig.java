package io.github.buzzxu.spuddy.security.shiro;


import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;


import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author xux
 * @date 2018/6/13 下午2:04
 */
@NoArgsConstructor
@Getter @Setter
public class ShiroConfig {


    private String authorizationCacheName;
    private String authenticationCacheName;
    private String loginUrl;
    private String successUrl;
    private String unauthorizedUrl;
    private Map<String,String> filterMapping;

    @Inject
    public ShiroConfig(@Value("${shiro.url.login:}") String loginUrl
            ,@Value("${shiro.url.success:}") String successUrl
            ,@Value("${shiro.url.unauthorized:}") String unauthorizedUrl
            ,@Value("${shiro.cacheName.authentication:shiro:authentication}") String authenticationCacheName
            ,@Value("${shiro.cacheName.authorization:shiro:authorization}") String authorizationCacheName
            ,@Value("${shiro.filterMapping:}")String filterMapping) {
        if(!Strings.isNullOrEmpty(authenticationCacheName)){
            this.authenticationCacheName = authenticationCacheName+":";
        }
        if(!Strings.isNullOrEmpty(authorizationCacheName)){
            this.authorizationCacheName = authorizationCacheName+":";
        }
        if(!Strings.isNullOrEmpty(filterMapping)){
            this.filterMapping = Maps.newHashMapWithExpectedSize(3);
            Iterable<String> vals = Splitter.on(",").split(filterMapping);
            vals.forEach(val->{
                String[] $v = val.split(":");
                checkArgument($v.length ==2,"filterMapping value error. filterMapping: /login:anon,/captcha:anon");
                this.filterMapping.put($v[0].trim(),$v[1].trim());
            });
        }

        this.loginUrl = loginUrl;
        this.successUrl = successUrl;
        this.unauthorizedUrl = unauthorizedUrl;
    }
}
