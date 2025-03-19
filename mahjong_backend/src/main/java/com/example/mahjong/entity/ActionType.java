package com.example.mahjong.entity;

import java.io.Serializable;

/**
 * 动作类型枚举类
 */
public enum ActionType implements Serializable {
    /**
     * 摸牌
     */
    DRAW("摸牌"),
    
    /**
     * 打牌
     */
    DISCARD("打牌"),
    
    /**
     * 吃
     */
    CHII("吃"),
    
    /**
     * 碰
     */
    PON("碰"),
    
    /**
     * 杠
     */
    KAN("杠"),
    
    /**
     * 立直
     */
    RIICHI("立直"),
    
    /**
     * 和牌
     */
    RON("荣和"),
    
    /**
     * 自摸
     */
    TSUMO("自摸"),
    
    /**
     * 流局
     */
    RYUUKYOKU("流局"),
    
    /**
     * 跳过
     */
    SKIP("跳过");
    
    private final String displayName;
    
    /**
     * 构造函数
     * 
     * @param displayName 显示名称
     */
    ActionType(String displayName) {
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
    
    @Override
    public String toString() {
        return displayName;
    }
} 