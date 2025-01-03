package io.github.buzzxu.spuddy.security.objects;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.github.buzzxu.spuddy.func.Tree;
import io.github.buzzxu.spuddy.jackson.Jackson;
import io.github.buzzxu.spuddy.objects.Id;
import io.github.buzzxu.spuddy.objects.Region;
import lombok.*;

import java.io.Serial;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: 
 * @description: 组织机构
 * @author: xuxiang
 * @create: 2021-01-01 21:16
 **/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Organization extends Id<Integer> implements Tree<Integer> {

    public static final String SQL_COLUMNS = "org.id AS org_id,org.parent_id AS org_parentId,org.name AS org_name,org.code AS org_code,org.icon AS org_icon,CAST(id AS CHAR(50)) AS org_pathId,CAST(name AS CHAR(100)) AS org_path,org.areas AS org_areasJson,org.created_at AS org_created_at,org.update_at AS org_update_at";
    public static final String SQL_SELECT_ALL = "SELECT " + SQL_COLUMNS + " FROM t_org org";
    public static final String SQL_INSERT = "INSERT INTO t_org (parent_id,name,code,icon,areas,created_at,update_at) VALUES (?,?,?,?,?,?,?)";
    public static final String SQL_UPDATE = "UPDATE t_org SET parent_id=?,name=?,code=?,icon=?,areas=?,update_at=? WHERE id =?";
    public static final String SQL_SELECT_CHILDS = "WITH recursive org_chain(org_id,org_parentId,org_name,org_code,org_icon,org_pathId,org_path,org_areasJson,org_created_at,org_update_at,level) AS (\n" +
            "SELECT " + SQL_COLUMNS + ",1 LEVEL FROM t_org org WHERE parent_id = ?\n" +
            " UNION ALL\n" +
            " SELECT org.id,org.parent_id,org.name,org.code,org.icon,CONCAT(oc.org_pathId,',',org.id),CONCAT(oc.org_path,'|',org.name),org.areas,org.created_at,org.update_at,LEVEL + 1 FROM org_chain oc JOIN t_org org ON oc.org_id = org.parent_id\n" +
            ") SELECT * FROM org_chain ";

    @Serial
    private static final long serialVersionUID = -7983622546441858046L;
    protected Integer parentId;

    protected String name;
    protected String code;
    protected String icon;
    protected List<Integer> pathIds;
    protected List<String> paths;
    protected String path;
    protected String pathId;
    protected int level;
    /**
     * 所属地区
     */
    protected List<Region> areas;
    protected Organization parent;
    protected List<Organization> childs;
    protected List<Integer> childIds;


    public void setPath(String path) {
        this.path = path;
        if (!Strings.isNullOrEmpty(path)) {
            paths = Arrays.stream(path.split("\\|")).map(String::trim).collect(Collectors.toList());
        }
    }

    public void setPathId(String pathId) {
        this.pathId = pathId;
        if (!Strings.isNullOrEmpty(pathId)) {
            pathIds = Arrays.stream(pathId.split(",")).map(String::trim).map(Integer::parseInt).collect(Collectors.toList());
        }
    }

    public void setAreasJson(String json) {
        areas = Jackson.json2Object(json, new TypeReference<List<Region>>() {
        });
    }

    public Organization addRegion(Region region) {
        if (areas == null) {
            areas = Lists.newArrayListWithCapacity(1);
        }
        areas.add(region);
        return this;
    }

    public Organization addRegion(String provinceId, String province) {
        return addRegion(Region.of(provinceId, province));
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public int parentId() {
        return parentId;
    }

    @Override
    public int sorted() {
        return 0;
    }

    @Override
    public List<? extends Tree<Integer>> children() {
        return childs;
    }

    @Override
    public void initChildren() {
        childs = Lists.newArrayListWithCapacity(1);
    }
}
