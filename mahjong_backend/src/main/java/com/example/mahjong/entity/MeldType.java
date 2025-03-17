package com.example.mahjong.entity;

/**
 * 面子类型枚举
 */
public enum MeldType {
    /**
     * 顺子（吃）
     */
    CHII("吃"),
    
    /**
     * 刻子（碰）
     */
    PON("碰"),
    
    /**
     * 杠子（杠）
     */
    KAN("杠");
    
    /**
     * 显示名称
     */
    private final String displayName;
    
    /**
     * 构造函数
     * 
     * @param displayName 显示名称
     */
    MeldType(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * 获取显示名称
     * 
     * @return 显示名称
     */
    @Override
    public String toString() {
        return displayName;
    }
} 