package com.tsu.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsu.token.JwtToken;
import com.tsu.utils.JwtUtil;
import com.tsu.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author zzz
 */
@Component
@Slf4j
public class JwtFilter extends BasicHttpAuthenticationFilter {


    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    Result result;

    /**
     * 前置处理，第一个执行
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        //转换请求和响应
        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }

    /**
     * 后置处理
     */
    @Override
    protected void postHandle(ServletRequest request, ServletResponse response) {
        // 添加跨域支持
        fillCorsHeader(WebUtils.toHttp(request), WebUtils.toHttp(response));
    }

    protected void fillCorsHeader(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,HEAD,PUT,DELETE");
        response.setHeader("Access-Control-Allow-Headers",
                request.getHeader("Access-Control-Request-Headers"));
    }

    /**
     * 过滤器拦截请求的入口方法
     * 返回 true 则允许访问
     * 返回false 则禁止访问，会进入 onAccessDenied()
     * 第二个执行
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        // 用来检测Header中是否包含 JWT token 字段，没有直接返回false
        if (isLoginRequest(request, response)) {
            return false;
        }
        //用来接受登录是否成功的值
        boolean allowed = false;
        try {
            // 检测Header里的 JWT token内容是否正确，尝试使用 token进行登录
            allowed = executeLogin(request, response);
        } catch (IllegalStateException e) { // not found any token
            log.error("Not found any token {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error occurs when login {}", e.getMessage());
        }
        return allowed || super.isPermissive(mappedValue);
    }

    /**
     * 检测Header中是否包含 JWT token 字段
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        return ((HttpServletRequest) request).getHeader(JwtUtil.AUTH_HEADER) == null;
    }


    /**
     * 身份验证,检查 JWT token 是否合法
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        AuthenticationToken token = null;
        try {
            token = createToken(request, response);
            if (token == null) {
                String msg = "createToken method implementation returned null. A valid non-null AuthenticationToken "
                        + "must be created in order to execute a login attempt.";
                throw new IllegalStateException(msg);
            }
            Subject subject = getSubject(request, response);
            // 交给 Shiro 去进行登录验证
            subject.login(token);
            return onLoginSuccess(token, subject, request, response);
        } catch (AuthenticationException e) {
            return onLoginFailure(token, e, request, response);
        }
    }


    /**
     * 从 Header 里提取 JWT token
     * 第三步执行
     */
    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String authorization = httpServletRequest.getHeader(JwtUtil.AUTH_HEADER);
        JwtToken token = new JwtToken(authorization);
        return token;
    }

    /**
     * isAccessAllowed()方法返回false，会进入该方法，表示拒绝访问
     * 第四个执行
     */
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletResponse httpResponse = WebUtils.toHttp(servletResponse);
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setContentType("application/json;charset=UTF-8");
        httpResponse.setStatus(200);
        PrintWriter writer = httpResponse.getWriter();
        writer.write(objectMapper.writeValueAsString(result));
        fillCorsHeader(WebUtils.toHttp(servletRequest), httpResponse);
        return false;
    }


    /**
     * Shiro 利用 JWT token 登录成功，会进入该方法
     */
    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
        //todo 这里时关于对token的自动续期
        HttpServletResponse httpResponse = WebUtils.toHttp(response);
        String newToken = null;
        if (token instanceof JwtToken) {
            newToken = JwtUtil.refreshTokenExpired(token.getCredentials().toString());
        }
        if (newToken != null) {
            httpResponse.setHeader(JwtUtil.AUTH_HEADER, newToken);
        }
        return true;
    }

    /**
     * Shiro 利用 JWT token 登录失败，会进入该方法
     */
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        // 此处直接返回 false ，交给后面的  onAccessDenied()方法进行处理
        return false;
    }


}
