package com.example.mahjong.controller;

import com.example.mahjong.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 * 提供API状态检查端点
 */
@RestController
@RequestMapping("/api/health")
public class HealthController {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 健康检查端点
     * 返回服务器状态信息
     */
    @GetMapping
    public Result<Map<String, Object>> healthCheck() {
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("timestamp", LocalDateTime.now().format(FORMATTER));
        healthInfo.put("service", "Mahjong Game API");
        healthInfo.put("version", "1.0.0");
        
        return Result.success(healthInfo);
    }
} 