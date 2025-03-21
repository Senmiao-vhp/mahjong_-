package com.example.mahjong.aspect;

import com.example.mahjong.entity.User;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.logging.Logger;

/**
 * 日志切面，集中处理API请求的日志记录
 */
@Aspect
@Component
public class LoggingAspect {
    
    private static final Logger logger = Logger.getLogger(LoggingAspect.class.getName());
    
    
    /**
     * 拦截JWT拦截器的preHandle方法，记录请求信息
     */
    @Before("execution(* com.example.mahjong.common.JwtAuthInterceptor.preHandle(..))")
    public void logBeforeJwtAuth(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof HttpServletRequest) {
            HttpServletRequest request = (HttpServletRequest) args[0];
            
            // 记录请求路径和方法
            String method = request.getMethod();
            String uri = request.getRequestURI();
            logger.info("JWT拦截器处理请求: " + method + " " + uri);
            
            // 记录所有请求头，帮助调试
            logRequestHeaders(request);
            
            // 记录Authorization头信息
            String token = request.getHeader("Authorization");
            logger.info("Authorization头: " + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null"));
            
            // 如果存在有效的token，记录token信息
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                logger.info("提取的JWT令牌: " + jwtToken.substring(0, Math.min(20, jwtToken.length())) + "...");
            }
        }
    }
    
    /**
     * 记录请求头信息
     */
    private void logRequestHeaders(HttpServletRequest request) {
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
    }
    
    /**
     * 拦截JwtUtil的generateToken方法，记录令牌生成过程
     */
    @Before("execution(* com.example.mahjong.common.JwtUtil.generateToken(..))")
    public void logBeforeGenerateToken(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof User) {
            User user = (User) args[0];
            logger.info("开始为用户生成JWT令牌: " + user.getId());
            logger.info("JWT载荷: id=" + user.getId() + ", nickname=" + user.getNickname());
        }
    }
    
    /**
     * 拦截JwtUtil的generateToken方法返回，记录令牌生成结果
     */
    @AfterReturning(pointcut = "execution(* com.example.mahjong.common.JwtUtil.generateToken(..))", returning = "token")
    public void logAfterGenerateToken(JoinPoint joinPoint, String token) {
        if (token != null) {
            logger.info("JWT令牌生成成功: " + token.substring(0, Math.min(20, token.length())) + "...");
        }
    }
    
    /**
     * 拦截JwtUtil的getUserIdFromToken方法，记录获取用户ID过程
     */
    @Before("execution(* com.example.mahjong.common.JwtUtil.getUserIdFromToken(..))")
    public void logBeforeGetUserIdFromToken(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof String) {
            String token = (String) args[0];
            logger.info("开始从JWT令牌中提取用户ID");
            logger.info("解析令牌: " + token.substring(0, Math.min(20, token.length())) + "...");
        }
    }
    
    /**
     * 拦截JwtUtil的validateToken方法，记录令牌验证过程
     */
    @Before("execution(* com.example.mahjong.common.JwtUtil.validateToken(..))")
    public void logBeforeValidateToken(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof String) {
            String token = (String) args[0];
            logger.info("开始验证JWT令牌");
            logger.info("验证令牌: " + token.substring(0, Math.min(20, token.length())) + "...");
        }
    }
    
    /**
     * 拦截JWT验证成功后的处理
     */
    @AfterReturning(
        pointcut = "execution(* com.example.mahjong.common.JwtUtil.validateToken(..))",
        returning = "result"
    )
    public void logTokenValidation(JoinPoint joinPoint, Object result) {
        boolean isValid = (Boolean) result;
        logger.info("JWT令牌验证结果: " + isValid);
    }
    
    /**
     * 拦截JWT验证异常
     */
    @AfterThrowing(
        pointcut = "execution(* com.example.mahjong.common.JwtUtil.validateToken(..))",
        throwing = "exception"
    )
    public void logTokenValidationException(JoinPoint joinPoint, Exception exception) {
        logger.severe("JWT令牌验证异常: " + exception.getMessage());
    }
    
    /**
     * 拦截获取用户ID方法
     */
    @AfterReturning(
        pointcut = "execution(* com.example.mahjong.common.JwtUtil.getUserIdFromToken(..))",
        returning = "result"
    )
    public void logUserIdFromToken(JoinPoint joinPoint, Object result) {
        Long userId = (Long) result;
        if (userId != null) {
            logger.info("从JWT令牌中提取的用户ID: " + userId);
            logger.info("用户ID已存储到请求属性中: " + userId);
        } else {
            logger.severe("从JWT令牌中提取的用户ID为null，这是一个严重问题");
        }
    }
    
    /**
     * 拦截获取用户ID方法异常
     */
    @AfterThrowing(
        pointcut = "execution(* com.example.mahjong.common.JwtUtil.getUserIdFromToken(..))",
        throwing = "exception"
    )
    public void logUserIdFromTokenException(JoinPoint joinPoint, Exception exception) {
        logger.severe("从JWT令牌中提取用户ID失败: " + exception.getMessage());
    }
} 