package com.example.hello.exception;

/**
 * 业务异常
 * 用于表示业务逻辑错误
 */
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
} 