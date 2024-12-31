package io.github.buzzxu.spuddy.security.services;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.buzzxu.spuddy.errors.*;
import io.github.buzzxu.spuddy.exceptions.ApplicationException;
import io.github.buzzxu.spuddy.exceptions.RepeatException;
import io.github.buzzxu.spuddy.i18n.I18n;
import io.github.buzzxu.spuddy.objects.Pair;
import io.github.buzzxu.spuddy.objects.RealnameVerified;
import io.github.buzzxu.spuddy.security.OAuthType;
import io.github.buzzxu.spuddy.security.OrganizationService;
import io.github.buzzxu.spuddy.security.UserService;
import io.github.buzzxu.spuddy.security.UserSource;
import io.github.buzzxu.spuddy.security.exceptions.LoginFailException;
import io.github.buzzxu.spuddy.security.exceptions.PasswordNullException;
import io.github.buzzxu.spuddy.security.exceptions.RepeatRegException;
import io.github.buzzxu.spuddy.security.objects.OAuthUser;
import io.github.buzzxu.spuddy.security.objects.PrivilegeInfo;
import io.github.buzzxu.spuddy.security.objects.User;
import io.github.buzzxu.spuddy.security.objects.UserInfo;
import io.github.buzzxu.spuddy.util.Arrays;
import io.github.buzzxu.spuddy.util.Password;
import io.github.buzzxu.spuddy.util.Patterns;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import java.lang.SecurityException;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkArgument;
import static io.github.buzzxu.spuddy.util.Dates.asDate;
import static io.github.buzzxu.spuddy.util.Dates.asLocalDateTime;
import static io.github.buzzxu.spuddy.util.Patterns.name;
import static java.util.stream.Collectors.joining;

/**
 * @author xux
 * @date 2018/5/22 下午2:58
 */
@Slf4j
public class StandardUser extends AbstractStandard implements UserService {

    @Resource
    private OrganizationService organizationService;
    private static final int SALT_COUNT = 12;



    @Transactional(rollbackFor = {Exception.class, ApplicationException.class, DuplicateException.class, IllegalArgumentException.class})
    @Override
    public <U extends User> long create(U userInfo, Consumer<U> consumer) {
        checkArgument(userInfo != null ,i18n("用户信息不能为空"));
        try {
            if(userInfo.isMobileIsUserName() && !Strings.isNullOrEmpty(userInfo.getMobile())){
                userInfo.setUserName(userInfo.getMobile());
            }
            if (userInfo.getOrgId() != 0) {
                checkArgument(organizationService.exists(userInfo.getOrgId()), i18n("组织架构不存在,请检查后再次尝试"));
            }
            if(!Strings.isNullOrEmpty(userInfo.getRealName())){
                checkArgument(name(userInfo.getRealName()),i18n("请输入真实的姓名"));
            }
            if (!Strings.isNullOrEmpty(userInfo.getUserName())) {
                if (!userInfo.isMobileIsUserName() && !(Patterns.userName(userInfo.getUserName())
                        || Patterns.userNameф(userInfo.getUserName()))) {
                    throw new IllegalArgumentException(i18n("非法的用户名格式,请重新输入"));
                }
                if (queryCount("user_name", userInfo.getUserName()) > 0) {
                    throw new RepeatRegException(i18n("已存在相同的用户名,请更换其他名称"),"userName");
                }
            }
            if (!Strings.isNullOrEmpty(userInfo.getMobile())) {
                if (!Patterns.mobile(userInfo.getMobile())) {
                    throw new IllegalArgumentException(i18n("非法的手机号格式,请重新输入"));
                }
                if(queryCount("mobile",userInfo.getMobile()) > 0){
                    throw new RepeatRegException(i18n("已存在相同的手机号,请更换其他号码"),"mobile");
                }
            }
            if (!Strings.isNullOrEmpty(userInfo.getEmail())) {
                if(!Patterns.email(userInfo.getEmail())){
                    throw new IllegalArgumentException(i18n("非法的Email格式,请重新输入"));
                }
                if(queryCount("email",userInfo.getEmail()) > 0){
                    throw new RepeatRegException(i18n("已存在相同的Email,请更换其他地址"),"email");
                }
            }
            if(!Strings.isNullOrEmpty(userInfo.getPassword())){
                var salt = Password.salt(SALT_COUNT);
                var password = Password.password(userInfo.getPassword(),salt);
                userInfo.setSalt(salt);
                userInfo.setPassword(password);
            }
//            昵称允许重复
//            if(!Strings.isNullOrEmpty(userInfo.getNikeName())){
//                checkArgument(!existedNikeName(userInfo.getNikeName()),"昵称已存在,请更换");
//            }
            long time = Instant.now().getEpochSecond();
            String sql = "INSERT INTO t_user_base(user_name,mobile,email,gender,password,salt,org_id,type,real_name,nick_name,avatar,source,verified,is_2fa,created_at,updated_at) values (?,?,?,?,?,?,?,?,?,?,?,?,false,false,?,?)";
            long userId = qr.insert(sql, rs -> {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return 0L;
            }, userInfo.getUserName(), userInfo.getMobile(), userInfo.getEmail(), userInfo.getGender(), userInfo.getPassword(), userInfo.getSalt(), userInfo.getOrgId(), userInfo.getType(), userInfo.getRealName(), userInfo.getNickName(), userInfo.getAvatar(), userInfo.getSource(), time, time);
            userInfo.setId(userId);
            if (userInfo.getOAuthUser() != null ){
                OAuthUser oAuthUser = userInfo.getOAuthUser();
                oAuthUser.setUserId(userId);
                bind(oAuthUser);
            }
            if(consumer != null){
                consumer.accept(userInfo);
            }
            return userId;
        } catch (ApplicationException | IllegalArgumentException e) {
            throw e;
        } catch (Exception ex) {
            throw ApplicationException.raise(ex);
        }
    }

    private long queryCount(String column,Object value) throws SQLException {
        // 请查看示例 https://gist.github.com/retanoj/5fd369524a18ab68a4fe7ac5e0d121e8
        return qr.query("SELECT COUNT(*) FROM t_user_base WHERE "+column+" = ? AND deleted = false ", new ScalarHandler<Long>(1), value);
    }

    @Transactional(rollbackFor = {ApplicationException.class, IllegalArgumentException.class})
    @Override
    public <U extends User> long create(String userName, String password, int type, UserSource source,Consumer<U> consumer,Class<U> clazz) {
        checkArgument(!Strings.isNullOrEmpty(userName) && !Strings.isNullOrEmpty(password),i18n("用户名或密码不能为空"));
        if (!(Patterns.userName(userName) || Patterns.userNameф(userName))) {
            throw new IllegalArgumentException(i18n("非法的用户名格式,请重新输入"));
        }
        String sql = "INSERT INTO t_user_base(user_name,password,salt,type,source,verified,is_2fa,created_at,updated_at) values (?,?,?,?,?,false,false,?,?)";
        return insert(sql, rs -> {
            try {
                if (rs.next()) {
                    U val = clazz.getDeclaredConstructor().newInstance();
                    val.setId(rs.getLong(1));
                    val.setUserName(userName);
                    val.setStatus(1);
                    val.setType(type);
                    val.setSource(source.val());
                    return Optional.of(val);
                }
            }catch (Exception ex){
                throw ApplicationException.raise(ex);
            }
            return Optional.empty();
        }, s -> {
            try {
                return s.query("SELECT COUNT(*) FROM t_user_base WHERE user_name = ?  AND deleted = false ", new ScalarHandler<Long>(1), userName) > 0;
            } catch (SQLException e) {
                throw ApplicationException.raise(e);
            }

        }, consumer, new PasswordSupplier(userName, password,type,source.val()));
    }
    @Transactional(rollbackFor = {ApplicationException.class, IllegalArgumentException.class})
    @Override
    public <U extends User> long create(String userName, int type,UserSource source,Consumer<U> consumer,Class<U> clazz) {
        checkArgument(!Strings.isNullOrEmpty(userName),i18n("用户名不能为空"));
        long time = Instant.now().getEpochSecond();
        String sql = "INSERT INTO t_user_base(user_name,type,source,verified,is_2fa,created_at,updated_at) values (?,?,?,false,false,?,?)";
        return insert0(sql, s -> {
            try {
                return s.query("SELECT COUNT(*) FROM t_user_base WHERE user_name = ?  AND deleted = false ", new ScalarHandler<Long>(1), userName) > 0;
            } catch (SQLException e) {
                throw ApplicationException.raise(e);
            }

        },clazz,consumer,()->new Object[]{userName,type,source.val(),time,time});
    }
    @Transactional(rollbackFor = {ApplicationException.class, IllegalArgumentException.class})
    @Override
    public long create(String userName,String password, int type, UserSource source,Consumer<UserInfo> consumer) {
        return create(userName,password,type,source,consumer, UserInfo.class);
    }

    @Override
    public long create(String userName, int type, UserSource source,Consumer<UserInfo> consumer) {
        return create(userName,type,source,consumer,UserInfo.class);
    }

    @Transactional(rollbackFor = {ApplicationException.class, IllegalArgumentException.class})
    @Override
    public <U extends User> long createByMobile(String mobile, String password, int type,UserSource source,Consumer<U> consumer,Class<U> clazz) {
        return createByMobile(mobile,password,type,source,$mobile-> Patterns.mobile(mobile),consumer,clazz);
    }
    @Transactional(rollbackFor = {ApplicationException.class, IllegalArgumentException.class})
    @Override
    public <U extends User> long createByMobile(String mobile, String password, int type, UserSource source, Predicate<String> predicate, Consumer<U> consumer, Class<U> clazz) {
        checkArgument(!Strings.isNullOrEmpty(mobile) && !Strings.isNullOrEmpty(password),i18n("手机号或密码不能为空"));
        checkArgument(predicate.test(mobile),i18n("非法的手机号格式,请重新输入"));
        String sql = "INSERT INTO t_user_base(mobile,password,salt,type,source,is_2fa,created_at,updated_at) values (?,?,?,?,?,false,?,?)";
        return insert(sql, rs -> {
            if (rs.next()) {
                try {
                    U val = clazz.getDeclaredConstructor().newInstance();
                    val.setId(rs.getLong(1));
                    val.setMobile(mobile);
                    val.setStatus(1);
                    val.setType(type);
                    val.setSource(source.val());
                    return Optional.of(val);
                }catch (Exception ex){
                    throw ApplicationException.raise(ex);
                }
            }
            return Optional.empty();
        }, s -> {
            try {
                return s.query("SELECT COUNT(*) FROM t_user_base WHERE mobile = ? AND deleted = false LIMIT 1 ", new ScalarHandler<Long>(1), mobile) > 0;
            } catch (SQLException e) {
                throw ApplicationException.raise(e);
            }

        }, consumer, new PasswordSupplier(mobile, password,type,source.val()));
    }


    @Transactional(rollbackFor = {ApplicationException.class, IllegalArgumentException.class})
    @Override
    public <U extends User> long createByMobile(String mobile, int type,UserSource source,Consumer<U> consumer,Class<U> clazz) {
        checkArgument(!Strings.isNullOrEmpty(mobile),i18n("手机号不能为空"));
        String sql = "INSERT INTO t_user_base(mobile,type,source,is_2fa,created_at,updated_at) values (?,?,?,false,?,?)";
        if (!Patterns.mobile(mobile)) {
            throw new IllegalArgumentException(i18n("非法的手机号格式,请重新输入"));
        }
        long time = Instant.now().getEpochSecond();
        return insert0(sql, s -> {
            try {
                return s.query("SELECT COUNT(*) FROM t_user_base WHERE mobile = ? AND deleted = false LIMIT 1", new ScalarHandler<Long>(1), mobile) > 0;
            } catch (SQLException e) {
                throw ApplicationException.raise(e);
            }

        },clazz,consumer,()->new Object[]{mobile,type,source.val(),time,time});
    }
    @Transactional(rollbackFor = {ApplicationException.class, IllegalArgumentException.class})
    @Override
    public long createByMobile(String mobile, String password,int type, UserSource source,Consumer<UserInfo> consumer) {
        return createByMobile(mobile,password,type,source,consumer,UserInfo.class);
    }


    @Transactional(rollbackFor = {ApplicationException.class, IllegalArgumentException.class})
    @Override
    public <U extends User> long createByEmail(String email, String password, int type,UserSource source,Consumer<U> consumer,Class<U> clazz) {
        checkArgument(!Strings.isNullOrEmpty(email) && !Strings.isNullOrEmpty(password),i18n("邮箱或密码不能为空"));
        if (!Patterns.email(email)) {
            throw new IllegalArgumentException(i18n("非法的Email格式,请重新输入"));
        }
        String sql = "INSERT INTO t_user_base(email,password,salt,type,source,is_2fa,created_at,updated_at) values (?,?,?,?,?,false,?,?)";
        return insert(sql, rs -> {
            if (rs.next()) {
                try {
                    U val = clazz.getDeclaredConstructor().newInstance();
                    val.setId(rs.getLong(1));
                    val.setEmail(email);
                    val.setStatus(1);
                    val.setType(type);
                    val.setSource(source.val());
                    return Optional.of(val);
                }catch (Exception ex){
                    throw ApplicationException.raise(ex);
                }
            }
            return Optional.empty();
        }, s -> {
            try {
                return s.query("SELECT COUNT(*) FROM t_user_base WHERE email = ? AND deleted = false LIMIT 1", new ScalarHandler<Long>(1), email) > 0;
            } catch (SQLException e) {
                throw ApplicationException.raise(e);
            }

        }, consumer, new PasswordSupplier(email, password,type,source.val()));
    }
    @Transactional(rollbackFor = {ApplicationException.class, IllegalArgumentException.class})
    @Override
    public <U extends User> long createByEmail(String email, int type,UserSource source,Consumer<U> consumer,Class<U> clazz) {
        checkArgument(!Strings.isNullOrEmpty(email),i18n("邮箱不能为空"));
        String sql = "INSERT INTO t_user_base(email,type,source,is_2fa,created_at,updated_at) values (?,?,?,false,?,?)";
        if (!Patterns.email(email)) {
            throw new IllegalArgumentException(i18n("非法的Email格式,请重新输入"));
        }
        long time = Instant.now().getEpochSecond();
        return insert0(sql, s -> {
            try {
                return s.query("SELECT COUNT(*) FROM t_user_base WHERE email = ? AND deleted = false LIMIT 1", new ScalarHandler<Long>(1), email) > 0;
            } catch (SQLException e) {
                throw ApplicationException.raise(e);
            }

        },clazz,consumer,()->new Object[]{email,type,source.val(),time,time});
    }
    @Transactional(rollbackFor = {ApplicationException.class, IllegalArgumentException.class})
    @Override
    public long createByEmail(String email,String password, int type, UserSource source,Consumer<UserInfo> consumer) {
        return createByEmail(email,password,type,source,consumer,UserInfo.class);
    }

    @Transactional(rollbackFor = {ApplicationException.class, IllegalArgumentException.class})
    @Override
    public boolean delete(long userId) {
        return delete(userId,true);
    }

    @Transactional(rollbackFor = {ApplicationException.class, IllegalArgumentException.class})
    @Override
    public boolean delete(long userId, boolean all) {
        try {
            checkArgument(userId > 0,"必须传入用户ID");
            //清理基础表
            if(all){
                if(qr.query("SELECT COUNT(*) FROM t_user_base WHERE id = ? LIMIT 1",new ScalarHandler<Long>(1),userId) > 0){
                    if(qr.update("DELETE FROM t_user_base WHERE id=?",userId) == 0){
                        throw ApplicationException.raise("清理用户基础信息失败,userId=%d",userId);
                    }
                }
            }else{
                //逻辑删除
                if(qr.update("UPDATE t_user_base SET status = false,deleted = true,updated_at=? WHERE id = ?",Instant.now().getEpochSecond(),userId) == 0){
                    throw new IllegalArgumentException(i18n("清理账户基础信息失败")); //清理账户基础信息失败
                }
            }
            //清理角色
            //用户角色不一定有关系，不强制校验
            if(((Long)qr.query("SELECT COUNT(*) FROM t_user_role WHERE user_id = ? LIMIT 1",new ScalarHandler<>(1),userId)) > 0){
                qr.update("DELETE FROM t_user_role WHERE user_id=?",userId);
            }
            //删除三方账户
            if(((Long)qr.query("SELECT COUNT(*) FROM t_user_oauth WHERE user_id = ? LIMIT 1",new ScalarHandler<>(1),userId)) > 0){
                if ( qr.update("DELETE FROM t_user_oauth WHERE user_id = ?",userId) == 0){
                    log.error("清理三方登录信息失败,userId=[{}}",userId);
                    throw ApplicationException.raise("清理三方登录信息失败[userId=%d]",userId);
                }
            }
            return true;
        }catch (IllegalArgumentException ex){
            throw ex;
        }catch (Exception ex){
            throw ApplicationException.raise(ex);
        }
    }

    @Transactional(rollbackFor = {ApplicationException.class, IllegalArgumentException.class})
    @Override
    public boolean destroy(long userId) {
        return delete(userId,false);
    }



    @Transactional(rollbackFor = {ApplicationException.class, IllegalArgumentException.class})
    @Override
    public boolean recover(long userId, int type) {
        return recover(userId,type,0);
    }
    @Transactional(rollbackFor = {ApplicationException.class, IllegalArgumentException.class})
    @Override
    public boolean recover(long userId, int type, int roleId) {
        try {
            checkArgument(userId > 0,"必须传入用户ID");
            if(qr.update("UPDATE t_user_base SET type = ? ,updated_at = ? WHERE id=?",type,Instant.now().getEpochSecond(),userId) == 0){
                throw ApplicationException.raise("恢复用户类型失败,userId=%d,type=%d",userId,type);
            }
            if(roleId > 0 || (qr.query("SELECT EXISTS (SELECT * FROM t_user_role WHERE user_id = ?)",new ScalarHandler<Long>(1),userId)) > 0){
                if(roleId >0){
                    qr.update("UPDATE t_user_role SET role_id = ? WHERE user_id=?",roleId,userId);
                }else{
                    qr.update("DELETE FROM t_user_role WHERE user_id=?",userId);
                }
            }
        }catch (IllegalArgumentException ex){
            throw ex;
        }catch (Exception ex){
            throw ApplicationException.raise(ex);
        }
        return true;
    }

    protected  <U extends User> long insert(String sql, ResultSetHandler<Optional<U>> rsh, Predicate<QueryRunner> test, Consumer<U> consumer, Supplier<Object[]> params) {
        if (test.test(qr)) {
            throw new IllegalArgumentException(i18n("已存在相同的用户名,请更换其他名称")); //已存在相同的用户名,请更换其他名称
        }
        Optional<U> user;
        try {
            Object[] $params = params.get();
            user = qr.insert(sql, rsh, $params);
            if (user.isPresent() && consumer != null) {
                U $user = user.get();
                $user.setPassword((String)$params[1]);
                $user.setSalt((String)$params[2]);
                $user.setType((int)$params[3]);
                consumer.accept($user);
            }
        } catch (SQLException ex) {
            throw ApplicationException.raise(ex);
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception ex) {
            throw ApplicationException.raise(ex);
        } finally {

        }
        return user.orElseThrow(() -> new IllegalArgumentException("保存用户失败，无法获取用户信息")).getId();
    }

    protected  <U extends User> long insert0(String sql,  Predicate<QueryRunner> test,Class<U> clazz, Consumer<U> consumer, Supplier<Object[]> params) {
        if (test.test(qr)) {
            throw ApplicationException.notifyUser(i18n("已存在相同的用户名,请更换其他名称")); //已存在相同的名称，请重新输入
        }
        Optional<U> user;
        try {
            Object[] $params = params.get();
            user = qr.insert(sql, rs->{
                if (rs.next()) {
                    try {
                        U val = clazz.getDeclaredConstructor().newInstance();
                        val.setId(rs.getLong(1));
                        val.setStatus(1);
                        val.setType((int)$params[1]);
                        val.setSource((int)$params[2]);
                        return Optional.of(val);
                    }catch (Exception ex){
                        throw ApplicationException.raise(ex);
                    }
                }
                return Optional.empty();
            }, $params);
            if (user.isPresent() && consumer != null) {
                consumer.accept(user.get());
            }
        } catch (SQLException ex) {
            throw ApplicationException.raise(ex);
        } catch (IllegalArgumentException | ApplicationException e) {
            throw e;
        } catch (Exception ex) {
            throw ApplicationException.raise(ex);
        } finally {

        }
        return user.orElseThrow(() -> new IllegalArgumentException("保存用户失败，无法获取用户ID")).getId();
    }

    @Override
    public Optional<Long> verify(String name) throws LockedAccountException {
        if (exist(name)) {
            String sql = "SELECT id,status,type FROM t_user_base WHERE ", column;
            if (Patterns.mobile(name)) {
                column = " mobile=? ";
            } else if (Patterns.email(name)) {
                column = " email=? ";
            } else {
                column = " user_name=? ";
            }
            //先查询ID 和 密码
            try {
                Object[] result = qr.query(sql + column + " AND deleted = false LIMIT 1", rs -> {
                    if (rs.next()) {
                        return new Object[]{rs.getLong(1), rs.getInt(2), rs.getInt(3)};
                    }
                    return null;
                }, name);
                if (result == null) {
                    throw new NotFoundException(i18n("账户不存在,请重新输入"));
                }
                if ((int) result[1] == 0) {
                    throw new LockedAccountException(i18n("您的账户已被禁用,请联系客服查询详情"));
                }
                return Optional.of((Long) result[0]);
            } catch (SQLException ex) {
                throw ApplicationException.raise(ex);
            }

        }
        throw new NotFoundException(i18n("账户不存在,请重新输入"));
    }

    @Override
    public Optional<Long> verify(String name, int... types) throws LockedAccountException {
        checkArgument(types != null && types.length >= 1, i18n("请设置账户类型"));
        if (exist(name, types)) {
            String sql = "SELECT id,status,type FROM t_user_base WHERE ", column;
            List<Object> params = Lists.newArrayListWithCapacity(types.length + 1);
            if (types.length == 1) {
                sql += " type = ? AND ";
                params.add(types[0]);
            } else {
                sql += " type IN (";
                for (int i = 0, size = types.length; i < size; i++) {
                    sql += "?";
                    if (i != size - 1) {
                        sql += ",";
                    }
                    params.add(types[i]);
                }
                sql += ") AND ";
            }
            if (Patterns.mobile(name)) {
                column = " mobile=? ";
            } else if (Patterns.email(name)) {
                column = " email=? ";
            } else {
                column = " user_name=? ";
            }
            params.add(name);
            //先查询ID 和 密码
            try {
                Object[] result = qr.query(sql + column + " AND deleted = false", rs -> {
                    if (rs.next()) {
                        return new Object[]{rs.getLong(1), rs.getInt(2), rs.getInt(3)};
                    }
                    return null;
                }, params.toArray(new Object[params.size()]));
                if (result == null) {
                    throw ApplicationException.argument(i18n("账户不存在,请重新输入"));
                }
                if ((int) result[1] == 0) {
                    throw new LockedAccountException(i18n("您的账户已被禁用,请联系客服查询详情"));
                }
                return Optional.of((Long) result[0]);
            } catch (IllegalArgumentException|ApplicationException ex) {
                throw ex;
            }catch (SQLException ex) {
                throw ApplicationException.raise(ex);
            }

        }
        throw new NotFoundException(i18n("账户不存在,请重新输入"));
    }

    
    @Override
    public Optional<Long> verifyPwd(String name, String pwd) throws UnauthorizedException, LockedAccountException {
        if (exist(name)) {
            //查询密码进行验证
            String sql = "SELECT id,password,status,type FROM t_user_base WHERE ", column;
            if (Patterns.mobile(name)) {
                column = " mobile=? ";
            } else if (Patterns.email(name)) {
                column = " email=? ";
            } else {
                column = " user_name=? ";
            }
            return loginVerify(sql + column,pwd,name);
        } else {
            throw new NotFoundException(i18n("账户不存在,请重新输入"));
        }
    }

    @Override
    public Optional<Long> verifyPwd(int type, String name, String pwd) throws UnauthorizedException, LockedAccountException {
        if (exist(name,type)) {
            //查询密码进行验证
            String sql = "SELECT id,password,status,type FROM t_user_base WHERE type=? AND ", column;
            if (Patterns.mobile(name)) {
                column = " mobile=? ";
            } else if (Patterns.email(name)) {
                column = " email=? ";
            } else {
                column = " user_name=? ";
            }
            return loginVerify(sql + column, pwd, type, name);
        } else {
            throw new NotFoundException(i18n("账户不存在,请重新输入"));
        }
    }

    @Override
    public Optional<Long> verifyPwd(String name, String pwd, int... types) throws UnauthorizedException, LockedAccountException {
        checkArgument(types != null && types.length >= 1, i18n("请设置账户类型"));
        if (exist(name, types)) {
            List<Object> params = Lists.newArrayListWithCapacity(types.length + 1);
            //查询密码进行验证
            String sql = "SELECT id,password,status,type FROM t_user_base WHERE ", column;
            if (types.length == 1) {
                sql += " type=? AND ";
                params.add(types[0]);
            } else {
                sql += " type IN (";
                for (int i = 0, size = types.length; i < size; i++) {
                    sql += "?";
                    if (i != size - 1) {
                        sql += ",";
                    }
                    params.add(types[i]);
                }
                sql += ") AND ";
            }
            if (Patterns.mobile(name)) {
                column = " mobile=? ";
            } else if (Patterns.email(name)) {
                column = " email=? ";
            } else {
                column = " user_name=? ";
            }
            params.add(name);
            return loginVerify(sql + column, pwd, params.toArray(new Object[params.size()]));
        } else {
            throw new NotFoundException(i18n("账户不存在,请重新输入"));
        }
    }

    private Optional<Long> loginVerify(String sql, String pwd, Object... params) throws LockedAccountException {
        try {
            //先查询ID 和 密码
            Object[] result = qr.query(sql + " AND deleted = false", rs -> {
                if (rs.next()) {
                    return new Object[]{rs.getLong(1), rs.getString(2), rs.getInt(3), rs.getInt(4)};
                }
                return null;
            }, params);
            if(result == null){
                throw new NotFoundException(i18n("账户不存在,请重新输入"));
            }
            Object password;
            if((password = result[1]) == null){
                throw new PasswordNullException();
            }
            //校验密码
            if (Password.checkPwd(pwd, (String) password)) {
                //密码验证成功,通过ID查询用户状态
                if ((int) result[2] == 0) {
                    throw new LockedAccountException(i18n("您的账户已被禁用,请联系客服查询详情"));
                }
                return Optional.of((Long) result[0]);
            }
            throw new LoginFailException(i18n("您的密码有误,请重新输入"));
        } catch (LoginFailException | NotFoundException | IllegalArgumentException | LockedAccountException ex) {
            throw ex;
        }catch (UnauthorizedException  ex) {
            throw ex;
        } catch (ApplicationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw ApplicationException.raise(ex);
        }
    }

    
    @Override
    public  <U extends User> Optional<U> get(long userId, Class<U> clazz) {
        return load(" id = ? ",userId,clazz);
    }

    @Override
    public <U extends User> Optional<U> get(String name, Class<U> clazz) {
        String sql = "";
        if (Patterns.mobile(name)) {
            sql += " mobile = ? ";
        } else if (Patterns.email(name)) {
            sql += " email = ? ";
        } else {
            sql += " user_name = ? ";
        }
        return load(sql,name,clazz);
    }

    private <U extends User> Optional<U> load(String where ,Object value,Class<U> clazz){
        try {
            return qr.query("SELECT id,org_id,user_name,mobile,email,gender,status,password,salt,real_name,nick_name,avatar,source,firstlogin,type,merge,is_2fa AS use2FA,secret_2fa AS secret2FA,created_at,updated_at FROM t_user_base WHERE " + where + " AND deleted = false ", rs -> {
                try {
                    if (rs.next()) {
                        U val = clazz.getDeclaredConstructor().newInstance();
                        rowMapeer(val,rs);
                        return Optional.of(val);
                    }
                    return Optional.empty();
                }catch (Exception ex){
                    throw ApplicationException.raise(ex);
                }

            }, value);
        } catch (ApplicationException e) {
            throw e;
        }catch (Exception e) {
            throw ApplicationException.raise(e);
        }
    }

    public static <U extends User> void rowMapeer(U val, ResultSet rs)throws SQLException{
        val.setId(rs.getLong("id"));
        val.setOrgId(rs.getInt("org_id"));
        val.setUserName(rs.getString("user_name"));
        val.setMobile(rs.getString("mobile"));
        val.setEmail(rs.getString("email"));
        val.setGender(rs.getInt("gender"));
        val.setStatus(rs.getInt("status"));
        val.setPassword(rs.getString("password"));
        val.setSalt(rs.getString("salt"));
        val.setRealName(rs.getString("real_name"));
        val.setNickName(rs.getString("nick_name"));
        val.setAvatar(rs.getString("avatar"));
        val.setSource(rs.getInt("source"));
        val.setFirstLogin(rs.getBoolean("firstlogin"));
        val.setType(rs.getInt("type"));
        val.setMerge(rs.getBoolean("merge"));
        val.setUse2FA(rs.getBoolean("use2FA"));
        val.setSecret2FA(rs.getString("secret2FA"));
        val.setCreatedAt(asLocalDateTime(Instant.ofEpochSecond(rs.getLong("created_at"))));
        val.setUpdatedAt(asLocalDateTime(Instant.ofEpochSecond(rs.getLong("updated_at"))));
    }

    
    @Override
    public Optional<UserInfo> get(long userId) {
        return get(userId,UserInfo.class);
    }

    @Override
    public Optional<String> mobile(long userId) {
        return column(userId,"mobile");
    }

    @Override
    public List<String> mobile(List<Long> userIds) {
        String sql = "SELECT mobile FROM t_user_base WHERE id IN ("+userIds.stream().map(v-> v.toString()).collect(joining(","))+")";
        try {
            return qr.query(sql, new ColumnListHandler<>("mobile"));
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
    }

    @Override
    public Optional<String> realName(long userId) {
        return column(userId,"real_name");
    }

    @Override
    public Optional<String> nickName(long userId) {
        return column(userId,"nick_name");
    }

    @Override
    public Optional<Long> orgId(long userId) {
        return column(userId,"org_id");
    }

    @Override
    public <T> Optional<T> column(long userId, String column){
        try {
            return Optional.ofNullable(qr.query("SELECT " +column+ " FROM t_user_base WHERE id = ? AND deleted = false", new ScalarHandler<>(1), userId));
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
    }
    /**
     * 合并账户
     * @param primary
     * @param target
     * @param <U>
     * @return
     */
    @Transactional
    @Override
    public <U extends User> boolean merge(long primary, U target, Consumer<U> consumer) {
        checkArgument(target != null && target.getId() != null ,i18n("用户信息不能为空"));
        checkArgument(primary != target.getId(),"无法对同一个账户进行合并");
        try {
            if (((Long) qr.query("SELECT EXISTS (SELECT * FROM t_user_base WHERE id = ? AND merge = true)", new ScalarHandler<>(1), primary)) > 0) {
                throw ApplicationException.argument("只允许合并一次基础信息");
            }
            if ((Long) qr.query("SELECT EXISTS (SELECT * FROM t_user_base WHERE id = ? AND status = 1 AND merge=false)", new ScalarHandler<>(1), target.getId()) == 0) {
                log.error("用户[id={}]信息无法获取，或状态异常,中止合并账户", target.getId());
                throw ApplicationException.argument("基础信息异常,中止合并账户", target.getId());
            }
            if (qr.update("UPDATE t_user_base SET org_id=?,type = ?,user_name = ?,mobile=?,email=?,nick_name=?,real_name=?,avatar=?,status=?,gender=?,firstlogin=?,merge=true,is_2fa=?,secret_2fa=?,updated_at=? WHERE id = ? AND merge = false "
                    , target.getOrgId()
                    , target.getType()
                    , target.getUserName()
                    , target.getMobile()
                    , target.getEmail()
                    , target.getNickName()
                    , target.getRealName()
                    , target.getAvatar()
                    , target.getStatus()
                    , target.getGender()
                    , target.isFirstLogin()
                    ,target.isUse2FA()
                    ,target.getSecret2FA()
                    ,Instant.now().getEpochSecond()
                    ,primary) > 0){
                consumer.andThen(u->{
                    if(!delete(target.getId())){
                        throw ApplicationException.notifyUser("合并过程中,清理基本信息失败,合并中止",target.getId());
                    }
                }).accept(target);

                return true;
            }
            return false;
        } catch (ApplicationException | IllegalArgumentException e) {
            throw e;
        }catch (SQLException e) {
            throw ApplicationException.raise("合并基本信息失败",e);
        }
    }

    @Transactional(rollbackFor = {ApplicationException.class, SecurityException.class, LockedAccountException.class, ForbiddenException.class})
    @Override
    public boolean changePassword(long userId, String oldPwd, String newPwd,Function<String,Boolean> function) throws LockedAccountException {
        checkArgument(userId!=0 && !Strings.isNullOrEmpty(oldPwd)&& !Strings.isNullOrEmpty(newPwd),i18n("原密码或旧密码不能为空"));//原密码或旧密码不能为空
        checkArgument(!StringUtils.equals(oldPwd,newPwd),i18n("新密码不能与旧密码相同,请选择其他密码")); //新密码不能与旧密码相同,请选择其他密码
        if (!Patterns.password(newPwd)) {
            throw new IllegalArgumentException(i18n("密码格式必须大于6位,且小于16位,数字或字母")); //密码格式必须大于6位，且小于16位，数字或字母
        }
        isDisableThrow(userId);
        try {
            Object[] salt$Pwd = qr.query("SELECT salt,password FROM t_user_base WHERE id=? LIMIT 1", new ArrayHandler(), userId);
            String salt = salt$Pwd[0] == null ? null : (String) salt$Pwd[0], oldEncry = salt$Pwd[1] == null ? null : (String) salt$Pwd[1];
            if (Strings.isNullOrEmpty(salt) || Strings.isNullOrEmpty(oldEncry)) {
                throw ApplicationException.raise("无法获取加密数据");
            }
            if (Password.checkPwd(oldPwd, oldEncry)) {
                String encryption = Password.password(newPwd, salt);
                if(StringUtils.equals(oldEncry,encryption)){
                    throw new IllegalArgumentException(i18n("您最近使用过此密码,请选择其他密码")); //您最近使用过此密码，请选择其他密码
                }
                if(qr.update("UPDATE t_user_base SET password=?,updated_at=? WHERE id=?", encryption, Instant.now().getEpochSecond(),userId) > 0){
                    return function != null ? function.apply(encryption) : true;
                }
                return false;
            } else {
                throw ApplicationException.notifyUser(i18n("密码验证失败,请重新输入"));//密码验证失败,请重新输入
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (SecurityException | ForbiddenException ex) {
            throw ex;
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw ApplicationException.raise(e);
        }
    }

    @Transactional(rollbackFor = {ApplicationException.class, SecurityException.class, IllegalArgumentException.class})
    @Override
    public boolean resetPassword(long userId, Supplier<String> defaultPwd) {
        checkArgument(userId!=0 && defaultPwd != null,i18n("必须提供默认密码"));//必须提供默认密码
        if (Strings.isNullOrEmpty(defaultPwd.get()) || !Patterns.password(defaultPwd.get())) {
            throw new IllegalArgumentException(i18n("密码格式必须大于6位,且小于16位,数字或字母"));
        }
        try {
            String salt = salt(userId, qr).orElseThrow(() -> new IllegalArgumentException("无法获取用户密码盐值"));
            String encryption = Password.password(defaultPwd.get(), salt);
            return qr.update("UPDATE t_user_base SET password=?,updated_at=? WHERE id=?", encryption, Instant.now().getEpochSecond(),userId) > 0;
        } catch (IllegalArgumentException | ApplicationException e) {
            throw e;
        } catch (SecurityException ex) {
            throw ex;
        } catch (Exception e) {
            throw ApplicationException.raise(e);
        }
    }
    @Transactional(rollbackFor = {ApplicationException.class, SecurityException.class, IllegalArgumentException.class})
    @Override
    public boolean newPassword(long userId, Supplier<String> defaultPwd) {
        checkArgument(userId != 0 && defaultPwd != null, i18n("请填写新密码")); //请填写新密码
        if (Strings.isNullOrEmpty(defaultPwd.get()) || !Patterns.password(defaultPwd.get())) {
            throw new IllegalArgumentException(i18n("密码格式必须大于6位,且小于16位,数字或字母"));
        }
        try {
            String salt = salt(userId, qr).orElse(Password.salt(SALT_COUNT));
            String encryption = Password.password(defaultPwd.get(), salt);
            return qr.update("UPDATE t_user_base SET password=?,salt=?,updated_at=? WHERE id=?", encryption, salt,Instant.now().getEpochSecond(),userId) > 0;
        } catch (IllegalArgumentException | ApplicationException e) {
            throw e;
        } catch (SecurityException ex) {
            throw ex;
        } catch (Exception e) {
            throw ApplicationException.raise(e);
        }
    }

    
    @Override
    public boolean existedNickName(String name) {
        try {
            return queryCount("nick_name",name) > 0;
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
    }

    
    @Override
    public boolean exist(long id) {
        checkArgument(id !=0 ,"请传入ID");
        try {
            Long count = qr.query("SELECT COUNT(*) FROM t_user_base WHERE id=?", new ScalarHandler<>(1), id);
            return count != null && count > 0;
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
    }

    
    @Override
    public boolean exist(String name) {
        checkArgument(!Strings.isNullOrEmpty(name),"登录标识不能为空");
        String sql = "SELECT COUNT(*) FROM t_user_base WHERE ";
        if (Patterns.mobile(name)) {
            sql += " mobile = ? ";
        } else if (Patterns.email(name)) {
            sql += " email = ? ";
        } else {
            sql += " user_name = ? ";
        }
        sql += " AND deleted = false";
        try {
            Long count = qr.query(sql, new ScalarHandler<>(1), name);
            return count != null && count > 0;
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
    }
    
    @Override
    public boolean exist(String name, int type) {
        checkArgument(!Strings.isNullOrEmpty(name),"登录标识不能为空");
        String sql = "SELECT COUNT(*) FROM t_user_base WHERE type = ? AND ";
        if (Patterns.mobile(name)) {
            sql += " mobile = ? ";
        } else if (Patterns.email(name)) {
            sql += " email = ? ";
        } else {
            sql += " user_name = ? ";
        }
        sql += " AND deleted = false";
        try {
            Long count = qr.query(sql, new ScalarHandler<>(1), type, name);
            return count != null && count > 0;
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
    }

    @Override
    public boolean exist(String name, int... types) {
        checkArgument(!Strings.isNullOrEmpty(name), "登录标识不能为空");
        checkArgument(types != null && types.length >= 0, i18n("请设置账户类型"));
        String sql = "SELECT COUNT(*) FROM t_user_base WHERE ";
        List<Object> params = Lists.newArrayListWithCapacity(types.length + 1);
        if (types.length == 1) {
            sql += " type = ? AND ";
            params.add(types[0]);
        } else {
            sql += " type IN ( ";
            for (int i = 0, size = types.length; i < size; i++) {
                sql += "?";
                if (i != size - 1) {
                    sql += ",";
                }
                params.add(types[i]);
            }
            sql += ") AND ";
        }
        if (Patterns.mobile(name)) {
            sql += " mobile = ? ";
        } else if (Patterns.email(name)) {
            sql += " email = ? ";
        } else {
            sql += " user_name = ? ";
        }
        sql += " AND deleted = false";
        params.add(name);
        try {
            Long count = qr.query(sql, new ScalarHandler<>(1), params.toArray(new Object[params.size()]));
            return count != null && count > 0;
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
    }


    @Transactional(rollbackFor = {ApplicationException.class})
    @Override
    public <T extends UserInfo> long ifPresentOrElse(String name,Consumer<T> consumer, Class<T> clazz,  Supplier<Long> supplier, int... type) {
        return orElse(name,result -> {
            long userId = result.getKey();
            int $type = result.getValue();
            //如果用户是传入类型的用户
            if (Arrays.contains(type, $type)) {
                consumer.accept(get(userId, clazz).orElseThrow(() -> ApplicationException.notFound("无法获取用户信息")));
                return userId;
            }
            log.warn("已查询出id[{}]用户,类型[{}]",userId,$type);
            return userId;
        },supplier);
    }

    @Transactional(rollbackFor = {ApplicationException.class})
    @Override
    public <T extends UserInfo> long orElse(String name, Consumer<T> consumer, Class<T> clazz, Supplier<Long> supplier,int... type) {
        return orElse(name,result->{
            long userId = result.getKey();
            int $type = result.getValue();
            //如果用户是传入类型的用户
            if(Arrays.contains(type,$type)){
                log.warn("已查询出id[{}]用户,类型[{}]",userId,$type);
                return userId;
            }
            consumer.accept(get(userId,clazz).orElseThrow(()-> ApplicationException.notFound("无法获取用户信息")));
            return userId;
        },supplier);
    }


    /**
     * 根据name获取用户类型，进行处理
     * @param function
     * @param name
     * @param <T>
     * @return
     */
    private <T extends UserInfo> long orElse(String name, Function<Pair<Long,Integer>,Long> function, Supplier<Long> supplier){
        checkArgument(!Strings.isNullOrEmpty(name),"登录标识不能为空");
        String count = "SELECT COUNT(*) FROM t_user_base ",sql = "SELECT id,type FROM t_user_base ",where = " WHERE ";
        if (Patterns.mobile(name)) {
            where += " mobile = ? ";
        } else if (Patterns.email(name)) {
            where += " email = ? ";
        } else {
            where += " user_name = ? ";
        }
        where += " AND deleted = false";
        try {
            long c = qr.query(count+ where, new ScalarHandler<>(1), name);
            if(c == 0){
                return supplier.get();
            }
            if( c > 1){
                throw ApplicationException.notifyUser("存在%d个账户名为 %s 的用户",c,name);
            }
            Optional<Pair<Long,Integer>> result = qr.query(sql+ where, rsh->{
                if(rsh.next()){
                    return Optional.of(Pair.of(rsh.getLong("id"),rsh.getInt("type")));
                }
                return Optional.empty();
            },name);

            if(result.isPresent()){
                return function.apply(result.get());
            }
            throw new IllegalArgumentException(String.format("账户 %s,获取信息(id,type)失败",name));
        } catch (IllegalArgumentException | ApplicationException e) {
            throw e;
        }catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
    }

    @Transactional(rollbackFor = {IllegalStateException.class,ApplicationException.class})
    @Override
    public boolean disable(long userId,Function<Long,Boolean> function) {
        try {
            if(qr.update("UPDATE t_user_base SET status = 0,updated_at=? WHERE id=? AND status = 1", Instant.now().getEpochSecond(),userId) > 0){
                if(function != null && !function.apply(userId)){
                    log.error("禁用账户: {},后处理失败",userId);
                    throw new IllegalStateException("禁用账户,处理失败");
                }
                return true;
            }
            return false;
        } catch (IllegalStateException| ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw ApplicationException.raise(e);
        }
    }

    @Transactional(rollbackFor = {ApplicationException.class})
    @Override
    public boolean normal(long userId,Function<Long,Boolean> function) {
        try {
            if(qr.update("UPDATE t_user_base SET status = 1,updated_at=? WHERE id=? AND status = 0", Instant.now().getEpochSecond(),userId) > 0){
                if(function != null && !function.apply(userId)){
                    throw ApplicationException.argument("用户后处理异常,请检查日志");
                }
                return true;
            }
            return false;
        } catch (IllegalArgumentException | ApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw ApplicationException.raise(e);
        }
    }

    
    @Override
    public boolean isDisable(long userId){
        //1=正常 0=禁用
        return status(userId).orElseThrow(() -> ApplicationException.notFound("无法获取身份信息")) == 0;
    }

    @Override
    public void isDisableThrow(long userId) throws LockedAccountException {
        if(isDisable(userId)){
            throw new LockedAccountException(i18n("您的账户已被禁用,请联系客服查询详情"));
        }
    }

    @Override
    public Optional<Integer> status(long userId) {
        try {
            return Optional.ofNullable(qr.query("SELECT status FROM t_user_base WHERE id=?", new ScalarHandler<>(1), userId));
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
    }

    
    @Override
    public Optional<String> salt(long userId) {
        try {
            return salt(userId, qr);
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
    }

    
    @Override
    public Optional<Long> id(String name) {
        checkArgument(!Strings.isNullOrEmpty(name), "请传入用户名");
        String sql = "SELECT id FROM t_user_base WHERE ";
        if (Patterns.mobile(name)) {
            sql += " mobile = ? ";
        } else if (Patterns.email(name)) {
            sql += " email = ? ";
        } else {
            sql += " user_name = ? ";
        }
        sql += " AND deleted = false";
        try {
            return Optional.ofNullable(qr.query(sql, new ScalarHandler<>(1), name));
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
    }

    
    @Override
    public Optional<Integer> type(long userId) {
        return column(userId,"type");
    }

    
    @Override
    public Optional<Integer> type(String name) {
        checkArgument(!Strings.isNullOrEmpty(name), "请传入用户名");
        String sql = "SELECT type FROM t_user_base WHERE ";
        if (Patterns.mobile(name)) {
            sql += " mobile = ? ";
        } else if (Patterns.email(name)) {
            sql += " email = ? ";
        } else {
            sql += " user_name = ? ";
        }
        sql += " AND deleted = false";
        try {
            return Optional.ofNullable(qr.query(sql, new ScalarHandler<>(1), name));
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
    }

    @Override
    public boolean setType(Long userId,int type) {
        try {
            if(qr.execute("UPDATE t_user_base SET type = ?,updated_at=? WHERE id=?",type,Instant.now().getEpochSecond(),userId) >0){
                return true;
            }
            return false;
        } catch (ApplicationException e) {
            throw e;
        }catch (Exception e) {
            throw ApplicationException.raise(e);
        }

    }

    
    @Override
    public Optional<String> password(long userId) {
        try {
            return Optional.ofNullable(qr.query("SELECT password FROM t_user_base WHERE id=?", new ScalarHandler<>(1), userId));
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
    }
    @Transactional(rollbackFor ={ ApplicationException.class,Exception.class})
    @Override
    public boolean noFirstlogin(long userId,Function<Long,Boolean> function) {
        try {
            if(qr.update("UPDATE t_user_base SET firstlogin = 0,updated_at=? WHERE id=? AND firstlogin = 1 ",Instant.now().getEpochSecond(),userId) > 0){
                if(function != null && !function.apply(userId)){
                    throw ApplicationException.argument("首次登陆处理失败,请检查日志");
                }
                return true;
            }
            return false;
        } catch (IllegalArgumentException | ApplicationException e) {
            throw e;
        }catch (Exception e) {
            throw ApplicationException.raise(e);
        }
    }

    @Transactional(rollbackFor = {ApplicationException.class,NotFoundException.class,IllegalArgumentException.class,Exception.class})
    @Override
    public <U extends User> boolean editUser(U userInfo) {
        return editUser(userInfo,null);
    }


    @Transactional(rollbackFor = {ApplicationException.class,NotFoundException.class,IllegalArgumentException.class,Exception.class})
    @SuppressWarnings("unchecked")
    @Override
    public <U extends User> boolean editUser(U userInfo, Function<U, Boolean> function) {
        checkArgument(userInfo!=null && userInfo.getId() !=0,i18n("用户信息不能为空"));
        U old = (U) get(userInfo.getId(),userInfo.getClass()).orElseThrow(()->new NotFoundException(i18n("无法获取用户信息"))); //无法获取用户信息
        if(function != null && !function.apply(old)){
            return false;
        }
        Map<String,Object> params = Maps.newHashMapWithExpectedSize(3);
        if(!Strings.isNullOrEmpty(userInfo.getUserName()) && !StringUtils.equals(old.getUserName(),userInfo.getUserName())){
            if(exist(userInfo.getId(),"user_name",userInfo.getUserName())){
                throw new RepeatException(i18n("已存在相同的用户名,请更换其他名称"),"userName");
            }
            params.put("user_name",userInfo.getUserName());
        }
        if(!Strings.isNullOrEmpty(userInfo.getMobile()) && !StringUtils.equals(old.getMobile(),userInfo.getMobile())){
            if(exist(userInfo.getId(),"mobile",userInfo.getMobile())){
                throw new RepeatException(i18n("已存在相同的手机号,请更换其他号码"),"mobile");
            }
            params.put("mobile",userInfo.getMobile());
        }
        if(!Strings.isNullOrEmpty(userInfo.getEmail()) && !StringUtils.equals(old.getEmail(),userInfo.getEmail())){
            if(!Patterns.email(userInfo.getEmail())){
                throw new IllegalArgumentException(i18n("非法的Email格式,请重新输入"));
            }
            if(exist(userInfo.getId(),"email",userInfo.getEmail())){
                throw new RepeatException(i18n("已存在相同的Email,请更换其他地址"),"email");
            }
            params.put("email", userInfo.getEmail());
        }
        if (userInfo.getStatus() != null && userInfo.getStatus().equals(old.getStatus())) {
            params.put("status", userInfo.getStatus());
        }
        if (userInfo.getType() != 0 && userInfo.getType() != old.getType()) {
            params.put("type", userInfo.getType());
        }
        if (userInfo.getOrgId() != 0 && userInfo.getOrgId() != old.getOrgId()) {
            params.put("org_id", userInfo.getOrgId());
        }
        if (userInfo.getGender() != 0 && userInfo.getGender() != old.getGender()) {
            params.put("gender", userInfo.getGender());
        }
        if (!Strings.isNullOrEmpty(userInfo.getRealName()) && !StringUtils.equals(old.getRealName(), userInfo.getRealName())) {
            params.put("real_name", userInfo.getRealName());
        }
        if (!Strings.isNullOrEmpty(userInfo.getAvatar()) && !StringUtils.equals(old.getAvatar(), userInfo.getAvatar())) {
            params.put("avatar", userInfo.getAvatar());
        }
        if (userInfo.getSource() != 0 && userInfo.getSource() != old.getSource()) {
            params.put("source",userInfo.getSource());
        }
        if( !Strings.isNullOrEmpty(userInfo.getNickName()) && !StringUtils.equals(old.getNickName(),userInfo.getNickName())){
            params.put("nick_name",userInfo.getNickName());
        }
        try {
            if(params.isEmpty()) {
                return true;
            }
            return edit(userInfo.getId(),params);
        }catch (IllegalArgumentException | ApplicationException ex){
            throw ex;
        }catch (Exception ex){
            throw ApplicationException.raise(ex);
        }
    }


    @Transactional
    @Override
    public boolean avatar(long userId, String avatar) {
        return edit(userId,Map.of("avatar",avatar));
    }


    @Transactional
    @Override
    public boolean edit(long userId, Map<String,Object> params) {
        checkArgument(userId>0,"必须设置userId");
        checkArgument(params != null && !params.isEmpty(),"params 不能为空");
        List<Object> vals = Lists.newArrayListWithCapacity(params.size());
        String sql = "UPDATE t_user_base SET ";
        for(Map.Entry<String,Object> entry : params.entrySet()){
            sql += entry.getKey() + "=?,";
            vals.add(entry.getValue());
        }
        sql += " updated_at = ? WHERE id = ?";
        vals.add(Instant.now().getEpochSecond());
        vals.add(userId);
        try {
            return qr.update(sql,vals.toArray()) > 0;
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
    }




    /**
     * 根据三方信息获取用户
     *
     * @param oauthId
     * @param unionid
     * @param type
     * @param clazz
     * @return
     */
    @Override
    public <U extends User> Optional<U> of(String oauthId, String unionid, OAuthType type, Class<U> clazz) {
        checkArgument(OAuthType.UNKNOWN != type ,"请确认三方平台");
        checkArgument(!Strings.isNullOrEmpty(oauthId) || !Strings.isNullOrEmpty(unionid),"请传入三方登录凭证");
        String sql = "SELECT uo.id AS oId,uo.user_id,uo.type AS oType,uo.oauth_id,uo.unionid,uo.credential,uo.created_at AS createdAt,ub.id,ub.type,ub.user_name,ub.mobile,ub.email,ub.password,ub.salt,ub.nick_name,ub.real_name,ub.avatar,ub.status,ub.gender,ub.source,ub.firstlogin,ub.merge,ub.is_2fa AS use2FA,ub.secret_2fa AS secret2FA,ub.created_at,ub.updated_at  FROM t_user_base ub JOIN t_user_oauth uo ON ub.id = uo.user_id WHERE uo.type = ?  ";
        List<Object> params = Lists.newArrayListWithCapacity(3);
        params.add(type.type());
        if(!Strings.isNullOrEmpty(oauthId)){
            sql += "AND uo.oauth_id = ? ";
            params.add(oauthId);
        }
        if(!Strings.isNullOrEmpty(unionid)){
            sql += " AND uo.unionid = ? ";
            params.add(unionid);
        }
        sql += " AND ub.deleted = false";
        try {
            return qr.query(sql,rs->{
                if(rs.next()){
                    U user;
                    try {
                        user = clazz.getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        throw ApplicationException.raise(e);
                    }
                    OAuthUser oauth = new OAuthUser();
                    oauth.setId(rs.getLong("oId"));
                    oauth.setUserId(rs.getLong("user_id"));
                    oauth.setType(rs.getInt("oType"));
                    oauth.setOAuthId(rs.getString("oauth_id"));
                    oauth.setUnionid(rs.getString("unionid"));
                    oauth.setCredential(rs.getString("credential"));
                    oauth.setCreatedAt(asLocalDateTime(rs.getTimestamp("createdAt").toInstant()));
                    user.setOAuthUser(oauth);
                    user.addOAuth(oauth);

                    user.setId(rs.getLong("id"));
                    user.setType(rs.getInt("type"));
                    user.setUserName(rs.getString("user_name"));
                    user.setMobile(rs.getString("mobile"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setSalt(rs.getString("salt"));
                    user.setNickName(rs.getString("nick_name"));
                    user.setRealName(rs.getString("real_name"));
                    user.setAvatar(rs.getString("avatar"));
                    user.setStatus(rs.getInt("status"));
                    user.setGender(rs.getInt("gender"));
                    user.setSource(rs.getInt("source"));
                    user.setFirstLogin(rs.getBoolean("firstlogin"));
                    user.setMerge(rs.getBoolean("merge"));
                    user.setUse2FA(rs.getBoolean("use2FA"));
                    user.setSecret2FA(rs.getString("secret2FA"));
                    user.setCreatedAt(asLocalDateTime(Instant.ofEpochSecond(rs.getLong("created_at"))));
                    user.setUpdatedAt(asLocalDateTime(Instant.ofEpochSecond(rs.getLong("updated_at"))));
                    return Optional.of(user);
                }
                return Optional.empty();
            },params.toArray());
        } catch (ApplicationException e) {
            throw e;
        }catch (Exception e) {
            throw ApplicationException.raise(e);
        }
    }


    @Transactional(rollbackFor = {ApplicationException.class, IllegalArgumentException.class, DuplicateException.class, Exception.class})
    @Override
    public long bind(OAuthUser oAuthUser) {
        checkArgument(oAuthUser != null ,"第三方平台信息不能为空");
        checkArgument(oAuthUser.getUserId() != 0 ,"绑定三方账户必须指定用户");
        checkArgument(oAuthUser.getType() != 0 || oAuthUser.getOAuthType() != OAuthType.UNKNOWN,"请设置第三方平台类型");
        checkArgument(!Strings.isNullOrEmpty(oAuthUser.getOAuthId()),"请设置第三方平台认证ID");
        try {
            if (oAuthUser.getType() == 0) {
                oAuthUser.setType(oAuthUser.getOAuthType().type());
            }
            //验证 oauth_id 是否已经存在
            if (qr.query("SELECT COUNT(*) FROM t_user_oauth WHERE type = ? AND user_id = ? LIMIT 1"
                    , new ScalarHandler<Long>(1)
                    , oAuthUser.getType(), oAuthUser.getUserId()) > 0) {
                if(!Strings.isNullOrEmpty(oAuthUser.getUnionid())){
                    //检查是否已存在 unionid
                    if(Strings.isNullOrEmpty(qr.query("SELECT unionid FROM t_user_oauth WHERE type = ? AND user_id = ? LIMIT 1"
                            , new ScalarHandler<String>(1)
                            , oAuthUser.getType(), oAuthUser.getUserId()))){
                        //不存在unionid 需要更新
                        if(qr.update("UPDATE t_user_oauth SET unionid = ? WHERE type = ? AND user_id = ?",oAuthUser.getUnionid(),oAuthUser.getType(), oAuthUser.getUserId()) > 0){
                            log.info("已更新三方平台[{}]认证凭据的unionid值 成功,userId = {},unionid= {}",oAuthUser.getType(),oAuthUser.getUserId(),oAuthUser.getUnionid());
                            return oAuthUser.getUserId();
                        }
                        log.error("更新三方平台[{}]认证凭据的unionid值 失败,userId = {},unionid= {}",oAuthUser.getType(),oAuthUser.getUserId(),oAuthUser.getUnionid());
                        return oAuthUser.getUserId();
                    }
                }
                log.error("已绑定三方平台,无法重复绑定[userId = {},type={},oauthId={}]", oAuthUser.getUserId(), oAuthUser.type(), oAuthUser.getOAuthId());
                return oAuthUser.getUserId();
            }
            String sql = "INSERT INTO t_user_oauth(user_id,type,oauth_id,unionid,credential,created_at) VALUES(?,?,?,?,?,?)";
            long oauthUserId = qr.insert(sql, rs -> {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return 0L;
            }, oAuthUser.getUserId(), oAuthUser.getType(), oAuthUser.getOAuthId(), oAuthUser.getUnionid(), oAuthUser.getCredential(), asDate(LocalDateTime.now()));
            log.info("create user oauth.[id={},user_id={},type=[{},{}],oauth_id={},unionid={}]", oauthUserId, oAuthUser.getUserId(),oAuthUser.getType(), oAuthUser.type().name(), oAuthUser.getOAuthId(), oAuthUser.getUnionid());
            oAuthUser.setId(oauthUserId);
            return oauthUserId;
        } catch (IllegalArgumentException | ApplicationException ex) {
            throw ex;
        } catch (SQLException ex) {
            throw ApplicationException.raise(ex);
        }
    }

    
    @Override
    public boolean isBind(long userId) {
        checkArgument(userId !=0 ,"请传入userId");
        try {
            Long count = qr.query("SELECT EXISTS (SELECT * FROM t_user_oauth WHERE user_id = ?) ", new ScalarHandler<>(1), userId);
            return count > 0;
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
    }

    
    @Override
    public List<OAuthUser> oauths(long userId) {
        checkArgument(userId != 0 ,"请设置user_id");
        try {
            return qr.query("SELECT id,user_id,type,oauth_id,unionid,credential,created_at FROM t_user_oauth WHERE user_id = ?",rs->{
                List<OAuthUser> datas = Lists.newArrayListWithCapacity(4);
                OAuthUser oauth;
                while (rs.next()){
                    oauth = new OAuthUser();
                    oauth.setId(rs.getLong("id"));
                    oauth.setUserId(rs.getLong("user_id"));
                    oauth.setType(rs.getInt("type"));
                    oauth.setOAuthId(rs.getString("oauth_id"));
                    oauth.setUnionid(rs.getString("unionid"));
                    oauth.setCredential(rs.getString("credential"));
                    oauth.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    datas.add(oauth);
                }
                return datas;
            },userId);
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
    }

    
    @Override
    public Optional<OAuthUser> oauth(long userId, OAuthType type) {
        checkArgument(userId != 0 ,"请设置user_id");
        try {
            return qr.query("SELECT id,user_id,type,oauth_id,unionid,credential,created_at FROM t_user_oauth WHERE type=? AND  user_id = ? ",rs->{
                if (rs.next()){
                    OAuthUser oauth = new OAuthUser();
                    oauth.setId(rs.getLong("id"));
                    oauth.setUserId(rs.getLong("user_id"));
                    oauth.setType(rs.getInt("type"));
                    oauth.setOAuthId(rs.getString("oauth_id"));
                    oauth.setUnionid(rs.getString("unionid"));
                    oauth.setCredential(rs.getString("credential"));
                    oauth.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return Optional.of(oauth);
                }
                return Optional.empty();
            },type.type(),userId);
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
    }



    /**
     * 是否已经实名认证
     * @param userId
     * @return
     */
    @Override
    public boolean isVerified(long userId) {
        checkArgument(userId > 0 ,"请传入userId");
        try {
            Long count = qr.query("SELECT COUNT(*) FROM t_user_base WHERE id = ? AND verified = 1 LIMIT 1", new ScalarHandler<>(1), userId);
            return count > 0;
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
    }

    @Override
    public void verified(long userId, RealnameVerified verified) {
        checkArgument(userId > 0 ,"请传入userId");
        checkArgument(!Strings.isNullOrEmpty(verified.getRealName()),"请填写真实姓名");
        checkArgument(!Strings.isNullOrEmpty(verified.getIdentityNo()),"请填写证件号码");
        checkArgument(Patterns.name_cn(verified.getRealName()),"请填写合法的真实姓名");
        checkArgument(isVerified(userId),"已实名认证");
        try {
            Long count = qr.query("SELECT COUNT(*) FROM t_user_info WHERE user_id = ? LIMIT 1", new ScalarHandler<>(1), userId);
            if(count >0){
                //已存在 修改
                if(qr.update("UPDATE t_user_info SET real_name = ?,identity_no = ? WHERE user_id = ?",verified.getRealName().strip(),verified.getIdentityNo().strip(),userId) == 0){
                    throw new IllegalArgumentException("实名认证信息设置有误,请稍后尝试");
                }
            }else{
                qr.insert("INSERT INTO t_user_info (user_id,real_name,identity_no) values (?,?,?)",rs->null,userId,verified.getRealName().strip(),verified.getIdentityNo().strip());
            }
            if(qr.update("UPDATE t_user_base SET verified = true,updated_at = ? WHERE  id = ?",Instant.now().getEpochSecond(),userId) == 0){
                throw new IllegalArgumentException("已实名认证状态修改失败,请稍后尝试");
            }
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }

    }

    @Override
    public Optional<PrivilegeInfo>  pvgInfo(long userId) {
        String sql =
                """
                                SELECT
                                    ub.id,
                                    ub.org_id,
                                    ub.user_name,
                                    ub.mobile,
                                    ub.type,
                                    ub.email,
                                    ub.gender,
                                    ub.real_name,
                                    ub.source,
                                    ub.avatar,
                                    STRING_AGG(DISTINCT CAST(r.id AS VARCHAR), ',') AS roleIds,
                                    STRING_AGG(DISTINCT r.code, ',') AS roleCodes,
                                    STRING_AGG(DISTINCT CAST(p.id AS VARCHAR), ',') AS permIds,
                                    STRING_AGG(DISTINCT p.code, ',') AS permCodes
                                FROM
                                    t_user_base ub
                                    LEFT JOIN t_user_role ur ON ub.id = ur.user_id
                                    LEFT JOIN t_role r ON ur.role_id = r.id
                                    LEFT JOIN t_role_permisson rp ON r.id = rp.role_id
                                    LEFT JOIN t_permisson p ON rp.permisson_id = p.id
                                WHERE
                                    ub.id = ?
                                GROUP BY
                                    ub.id
                        """;
        try {
            return  qr.query(sql,rs->{
                if(rs.next()){
                    PrivilegeInfo val = new PrivilegeInfo();
                    val.setId(userId);
                    val.setOrgId(rs.getInt("org_id"));
                    val.setUserName(rs.getString("user_name"));
                    val.setMobile(rs.getString("mobile"));
                    val.setType(rs.getInt("type"));
                    val.setEmail(rs.getString("email"));
                    val.setGender(rs.getInt("gender"));
                    val.setRealName(rs.getString("real_name"));
                    val.setSource(rs.getInt("source"));
                    val.setAvatar(rs.getString("avatar"));
                    var roleIds = rs.getString("roleIds");
                    if(!Strings.isNullOrEmpty(roleIds)){
                        Splitter.on(",").split(roleIds).forEach(val::addRoleId);
                    }
                    var roleCodes = rs.getString("roleCodes");
                    if(!Strings.isNullOrEmpty(roleCodes)){
                        Splitter.on(",").split(roleCodes).forEach(val::addRole);
                    }
                    var permIds = rs.getString("permIds");
                    if(!Strings.isNullOrEmpty(permIds)){
                        Splitter.on(",").split(permIds).forEach(val::addPermIds);
                    }
                    var permCodes = rs.getString("permCodes");
                    if(!Strings.isNullOrEmpty(permCodes)){
                        Splitter.on(",").split(permCodes).forEach(val::addPermissions);
                    }
                    return Optional.of(val);
                }
                return Optional.empty();
            },userId);
        } catch (Exception e) {
            throw ApplicationException.raise(e);
        }
    }
    private boolean exist(String column, Object val){
        try {
            Long count = qr.query("SELECT COUNT(*) FROM t_user_base WHERE " + column + " =? AND deleted = false ", new ScalarHandler<>(1), val);
            return count > 0;
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
    }
    private boolean exist(Long id,String column,Object val){
        try {
            Long count = qr.query("SELECT COUNT(*) FROM t_user_base WHERE id != ? AND " + column + " =? AND deleted = false ", new ScalarHandler<>(1), id,val);
            return count > 0;
        } catch (SQLException e) {
            throw ApplicationException.raise(e);
        }
    }
    /**
     * 根据ID获取用户盐值
     *
     * @param userId
     * @param qr
     * @return
     * @throws SQLException
     */
    private Optional<String> salt(long userId, QueryRunner qr) throws SQLException {
        return Optional.ofNullable(qr.query("SELECT salt FROM t_user_base WHERE id=?", new ScalarHandler<>(1), userId));
    }



    protected static class PasswordSupplier implements Supplier<Object[]> {
        private final String val;
        private final String password;
        private final int type;
        private final int source;
        private final long time;

        private PasswordSupplier(String val, String password,int type,int source) {
            this.val = val;
            if (Strings.isNullOrEmpty(password) || !Patterns.password(password)) {
                throw new IllegalArgumentException(I18n.use("i18n/security").get("密码格式必须大于6位,且小于16位,数字或字母"));
            }
            this.password = password;
            this.type = type;
            this.source = source;
            this.time = Instant.now().getEpochSecond();
        }

        @Override
        public Object[] get() {
            var salt = Password.salt(SALT_COUNT); //加盐 12位
            return new Object[]{val, Password.password(password, salt), salt,type,source,time,time};
        }
    }
}
