package io.github.buzzxu.spuddy.security.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import lombok.*;

import java.util.List;

@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor @EqualsAndHashCode
public class Operator {
    private Long id;
    private String userName;
    private String realName;
    private String nickName;
    private String mobile;
    private String email;
    private String avatar;
    private Integer type;
    @JsonIgnore
    private List<String> roleCodes;
    private static Operator SYS = Operator.builder().id(0L).userName("sys").type(99).realName("系统").email("downloadxu@163.com").mobile("15333819906").nickName("sys").roleCodes(List.of("superman")).build();

    @JsonIgnore
    public String name(){
        return !Strings.isNullOrEmpty(realName) ? realName : (!Strings.isNullOrEmpty(userName) ? userName : (!Strings.isNullOrEmpty(mobile) ? mobile : (!Strings.isNullOrEmpty(nickName) ?  nickName : "未知")));
    }
    @JsonIgnore
    public static Operator sys(){
        return SYS;
    }

    @Override
    public String toString() {
        return "Operator{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", realName='" + realName + '\'' +
                ", nickName='" + nickName + '\'' +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", avatar='" + avatar + '\'' +
                ", type=" + type +
                ", roleCodes=" + roleCodes +
                '}';
    }
}
