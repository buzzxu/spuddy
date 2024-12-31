package io.github.buzzxu.spuddy.security.objects;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author xux
 * @date 2024年12月30日 16:22:53
 */
@Getter @Setter
public class BossUserInfo extends UserInfo {
    public static final String SQL_COLUMNS = """
            ub.id AS u_id,ub.type AS u_type,ub.user_name AS u_userName,ub.mobile AS u_mobile,ub.email AS u_email,ub.password AS u_password,
            ub.salt AS u_salt,ub.nick_name AS u_nikeName,ub.real_name AS u_realName,ub.avatar AS u_avatar,ub.status AS u_status,
            ub.gender AS u_gender,ub.source AS u_source,ub.firstlogin AS u_firstLogin,ub.is_2fa AS u_use2FA,ub.secret_2fa AS u_secret2FA,
            ub.deleted AS u_deleted,r.id AS u_roleId,r.name AS u_roleName,r.code AS u_roleCode
            """;
    public static final String SQL_FROM = " FROM t_user_base ub LEFT JOIN t_user_role ur ON ub.id = ur.user_id LEFT JOIN t_role r ON ur.role_id = r.id ";
    public static final String SQL_SELECT= "SELECT  " + SQL_COLUMNS + SQL_FROM;
    private int roleId;
    private String roleCode;
    private String roleName;
    private List<Integer> roleIds;
}
