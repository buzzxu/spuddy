package io.github.buzzxu.spuddy.security;



import io.github.buzzxu.spuddy.errors.LockedAccountException;
import io.github.buzzxu.spuddy.errors.UnauthorizedException;
import io.github.buzzxu.spuddy.objects.RealnameVerified;
import io.github.buzzxu.spuddy.security.objects.OAuthUser;
import io.github.buzzxu.spuddy.security.objects.PrivilegeInfo;
import io.github.buzzxu.spuddy.security.objects.User;
import io.github.buzzxu.spuddy.security.objects.UserInfo;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface UserService {

    String SQL_COLUMNS = """
            ub.id,ub.org_id,ub.type,ub.user_name,ub.mobile,ub.email,ub.password,ub.salt,nick_name AS nickName,
            ub.real_name,ub.avatar,ub.status,ub.gender,ub.source,ub.verified,ub.firstlogin,ub.merge,ub.is_2fa AS use2FA,
            ub.secret_2fa AS secret2FA,ub.operator,ub.created_at,ub.updated_at
            """;
    /**
     * 创建用户
     * @param userInfo
     * @param consumer
     * @param <U>
     * @return
     */
    <U extends User> long create(U userInfo, Consumer<U> consumer);
    /**
     * 创建用户
     * @param userName
     * @param password
     * @param type
     * @param consumer
     * @param clazz
     * @param <U>
     * @return
     */
    <U extends User> long create(String userName, String password, int type, UserSource source, Consumer<U> consumer, Class<U> clazz);

    default <U extends User> long create(String userName, String password, int type,Consumer<U> consumer, Class<U> clazz){
        return create(userName, password, type, UserSource.PC,consumer, clazz);
    }
    <U extends User> long create(String userName, int type, UserSource source,Consumer<U> consumer,Class<U> clazz);

    default <U extends User> long create(String userName, int type, Consumer<U> consumer,Class<U> clazz){
        return create(userName,type,UserSource.PC,consumer,clazz);
    }
    long create(String userName, String password,int type, UserSource source,Consumer<UserInfo> consumer);

    default long create(String userName, String password,int type, Consumer<UserInfo> consumer){
        return create(userName,password,type,UserSource.PC,consumer);
    }
    long create(String userName, int type, UserSource source,Consumer<UserInfo> consumer);

    default long create(String userName, int type, Consumer<UserInfo> consumer){
        return create(userName, type, UserSource.PC,consumer);
    }
    /**
     * 通过手机号创建用户
     * @param mobile
     * @param password
     * @param type
     * @param consumer
     * @param clazz
     * @param <U>
     * @return
     */
    <U extends User> long createByMobile(String mobile,String password,int type, UserSource source,Consumer<U> consumer,Class<U> clazz);

    <U extends User> long createByMobile(String mobile, String password, int type, UserSource source, Predicate<String> predicate,Consumer<U> consumer, Class<U> clazz);
    <U extends User> long createByMobile(String mobile,int type, UserSource source,Consumer<U> consumer,Class<U> clazz);
    long createByMobile(String mobile,String password,int type, UserSource source,Consumer<UserInfo> consumer);


    /**
     * 通过邮箱创建用户
     * @param email
     * @param password
     * @param type
     * @param consumer
     * @param clazz
     * @param <U>
     * @return
     */
    <U extends User> long createByEmail(String email,String password,int type, UserSource source,Consumer<U> consumer,Class<U> clazz);

    <U extends User> long createByEmail(String email, int type, UserSource source, Consumer<U> consumer, Class<U> clazz);

    long createByEmail(String email, String password, int type, UserSource source, Consumer<UserInfo> consumer);

    /**
     * 删除用户
     *
     * @param userId
     * @return
     */
    boolean delete(long userId);

    /**
     * 删除用户
     * @param userId
     * @param all       是否全部删除
     * @return
     */
    boolean delete(long userId,boolean all);
    /**
     * 销毁账户
     * 删除t_user_oauth,t_user_role表 ,t_user_base做标记
     * @param userId
     * @return
     */
    boolean destroy(long userId);
    /**
     * 恢复初始状态
     * @param userId
     * @param type
     * @return
     */
    boolean recover(long userId,int type);

    /**
     * 恢复初始状态
     * @param userId
     * @param type
     * @param roleId
     * @return
     */
    boolean recover(long userId,int type,int roleId);
    /**
     * 验证 用户名/手机号/邮箱
     *
     * @param name 用户名/手机号/邮箱
     * @return
     * @throws LockedAccountException
     */
    Optional<Long> verify(String name) throws LockedAccountException;

    /**
     * 验证 用户名/手机号/邮箱
     *
     * @param name  用户名/手机号/邮箱
     * @param types
     * @return
     * @throws LockedAccountException
     */
    Optional<Long> verify(String name, int... types) throws LockedAccountException;

    /**
     * 验证密码
     *
     * @param name 用户名/手机号/邮箱
     * @param pwd
     * @return 用户ID
     * @throws UnauthorizedException,LockedAccountException
     */
    Optional<Long> verifyPwd(String name, String pwd) throws UnauthorizedException, LockedAccountException;


    /**
     * 验证密码
     *
     * @param type 用户类型
     * @param name 用户名/手机号/邮箱
     * @param pwd
     * @return
     * @throws UnauthorizedException
     * @throws LockedAccountException
     */
    Optional<Long> verifyPwd(int type, String name, String pwd) throws UnauthorizedException, LockedAccountException;

    /**
     * 验证密码
     *
     * @param name  用户类型
     * @param pwd   用户名/手机号/邮箱
     * @param types 用户类型
     * @return
     * @throws UnauthorizedException
     * @throws LockedAccountException
     */
    Optional<Long> verifyPwd(String name, String pwd, int... types) throws UnauthorizedException, LockedAccountException;

    /**
     * 获取用户
     *
     * @param userId
     * @param clazz
     * @param <U>
     * @return
     */
    <U extends User> Optional<U> get(long userId, Class<U> clazz);

    /**
     * 获取用户
     * @param name
     * @param clazz
     * @param <U>
     * @return
     */
    <U extends User> Optional<U> get(String name,Class<U> clazz);
    /**
     * 获取用户
     * @param userId
     * @return
     */
    Optional<UserInfo> get(long userId);

    Optional<String> mobile(long userId);

    /**
     * 获取手机号码
     * @param userIds
     * @return
     */
    List<String> mobile(List<Long> userIds);

    Optional<String> realName(long userId);

    Optional<String> nickName(long userId);

    Optional<Long> orgId(long userId);

    <T> Optional<T> column(long userId, String column);
    /**
     * 合并信息 将目标用户的信息(除密码和盐值)其余全部复制到当前用户基础信息中
     * @param primary
     * @param target
     * @param <U>
     * @return
     */
    <U extends User> boolean merge(long primary, U target, Consumer<U> consumer);

    /**
     * 修改密码
     * @param userId
     * @param oldPwd
     * @param newPwd
     * @param function
     * @return
     * @throws LockedAccountException
     */
    boolean changePassword(long userId,String oldPwd,String newPwd,Function<String,Boolean> function)throws LockedAccountException;

    /**
     * 重置密码  不改变盐值
     * @param userId
     * @param defaultPwd
     * @return
     */
    boolean resetPassword(long userId,Supplier<String> defaultPwd);

    /**
     * 这是新密码
     * @param userId
     * @param defaultPwd
     * @return
     */
    boolean newPassword(long userId,Supplier<String> defaultPwd);
    /**
     * 检查昵称是否合法
     * @param name
     * @return
     */
    boolean existedNickName(String name);
    /**
     * 是否包含用户
     * @param name  用户名/手机号/邮箱
     * @return
     */
    @Deprecated
    default boolean contain(String name){
        return exist(name);
    }

    /**
     * 是否包含用户
     * @param name  用户名/手机号/邮箱
     * @param type
     * @return
     */
    @Deprecated
    default boolean contain(String name,int type){
        return exist(name,type);
    }

    /**
     * 是否存在此ID
     * @param id
     * @return
     */
    boolean exist(long id);
    /**
     * 用户名称是否存在
     * @param name  用户名/手机号/邮箱
     * @return
     */
    boolean exist(String name);

    /**
     * 用户名称是否存在
     *
     * @param name 用户名/手机号/邮箱
     * @param type
     * @return
     */
    boolean exist(String name, int type);

    /**
     * 用户名称是否存在
     *
     * @param name  用户名/手机号/邮箱
     * @param types
     * @return
     */
    boolean exist(String name, int... types);


    /**
     * 如果存在指定类型的用户
     * @param name
     * @param consumer
     * @param supplier
     * @param type
     * @return
     */
    default  long ifPresentOrElse(String name,Consumer<UserInfo> consumer,Supplier<Long> supplier,int... type){
        return ifPresentOrElse(name,consumer,UserInfo.class,supplier,type);
    }

    <T extends UserInfo> long ifPresentOrElse(String name,Consumer<T> consumer,Class<T> clazz,Supplier<Long> supplier,int... type);



    /**
     * 如果不存在指定类型的用户
     * @param consumer
     * @param name
     * @param supplier
     * @param type
     * @return
     */
    default  long orElse(Consumer<UserInfo> consumer,String name,Supplier<Long> supplier,int... type){
        return orElse(name,consumer,UserInfo.class,supplier,type);
    }

    <T extends UserInfo> long orElse(String name, Consumer<T> consumer, Class<T> clazz, Supplier<Long> supplier,int... type);


    /**
     * 用户禁用
     * @param userId
     * @param function
     * @return
     */
    boolean disable(long userId,Function<Long,Boolean> function);

    /**
     * 用户正常
     * @param userId
     * @param function
     * @return
     */
    boolean normal(long userId,Function<Long,Boolean> function);

    /**
     * 是否被禁用
     * @param userId
     * @return
     */
    boolean isDisable(long userId);

    /**
     * 是否被禁用
     * @param userId
     * @throws LockedAccountException
     */
    void isDisableThrow(long userId) throws LockedAccountException;
    /**
     * 获取用户状态
     *
     * @param userId
     * @return
     */
    Optional<Integer> status(long userId);

    /**
     * 获取用户盐值
     * @param userId
     * @return
     */
    Optional<String> salt(long userId);

    /**
     * 根据用户名获取用户ID
     *
     * @param name
     * @return
     */
    Optional<Long> id(String name);

    /**
     * 获取用户类型
     *
     * @param userId
     * @return
     */
    Optional<Integer> type(long userId);

    /**
     * 获取用户类型
     *
     * @param name
     * @return
     */
    Optional<Integer> type(String name);

    /**
     * 设置类型
     * @param userId
     * @param type
     * @return
     */
    boolean setType(Long userId,int type);

    /**
     * 获取用户密码
     *
     * @param userId
     * @return
     */
    Optional<String> password(long userId);

    /**
     * 不是首次登陆
     * @param userId
     * @param function
     * @return
     */
    boolean noFirstlogin(long userId,Function<Long,Boolean> function);

    /**
     * 修改用户信息
     * @param userInfo
     * @param <U>
     * @return
     */
    <U extends User> boolean  editUser(U userInfo);
    /**
     * 修改用户信息
     * @param userInfo
     * @param function
     * @param <U>
     * @return
     */
    <U extends User> boolean  editUser(U userInfo, Function<U,Boolean> function);

    /**
     * 修改头像
     * @param userId
     * @param avatar
     * @return
     */
    boolean avatar(long userId,String avatar);

    /**
     * 修改用户信息
     * @param userId
     * @param params key=列名 value=参数值
     * @return
     */
    boolean edit(long userId, Map<String,Object> params);


    /**
     * 根据三方信息获取用户
     * @param oauthId
     * @param type
     * @param clazz
     * @param <U>
     * @return
     */
    default <U extends User> Optional<U>  of(String oauthId,OAuthType type,Class<U> clazz){
        return of(oauthId, null,type, clazz);
    }

    /**
     * 根据三方信息获取用户
     * @param oauthId
     * @param unionid
     * @param type
     * @param clazz
     * @param <U>
     * @return
     */
    default <U extends User> Optional<U>  of(String oauthId, String unionid, OAuthType type, Class<U> clazz){
        throw new UnsupportedOperationException();
    }
    /**
     * 绑定三方登录信息
     * @return
     */
    default long bind(OAuthUser oAuthUser){
        throw new UnsupportedOperationException();
    }
    /**
     * 是否绑定三方账户
     * @param userId
     * @return
     */
    default boolean isBind(long userId){
        throw new UnsupportedOperationException();
    }
    /**
     * 获取第三方授权信息
     * @param userId
     * @return
     */
    default List<OAuthUser> oauths(long userId){
        throw new UnsupportedOperationException();
    }



    /**
     * 根据社交类型获取三方授权信息
     * @param userId
     * @param type
     * @return
     */
    default Optional<OAuthUser> oauth(long userId,OAuthType type){
        throw new UnsupportedOperationException();
    }




    /**
     * 是否已实名认证
     * @param userId
     * @return
     */
    default boolean isVerified(long userId){
        throw new UnsupportedOperationException();
    }


    /**
     * 实名认证
     * @param userId
     * @param verified
     */
    default void verified(long userId, RealnameVerified verified){
        throw new UnsupportedOperationException();
    }

    /**
     * 获取特权信息
     * @param userId
     * @return
     */
    Optional<PrivilegeInfo> pvgInfo(long userId);

    /**
     * 修改个人设置
     * @param userId
     * @param settings
     * @return
     */
    boolean editSettings(long userId,Map<String,Object> settings);
}
