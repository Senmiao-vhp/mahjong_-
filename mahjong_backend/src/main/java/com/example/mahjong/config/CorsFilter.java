package com.example.mahjong.config;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * 全局CORS过滤器，确保所有请求都带有正确的CORS头
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

    private static final Logger logger = Logger.getLogger(CorsFilter.class.getName());

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String origin = request.getHeader("Origin");
        if (origin != null && (origin.equals("http://localhost:5174") || origin.contains("localhost"))) {
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", 
                "Origin, Content-Type, Accept, Authorization, X-Requested-With, Access-Control-Request-Method, Access-Control-Request-Headers");
            response.setHeader("Access-Control-Max-Age", "3600");
        }

        // 对于OPTIONS请求，直接返回200状态码
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            logger.info("处理OPTIONS预检请求: " + request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(req, res);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {
        logger.info("CORS过滤器初始化");
    }

    @Override
    public void destroy() {
        logger.info("CORS过滤器销毁");
    }
} 