package io.github.buzzxu.spuddy.security.objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xux
 * @date 2018/5/22 下午2:47
 */
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter @ToString
public class PrivilegeInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1361311955157226017L;
    @Setter
    private long id;
    @Setter
    private String userName;
    @Setter
    private String mobile;
    @Setter
    private String email;
    @Setter
    private String realName;
    @Setter
    private String avatar;
    @Setter
    private int gender;
    @Setter
    private int type;
    @Setter
    private int source;
    private final List<Integer> roleIds = new ArrayList<>(2);
    private final List<Integer> permIds = new ArrayList<>(2);
    private List<String> roles = new ArrayList<>(2);
    private List<String> permissions = new ArrayList<>(2);
    @Setter
    private int orgId;

    public PrivilegeInfo fill(User user) {
        return fill(user, null, null);
    }

    public PrivilegeInfo fill(User user, List<String> roles, List<String> permissions) {
        id = user.getId();
        userName = user.getUserName();
        mobile = user.getMobile();
        email = user.getEmail();
        realName = user.getRealName();
        avatar = user.getAvatar();
        gender = user.getGender();
        type = user.getType();
        source = user.getSource();
        this.roles = roles;
        this.permissions = permissions;
        return this;
    }

    public PrivilegeInfo addRoleId(String roleId){
        roleIds.add(Integer.parseInt(roleId));
        return this;
    }
    public void addRole(String role){
        roles.add(role);
    }
    public void addPermIds(String permId){
        permIds.add(Integer.parseInt(permId));
    }
    public void addPermissions(String perm){
        permissions.add(perm);
    }

}
