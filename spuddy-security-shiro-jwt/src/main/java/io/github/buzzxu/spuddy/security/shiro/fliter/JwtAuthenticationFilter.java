package io.github.buzzxu.spuddy.security.shiro.fliter;

import com.google.common.base.Strings;
import com.google.common.net.MediaType;
import io.github.buzzxu.spuddy.errors.SecurityException;
import io.github.buzzxu.spuddy.exceptions.ApplicationException;
import io.github.buzzxu.spuddy.security.GetMeToken;
import io.github.buzzxu.spuddy.security.jwt.GetToken;
import io.github.buzzxu.spuddy.security.shiro.JwtToken;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.lang.ShiroException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;


import java.io.IOException;
import java.util.stream.Stream;


@Slf4j
public class JwtAuthenticationFilter extends AuthenticatingFilter {

    /**
     * 判断用户是否想要登入
     * @param request
     * @param response
     * @return
     */
    @Override
    protected boolean isLoginRequest(ServletRequest request, ServletResponse response) {
        return !Strings.isNullOrEmpty(getAuthzHeader(request));
    }




    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String authorization = getAuthzHeader(httpServletRequest);
        if(Strings.isNullOrEmpty(authorization)){
            ((HttpServletResponse)response).setStatus(HttpServletResponse.SC_OK);
            response.setContentType(MediaType.JSON_UTF_8.toString());
            response.setCharacterEncoding("UTF-8");
            response.getWriter().println("{\"code+\":"+HttpServletResponse.SC_UNAUTHORIZED+",\"message\":\"鉴权失败,未获取访问权限\"}");
            return false;
        }
        JwtToken token = new JwtToken(authorization);
        // 提交给realm进行登入，如果错误他会抛出异常并被捕获
        try {
            getSubject(request, response).login(token);
            // 如果没有抛出异常则代表登入成功，返回true
            return true;
        }catch (SecurityException ex){
            ((HttpServletResponse)response).setStatus(HttpServletResponse.SC_OK);
            response.setContentType(MediaType.JSON_UTF_8.toString());
            response.setCharacterEncoding("UTF-8");
            response.getWriter().println("{\"code+\":"+ex.status()+",\"message\":\""+ex.getMessage()+"\"}");
            return false;
        }catch (AuthenticationException ex){
            ((HttpServletResponse)response).setStatus(HttpServletResponse.SC_OK);
            response.setContentType(MediaType.JSON_UTF_8.toString());
            response.setCharacterEncoding("UTF-8");
            response.getWriter().println("{\"code\":"+HttpServletResponse.SC_UNAUTHORIZED+",\"message\":\""+ex.getMessage()+"\"}");
            return false;
        }catch (AuthorizationException ex){
            ((HttpServletResponse)response).setStatus(HttpServletResponse.SC_OK);
            response.setContentType(MediaType.JSON_UTF_8.toString());
            response.setCharacterEncoding("UTF-8");
            response.getWriter().println("{\"code\":"+HttpServletResponse.SC_UNAUTHORIZED+",\"message\":\""+ex.getMessage()+"\"}");
            return false;
        }catch (ShiroException ex){
            log.error(ex.getMessage());
            ((HttpServletResponse)response).setStatus(HttpServletResponse.SC_OK);
            response.setContentType(MediaType.JSON_UTF_8.toString());
            response.setCharacterEncoding("UTF-8");
            response.getWriter().println("{\"code\":"+HttpServletResponse.SC_UNAUTHORIZED+",\"message\":\""+ex.getMessage()+"\"}");
            return false;
        }
    }



    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        return new JwtToken(getAuthzHeader(httpRequest));
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        return super.isAccessAllowed(request, response, mappedValue);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        if(isLoginRequest(request, response)){
            return executeLogin(request, response);
        }else{
            ((HttpServletResponse)response).setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.JSON_UTF_8.toString());
            response.setCharacterEncoding("UTF-8");
            response.getWriter().println("{\"code\":"+HttpServletResponse.SC_FORBIDDEN+",\"message\":\"无权访问此资源\"}");
            return false;
        }

    }


    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        try {
            HttpServletResponse httpResponse = WebUtils.toHttp(response);
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.JSON_UTF_8.toString());
            response.setCharacterEncoding("UTF-8");
            response.getWriter().println("{\"code\":"+HttpServletResponse.SC_UNAUTHORIZED+",\"message\":\"登录失败,请验证登录信息\"}");
        }catch (Exception ex){
           response401(request,response);
        }
        return false;
    }



    protected String getAuthzHeader(ServletRequest request) {
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        return GetToken.of(httpRequest);
    }

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (httpServletRequest.getMethod().equals("OPTIONS")) {
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            return false;
        }
        return super.preHandle(request, response);
    }

    @Override
    protected void postHandle(ServletRequest request, ServletResponse response) throws Exception {
        super.postHandle(request, response);
        GetMeToken.clear();
    }

    protected boolean checkRoles(Subject subject, Object mappedValue){
        String[] rolesArray = (String[]) mappedValue;
        if (rolesArray == null || rolesArray.length == 0) {
            return true;
        }
        return Stream.of(rolesArray)
                .anyMatch(subject::hasRole);
    }

    protected boolean checkPerms(Subject subject, Object mappedValue){
        String[] perms = (String[]) mappedValue;
        boolean isPermitted = true;
        if (perms != null && perms.length > 0) {
            if (perms.length == 1) {
                if (!subject.isPermitted(perms[0])) {
                    isPermitted = false;
                }
            } else {
                if (!subject.isPermittedAll(perms)) {
                    isPermitted = false;
                }
            }
        }
        return isPermitted;
    }


    /**
     * 将非法请求跳转到 /401
     */
    private void response401(ServletRequest req, ServletResponse resp) {
        try {
            HttpServletResponse httpServletResponse = (HttpServletResponse) resp;
            httpServletResponse.sendRedirect("/401");
        } catch (IOException ex) {
            throw ApplicationException.raise(ex);
        }
    }
}
