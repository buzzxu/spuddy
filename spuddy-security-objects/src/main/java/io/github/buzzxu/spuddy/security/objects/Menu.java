package io.github.buzzxu.spuddy.security.objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import io.github.buzzxu.spuddy.objects.i18n.Langs;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author xux
 */
@NoArgsConstructor @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter @Setter @EqualsAndHashCode @ToString
public class Menu implements Serializable {

    @Serial
    private static final long serialVersionUID = -6185203323291134159L;
    public static final String COLUMNS = " id,region,name,parent_id,code,enable,target,path,icon,depth,remark,sort,ext,langs ";
    public static final String COLUMNS_PREFIX = " m.id,m.region,m.name,m.parent_id,m.code,m.enable,m.target,m.path,m.icon,m.depth,m.remark,m.sort,m.ext,m.langs ";
    private Integer id;
    private Integer parentId;
    private String region;
    private String name;
    private String code;
    private String target;

    private String path;
    private String icon;
    private boolean enable;
    private int depth;
    /**
     * 多语言
     */
    private Langs langs;
    private Map<String,Object> ext;
    private String remark;
    private int sort;
    @JsonProperty("children")
    private List<Menu> childs = Lists.newArrayListWithCapacity(3);


    public Menu(int id, int parentId,String region, String name, String code, String target, String path,String icon, boolean enable, int depth,Langs langs,String remark,int sort) {
        this(id,parentId,region,name, code, target, path,icon,enable, depth, langs,null,remark,sort);
    }
    public Menu(int id, int parentId, String region,String name, String code, String target, String path,String icon, boolean enable, int depth,Langs langs,Map<String,Object> ext,String remark,int sort) {
        this(id,parentId,region,name, code, target, path,icon,enable, depth, langs,ext,remark,sort,null);
    }

    public String getName() {
        return name;
    }


}
