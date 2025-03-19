package com.example.mahjong.common;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "统一返回结果")
public class Result<T> {
    @Schema(description = "状态码", example = "200")
    private Integer code;
    
    @Schema(description = "消息", example = "成功")
    private String msg;
    
    @Schema(description = "数据")
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("成功");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(message);
        return result;
    }
}