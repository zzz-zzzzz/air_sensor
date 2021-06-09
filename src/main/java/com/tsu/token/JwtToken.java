package com.tsu.token;


import com.tsu.utils.JwtUtil;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author zzz
 */
public class JwtToken implements AuthenticationToken {

    private static final long serialVersionUID = 1L;

    /**
     * 加密后的 JWT token串
     */
    private String token;

    private String username;


    public JwtToken(String token) {
        this.token = token;
        this.username = JwtUtil.getUsername(token);
    }

    @Override
    public Object getPrincipal() {
        return this.username;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}

