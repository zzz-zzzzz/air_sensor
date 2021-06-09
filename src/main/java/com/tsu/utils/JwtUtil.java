package com.tsu.utils;

/**
 * @author ZZZ
 * @description
 */

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.Map;

/**
 *
 */
public class JwtUtil {
    /**
     * 过期时间
     */
    private static final long EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000;
    /**
     * 签名
     */
    private static final String SIGNATURE = "6587ed5d71304757863dc4bb4bf72462";

    public static final String AUTH_HEADER = "token";


    /**
     * 生成Jwt token
     *
     *
     * @return
     */
    public static String getJwtToken(Integer userId, String username,boolean isAdmin) {
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        Algorithm algorithm = Algorithm.HMAC256(SIGNATURE);
        return JWT.create()
                .withClaim("username", username)
                .withClaim("userId", userId)
                .withClaim("isAdmin",isAdmin)
                //过期时间
                .withExpiresAt(date)
                //签名算法
                .sign(algorithm);
    }

    /**
     * 校验Jwt token
     *
     * @param token 令牌
     * @return
     */
    public static boolean verify(String token) {
        try {
            verifyAndGetDecodeJwt(token);
            return true;
        } catch (JWTVerificationException exception) {
            return false;
        }
    }

    private static DecodedJWT verifyAndGetDecodeJwt(String token) throws JWTVerificationException {
        return JWT.require(Algorithm.HMAC256(SIGNATURE)).build().verify(token);
    }

    /**
     * 获取token中存储的内容
     *
     * @param token 令牌
     * @return
     */
    public static Map<String, Claim> getTokenInfo(String token) {
        DecodedJWT verify = verifyAndGetDecodeJwt(token);
        return verify.getClaims();
    }

    public static String getTokenInfo(String token, String key) {
        return getTokenInfo(token).get(key).asString();
    }

    public static String getUsername(String token) {
        return getTokenInfo(token, "username");
    }

    /**
     * 判断是否过期
     */
    public static boolean isExpire(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getExpiresAt().getTime() < System.currentTimeMillis();
    }

    public static String refreshTokenExpired(String toString) {
        return null;
    }
}
