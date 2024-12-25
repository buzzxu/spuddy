package io.github.buzzxu.spuddy.security.objects;


import io.github.buzzxu.spuddy.objects.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @program: 
 * @description: 部门(用户组)
 * @author: xuxiang
 * @create: 2021-01-02 18:36
 **/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Department extends Id<Integer> {

    protected Integer parentId;
    protected String name;
    protected String code;
    protected String icon;
    protected List<String> pathIds;
    protected List<String> paths;
    protected int level;
    /**
     * 是否是管理
     */
    protected boolean manager;

    protected Department parent;
    protected List<Department> childs;
}
