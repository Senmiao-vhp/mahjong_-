package com.example.mahjong.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

@Component
public class JwtAuthInterceptor implements HandlerInterceptor {
    
    // 保留日志记录器用于非切面的日志记录
    private static final Logger logger = Logger.getLogger(JwtAuthInterceptor.class.getName());
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 记录请求路径和方法 - 日志已移至切面
        String method = request.getMethod();
        String uri = request.getRequestURI();
        
        // 特殊处理OPTIONS请求（预检请求），直接放行
        if (method.equals("OPTIONS")) {
            logger.info("OPTIONS预检请求，不需要认证，直接放行");
            return true;
        }
        
        // 特殊处理GET /rooms请求
        if (uri.equals("/rooms") && method.equals("GET")) {
            logger.info("GET /rooms 请求，不需要认证，直接放行");
            return true;
        }
        
        // 记录所有请求头 - 日志已移至切面
        
        // 获取请求头中的Authorization
        String token = request.getHeader("Authorization");
        
        // 检查是否存在token且格式正确
        if (token != null && token.startsWith("Bearer ")) {
            // 提取token内容
            token = token.substring(7);
            
            try {
                // 验证token - 日志已移至切面
                boolean isValid = jwtUtil.validateToken(token);
                
                if (isValid) {
                    // 从token中获取用户ID - 日志已移至切面
                    Long userId = jwtUtil.getUserIdFromToken(token);
                    
                    if (userId == null) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("{\"code\": 401, \"msg\": \"无效的令牌，无法提取用户ID\", \"data\": null}");
                        return false;
                    }
                    
                    // 将用户ID存储到请求属性中，供后续使用
                    request.setAttribute("userId", userId);
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