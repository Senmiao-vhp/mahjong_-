package com.example.mahjong.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mahjong.common.Result;
import com.example.mahjong.common.ApiDocResponseModel;
import com.example.mahjong.entity.Room;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.Map;

/**
 * API文档示例控制器
 * 演示正确的Swagger文档配置，用于解决Apifox导入问题
 */
@Tag(name = "API文档", description = "API文档示例")
@RestController
@RequestMapping("/api-doc")
public class ApiDocController {

    /**
     * 正确的响应定义示例
     */
    @Operation(summary = "获取示例数据", description = "演示正确的Swagger响应结构")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功", 
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ApiDocResponseModel.class)))
    })
    @GetMapping("/example")
    public Result<Map<String, Object>> getExample() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "示例数据");
        data.put("value", 123);
        return Result.success(data);
    }
    
    /**
     * 另一个示例接口
     */
    @Operation(summary = "获取简单示例", description = "演示简单对象返回结构")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功", 
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ApiDocResponseModel.class)))
    })
    @GetMapping("/simple")
    public Result<String> getSimpleExample() {
        return Result.success("这是一个简单的字符串示例");
    }
} 