package com.example.mahjong.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.mahjong.config.JwtConfig;
import com.example.mahjong.entity.User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Autowired
    private JwtConfig jwtConfig;

    /**
     * 生成JWT令牌
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("nickname", user.getNickname());

        SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));

        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtConfig.getExpiration());

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        
        return token;
    }

    /**
     * 从JWT令牌中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
            
            // 解析令牌
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            // 获取id字段
            Object idObj = claims.get("id");
            
            // 如果id为null，直接返回null
            if (idObj == null) {
                return null;
            }
            
            // 尝试转换为Long类型
            try {
                return Long.valueOf(idObj.toString());
            } catch (NumberFormatException e) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 验证JWT令牌
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
            
            // 验证令牌
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}