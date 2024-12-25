package io.github.buzzxu.spuddy.security.objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.buzzxu.spuddy.objects.i18n.Langs;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author xux
 * @date 2018/5/23 下午4:47
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString @Builder
public class Role implements Serializable {
    @Serial
    private static final long serialVersionUID = 8818020816468715785L;
    public static final String SQL_COLUMNS = " id,parent_id,region,code,name,type,description,ext,langs,created_at,updated_at ";
    private int id;
    private int parentId;
    private int region;
    private String name;
    private String code;
    private RoleType type;
    /**
     * 描述
     */
    private String description;
    private List<Integer> permIds;
    private List<Permisson> perms;
    private Map<String,Object> ext;
    private Langs langs;
    private Date createdAt;
    private Date updatedAt;

    public Role(int id, int parentId, String name, String code,RoleType type, Date createdAt, Date updatedAt) {
        this(id,parentId,name,code,type,null,null,createdAt,updatedAt);
    }
    public Role(int id, int parentId, String name, String code, RoleType type,Map<String,Object> ext,Langs langs,Date createdAt, Date updatedAt) {
        this(id,parentId,0,name,code,type,null,null,null,ext,langs,createdAt,updatedAt);
    }

    public String getTypeText(){
        return type.text();
    }
}
