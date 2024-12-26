package io.github.buzzxu.spuddy.security.shiro;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import io.github.buzzxu.spuddy.errors.LockedAccountException;
import io.github.buzzxu.spuddy.errors.SecurityException;
import io.github.buzzxu.spuddy.security.exceptions.TokenAuthException;
import io.github.buzzxu.spuddy.security.exceptions.TokenExpiredException;
import io.github.buzzxu.spuddy.security.exceptions.TokenGenerateException;
import io.github.buzzxu.spuddy.security.handler.SecurityUserHandler;
import io.github.buzzxu.spuddy.security.objects.PrivilegeInfo;
import io.github.buzzxu.spuddy.security.objects.UserInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.HostUnauthorizedException;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;




@Slf4j
public abstract class SecurityRealm extends AuthorizingRealm {

    @SuppressWarnings("rawtypes")
    @Resource
    protected SecurityUserHandler securityUserHandler;
    @Resource
    protected ShiroConfig shiroConfig;



    @Override
    public String getAuthenticationCacheName() {
        return Strings.isNullOrEmpty(shiroConfig.getAuthenticationCacheName()) ? super.getAuthenticationCacheName() : shiroConfig.getAuthenticationCacheName();
    }

    @Override
    public String getAuthorizationCacheName() {
        return Strings.isNullOrEmpty(shiroConfig.getAuthorizationCacheName()) ? super.getAuthorizationCacheName() : shiroConfig.getAuthorizationCacheName();
    }

    /**
     * 认证
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        String credentials = (String) token.getCredentials();
        UserInfo user;
        try {
            user = securityUserHandler.auth(token.getPrincipal(),credentials);
        } catch (HostUnauthorizedException | DisabledAccountException | ExcessiveAttemptsException | IncorrectCredentialsException | UnsupportedTokenException |  ConcurrentAccessException ex){
            throw ex;
        } catch (LockedAccountException ex){
            throw new org.apache.shiro.authc.LockedAccountException(ex.getMessage());
        } catch (UnknownAccountException ex){
            throw new org.apache.shiro.authc.UnknownAccountException(ex.getMessage());
        } catch (TokenAuthException ex){
            throw new IncorrectCredentialsException(ex.getMessage());
        } catch (TokenGenerateException ex){
            throw new CredentialsException(ex.getMessage());
        } catch (TokenExpiredException ex){
            throw new ExpiredCredentialsException(ex.getMessage());
        } catch (SecurityException e) {
            throw new AuthenticationException(e.getMessage(), e);
        }
        return new SimpleAuthenticationInfo(user,credentials,user.getId().toString());
    }


    /**
     * 授权
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        if (!SecurityUtils.getSubject().isAuthenticated()) {
            doClearCache(principals);
            SecurityUtils.getSubject().logout();
            return null;
        }
        UserInfo user = (UserInfo) principals.getPrimaryPrincipal();
        log.info("[auth] GetUser userId: {} , userName: {}, realName: {}",user.getId(),user.getUserName(),user.getRealName());
        PrivilegeInfo pvg = securityUserHandler.pvgInfo(user.getId());
        log.info("[auth] PvgInfo userId: {} , userName: {}, realName: {} ,roles:{}",pvg.getId(),pvg.getUserName(),pvg.getRealName(),pvg.getRoles());
        //如果pvg没有roleCode，从User中获取，如果都没有，则不处理
        if (pvg.getRoles() == null || Iterables.isEmpty(pvg.getRoles())) {
            if (user.getRoleCodes() != null && !user.getRoleCodes().isEmpty()) {
                user.getRoleCodes().forEach(pvg::addRole);
            }
        }
        //SimpleAuthorizationInfo 类序列化并存储在redis中
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.addRoles(pvg.getRoles());
        info.addStringPermissions(pvg.getPermissions());
        return info;
    }


}
