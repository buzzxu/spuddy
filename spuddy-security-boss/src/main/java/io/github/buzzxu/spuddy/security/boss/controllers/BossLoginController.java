package io.github.buzzxu.spuddy.security.boss.controllers;

import com.google.common.base.Strings;
import io.github.buzzxu.spuddy.R;
import io.github.buzzxu.spuddy.errors.LockedAccountException;
import io.github.buzzxu.spuddy.exceptions.ApplicationException;
import io.github.buzzxu.spuddy.objects.Pair;
import io.github.buzzxu.spuddy.security.objects.BossUserInfo;
import io.github.buzzxu.spuddy.security.boss.controllers.requests.LoginUserPwdRequest;
import io.github.buzzxu.spuddy.security.boss.controllers.responses.CaptchaResponse;
import io.github.buzzxu.spuddy.security.boss.controllers.responses.LoginResponse;
import io.github.buzzxu.spuddy.security.controllers.AbstractLoginController;
import io.github.buzzxu.spuddy.security.funs.FunctionThrowsSecurity;
import io.github.buzzxu.spuddy.security.handler.SecurityUserHandler;
import io.github.buzzxu.spuddy.security.jwt.JwtConfig;
import io.github.buzzxu.spuddy.security.objects.UserInfo;
import io.github.buzzxu.spuddy.security.services.CaptchaService;
import io.github.buzzxu.spuddy.security.shiro.JwtToken;
import jakarta.annotation.Resource;
import org.apache.shiro.SecurityUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;



import static com.google.common.base.Preconditions.checkArgument;

/**
 * @program: yuanmai-components
 * @description:
 * @author: 徐翔
 * @create: 2019-12-02 20:26
 **/
@RequestMapping(value = "/",produces = MediaType.APPLICATION_JSON_VALUE)
public abstract class BossLoginController<U extends BossUserInfo> extends AbstractLoginController<U> {
    @Resource
    protected CaptchaService captchaService;
    @Resource
    protected JwtConfig jwtConfig;

    private Class<U> bossInfoClass;

    public BossLoginController(Class<U> bossInfoClass) {
        this.bossInfoClass = bossInfoClass;
    }

    /**
     *
     * @param param 用户登录
     * @return
     * @throws SecurityException
     */
    @PostMapping("/login")
    public  R<Object> login(@RequestBody LoginUserPwdRequest param) throws SecurityException {
        checkArgument(!Strings.isNullOrEmpty(param.getValiCode()),"验证码不能为空");
        checkArgument(!Strings.isNullOrEmpty(param.getUserName()),"请填写账户");
        checkArgument(!Strings.isNullOrEmpty(param.getPassword()),"请填写密码");
        try {
            Pair<String,U> user = accountService.login(param.getUserName().trim(),param.getPassword().trim(),param.getKey(),param.getValiCode().trim(), param.getRegion() != null ? param.getRegion() : jwtConfig.getRegion(),param.getLang(),userInfoFunc(param.getLang()));
            JwtToken token = new JwtToken(user.getKey());
            SecurityUtils.getSubject().login(token);
            return R.of(loginInfo(user));
        }catch (LockedAccountException ex){
            return R.error(400,ex.getMessage());
        }
    }


    protected FunctionThrowsSecurity<Long,U> userInfoFunc(String lang){
        return userId->{
            try {
                return (U) userInfoService.of(userId,bossInfoClass);
            } catch (SecurityException e) {
                throw ApplicationException.argument(e.getMessage());
            }catch (Exception e) {
                throw ApplicationException.raise(e);
            }
        };
    }

    protected  Object loginInfo(Pair<String,U> user){
        UserInfo $user = user.getValue();
        LoginResponse val = new LoginResponse();
        val.setFirstLogin($user.isFirstLogin());
        val.setToken(user.getKey());
        val.setUserName($user.getUserName());
        return val;
    }
    /**
     * 用户注销
     * @param token 登录令牌
     * @return
     * @throws SecurityException
     */
    @DeleteMapping("/logout")
    public R<Boolean> logout(@RequestHeader(SecurityUserHandler.KEY) String token) throws SecurityException {
        UserInfo userInfo = userInfo();
        if(Strings.isNullOrEmpty(token)){
            accountService.logOut(userInfo.getId());
        }else{
            accountService.logOut(userInfo.getId(),token);
        }
        SecurityUtils.getSubject().logout();
        return R.of(true);
    }


    /**
     * 获取验证码
     * @param key
     * @return
     */
    @GetMapping("/captcha")
    public R<CaptchaResponse> captcha(@RequestParam(value = "key",required = false) String key)  {
        Pair<String,String> result = captchaService.generator(key);
        return R.of(new CaptchaResponse(result.getKey(),result.getValue()));
    }


    @Override
    public <U extends UserInfo> U userInfo() {
        return (U) SecurityUtils.getSubject().getPrincipal();
    }
}
