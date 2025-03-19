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
import java.util.logging.Logger;

@Component
public class JwtUtil {

    // 添加日志记录器
    private static final Logger logger = Logger.getLogger(JwtUtil.class.getName());

    @Autowired
    private JwtConfig jwtConfig;

    /**
     * 生成JWT令牌
     */
    public String generateToken(User user) {
        logger.info("开始为用户生成JWT令牌: " + user.getId());
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("nickname", user.getNickname());
        
        logger.info("JWT载荷: id=" + user.getId() + ", nickname=" + user.getNickname());

        SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
        logger.info("JWT密钥长度: " + jwtConfig.getSecret().length());

        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtConfig.getExpiration());
        logger.info("JWT过期时间: " + expiration);

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        
        logger.info("JWT令牌生成成功: " + token.substring(0, Math.min(20, token.length())) + "...");
        return token;
    }

    /**
     * 从JWT令牌中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        logger.info("开始从JWT令牌中提取用户ID");
        
        try {
            // 记录令牌前20个字符，帮助调试
            logger.info("解析令牌: " + token.substring(0, Math.min(20, token.length())) + "...");
            
            // 记录密钥信息
            String secret = jwtConfig.getSecret();
            logger.info("使用密钥: " + secret.substring(0, Math.min(10, secret.length())) + "..., 长度: " + secret.length());
            
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            
            // 解析令牌
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            logger.info("令牌解析成功，获取到的Claims: " + claims);
            
            // 获取id字段
            Object idObj = claims.get("id");
            logger.info("从JWT令牌中提取的ID对象: " + idObj + ", 类型: " + (idObj != null ? idObj.getClass().getName() : "null"));
            
            // 如果id为null，直接返回null
            if (idObj == null) {
                logger.severe("JWT令牌中没有id字段");
                return null;
            }
            
            // 尝试转换为Long类型
            try {
                Long userId = Long.valueOf(idObj.toString());
                logger.info("从JWT令牌中提取的用户ID: " + userId);
                return userId;
            } catch (NumberFormatException e) {
                logger.severe("JWT令牌中的id字段无法转换为Long类型: " + e.getMessage());
                return null;
            }
        } catch (Exception e) {
            logger.severe("从JWT令牌中提取用户ID失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 验证JWT令牌
     */
    public boolean validateToken(String token) {
        logger.info("开始验证JWT令牌");
        
        try {
            // 记录令牌前20个字符，帮助调试
            logger.info("验证令牌: " + token.substring(0, Math.min(20, token.length())) + "...");
            
            // 记录密钥信息
            String secret = jwtConfig.getSecret();
            logger.info("使用密钥: " + secret.substring(0, Math.min(10, secret.length())) + "..., 长度: " + secret.length());
            
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            
            // 验证令牌
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            
            logger.info("JWT令牌验证成功");
            return true;
        } catch (Exception e) {
            logger.severe("JWT令牌验证失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}