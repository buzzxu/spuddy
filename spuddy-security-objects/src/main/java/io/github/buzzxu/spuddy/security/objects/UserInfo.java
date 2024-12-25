package io.github.buzzxu.spuddy.security.objects;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class UserInfo extends User {

    @Serial
    private static final long serialVersionUID = -9087203302776796248L;
    private String openId;

    private List<String> roleCodes;

    private static UserInfo ANONYMOUS = new UserInfo();

    public UserInfo(Long id, boolean deleted, LocalDateTime createdAt, LocalDateTime updatedAt, String realName, String avatar, String userName, String nikeName, String mobile, String email, String password, String salt, int status, boolean firstLogin, int type, int gender, int source, OAuthUser oAuthUser) {
        super(id, deleted, createdAt, updatedAt, realName, avatar, userName, nikeName, mobile, email, password, salt, status, firstLogin, type, gender, source, oAuthUser);
    }

    public boolean isAnonymous() {
        return this == ANONYMOUS;
    }
    public static UserInfo anonymous(){return ANONYMOUS;}

    public PrivilegeInfo pvgInfo(){
        return new PrivilegeInfo().fill(this);
    }
    public Operator of(){
        return Operator.builder()
                .id(id)
                .userName(getUserName())
                .nickName(getNikeName())
                .realName(getRealName())
                .email(getEmail())
                .mobile(getMobile())
                .type(getType())
                .roleCodes(roleCodes)
                .build();
    }

    public static <T extends UserInfo> T bean(Map<String,String> map,Class<T> clazz){
        return User.from(map,clazz);
    }
}
