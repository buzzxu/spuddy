package io.github.buzzxu.spuddy.security.services;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.github.buzzxu.spuddy.db.SqlException;
import io.github.buzzxu.spuddy.errors.LockedAccountException;
import io.github.buzzxu.spuddy.exceptions.ApplicationException;
import io.github.buzzxu.spuddy.i18n.I18n;
import io.github.buzzxu.spuddy.jackson.Jackson;
import io.github.buzzxu.spuddy.objects.Pager;
import io.github.buzzxu.spuddy.objects.Pair;
import io.github.buzzxu.spuddy.objects.i18n.Lang;
import io.github.buzzxu.spuddy.objects.i18n.Langs;
import io.github.buzzxu.spuddy.security.RoleService;
import io.github.buzzxu.spuddy.security.objects.Role;
import io.github.buzzxu.spuddy.security.objects.RoleType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static io.github.buzzxu.spuddy.security.objects.Role.SQL_COLUMNS;
import static io.github.buzzxu.spuddy.util.Dates.asDate;

/**
 * @author xux
 * @date 2018/5/23 下午5:03
 */
@RequiredArgsConstructor @Slf4j
public class StandardRole extends AbstractStandard implements RoleService {


    @Transactional(rollbackFor = {ApplicationException.class,IllegalArgumentException.class})
    @Override
    public int create(String name, String code) {
        return create(name,code,0);
    }
    @Transactional(rollbackFor = {ApplicationException.class,IllegalArgumentException.class})
    @Override
    public int create(String name, String code, RoleType type) {
        return create(name,code,type,null);
    }
    @Transactional(rollbackFor = {ApplicationException.class,IllegalArgumentException.class})
    @Override
    public int create(String name, String code, RoleType type, String description) {
        return create(0,0,name,code,type,description,null,null,null);
    }

    @Transactional(rollbackFor = {ApplicationException.class,IllegalArgumentException.class})
    @Override
    public int create(String name, String code, int region) {
        return create(name,code,region,null);
    }

    @Transactional(rollbackFor = {ApplicationException.class,IllegalArgumentException.class})
    @Override
    public int create(String name, String code, Consumer<Role> consumer) {
        return create(0,name,code,0,consumer);
    }
    @Transactional(rollbackFor = {ApplicationException.class,IllegalArgumentException.class})
    @Override
    public int create(String name, String code, int region,Consumer<Role> consumer) {
        return create(0,name,code,region,consumer);
    }
    @Transactional(rollbackFor = {ApplicationException.class,IllegalArgumentException.class})
    @Override
    public int create(int parentId, String name, String code, Consumer<Role> consumer) {
        return create(parentId, name, code,null,null,consumer);
    }

    @Transactional(rollbackFor = {ApplicationException.class,IllegalArgumentException.class})
    @Override
    public int create(int parentId, String name, String code, int region, Consumer<Role> consumer) {
        return create(parentId, region,name, code,null,null,null,consumer);
    }

    @Transactional(rollbackFor = {ApplicationException.class,IllegalArgumentException.class})
    @Override
    public int create(int parentId, String name, String code, Map<String, Object> ext, Langs langs, Consumer<Role> consumer) {
        return create(parentId, name, code, RoleType.PROTECTED,ext, langs, consumer);
    }

    @Transactional(rollbackFor = {ApplicationException.class,IllegalArgumentException.class})
    @Override
    public int create(int parentId, String name, String code, RoleType type, Map<String, Object> ext, Langs langs, Consumer<Role> consumer) {
        return create(parentId,0, name, code, type,null,ext, langs, consumer);
    }

    @Transactional(rollbackFor = {ApplicationException.class,IllegalArgumentException.class})
    @Override
    public int create(int parentId, int region,String name, String code, String description,Map<String,Object> ext, Langs langs, Consumer<Role> consumer) {
        return create(parentId,region,name,code,RoleType.PROTECTED,description,ext,langs,consumer);
    }
    @Transactional(rollbackFor = {ApplicationException.class,IllegalArgumentException.class})
    @Override
    public int create(int parentId, int region, String name, String code, RoleType type, String description, Map<String, Object> ext, Langs langs, Consumer<Role> consumer) {
        checkArgument(!Strings.isNullOrEmpty(name) && !Strings.isNullOrEmpty(code) && (code.length() > 3 && code.length() < 15),"名称和编码不能为空，或者编码不符合规范" );
        Date now = asDate(LocalDateTime.now());
        try {
            if(qr.query("SELECT COUNT(*) FROM t_role WHERE code=? OR name =? LIMIT 1",new ScalarHandler<Long>(1),code.trim(),name.trim())>0){
                throw new IllegalArgumentException("已存在相同的角色编码");
            }
            //判断父节点是否存在
            if(parentId !=0){
                if(qr.query("SELECT COUNT(*) FROM t_role WHERE id=? LIMIT 1",new ScalarHandler<Long>(1),parentId)==0){
                    throw new IllegalArgumentException("父角色不存在,请检查后重试");
                }
            }
            RoleType _type = type == null ? RoleType.PROTECTED : type;
            Optional<Role> role = qr.insert("INSERT INTO t_role (parent_id,region,code,name,type,description,ext,langs,created_at,updated_at) VALUES (?,?,?,?,?,?,?,?,?,?)", rs -> {
                if(rs.next()){
                    Role val = new Role();
                    val.setId(rs.getInt(1));
                    val.setParentId(parentId);
                    val.setRegion(region);
                    val.setCode(code);
                    val.setName(name);
                    val.setType(_type);
                    val.setDescription(description);
                    val.setExt(ext);
                    val.setLangs(langs);
                    val.setCreatedAt(now);
                    val.setUpdatedAt(now);
                    return Optional.of(val);
                }
                return Optional.empty();
            }, parentId,region,code.trim(), name.trim(),_type.value(),Strings.nullToEmpty(description),ext == null? null : Jackson.object2Json(ext), langs == null ? null : Jackson.object2Json(langs),now, now);
            if(role.isPresent()){
                if(consumer != null){
                    consumer.accept(role.get());
                }
                return role.orElseThrow(()->ApplicationException.notifyUser("角色创建失败,无法获取ID")).getId();
            }
            return 0;
        } catch (IllegalArgumentException | ApplicationException e) {
            throw e;
        }catch (Exception e) {
            throw  ApplicationException.raise(e);
        }
    }



    @Transactional(rollbackFor = {ApplicationException.class,IllegalArgumentException.class})
    @Override
    public void delete(int id,Function<Integer,Boolean> function) {
        try {
            //查询此角色是否还有子角色
            if(qr.query("SELECT COUNT(*) FROM t_role WHERE parent_id = ? LIMIT 1",new ScalarHandler<Long>(1),id) > 0){
                throw new IllegalArgumentException("请先删除依赖角色之后，再重试");
            }
            //查询此role 是否有关联的用户
            if(qr.query("SELECT COUNT(*) FROM t_user_role WHERE role_id = ? LIMIT 1",new ScalarHandler<Long>(1),id) > 0){
                throw new IllegalArgumentException("此角色关联的有账户,请先调整账户角色");
            }
            boolean flag =  qr.update("DELETE FROM t_role WHERE id=?",id) > 0;
            if(!flag || (flag && function != null &&  !function.apply(id))){
               throw ApplicationException.notifyUser("删除角色失败");
            }
        } catch (IllegalArgumentException | ApplicationException e) {
            throw e;
        }catch (Exception e) {
            throw ApplicationException.raise(e);
        }
    }

    @Transactional(rollbackFor = {ApplicationException.class,IllegalArgumentException.class})
    @Override
    public boolean update(int id, int parentId,String name, String code, Consumer<Role> consumer) {
        return update(id,parentId,0,name,code,consumer);
    }

    @Transactional(rollbackFor = {ApplicationException.class,IllegalArgumentException.class})
    @Override
    public boolean update(int id, int parentId, int region, String name, String code, Consumer<Role> consumer) {
        checkArgument(id != 0 && (!Strings.isNullOrEmpty(name) && !Strings.isNullOrEmpty(code)) && (code.length() > 3 && code.length() < 15),"名称或编码不能为空,或者编码不符合规范");
        try {
            Optional<Role> role = get(id);
            Role $role = role.orElseThrow(() -> ApplicationException.notifyUser("无法获取角色数据"));
            $role.setName(name);
            $role.setCode(code);
            if(parentId !=0){
                $role.setParentId(parentId);
            }
            if(region > 0){
                $role.setRegion(region);
            }
            $role.setUpdatedAt(asDate(LocalDateTime.now()));
            boolean flag = qr.update("UPDATE t_role SET parent_id=?,region = ?,code=?,name=?,updated_at=? WHERE id=?",$role.getParentId(),region,$role.getCode(),$role.getName(),$role.getUpdatedAt(),id) > 0;
            if(flag && consumer != null){
                consumer.accept($role);
            }
            return flag;
        }catch ( ApplicationException | IllegalArgumentException e) {
            throw e;
        }catch (Exception ex){
            throw ApplicationException.raise(ex);
        }
    }



    
    @Override
    public Optional<Role> get(int id) {
        try {
            return query("id=?", id);
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
    }

    @Override
    public Optional<Role> of(String code) {
        try {
            return query("code=?", code);
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
    }


    private Optional<Role> query(String where, Object... params) throws SQLException {
        Locale locale = I18n.get();
        return qr.query("SELECT "+SQL_COLUMNS+" FROM t_role WHERE " + where, rs -> {
            if (rs.next()) {
                Role val = new Role();
                val.setId(rs.getInt(1));
                val.setParentId(rs.getInt(2));
                val.setCode(rs.getString(3));
                val.setName(rs.getString(4));
                String langs;
                if(!Strings.isNullOrEmpty(langs = rs.getString("langs"))){
                    val.setLangs(Jackson.json2Object(langs, Langs.class));
                    if(locale != null){
                        Lang lang = val.getLangs().get(locale);
                        if(lang != null && !Strings.isNullOrEmpty(lang.getName())){
                            val.setName(lang.getName());
                        }
                    }
                }
                var ext = rs.getString("ext");
                if(!Strings.isNullOrEmpty(ext)){
                    val.setExt(Jackson.json2Map(ext));
                }
                val.setCreatedAt(rs.getDate("created_at"));
                val.setUpdatedAt(rs.getDate("updated_at"));
                return Optional.of(val);
            }
            return Optional.empty();
        }, params);
    }

    
    @Override
    public List<Role> getAll(boolean all) {
        return getAll(all,null);
    }

    @Override
    public List<Role> getAll(boolean all, Map<String, Object> ext) {
        try {
            Locale locale = I18n.get();
            return qr.query(sql(SQL_COLUMNS,all,ext), rs -> {
                List<Role> roles = Lists.newArrayListWithCapacity(5);
                while (rs.next()){
                    roles.add($(locale,rs));
                }
                return roles;
            });
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            throw ApplicationException.raise(e);
        }
    }

    @Override
    public Pager<Role> paginate(int pageNumber, int pageSize, boolean all, Map<String, Object> ext) {
        try {
            Locale locale = I18n.get();
            String sql = sql(SQL_COLUMNS,all,ext);
            Pager<Role> pager = new Pager<>(pageNumber,pageSize);
            jdbcer.paginate(sql,pager,Role.class,rs->{
                List<Role> datas = Lists.newArrayListWithCapacity(2);
                while (rs.next()) {
                    datas.add($(locale,rs));
                }
                return datas;
            },null);
            return pager;
        } catch (Exception e) {
            throw ApplicationException.raise(e);
        }
    }

    private static Role $(Locale locale, ResultSet rs) throws SQLException {
        Role val = new Role();
        val.setId(rs.getInt(1));
        val.setParentId(rs.getInt(2));
        val.setRegion(rs.getInt(3));
        val.setCode(rs.getString(4));
        val.setName(rs.getString(5));
        val.setType(RoleType.of(rs.getInt(6)));
        val.setDescription(rs.getString(7));
        String langs;
        if(!Strings.isNullOrEmpty(langs = rs.getString("langs"))){
            val.setLangs(Jackson.json2Object(langs, Langs.class));
            if(locale != null){
                Lang lang = val.getLangs().get(locale);
                if(lang != null && !Strings.isNullOrEmpty(lang.getName())){
                    val.setName(lang.getName());
                }
            }
        }
        var $ext = rs.getString("ext");
        if(!Strings.isNullOrEmpty($ext)){
            val.setExt(Jackson.json2Map($ext));
        }
        val.setCreatedAt(rs.getDate("created_at"));
        val.setUpdatedAt(rs.getDate("updated_at"));
        return val;
    }
    @Override
    public List<Pair<Integer, String>> getOptions(boolean all) {
        return getOptions(all,null);
    }

    @Override
    public List<Pair<Integer, String>> getOptions(boolean all, Map<String, Object> ext) {
        try {
            Locale locale = I18n.get();
            return qr.query(sql("id,name,langs",all,ext),rs->{
                List<Pair<Integer,String>> roles = Lists.newArrayListWithCapacity(5);
                while (rs.next()){
                    String langs;
                    String name;
                    if(locale != null && (langs = rs.getString("langs")) != null){
                        Langs $langs = Jackson.json2Object(langs, Langs.class);
                        Lang lang;
                        if($langs != null && (lang = $langs.get(locale)) != null && !Strings.isNullOrEmpty(lang.getName())){
                            name = lang.getName();
                        }else{
                            name = rs.getString(2);
                        }
                    }else{
                        name = rs.getString(2);
                    }
                    roles.add(Pair.of(rs.getInt(1),name));
                }
                return roles;
            });
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
    }

    @Override
    public List<Triple<Integer, String, String>> getData(boolean all) {
        return getData(all,null);
    }

    protected String sql(String COLUMNS,boolean all, Map<String, Object> ext){
        String sql = "SELECT "+COLUMNS+" FROM t_role WHERE 1= 1 ";
        if(!all){
            sql+= " AND is_show = 1 ";
        }
        if(ext != null && !ext.isEmpty()){
            for(Map.Entry<?,?> entry : ext.entrySet()){
                Object value = entry.getValue();
                if(value instanceof String val){
                    sql += " AND ext->'$."+entry.getKey().toString()+"' = '"+ val+"'";
                }else {
                    sql += " AND ext->'$."+entry.getKey().toString()+"' = "+ value;
                }
            }
        }
        sql+= " ORDER BY id DESC";
        return sql;
    }
    @Override
    public List<Triple<Integer, String, String>> getData(boolean all, Map<String, Object> ext) {
        try {
            Locale locale = I18n.get();
            return qr.query(sql("id,code,name,langs",all,ext),rs->{
                List<Triple<Integer, String, String>> roles = Lists.newArrayListWithCapacity(5);
                while (rs.next()) {
                    String langs;
                    String name;
                    if(locale != null && (langs = rs.getString("langs")) != null){
                        Langs $langs = Jackson.json2Object(langs, Langs.class);
                        Lang lang;
                        if($langs != null && (lang = $langs.get(locale)) != null && !Strings.isNullOrEmpty(lang.getName())){
                            name = lang.getName();
                        }else{
                            name = rs.getString(2);
                        }
                    }else{
                        name = rs.getString(2);
                    }
                    roles.add(Triple.of(rs.getInt(1), name, rs.getString(3)));
                }
                return roles;
            });
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
    }

    @Override
    public boolean isBind(long userId) {
        try {
            return qr.query("SELECT COUNT(*) FROM t_user_role WHERE user_id=?", new ScalarHandler<Long>(1), userId) > 0;
        } catch (SQLException ex) {
            throw ApplicationException.raise(ex);
        }
    }

    @Transactional(rollbackFor = {ApplicationException.class, IllegalArgumentException.class, LockedAccountException.class})
    @Override
    public void user2Role0(long userId, Integer... roleId) {
        user2Role0(userId, null, roleId);
    }

    @Transactional(rollbackFor = {ApplicationException.class, IllegalArgumentException.class})
    @Override
    public void user2Role0(long userId, Function<Integer[], Boolean> function, Integer... roleId) {
        checkArgument(userId != 0 && (roleId != null && roleId.length >0),"参数不能为空");
        try {

            //检查角色是否合法
            checkRoleId(roleId);

            ////获取所有角色
            var allRoleIds = allRole(qr,roleId);
            //获取用户已有的角色
            List<Integer> hasRoleIds = roleIdsByUserId(userId,null,null,qr).orElse(Collections.emptyList());
            //过滤掉已经包含的角色
            Integer[] ids = Stream.of(allRoleIds).distinct().filter(id-> !hasRoleIds.contains(id)).toArray(size->new Integer[size]);
            if(ids.length> 0){
                user2Role(qr,function,userId,ids);
            }
        }catch (IllegalArgumentException | ApplicationException e) {
            throw e;
        }catch (Exception ex){
            throw ApplicationException.raise(ex);
        }
    }

    @Transactional(rollbackFor = {ApplicationException.class,IllegalArgumentException.class})
    @Override
    public void user2Role(long userId, Integer... roleId) {
         user2Role(userId,null,roleId);
    }

    @Transactional(rollbackFor = {ApplicationException.class,IllegalArgumentException.class})
    @Override
    public void user2Role(long userId,Function<Integer[],Boolean> function, Integer... roleId) {
        try {
            checkRoleId(roleId);
            if (isBind(userId) && qr.update("DELETE FROM t_user_role WHERE user_id=?", userId) == 0) {
                throw ApplicationException.notifyUser("删除用户角色失败，请稍后重试");
            }
            //获取所有角色(包含子角色)
            var ids = allRole(qr, roleId);
            if (ids.length > 0) {
                user2Role(qr, function, userId, ids);
            }
        } catch (IllegalArgumentException | ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw ApplicationException.raise(e);
        }
    }

    private void user2Role(QueryRunner qr,Function<Integer[],Boolean> function, long userId,Integer... roleId) throws SQLException {
        String sql = "INSERT INTO t_user_role (user_id,role_id) VALUES (?,?)";
        if(roleId.length == 1) {
            if (roleId[0] == null || roleId[0] == 0) {
                throw new IllegalArgumentException("roleId is null");
            }
            qr.insert(sql, rs -> null, userId, roleId[0]);
        }else {
            Object[][] params = new Object[roleId.length][2];
            for(var i=0;i < roleId.length;i++) {
                if (roleId[i] == null || roleId[i] == 0) {
                    throw new IllegalArgumentException("roleId is null");
                }
                params[i][0] = userId;
                params[i][1] = roleId[i];
            }
            qr.batch(sql,params);
        }
        if(function != null && !function.apply(roleId)){
            throw ApplicationException.notifyUser("用户添加角色失败,请查看服务器日志定位问题");
        }
    }

    @Transactional(rollbackFor = {ApplicationException.class,IllegalArgumentException.class})
    @Override
    public boolean user2Role(long userId, int oldRole, int newRole) {
        return user2Role(userId,oldRole,newRole,true);
    }

    @Transactional(rollbackFor = {ApplicationException.class,IllegalArgumentException.class})
    @Override
    public boolean user2Role(long userId, int oldRole, int newRole, boolean parent)  {
        checkArgument(oldRole!=newRole,"新角色与旧角色相等");
        checkArgument(userId !=0 && oldRole != 0 && newRole != 0 ,"参数不为空");
        try {
            checkRoleId(newRole);
            if(parent){
                //先获取老角色所包含的父子角色
                var oldRoleIds = allRole(qr,oldRole);
                var newRoleIds = allRole(qr,newRole);
                //先删除老角色
                delUserRole(false,null,userId,oldRoleIds);
                user2Role(qr,null,userId,newRoleIds);
                return true;
            }else{
                return qr.update("UPDATE t_user_role SET role_id=? WHERE user_id=? AND role_id=?",newRole,userId,oldRole) > 0;
            }
        }catch (IllegalArgumentException | ApplicationException e) {
            throw e;
        }catch (Exception ex){
            throw ApplicationException.raise(ex);
        }
    }

    @Transactional(rollbackFor = {ApplicationException.class,IllegalArgumentException.class})
    @Override
    public void delUserRole(long userId, Integer... roleId) {
        delUserRole(null,userId,roleId);
    }

    @Transactional(rollbackFor = ApplicationException.class)
    @Override
    public boolean clearUserRole(long userId) {
        try {
            return qr.update("DELETE FROM t_user_role WHERE user_id=?",userId) >0;
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
    }

    @Transactional(rollbackFor = {ApplicationException.class,IllegalArgumentException.class})
    @Override
    public void delUserRole(Function<Integer[], Boolean> function, long userId, Integer... roleId) {
        delUserRole(false,function,userId,roleId);
    }



    @Transactional(rollbackFor = {ApplicationException.class,IllegalArgumentException.class})
    @Override
    public void delUserRole(boolean parent, Function<Integer[],Boolean> function,long userId, Integer... roleId) {
        checkArgument(userId !=0 && roleId.length >0 ,"参数不为空");
        boolean flag;
        try {
            //是否查询父节点
            if(parent){
                roleId = allRole(qr,roleId);
            }
            if(roleId.length == 1) {
                if (roleId[0] == null || roleId[0] == 0) {
                    throw new IllegalArgumentException("roleId is null,can't to delete");
                }
                flag = qr.update("DELETE FROM t_user_role WHERE user_id=? AND role_id=?", userId, roleId[0]) > 0;
            }else {
                String sql = "DELETE FROM t_user_role WHERE user_id=? AND role_id IN (";
                for (Integer id : roleId) {
                    if (id == null || id == 0) {
                        throw new IllegalArgumentException("roleId is null,can't to delete");
                    }
                    sql += id + ",";
                }
                sql = sql.substring(0, sql.length() - 1);
                sql += ")";
                flag = qr.update(sql, userId) > 0;
            }
            if(!flag || (function != null && !function.apply(roleId))){
                throw ApplicationException.notifyUser("角色操作失败");
            }
        }catch (IllegalArgumentException | ApplicationException e) {
            throw e;
        }catch (Exception ex){
            throw ApplicationException.raise(ex);
        }
    }




    
    @Override
    public Optional<List<String>> rolesByUserId(long userId, Supplier<Optional<List<String>>> supplier,Consumer<List<String>> consumer) {
        try {
            if(supplier != null){
                Optional<List<String>> data = supplier.get();
                if(data.isPresent()){
                    return data;
                }
            }
            List<String> data =  roleCodeByUserId(userId);
            if(!data.isEmpty()){
                if(consumer != null){
                    consumer.accept(data);
                }
                return Optional.of(data);
            }
            return Optional.empty();
        } catch (IllegalArgumentException | ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw ApplicationException.raise(e);
        }
    }

    
    @Override
    public Optional<List<Integer>> roleIdsByUserId(long userId, Supplier<Optional<List<Integer>>> supplier, Consumer<List<Integer>> consumer) {
        return roleIdsByUserId(userId, supplier, consumer, qr);
    }

    /**
     * 根据用户id获取角色code
     *
     * @param userId
     * @return
     */
    
    @Override
    public List<String> roleCodeByUserId(long userId) {
        try {
            return qr.query("SELECT r.code FROM t_user_role ur LEFT JOIN t_role r ON ur.role_id=r.id WHERE ur.user_id=?", rs -> {
                List<String> roleCodes = Lists.newArrayListWithCapacity(3);
                while (rs.next()) {
                    roleCodes.add(rs.getString(1));
                }
                return roleCodes;
            },userId);
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
    }
    
    @Override
    public List<Integer> roleIdByUserId(long userId) {
        try {
            return qr.query("SELECT role_id FROM t_user_role WHERE user_id=?", rs->{
                List<Integer> roleIds = Lists.newArrayListWithCapacity(3);
                while (rs.next()){
                    roleIds.add(rs.getInt(1));
                }
                return roleIds;
            },userId);
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
    }

    
    @Override
    public List<Pair<Integer,String>> roleByUserId(long userId) {
        try {
            return qr.query("SELECT r.id,r.code FROM t_user_role ur LEFT JOIN t_role r ON ur.role_id=r.id WHERE ur.user_id=?", rs -> {
                List<Pair<Integer, String>> roles = Lists.newArrayListWithCapacity(3);
                while (rs.next()){
                    roles.add(Pair.of(rs.getInt(1),rs.getString(2)));
                }
                return roles;
            },userId);
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
    }

    protected Optional<List<Integer>> roleIdsByUserId(long userId,Supplier<Optional<List<Integer>>> supplier,Consumer<List<Integer>> consumer,QueryRunner qr) {
        try {
            if(supplier != null){
                Optional<List<Integer>> data = supplier.get();
                if(data.isPresent()){
                    return data;
                }
            }
            var data = roleIdByUserId(userId);
            if(!data.isEmpty()){
                if(consumer != null){
                    consumer.accept(data);
                }
                return Optional.of(data);
            }
            return Optional.empty();
        } catch (IllegalArgumentException | ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw ApplicationException.raise(e);
        }
    }

    /**
     * 通过角色父子关系获取所有角色
     * @param qr
     * @param roleId
     * @return
     * @throws SQLException
     */
    private Integer[] allRole(QueryRunner qr,Integer... roleId)throws SQLException{
        List<Integer> allRoleIds = Lists.newArrayListWithCapacity(2);
        findRoleParent(qr,allRoleIds,roleId);
        return allRoleIds.stream().distinct().toArray(size->new Integer[size]);
    }

    private void findRoleParent(QueryRunner qr,int id,List<Integer> ids) throws SQLException {
        int parent_id = qr.query("SELECT parent_id FROM t_role WHERE id = ? ",new ScalarHandler<>(1),id);
        ids.add(id);
        if(parent_id !=0){
            //含有父角色
            findRoleParent(qr,parent_id,ids);
        }
    }

    private void findRoleParent(QueryRunner qr,List<Integer> ids,Integer... id) throws SQLException {
        if(id.length == 1){
            findRoleParent(qr,id[0],ids);
        }else {
            String sql = "SELECT id,parent_id FROM t_role WHERE id IN (";
            for(Integer i : id){
                sql += i+",";
            }
            sql = sql.substring(0,sql.length()-1);
            sql += ")";

            List<Integer[]> datas = qr.query(sql, rs -> {
                List<Integer[]> $v = Lists.newArrayListWithCapacity(id.length);
                while (rs.next()){
                    $v.add(new Integer[]{rs.getInt(1),rs.getInt(2)});
                }
                return $v;
            });
            //添加没有父角色的ID
//            ids.addAll(datas.stream().filter(val->val[1] ==0).map(val->val[0]).distinct().collect(toList()));
            Stream.of(id).forEach(v->ids.add(v));
            //筛选含有父角色的ID
            Integer[] parentId = datas.stream().filter(val->val[1] !=0).map(val->val[1]).distinct().toArray(size->new Integer[size]);
            if(parentId.length >0){
                findRoleParent(qr,ids,parentId);
            }
        }

    }

    /**
     * 检查Role ID 是否合法
     * @param roleId
     * @throws SQLException
     */
    
    @Override
    public void checkRoleId(Integer... roleId)  {
        String sql = "SELECT COUNT(*) FROM t_role WHERE id ";
        try {
            checkId(sql,roleId);
        } catch (SqlException e) {
            throw ApplicationException.raise(e);
        }
    }


}
