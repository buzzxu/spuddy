package io.github.buzzxu.spuddy.security;


import io.github.buzzxu.spuddy.objects.Pager;
import io.github.buzzxu.spuddy.objects.Pair;
import io.github.buzzxu.spuddy.objects.i18n.Langs;
import io.github.buzzxu.spuddy.security.objects.Role;
import io.github.buzzxu.spuddy.security.objects.RoleType;
import org.apache.commons.lang3.tuple.Triple;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author xux
 * @date 2018/5/21 下午4:23
 */
public interface RoleService {

    int create(String name, String code);
    int create(String name, String code, RoleType type);
    int create(String name, String code,RoleType type,String description);
    int create(String name, String code,int region);

    int create(String name, String code, Consumer<Role> consumer);

    int create(String name, String code,int region, Consumer<Role> consumer);

    int create(int parentId, String name, String code, Consumer<Role> consumer);

    int create(int parentId, String name, String code,int region, Consumer<Role> consumer);

    int create(int parentId, String name, String code, Map<String,Object> ext, Langs langs, Consumer<Role> consumer);
    int create(int parentId, String name, String code, RoleType type,Map<String,Object> ext, Langs langs, Consumer<Role> consumer);
    int create(int parentId, int region,String name, String code, String description,Map<String,Object> ext, Langs langs, Consumer<Role> consumer);

    int create(int parentId, int region, String name, String code, RoleType type,String description, Map<String, Object> ext, Langs langs, Consumer<Role> consumer);


    void delete(int id, Function<Integer, Boolean> function);

    boolean update(int id, int parentId, String name, String code, Consumer<Role> consumer);
    boolean update(int id, int parentId, int region,String name, String code, Consumer<Role> consumer);

    Optional<Role> get(int id);

    Optional<Role> of(String code);

    default List<Role> getAll() {
        return getAll(true);
    }

    List<Role> getAll(boolean all);

    List<Role> getAll(boolean all,Map<String,Object> ext);

    default Pager<Role> paginate(int pageNumber, int pageSize, Map<String,Object> ext){
        return paginate(pageNumber, pageSize,true,ext);
    }

    Pager<Role> paginate(int pageNumber, int pageSize,boolean all,Map<String,Object> ext);

    default List<Pair<Integer, String>> getOptions() {
        return getOptions(true);
    }

    List<Pair<Integer, String>> getOptions(boolean all);

    List<Pair<Integer, String>> getOptions(boolean all,Map<String,Object> ext);
    default List<Triple<Integer, String, String>> getData() {
        return getData(true);
    }

    List<Triple<Integer, String, String>> getData(boolean all);

    List<Triple<Integer, String, String>> getData(boolean all,Map<String,Object> ext);
    /**
     * 是否已经绑定角色
     *
     * @param userId
     * @return
     */
    boolean isBind(long userId);

    /**
     * 将某角色赋给用户 已有的角色不做处理
     *
     * @param userId
     * @param roleId 新增的角色
     */
    void user2Role0(long userId, Integer... roleId);

    /**
     * 将某角色赋给用户 已有的角色不做处理
     * @param userId
     * @param function
     * @param roleId    新增的角色
     */
    void user2Role0(long userId,Function<Integer[],Boolean> function,Integer... roleId);


    /**
     * 将某角色赋给用户 先删除后增加
     * @param userId
     * @param roleId    赋予的角色
     */
    void user2Role(long userId,Integer... roleId);

    /**
     * 将某角色赋给用户 先删除后增加
     * @param userId
     * @param function
     * @param roleId    赋予的角色
     */
    void user2Role(long userId,Function<Integer[],Boolean> function,Integer... roleId);

    /**
     * 修改用户的某个角色
     * @param userId
     * @param oldRole
     * @param newRole
     * @return
     */
    boolean user2Role(long userId,int oldRole,int newRole);


    /**
     * 修改用户的某个角色
     * @param userId
     * @param oldRole
     * @param newRole
     * @param parent    是否处理角色父子关系
     * @return
     */
    boolean user2Role(long userId,int oldRole,int newRole,boolean parent);


    /**
     * 删除用户的角色
     * @param userId
     * @param roleId
     */
    void delUserRole(long userId, Integer... roleId);

    /**
     * 清除用户角色
     * @param userId
     * @return
     */
    boolean clearUserRole(long userId);

    /**
     * 删除用户的角色
     * @param function
     * @param userId
     * @param roleId
     */
    void delUserRole(Function<Integer[],Boolean> function, long userId, Integer... roleId);

    /**
     * 删除用户的角色
     * @param parent     是否处理角色父子关系
     * @param function
     * @param userId
     * @param roleId
     */
    void delUserRole(boolean parent, Function<Integer[],Boolean> function, long userId, Integer... roleId);

    /**
     * 根据用户ID获取角色编码
     * @param userId
     * @param supplier
     * @param consumer
     * @return
     */
    Optional<List<String>> rolesByUserId(long userId, Supplier<Optional<List<String>>> supplier,Consumer<List<String>> consumer);

    /**
     * 根据用户ID获取角色ID
     * @param userId
     * @param supplier
     * @param consumer
     * @return
     */
    Optional<List<Integer>> roleIdsByUserId(long userId,Supplier<Optional<List<Integer>>> supplier,Consumer<List<Integer>> consumer);

    List<String> roleCodeByUserId(long userId);

    List<Integer> roleIdByUserId(long userId);

    List<Pair<Integer,String>> roleByUserId(long userId);

    /**
     * 检查Role ID 是否合法
     * @param roleId
     * @throws SQLException
     */
    void checkRoleId(Integer... roleId)throws SQLException;







}
