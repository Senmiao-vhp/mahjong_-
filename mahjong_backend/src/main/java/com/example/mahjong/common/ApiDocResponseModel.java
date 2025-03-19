package com.example.mahjong.common;

import java.util.Map;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 专门用于API文档的响应模型类
 * 用于解决Apifox导入Swagger时的类型问题
 */
@Schema(description = "通用响应结构")
public class ApiDocResponseModel {
    
    @Schema(description = "响应码", example = "200")
    private int code;
    
    @Schema(description = "响应消息", example = "操作成功")
    private String msg;
    
    @Schema(description = "响应数据")
    private Map<String, Object> data;
    
    public int getCode() {
        return code;
    }
    
    public void setCode(int code) {
        this.code = code;
    }
    
    public String getMsg() {
        return msg;
    }
    
    public void setMsg(String msg) {
        this.msg = msg;
    }
    
    public Map<String, Object> getData() {
        return data;
    }
    
    public void setData(Map<String, Object> data) {
        this.data = data;
    }
} 