package com.nexus.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 * 用于生成、解析和校验JWT Token
 */
public class JwtUtil {
    
    /**
     * JWT密钥（实际项目中应该从配置文件读取）
     */
    private static final String SECRET = "nexus-brain-jwt-secret-key-2024-very-long-secret-key-for-security";
    
    /**
     * Token过期时间（毫秒），默认7天
     */
    private static final long EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000L;
    
    /**
     * 生成SecretKey
     */
    private static SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * 生成JWT Token
     * @param userId 用户ID
     * @param username 用户名
     * @return JWT Token字符串
     */
    public static String generateToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION_TIME);
        
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSecretKey())
                .compact();
    }
    
    /**
     * 从Token中解析Claims
     * @param token JWT Token
     * @return Claims对象
     */
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    /**
     * 从Token中获取用户ID
     * @param token JWT Token
     * @return 用户ID
     */
    public static Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        Object userId = claims.get("userId");
        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        }
        return (Long) userId;
    }
    
    /**
     * 从Token中获取用户名
     * @param token JWT Token
     * @return 用户名
     */
    public static String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("username", String.class);
    }
    
    /**
     * 校验Token是否有效
     * @param token JWT Token
     * @return 是否有效
     */
    public static boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            // 检查是否过期
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 检查Token是否过期
     * @param token JWT Token
     * @return 是否过期
     */
    public static boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}
