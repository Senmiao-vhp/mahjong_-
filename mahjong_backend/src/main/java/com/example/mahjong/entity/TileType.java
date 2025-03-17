package com.example.mahjong.entity;

/**
 * 牌类型枚举
 */
public enum TileType {
    /**
     * 万子
     */
    MANZU,
    
    /**
     * 筒子
     */
    PINZU,
    
    /**
     * 索子
     */
    SOUZU,
    
    /**
     * 字牌
     */
    HONOR;
    
    /**
     * 判断是否为数牌
     */
    public boolean isNumberTile() {
        return this == MANZU || this == PINZU || this == SOUZU;
    }
    
    /**
     * 判断是否为字牌
     */
    public boolean isHonorTile() {
        return this == HONOR;
    }
    
    /**
     * 获取牌类型名称
     */
    public String getName() {
        switch (this) {
            case MANZU:
                return "万子";
            case PINZU:
                return "筒子";
            case SOUZU:
                return "索子";
            case HONOR:
                return "字牌";
            default:
                return "";
        }
    }
} 