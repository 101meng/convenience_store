package com.lin101.store.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secretString;

    @Value("${jwt.expiration}")
    private long expiration;

    // 获取加密用的 Key
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secretString.getBytes());
    }

    /**
     * 生成 JWT Token
     */
    public String generateToken(Integer userId, String phone) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(String.valueOf(userId)) // 将 userId 作为主体存放
                .claim("phone", phone)           // 可以额外存放手机号等非敏感信息
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSecretKey())        // 使用密钥签名
                .compact();
    }
}