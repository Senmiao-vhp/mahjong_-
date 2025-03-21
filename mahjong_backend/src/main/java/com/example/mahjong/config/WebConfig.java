package com.example.mahjong.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.mahjong.common.JwtAuthInterceptor;

import java.util.logging.Logger;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    // 添加日志记录器
    private static final Logger logger = Logger.getLogger(WebConfig.class.getName());
    
    @Autowired
    private JwtAuthInterceptor jwtAuthInterceptor;
    
    // 添加CORS配置
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        logger.info("配置CORS跨域");
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5174") // 明确指定前端开发服务器地址
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Origin", "Content-Type", "Accept", "Authorization", "X-Requested-With", "Access-Control-Request-Method", "Access-Control-Request-Headers")
                .exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials")
                .allowCredentials(true) // 允许发送Cookie
                .maxAge(3600L); // 1小时内不再预检
        logger.info("CORS配置完成，允许来自http://localhost:5174的请求");
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        logger.info("配置JWT拦截器");
        
        // 主要拦截器，排除基本路径
        registry.addInterceptor(jwtAuthInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                    "/",              // 排除根路径
                    "/health",        // 排除健康检查路径
                    "/auth/**",       // 排除登录注册等不需要认证的路径
                    "/error",         // 排除错误页面
                    "/swagger-ui/**", // 排除swagger-ui路径
                    "/v3/api-docs/**", // 排除OpenAPI文档路径
                    "/swagger-ui.html", // 排除swagger-ui主页
                    "/webjars/**"      // 排除webjars路径
                );
        
        logger.info("JWT拦截器配置完成，排除路径: /auth/**, /error, /swagger-ui/**, /v3/api-docs/**");
        logger.info("所有/rooms路径都需要认证，但在JwtAuthInterceptor中会特殊处理GET /rooms请求");
    }
    
    // 确保静态资源可以正确加载
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/");
    }
} 