package io.github.buzzxu.spuddy.security;



import io.github.buzzxu.spuddy.objects.Pager;
import io.github.buzzxu.spuddy.objects.i18n.Langs;
import io.github.buzzxu.spuddy.security.objects.Menu;
import io.github.buzzxu.spuddy.errors.SecurityException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface MenuService {

    int create(Menu menu);
    int create(Integer parentId, String name, String code, String target,String icon, String remark,Function<Menu,Boolean> function);
    int create(Integer parentId, String name, String code, String target,String icon, String remark,Map<String,Object> ext,Function<Menu,Boolean> function);

    int create(Integer parentId, String name, String code, String target, String path,String icon, String remark,Function<Menu,Boolean> function);
    int create(Integer parentId, String name, String code, String target, String path,String icon, String remark,Map<String,Object> ext,Function<Menu,Boolean> function);
    int create(Integer parentId, String region,String name, String code, String target, String path,String icon, String remark,Map<String,Object> ext,Function<Menu,Boolean> function);


    int create(Integer parentId, String name, String code, String target, String path, String icon, String remark, Map<String,Object> ext, Langs langs, Function<Menu, Boolean> function);

    int create(Integer parentId, String region,String name, String code, String target, String path,String icon, String remark,Map<String,Object> ext, Langs langs, Function<Menu, Boolean> function);

    /**
     * 更新
     * @param menu
     * @return
     */
    boolean update(Menu menu);
    /**
     * 删除菜单
     * @param function
     * @param id
     * @return
     */
    boolean delete(Function<Set<Integer>,Boolean> function, int... id);


    /**
     * 开启/关闭
     * @param id
     * @return
     */
    boolean reEnable(Integer id);


    /**
     * 分配菜单到角色
     * @param roleId
     * @param menuIds
     * @return
     */
    boolean menu2Role(int roleId,Set<Integer> menuIds,Function<List<Integer>,Boolean> function);
    /**
     * 根据角色获取菜单
     * @param roleId
     * @return
     */
    default List<Menu> getTreeByRoleId(int... roleId) throws SecurityException {
        return getTreeByRoleId("",roleId);
    }

    /**
     * 根据角色获取菜单
     * @param region
     * @param roleId
     * @return
     * @throws SecurityException
     */
    default List<Menu> getTreeByRoleId(String region,int... roleId) throws SecurityException{
        return getTreeByRoleId(region,null,roleId);
    }

    List<Menu> getTreeByRoleId(String region,Map<String,Object> ext,int... roleId) throws SecurityException;
    /**
     * 获取角色菜单
     * @param supplier
     * @param consumer
     * @param roleId
     * @return
     * @throws SecurityException
     */
    List<Menu> getTreeByRoleId(Supplier<List<Menu>> supplier, Consumer<List<Menu>> consumer, int... roleId) throws SecurityException;

    /**
     * 获取角色菜单
     * @param supplier
     * @param consumer
     * @param region
     * @param roleId
     * @return
     * @throws SecurityException
     */
    List<Menu> getTreeByRoleId(Supplier<List<Menu>> supplier, Consumer<List<Menu>> consumer, String region,int... roleId) throws SecurityException;

    default List<Menu> findByRoleId(int... roleIds) throws SecurityException{
        return findByRoleId("",roleIds);
    }

    List<Menu> findByRoleId(String region,int... roleIds) throws SecurityException;

    default List<Integer> findIdsByRoleIds(int... roleIds)throws SecurityException{
        return findIdsByRoleIds("",roleIds);
    }

    default List<Integer> findIdsByRoleIds(String region,int... roleIds)throws SecurityException{
        return findIdsByRoleIds(region,null,roleIds);
    }

    List<Integer> findIdsByRoleIds(String region,Map<String,Object> ext,int... roleIds)throws SecurityException;
    /**
     * 获取开启全部菜单
     * @return
     */
    List<Menu> getAll();

    List<Menu> getAll(Map<String,Object> ext);
    /**
     * 获取开启全部菜单
     * @return
     */
    List<Menu> getOnlyEnable();

    /**
     * 获取全部菜单列表
     * @return
     */
    List<Menu> getAllTree();

    List<Menu> getAllTree(Map<String,Object> ext);

    List<Integer> findChildIds(int id);

    default Pager<Menu> paginate(int pageNumber, int pageSize, Map<String,Object> ext){
        return paginate(pageNumber,pageSize,"",ext);
    }
    Pager<Menu> paginate(int pageNumber,int pageSize,String region,Map<String,Object> ext);

    /**
     * 获取菜单信息
     * @param id
     * @return
     */
    Optional<Menu> of(int id);

    Optional<Menu> one(Map<String,Object> params);
}
