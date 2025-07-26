package io.github.buzzxu.spuddy.security.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import io.github.buzzxu.spuddy.objects.Id;
import io.github.buzzxu.spuddy.objects.Location;
import io.github.buzzxu.spuddy.security.OAuthType;
import io.github.buzzxu.spuddy.util.Converts;
import io.github.buzzxu.spuddy.util.Replaces;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author xux
 * @date 2018/5/22 下午2:29
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter @Setter @NoArgsConstructor
public abstract class User extends Id<Long> implements Principal{

    protected int orgId;
    /**
     * 真实姓名
     */
    protected String realName;
    /**
     * 头像
     */
    protected String avatar;
    protected String userName;
    /**
     * 昵称
     */
    protected String nickName;
    protected String mobile;
    protected String email;
    @JsonIgnore
    protected String password;
    @JsonIgnore
    protected String salt;
    /**
     * 用户状态 1=正常,0=禁用
     */
    protected Integer status;
    /**
     * 是否首次登陆,0=不是，1=是
     */
    protected boolean firstLogin;
    /**
     * 是否已经合并
     */
    protected boolean merge;
    /**
     * 是否开启两步验证
     */
    protected boolean use2FA;
    /**
     * 两步验证密钥
     */
    @JsonIgnore
    protected String secret2FA;
    /**
     * 用户类型
     */
    protected int type;
    /**
     * 0=未知,1=男性,2=女性
     */
    protected int gender;
    /**
     * 用户来源 -1=人工,0=PC,1=h5,2=微信公众号,3=微信小程序
     */
    protected int source;

    protected OAuthUser oAuthUser;

    protected Set<OAuthUser> oauths;
    //语言 默认 中文   越南=vi_VN 日本=ja_JP 美国=en_US 英国en_GB
    protected String language = "zh_CN";
    /**
     * 位置区域
     */
    protected Location location;
    /**
     * 是否已使命认证
     */
    protected boolean verified;
    /**
     * 自定义设置
     */
    protected Map<String,Object> settings;
    @JsonIgnore
    protected boolean mobileIsUserName;
    public User(Long id, boolean deleted, LocalDateTime createdAt, LocalDateTime updatedAt, String realName, String avatar, String userName, String nickName, String mobile, String email, String password, String salt, int status, boolean firstLogin, int type, int gender, int source, OAuthUser oAuthUser) {
        super(id, deleted, createdAt, updatedAt);
        this.realName = realName;
        this.avatar = avatar;
        this.userName = userName;
        this.nickName = nickName;
        this.mobile = mobile;
        this.email = email;
        this.password = password;
        this.salt = salt;
        this.status = status;
        this.firstLogin = firstLogin;
        this.type = type;
        this.gender = gender;
        this.source = source;
        this.oAuthUser = oAuthUser;
        if(this.oAuthUser != null){
            this.oauths = Sets.newHashSetWithExpectedSize(1);
            this.oauths.add(oAuthUser);
        }
    }

    public Map<String,String> map(){
        Map<String,String> hash = Converts.map(this);
        hash.put("password",getPassword());
        if(!Strings.isNullOrEmpty(this.secret2FA)){
            hash.put("secret2FA",secret2FA);
        }
        return hash;
    }

    @JsonIgnore
    @Override
    public String getName() {
        return getId().toString();
    }


    public Optional<String> credential(){
        return !Strings.isNullOrEmpty(password) ?
                Optional.of(password) : (oAuthUser !=null ?
                (!Strings.isNullOrEmpty(oAuthUser.getOAuthId())?
                        Optional.of(oAuthUser.getOAuthId()): (!Strings.isNullOrEmpty(oAuthUser.getUnionid()) ?
                        Optional.of(oAuthUser.getUnionid()) : (!Strings.isNullOrEmpty(oAuthUser.getCredential()) ?
                        Optional.of(oAuthUser.getCredential()) : Optional.empty())) )
                : Optional.empty());
    }

    public static <T extends User> T from(Map<String,String> map, Class<T> clazz){
        T bean = Converts.bean(map,clazz);
        bean.setPassword(map.get("password"));
        bean.setSecret2FA(map.get("secret2FA"));
        return bean;
    }

    public void addOAuth(OAuthUser oAuthUser){
        if(oauths == null){
            oauths = Sets.newHashSetWithExpectedSize(1);
        }
        oauths.add(oAuthUser);
    }
    public Optional<OAuthUser> oauth(OAuthType type){
        return oauths == null ? Optional.empty() : oauths.stream().filter(v-> v.getOAuthType() == type).findFirst();
    }
    @JsonIgnore
    public String name(){
        return !Strings.isNullOrEmpty(realName) ? Replaces.name(realName) : (!Strings.isNullOrEmpty(mobile)? Replaces.mobile(mobile) : (!Strings.isNullOrEmpty(email) ? email : (!Strings.isNullOrEmpty(userName) ? userName : nickName)));
    }

    public boolean isDisabled(){
        return status != null && status == 0 ;
    }
}
