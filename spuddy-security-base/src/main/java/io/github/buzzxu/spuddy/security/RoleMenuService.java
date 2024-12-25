package io.github.buzzxu.spuddy.security;



import io.github.buzzxu.spuddy.security.objects.Operator;
import io.github.buzzxu.spuddy.security.objects.Role;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * @author xux
 * @date 2024年04月24日 8:24:36
 */
public interface RoleMenuService {

    int create(Role role, Set<Integer> menuIds, Function<List<Integer>,Boolean> function, Operator operator);


    boolean delete(int roleId, Function<Integer,Boolean> function, Operator operator);
    default List<Integer> findIdsByRoleIds(int... roleIds)throws SecurityException {
        return findIdsByRoleIds("",roleIds);
    }

    default List<Integer> findIdsByRoleIds(String region,int... roleIds)throws SecurityException{
        return findIdsByRoleIds(region,null,roleIds);
    }

    List<Integer> findIdsByRoleIds(String region, Map<String,Object> ext, int... roleIds)throws SecurityException;
}
