package com.example.mahjong.entity;

/**
 * 面子类型枚举
 */
public enum MeldType {
    /**
     * 顺子（吃）
     */
    CHI("吃"),
    
    /**
     * 刻子（碰）
     */
    PON("碰"),
    
    /**
     * 杠子（杠）
     */
    KAN("杠"),
    
    /**
     * 暗杠
     */
    ANKAN("暗杠");
    
    /**
     * 显示名称
     */
    private final String name;
    
    /**
     * 构造函数
     * 
     * @param name 显示名称
     */
    MeldType(String name) {
        this.name = name;
    }
    
    /**
     * 获取显示名称
     * 
     * @return 显示名称
     */
    public String getName() {
        return name;
    }
} 