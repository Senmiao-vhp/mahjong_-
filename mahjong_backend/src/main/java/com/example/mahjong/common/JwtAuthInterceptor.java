package com.example.mahjong.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.logging.Logger;

@Component
public class JwtAuthInterceptor implements HandlerInterceptor {
    
    // 添加日志记录器
    private static final Logger logger = Logger.getLogger(JwtAuthInterceptor.class.getName());
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 记录请求路径和方法
        String method = request.getMethod();
        String uri = request.getRequestURI();
        logger.info("JWT拦截器处理请求: " + method + " " + uri);
        
        // 特殊处理GET /rooms请求
        if (uri.equals("/rooms") && method.equals("GET")) {
            logger.info("GET /rooms 请求，不需要认证，直接放行");
            return true;
        }
        
        // 记录所有请求头，帮助调试
        logger.info("请求头信息:");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            // 对于敏感信息，只显示部分内容
            if (headerName.equalsIgnoreCase("Authorization") && headerValue != null && headerValue.length() > 20) {
                headerValue = headerValue.substring(0, 20) + "...";
            }
            logger.info("  " + headerName + ": " + headerValue);
        }
        
        // 获取请求头中的Authorization
        String token = request.getHeader("Authorization");
        logger.info("Authorization头: " + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null"));
        
        // 检查是否存在token且格式正确
        if (token != null && token.startsWith("Bearer ")) {
            // 提取token内容
            token = token.substring(7);
            logger.info("提取的JWT令牌: " + token.substring(0, Math.min(20, token.length())) + "...");
            
            try {
                // 验证token
                boolean isValid = jwtUtil.validateToken(token);
                logger.info("JWT令牌验证结果: " + isValid);
                
                if (isValid) {
                    // 从token中获取用户ID
                    Long userId = jwtUtil.getUserIdFromToken(token);
                    logger.info("从JWT令牌中提取的用户ID: " + userId);
                    
                    if (userId == null) {
                        logger.severe("从JWT令牌中提取的用户ID为null，这是一个严重问题");
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("{\"code\": 401, \"msg\": \"无效的令牌，无法提取用户ID\", \"data\": null}");
                        return false;
                    }
                    
                    // 将用户ID存储到请求属性中，供后续使用
                    request.setAttribute("userId", userId);
                    logger.info("用户ID已存储到请求属性中: " + userId);
                    return true;
                } else {
                    logger.warning("JWT令牌无效");
                }
            } catch (Exception e) {
                logger.severe("JWT令牌处理异常: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            logger.warning("请求中没有有效的Authorization头");
        }
        
        // 对于未认证的请求，返回401错误
        logger.warning("认证失败，返回401错误");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"code\": 401, \"msg\": \"未授权\", \"data\": null}");
        return false;
    }
} 