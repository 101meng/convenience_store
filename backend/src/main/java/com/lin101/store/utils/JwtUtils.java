package com.lin101.store.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT 工具：密钥与有效期来自 {@code jwt.secret}、{@code jwt.expiration}（毫秒）；
 * {@link #generateToken} 将 {@code userId} 写入 subject 供 {@link com.lin101.store.interceptor.JwtInterceptor} 还原。
 */
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secretString;

    @Value("${jwt.expiration}")
    private long expiration;

    /** HMAC-SHA 密钥由配置字符串派生。 */
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secretString.getBytes());
    }

    /**
     * 签发访问令牌；subject 为 {@code userId} 字符串，附带非敏感 claim {@code phone}。
     */
    public String generateToken(Integer userId, String phone) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("phone", phone)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * 解析 Token subject 为整数用户 ID；签名错误或过期返回 {@code null}。
     */
    public Integer getUserIdFromToken(String token) {
        try {
            return Integer.parseInt(Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject());
        } catch (Exception e) {
            return null;
        }
    }
}