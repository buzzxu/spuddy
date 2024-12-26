package io.github.buzzxu.spuddy.security.services;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.github.buzzxu.spuddy.dal.jdbc.Jdbcer;
import io.github.buzzxu.spuddy.exceptions.ApplicationException;
import io.github.buzzxu.spuddy.i18n.I18n;
import io.github.buzzxu.spuddy.jackson.Jackson;
import io.github.buzzxu.spuddy.objects.Pager;
import io.github.buzzxu.spuddy.objects.i18n.Lang;
import io.github.buzzxu.spuddy.objects.i18n.Langs;
import io.github.buzzxu.spuddy.security.MenuService;
import io.github.buzzxu.spuddy.security.objects.Menu;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static io.github.buzzxu.spuddy.security.objects.Menu.COLUMNS;
import static io.github.buzzxu.spuddy.util.Dates.asDate;
import static java.util.stream.Collectors.toList;

@Slf4j
public class StandardMenu extends AbstractStandard implements MenuService {
    private final String tableName;
    @Resource
    private Jdbcer jdbcer;

    public StandardMenu() {
        this("t_menu");
    }
    public StandardMenu(String tableName) {
        this.tableName = tableName;
    }

    @Transactional(rollbackFor = {ApplicationException.class, IllegalArgumentException.class})
    @Override
    public int create(Menu menu) {
        return create(menu.getParentId(),menu.getName(),menu.getCode(),menu.getTarget(),menu.getPath(),menu.getIcon(),menu.getRemark(),menu.getExt(),menu.getLangs(),null);
    }

    @Transactional(rollbackFor = {ApplicationException.class, IllegalArgumentException.class})
    @Override
    public int create(Integer parentId, String name, String code, String target, String icon, String remark, Function<Menu, Boolean> function) {
        return create(parentId, name, code, target,null, icon, remark, function);
    }

    @Transactional(rollbackFor = {ApplicationException.class, IllegalArgumentException.class})
    @Override
    public int create(Integer parentId, String name, String code, String target, String icon, String remark, Map<String, Object> ext, Function<Menu, Boolean> function) {
        return create(parentId, name, code, target,null, icon, remark, ext,function);
    }

    @Transactional(rollbackFor = {ApplicationException.class, IllegalArgumentException.class})
    @Override
    public int create(Integer parentId, String name,String code, String target,String path, String icon, String remark,Function<Menu, Boolean> function) {
        return create(parentId, name, code, target,path, icon, remark, null,function);
    }

    @Transactional(rollbackFor = {ApplicationException.class, IllegalArgumentException.class})
    @Override
    public int create(Integer parentId, String name, String code, String target, String path,String icon, String remark, Map<String, Object> ext, Function<Menu, Boolean> function) {
        return create(parentId, null,name, code, target, path,icon, remark, ext,null,function);
    }

    @Override
    public int create(Integer parentId, String region, String name, String code, String target, String path, String icon, String remark, Map<String, Object> ext, Function<Menu, Boolean> function) {
        return create(parentId, region,name, code, target, path,icon, remark, ext,null,function);
    }

    @Transactional(rollbackFor = {ApplicationException.class, IllegalArgumentException.class})
    @Override
    public int create(Integer parentId, String name, String code, String target, String path,String icon, String remark, Map<String,Object> ext,Langs langs, Function<Menu, Boolean> function) {
        return create(parentId,null,name,code,target,path,icon,remark,ext,langs,function);
    }

    @Transactional(rollbackFor = {ApplicationException.class, IllegalArgumentException.class})
    @Override
    public int create(Integer parentId, String region, String name, String code, String target, String path, String icon, String remark, Map<String, Object> ext, Langs langs, Function<Menu, Boolean> function) {
        checkArgument(!Strings.isNullOrEmpty(name) ,"菜单名称必填");
        if(parentId == null){
            parentId = 0;
        }
        try {
            Optional<Menu> menu= qr.insert("INSERT INTO  "+tableName+" (parent_id,region,name,code,target,path,icon,ext,langs,remark,created_at) VALUES (?,?,?,?,?,?,?,?,?,?,?)", rs->{
                if(rs.next()){
                    Menu val = new Menu();
                    val.setId(rs.getInt(1));
                    return Optional.of(val);
                }
                return Optional.empty();
            },parentId,Strings.nullToEmpty(region),name,code,target,path,icon,ext == null ? null : Jackson.object2Json(ext),langs == null ? null : Jackson.object2Json(langs),Strings.nullToEmpty(remark),asDate(LocalDateTime.now()));
            if(menu.isPresent()){
                if(function != null && !function.apply(menu.get())){
                    throw new IllegalArgumentException("菜单创建被系统拒绝");
                }
                return menu.orElseThrow(()->ApplicationException.notifyUser("菜单创建失败,无法获取ID")).getId();
            }
            return 0;
        } catch (IllegalArgumentException | ApplicationException e) {
            log.error("创建菜单,发生异常: {}",e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("创建菜单,发生异常: {}",e.getMessage(),e);
            throw ApplicationException.raise(e);
        }
    }

    @Transactional(rollbackFor = {ApplicationException.class, IllegalArgumentException.class})
    @Override
    public boolean update(Menu menu) {
        checkNotNull(menu,"菜单信息不能为空");
        checkArgument(menu.getId() !=0,"菜单ID不能为0");
        checkArgument(!Strings.isNullOrEmpty(menu.getName()),"请填写菜单名称");
        try {
            return qr.update("UPDATE "+tableName+ " SET parent_id = ?,enable =?,name=?,code=?,target=?,path = ?,icon=?,ext = ?,langs= ?,remark=?,sort=? WHERE id = ?"
                    ,menu.getParentId() == null ? 0 : menu.getParentId()
                    ,menu.isEnable()
                    ,menu.getName()
                    ,menu.getCode()
                    ,menu.getTarget()
                    ,menu.getPath()
                    ,menu.getIcon()
                    ,menu.getExt() == null ? null : Jackson.object2Json(menu.getExt())
                    ,menu.getLangs() == null ? null : Jackson.object2Json(menu.getLangs())
                    ,Strings.nullToEmpty(menu.getRemark())
                    ,menu.getSort() == 0 ? 100: menu.getSort()
                    ,menu.getId()) > 0;
        } catch (SQLException e) {
            log.error("修改菜单,发生异常: {}",e.getMessage(),e);
            throw ApplicationException.raise(e);
        }
    }

    @Transactional(rollbackFor = {ApplicationException.class, IllegalArgumentException.class})
    @Override
    public boolean delete(Function<Set<Integer>, Boolean> function, int... id) {
        try {
            checkArgument(id.length !=0 ,"必须传入菜单ID");
            Set<Integer> ids = Sets.newHashSetWithExpectedSize(id.length);
            for(int $id : id){
                //检查此ID是否还有子菜单 如果有子菜单，则跳过,只删除没有子节点的菜单
                if(hasChildNode($id)){
                    log.info("菜单[{}]还有子菜单,无法删除,只删除没有子节点的菜单",$id);
                    continue;
                }
                //检查是否已经关联角色，如果有 删除
                delMenuRole($id);
                //获取父节点ID
                Optional<Integer> parentId = parentId($id);
                //删除当前节点
                if(delete($id)){
                    ids.add($id);
                    //检查当前节点的父节点 没有子节点了，则删除
                    //跳过根节点
                    parentId.ifPresent($parentId->{
                        try {
                            if($parentId > 0 && !hasChildNode($parentId)){
                                //删除父节点的 角色关系
                                delMenuRole($parentId);
                                //删除父节点
                                if(!delete($parentId)){
                                    throw ApplicationException.argument("删除父节点ID[%d]失败",parentId);
                                }
                                ids.add($parentId);
                            }
                        }catch (SQLException ex){
                            throw ApplicationException.raise(ex);
                        }
                    });
                }else{
                    throw ApplicationException.argument("删除节点ID[%d]失败",$id);
                }
            }
            if(!ids.isEmpty() && function!= null && !function.apply(ids)){
                throw ApplicationException.argument("删除节点后处理异常,请检查日志");
            }
            return !ids.isEmpty();
        } catch (IllegalArgumentException | ApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除菜单,发生异常: {}",e.getMessage(),e);
            throw ApplicationException.raise(e);
        }
    }

    /**
     * 获取父节点
     * @param id
     * @return
     * @throws SQLException
     */
    private Optional<Integer> parentId(int id) throws SQLException {
        Integer parentId = qr.query("SELECT parent_id FROM "+tableName+" WHERE id=?",new ScalarHandler<>(1),id);
        return Optional.ofNullable(parentId);
    }
    /**
     * 是否含有子节点
     * @param id
     * @return
     * @throws SQLException
     */
    private  boolean hasChildNode(int id) throws SQLException {
        return qr.query("SELECT COUNT(*) FROM "+tableName+" WHERE parent_id=? LIMIT 1",new ScalarHandler<Long>(1),id) >0;
    }

    private boolean delete(int id) throws SQLException {
        return qr.update("DELETE FROM "+tableName+" WHERE id=?",id) >0;
    }

    /**
     * 删除菜单对应角色的所有关联关系
     * @param id
     * @throws SQLException
     */
    private void delMenuRole(Integer id) throws SQLException {
        if(qr.query("SELECT COUNT(*) FROM t_role_menu WHERE menu_id=? LIMIT 1",new ScalarHandler<Long>(1),id) >0){
            if(qr.update("DELETE FROM t_role_menu WHERE menu_id=?",id) ==0){
                throw ApplicationException.notifyUser("删除关联角色失败");
            }
        }
    }

    @Transactional(rollbackFor = ApplicationException.class)
    @Override
    public boolean reEnable(Integer id) {
        try {
            boolean enable = qr.query("SELECT enable FROM "+tableName+" WHERE id=?",new ScalarHandler<>(1),id);
            if(hasChildNode(id)){
                //关闭此节点所有子节点的
                List<Integer> childIds = findChildIds(id);
                String sql = "UPDATE "+tableName+" SET enable="+!enable+" WHERE id ";
                sql += whereIds( childIds) +" AND enable="+enable;
                if(qr.update(sql) != childIds.size()){
                    throw ApplicationException.notifyUser("修改节点失败,请检查数据");
                }
            }else{
                if(qr.update("UPDATE "+tableName+" SET enable="+!enable+" WHERE id= ? AND enable="+enable,id) ==0){
                    throw ApplicationException.notifyUser("修改菜单失败,请检查数据");
                }
            }
            return !enable;
        }catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw ApplicationException.raise(e);
        }
    }
    @Transactional(rollbackFor = {ApplicationException.class, IllegalArgumentException.class})
    @Override
    public boolean menu2Role(int roleId, Set<Integer> menuIds,Function<List<Integer>,Boolean> function) {
        //只存储子节点，不保存父节点
        try {
            List<Object[]> ids = Lists.newArrayListWithCapacity(3);
            log.info("已删除的角色[{}]菜单,数量:[{}]",roleId,qr.update("DELETE FROM t_role_menu WHERE role_id = ?",roleId));
            for(int id : menuIds){
                //当前节点是否合法
                if(!check(id)){
                    throw ApplicationException.notifyUser("不存在ID["+id+"]的菜单");
                }
                //判断是否已经包含角色关系
                if(qr.query("SELECT COUNT(*) FROM t_role_menu WHERE role_id =? AND menu_id=? LIMIT 1",new ScalarHandler<Long>(1),roleId,id) >0){
                    continue;
                }
                //先判断当前ID是否含有子节点，如果有，跳过
                if(hasChildNode(id)){
                    continue;
                }
                //判断是否已经包含此关系 则跳过
//                if(qr.query("SELECT COUNT(*) FROM t_role_menu WHERE role_id=? AND menu_id=?",new ScalarHandler<Long>(1),roleId,id) >0){
//                    continue;
//                }
                ids.add(new Object[]{roleId,id});
            }
            if(!ids.isEmpty()){
                Object[][] params = new Object[ids.size()][];
                ids.toArray(params);
                qr.insertBatch("INSERT INTO t_role_menu (role_id,menu_id) VALUES(?, ?)",rs->null,params);
                if(function!=null && !function.apply(ids.stream().map(val->(int)val[1]).collect(toList()))){
                    throw ApplicationException.notifyUser("角色关联菜单后处理异常，请检查日志");
                }
                return true;
            }
            return false;
        }catch (ApplicationException e) {
            log.error("设置菜单角色,发生异常: {}",e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("设置菜单角色,发生异常: {}",e.getMessage(),e);
            throw ApplicationException.raise(e);
        }
    }

    @Override
    public List<Menu> getTreeByRoleId(String region, Map<String, Object> ext, int... roleId) throws SecurityException {
        try {
            String sql = "SELECT "+Menu.COLUMNS_PREFIX+" FROM t_role_menu rm ";
            if(ext != null){
                sql += " JOIN t_role r ON (rm.role_id = r.id) ";
            }
            sql += " LEFT JOIN "+tableName+" m ON rm.menu_id = m.id  WHERE rm.role_id "+whereIds(IntStream.of(roleId).boxed().toList())+" AND m.enable=1 ";
            if(region != null){
                sql += " AND m.region = '"+region+"' ";
            }
            List<Object> params = Lists.newArrayListWithCapacity(3);
            if(ext != null){
                if(ext.containsKey("json")){
                    sql += " AND JSON_OVERLAPS(r.ext,?) ";
                    params.add(Jackson.object2Json(ext.get("json")));
                }else{
                    sql += " AND (";
                    for(Map.Entry<?,?> entry : ext.entrySet()){
                        Object value = entry.getValue();
                        if(value instanceof String val){
                            sql += "  r.ext->'$."+entry.getKey().toString()+"' = '"+ val+"' AND ";
                        }else {
                            sql += "  r.ext->'$."+entry.getKey().toString()+"' = "+ value.toString() + " AND ";
                        }
                    }
                    sql += " 1 = 1 ) ";
                }
            }
            return menuTree(sql,this::getOnlyEnable,params.toArray());
        }catch (Exception ex){
            log.error("查询菜单,发生异常: {}",ex.getMessage(),ex);
            throw ApplicationException.notifyUser("用户角色异常，请重新登录或联系管理员");
        }
    }

    @Override
    public List<Menu> getTreeByRoleId(Supplier<List<Menu>> supplier, Consumer<List<Menu>> consumer, int... roleId) throws SecurityException {
        return getTreeByRoleId(supplier,consumer,"",roleId);
    }

    @Override
    public List<Menu> getTreeByRoleId(Supplier<List<Menu>> supplier, Consumer<List<Menu>> consumer, String region, int... roleId) throws SecurityException {
        checkArgument(supplier!=null && consumer !=null,"需要设置角色菜单的缓存存取方式");
        try {
            List<Menu> menus = supplier.get();
            if(menus == null || menus.isEmpty()){
                //直接查询数据库
                menus = getTreeByRoleId(region,roleId);
                if(menus == null || menus.isEmpty()){
                    throw ApplicationException.notifyUser("无法获取菜单，请重新登录或联系管理员");
                }
                consumer.accept(menus);
            }
            return menus;
        }catch (ApplicationException e) {
            log.error("查询菜单,发生异常: {}",e.getMessage(),e);
            throw e;
        }catch (Exception e) {
            log.error("查询菜单,发生异常: {}",e.getMessage(),e);
            throw ApplicationException.notifyUser("用户角色异常，请重新登录或联系管理员");
        }
    }

    @Override
    public List<Menu> findByRoleId(int... roleIds) throws SecurityException {
        return findByRoleId("",roleIds);
    }

    @Override
    public List<Menu> findByRoleId(String region, int... roleIds) throws SecurityException {
        try {
            String sql = "SELECT m.id,m.region,m.name,m.parent_id,m.code,m.enable,m.target,m.path,m.icon,m.depth,m.remark,m.sort,m.ext,m.langs FROM t_role_menu rm LEFT JOIN "+tableName+" m ON rm.menu_id = m.id  WHERE rm.role_id "+whereIds(IntStream.of(roleIds).boxed().toList())+" AND m.enable=1 ";
            if(region != null){
                sql += " AND m.region = '"+region+"' ";
            }
            return queryMenus(sql,(Object[])null);
        } catch (SQLException e) {
            log.error("查询菜单,发生异常: {}",e.getMessage(),e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Integer> findIdsByRoleIds(String region, Map<String, Object> ext, int... roleIds) throws SecurityException {
        try {
            String sql = "SELECT m.id FROM t_role_menu rm ";
            if(ext != null){
                sql += " JOIN t_role r ON (rm.role_id = r.id) ";
            }
            sql += " LEFT JOIN "+tableName+" m ON rm.menu_id = m.id  WHERE rm.role_id "+whereIds(IntStream.of(roleIds).boxed().toList())+" AND m.enable=1 ";
            if(region != null){
                sql += " AND m.region = '"+region+"' ";
            }
            List<Object> params = Lists.newArrayListWithCapacity(3);
            if(ext != null){
                if(ext.containsKey("json")){
                    sql += " AND JSON_OVERLAPS(r.ext,?) ";
                    params.add(Jackson.object2Json(ext.get("json")));
                }else{
                    sql += " AND (";
                    for(Map.Entry<?,?> entry : ext.entrySet()){
                        Object value = entry.getValue();
                        if(value instanceof String val){
                            sql += "  r.ext->'$."+entry.getKey().toString()+"' = '"+ val+"' AND ";
                        }else {
                            sql += "  r.ext->'$."+entry.getKey().toString()+"' = "+ value.toString() + " AND ";
                        }
                    }
                    sql += " 1 = 1 ) ";
                }
            }
            return qr.query(sql,rs->{
                List<Integer> vals = Lists.newArrayListWithCapacity(1);
                while (rs.next()){
                    vals.add(rs.getInt("id"));
                }
                return vals;
            },params.toArray());
        } catch (SQLException e) {
            log.error("查询菜单,发生异常: {}",e.getMessage(),e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Menu> getAll() {
        return getAll(null);
    }

    @Override
    public List<Menu> getAll(Map<String, Object> ext) {
        try {
            return queryMenus(sql(ext),(Object[])null);
        } catch (SQLException e) {
            log.error("查询菜单,发生异常: {}",e.getMessage(),e);
            throw ApplicationException.raise(e);
        }
    }

    @Override
    public List<Menu> getOnlyEnable() {
        try {
            return queryMenus("SELECT "+COLUMNS+" FROM "+tableName+" WHERE enable=1 ORDER BY sort DESC",(Object[])null);
        } catch (SQLException e) {
            log.error("查询菜单,发生异常: {}",e.getMessage(),e);
            throw ApplicationException.raise(e);
        }
    }
    @Override
    public List<Menu> getAllTree() {
        return getAllTree(null);
    }

    @Override
    public List<Menu> getAllTree(Map<String, Object> ext) {
        try {
            return menuTree(Sets.newHashSet(queryMenus(sql(ext))));
        } catch (ApplicationException e) {
            log.error(e.getMessage(),e);
            throw e;
        }catch (Exception e) {
            log.error(e.getMessage(),e);
            throw ApplicationException.raise(e);
        }
    }

    @Override
    public Pager<Menu> paginate(int pageNumber, int pageSize,String region, Map<String, Object> ext) {
        String sql = "SELECT "+COLUMNS+" FROM "+tableName + " WHERE parent_id = 0  ";
        if(region != null){
            sql += " AND region = '"+region+"' ";
        }
        if(ext != null && !ext.isEmpty()){
            sql += " AND (";
            for(Map.Entry<?,?> entry : ext.entrySet()){
                Object value = entry.getValue();
                if(value instanceof String val){
                    sql += " ext->'$."+entry.getKey().toString()+"' = '"+ val+"' AND ";
                }else {
                    sql += " ext->'$."+entry.getKey().toString()+"' = "+ value + " AND ";
                }
            }
            sql += " 1=1 ) ";
        }
        sql += "  ORDER BY sort DESC ";
        Pager<Menu> pager = new Pager<>(pageNumber,pageSize);
        Locale locale = I18n.get();
        jdbcer.paginate(sql,pager,Menu.class,rs->{
            List<Menu> vals = Lists.newArrayListWithCapacity(3);
            while (rs.next()){
                Menu menu = $(locale,rs);
                vals.add(menu);
            }
            return vals;
        },null).forEach(menu -> {
            try {
                menu.setChilds(findChildMenus(menu.getId()));
            } catch (SQLException e) {
                throw ApplicationException.raise("查询子菜单失败(id=%d)",menu.getId());
            }
        });
        return pager;
    }

    protected String sql(Map<String, Object> ext){
        String sql = "SELECT "+COLUMNS+" FROM "+tableName;
        if(ext != null && !ext.isEmpty()){
            sql += " WHERE ";
            for(Map.Entry<?,?> entry : ext.entrySet()){
                Object value = entry.getValue();
                if(value instanceof String val){
                    sql += " ext->'$."+entry.getKey().toString()+"' = '"+ val+"' AND ";
                }else {
                    sql += " ext->'$."+entry.getKey().toString()+"' = "+ value + " AND ";
                }
            }
            sql += " 1=1 ";
        }
        sql += "  ORDER BY sort DESC ";
        return sql;
    }

    protected List<Menu> menuTree(String sql, Supplier<List<Menu>> supplier) {
        return menuTree(sql,supplier,(Object[])null);
    }
    protected List<Menu> menuTree(String sql, Supplier<List<Menu>> supplier,Object... params) {
        try {
            List<Menu> roleMenu = queryMenus(sql,params);
            return menuTree(roleMenu,supplier);
        } catch (Exception e) {
            log.error("查询菜单,发生异常: {}",e.getMessage(),e);
            throw  ApplicationException.raise(e);
        }
    }
    protected List<Menu> menuTree(List<Menu> roleMenu, Supplier<List<Menu>> supplier) {
        try {
            List<Menu> allMenu = supplier.get();
            //获取每一个菜单的父菜单链
            Set<Menu> chainSet = Sets.newHashSetWithExpectedSize(5);
            for (Menu menu : roleMenu) {
                chainSet.add(menu);
                findParentId(allMenu, menu, chainSet);
            }
           return menuTree(chainSet);
        } catch (ApplicationException e) {
            log.error("查询菜单,发生异常: {}",e.getMessage(),e);
            throw  e;
        } catch (Exception e) {
            log.error("查询菜单,发生异常: {}",e.getMessage(),e);
            throw  ApplicationException.raise(e);
        }
    }

    protected List<Menu> menuTree(Set<Menu> chainSet)  {
        try {
            List<Menu> chainList = Lists.newArrayList(chainSet);
            //倒序
            chainList.sort((left, rigth) -> {
                if (left.getSort() > rigth.getSort()) {
                    return -1;
                } else if (left.getSort() < rigth.getSort()) {
                    return 1;
                } else {
                    return 0;
                }
            });
            findSubMenu(chainList);
            return chainList.stream().filter(m-> m.getParentId() ==0).collect(toList());
        }catch (Exception e) {
            log.error("查询菜单,发生异常: {}",e.getMessage(),e);
            throw  ApplicationException.raise(e);
        }
    }
    Optional<Menu> queryMenu(String sql,Object... params) throws SQLException {
        Locale locale = I18n.get();
        return qr.query(sql,rs->{
            if (rs.next()){
                return Optional.of($(locale,rs));
            }
            return Optional.empty();
        },params);
    }
    List<Menu> queryMenus(String sql,Object... params) throws SQLException {
        Locale locale = I18n.get();
        return qr.query(sql,rs->{
            List<Menu> vals = Lists.newArrayListWithCapacity(3);
            while (rs.next()){
                vals.add($(locale,rs));
            }
            return vals;
        },params);
    }

    private static Menu $(Locale locale,ResultSet rs) throws SQLException {
        Menu menu = new Menu();
        menu.setId(rs.getInt(1));
        menu.setRegion(rs.getString(2));
        menu.setName(rs.getString(3));
        menu.setParentId(rs.getInt(4));
        menu.setCode(rs.getString(5));
        menu.setEnable(rs.getBoolean(6));
        menu.setTarget(rs.getString(7));
        menu.setPath(rs.getString(8));
        menu.setIcon(rs.getString(9));
        menu.setDepth(rs.getInt(10));
        menu.setRemark(rs.getString(11));
        menu.setSort(rs.getInt(12));
        String langs;
        if(locale != null && !Strings.isNullOrEmpty(langs = rs.getString("langs"))){
            menu.setLangs(Jackson.json2Object(langs, Langs.class));
            Lang lang = menu.getLangs().get(locale);
            if(lang != null){
                if(!Strings.isNullOrEmpty(lang.getName())){
                    menu.setName(lang.getName());
                }
                if(!Strings.isNullOrEmpty(lang.getRemark())){
                    menu.setRemark(lang.getRemark());
                }
            }
        }
        var ext = rs.getString("ext");
        if(!Strings.isNullOrEmpty(ext)){
            menu.setExt(Jackson.json2Map(ext));
        }
        return menu;
    }

    @Override
    public List<Integer> findChildIds(int id) {
        List<Integer> ids = Lists.newArrayListWithCapacity(3);
        try {
            findChildIds(id,ids);
        } catch (SQLException e) {
            log.error("查询菜单,发生异常: {}",e.getMessage(),e);
            throw ApplicationException.raise(e);
        }
        return ids;
    }

    @Override
    public Optional<Menu> of(int id) {
        try {
            if(id < 0){
                return Optional.empty();
            }
            return queryMenu("SELECT " + COLUMNS + " FROM "+tableName+" WHERE id=? LIMIT 1",id);
        } catch (SQLException throwables) {
            log.error("查询菜单,发生异常: {}",throwables.getMessage(),throwables);
            throw ApplicationException.raise(throwables);
        }
    }

    @Override
    public Optional<Menu> one(Map<String, Object> params) {
        String sql = "SELECT " + COLUMNS + " FROM "+tableName +" WHERE 1=1 ";
        Object[] args = new Object[params.size()];
        int i=0;
        for(Map.Entry<String,Object> entry : params.entrySet()){
            sql += " AND "+ entry.getKey() + " = ? ";
            args[i] = entry.getValue();
            i++;
        }
        sql += " LIMIT 1 ";
        try {
            return queryMenu(sql,args);
        } catch (SQLException throwables) {
            log.error("查询菜单,发生异常: {}",throwables.getMessage(),throwables);
            throw ApplicationException.raise(throwables);
        }
    }


    private boolean check(int id) throws SQLException {
        return qr.query("SELECT COUNT(*) FROM "+tableName+" WHERE id=? LIMIT 1",new ScalarHandler<Long>(1),id) >0;
    }

    private void findParentId(Collection<Menu> list, Menu menu, Set<Menu> chainlist) throws SQLException {
       for(Menu m : list){
           if(Objects.equals(menu.getParentId(), m.getId())){
               chainlist.add(m);
               findParentId(list, m, chainlist);
           }
       }
    }
    private void findSubMenu(Collection<Menu> list) {
        for (Menu menu1 : list) {
            for (Menu menu2 : list) {
                if(Objects.equals(menu1.getId(), menu2.getParentId())) {
                    menu1.getChilds().add(menu2);
                }
            }
        }
    }

    private void findChildIds(int id,List<Integer> ids) throws SQLException {
        if(hasChildNode(id)){
           List<Object[]> datas = qr.query("SELECT id FROM "+tableName+" WHERE parent_id=?",new ArrayListHandler(),id);
           for(Object[] data:datas){
               findChildIds((int)data[0],ids);
           }
        }
        ids.add(id);
    }

    private List<Menu> findChildMenus(int parentId) throws SQLException {
        List<Menu> childs = null;
        if(hasChildNode(parentId)){
            childs = queryMenus("SELECT " + COLUMNS + " FROM "+tableName+" WHERE parent_id=?",parentId);
            for(Menu menu :childs){
               menu.setChilds(findChildMenus(menu.getId()));
            }
        }
        return childs;
    }
}
