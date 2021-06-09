package com.tsu.realm;

import com.tsu.credentialsmatcher.JwtCredentialsMatcher;
import com.tsu.entity.Role;
import com.tsu.entity.User;
import com.tsu.service.RoleService;
import com.tsu.service.UserService;
import com.tsu.token.JwtToken;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author zzz
 */
@Component
public class JwtRealm extends AuthorizingRealm {


    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @Autowired
    HttpServletRequest request;

    /**
     * 限定这个 Realm 只处理自定义的JwtToken
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        User user = (User) principalCollection.getPrimaryPrincipal();
        List<String> roleList = roleService.getRoleByUserId(user.getId());
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.addRoles(roleList);
        return authorizationInfo;
    }

    /**
     * 此处的 SimpleAuthenticationInfo 可返回任意值，密码校验时不会用到它
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        JwtToken jwtToken = (JwtToken) authenticationToken;
        if (jwtToken.getPrincipal() == null) {
            throw new AccountException("JWT token参数异常！");
        }
        // 从 JwtToken 中获取当前用户的用户名
        String username = jwtToken.getPrincipal().toString();
        User user = userService.getByUsername(username);
        // 用户不存在
        if (user == null) {
            throw new UnknownAccountException("用户不存在！");
        }
        request.setAttribute("userId",user.getId());
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, username, getName());
        return info;
    }


    /**
     * 配置密码匹配器
     */
    @PostConstruct
    public void updateCredentialsMatcher() {
        setCredentialsMatcher(new JwtCredentialsMatcher());
    }
}
