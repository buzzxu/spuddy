package io.github.buzzxu.spuddy.security.services;

import io.github.buzzxu.spuddy.exceptions.ApplicationException;
import io.github.buzzxu.spuddy.security.MenuService;
import io.github.buzzxu.spuddy.security.RoleMenuService;
import io.github.buzzxu.spuddy.security.RoleService;
import io.github.buzzxu.spuddy.security.objects.Operator;
import io.github.buzzxu.spuddy.security.objects.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author xux
 * @date 2024年12月30日 22:55:51
 */
@RequiredArgsConstructor
@Slf4j
public class StandardRoleMenuService extends AbstractStandard implements RoleMenuService {
    private final MenuService menuService;
    private final RoleService roleService;



    @Transactional(rollbackFor = {ApplicationException.class,IllegalArgumentException.class})
    @Override
    public int create(Role role, Set<Integer> menuIds, Function<List<Integer>, Boolean> function, Operator operator) {
        checkArgument(role != null ,"参数缺失: role");
        checkArgument(menuIds != null,"请设置菜单");
        int roleId = role.getId();
        log.info("账户: {}({}) ,开始创建新角色: {},分配菜单: {}",operator.name(),operator.getId(),role.getName(),menuIds);
        if(role.getId() == 0){
            roleId = roleService.create(role.getParentId(),role.getRegion(),role.getName(),role.getCode(),role.getType(),role.getDescription(),role.getExt(),role.getLangs(),null);;
        }else{
            roleService.update(roleId,role.getParentId(),role.getRegion(),role.getName(),role.getCode(),null);
        }

        checkArgument(roleId > 0,"创建角色失败");
        menuService.menu2Role(roleId,menuIds,function);
        log.info("账户: {}({}) ,创建新角色: {},分配菜单: {},成功",operator.name(),operator.getId(),role.getName(),menuIds);
        return roleId;
    }
    @Transactional(rollbackFor = {ApplicationException.class,IllegalArgumentException.class})
    @Override
    public boolean delete(int roleId,Function<Integer,Boolean> function, Operator operator) {
        checkArgument(roleId > 0,"参数错误:roleId");
        log.info("账户: {}({}) ,删除角色: {}",operator.name(),operator.getId(),roleId);
        //先取消角色关联的菜单
        try {
            if(qr.execute("DELETE FROM t_role_menu WHERE role_id = ?",roleId) > 0){
                roleService.delete(roleId,function);
                log.info("账户: {}({}) ,删除角色: {},成功",operator.name(),operator.getId(),roleId);
                return true;
            }
            log.info("账户: {}({}) ,删除角色: {},失败",operator.name(),operator.getId(),roleId);
            return false;
        }catch (Exception ex){
            log.error(ex.getMessage(),ex);
            throw new IllegalArgumentException("删除角色失败");
        }
    }

    @Override
    public List<Integer> findIdsByRoleIds(String region, Map<String, Object> ext, int... roleIds) throws SecurityException {
        return menuService.findIdsByRoleIds(region, ext, roleIds);
    }
}
