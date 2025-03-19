package com.example.mahjong.entity;

import java.io.Serializable;

/**
 * 风位枚举类
 */
public enum Wind implements Serializable {
    /**
     * 东风
     */
    EAST("东"),
    
    /**
     * 南风
     */
    SOUTH("南"),
    
    /**
     * 西风
     */
    WEST("西"),
    
    /**
     * 北风
     */
    NORTH("北");
    
    private final String displayName;
    
    /**
     * 构造函数
     * 
     * @param displayName 显示名称
     */
    Wind(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * 获取显示名称
     * 
     * @return 显示名称
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 获取下一个风位
     * 
     * @return 下一个风位
     */
    public Wind next() {
        return values()[(this.ordinal() + 1) % values().length];
    }
    
    @Override
    public String toString() {
        return displayName;
    }
} 