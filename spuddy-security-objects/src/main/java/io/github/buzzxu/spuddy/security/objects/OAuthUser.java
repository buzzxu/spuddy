package io.github.buzzxu.spuddy.security.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import io.github.buzzxu.spuddy.security.OAuthType;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author XuXiang
 * @version 0.0.1
 * @date 2019-03-14 21:08
 **/
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@ToString @AllArgsConstructor @Builder
public class OAuthUser implements Serializable {
    @Serial
    private static final long serialVersionUID = -9122534497833138043L;

    @Setter
    private Long id;
    @Setter
    private long userId;
    /***
     * 1=微信,2=qq,3=支付宝,4=微博
     */
    @JsonProperty("oAuthType")
    private int type;
    @JsonIgnore
    private OAuthType oAuthType;
    /**
     * 第三方uid 、openid等
     */
    @Setter
    private String oAuthId;
    @Setter
    private String unionid;
    /**
     * 密码凭证/access_token
     */
    @Setter
    private String credential;
    @Setter
    private LocalDateTime createdAt;



    public OAuthType type(){
        return OAuthType.of(type);
    }

    public void type(OAuthType type){
        this.type = type.type();
    }



    public static OAuthUser of(Long userId,OAuthType type,String oAuthId,String unionid,String credential){
        return of(userId,type.type(),oAuthId,unionid,credential);
    }

    public static OAuthUser of(Long userId,int type,String oAuthId,String unionid,String credential){
        OAuthUser oAuthUser = new OAuthUser();
        oAuthUser.setUserId(userId);
        oAuthUser.type = type;
        oAuthUser.setOAuthId(oAuthId);
        oAuthUser.setUnionid(unionid);
        oAuthUser.credential = credential;
        return oAuthUser;
    }

    public void setType(int type) {
        this.type = type;
        this.oAuthType = OAuthType.of(type);
    }

    public void setOAuthType(OAuthType oAuthType) {
        this.oAuthType = oAuthType;
        this.type = oAuthType.type();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OAuthUser oAuthUser = (OAuthUser) o;
        return userId == oAuthUser.userId && type == oAuthUser.type && Objects.equal(id, oAuthUser.id) && oAuthType == oAuthUser.oAuthType && Objects.equal(oAuthId, oAuthUser.oAuthId) && Objects.equal(unionid, oAuthUser.unionid) && Objects.equal(credential, oAuthUser.credential) && Objects.equal(createdAt, oAuthUser.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, userId, type, oAuthType, oAuthId, unionid, credential, createdAt);
    }
}
