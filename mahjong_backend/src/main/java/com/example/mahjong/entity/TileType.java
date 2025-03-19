package com.example.mahjong.entity;

/**
 * 牌类型枚举
 */
public enum TileType {
    /**
     * 万子
     */
    MANZU(9),
    
    /**
     * 筒子
     */
    PINZU(9),
    
    /**
     * 索子
     */
    SOUZU(9),
    
    /**
     * 风牌
     */
    WIND(4),
    
    /**
     * 三元牌
     */
    DRAGON(3);
    
    /**
     * 该类型的最大数字
     */
    private final int maxNumber;
    
    /**
     * 构造函数
     */
    TileType(int maxNumber) {
        this.maxNumber = maxNumber;
    }
    
    /**
     * 获取该类型的最大数字
     */
    public int getMaxNumber() {
        return maxNumber;
    }
    
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
        return this == WIND || this == DRAGON;
    }
    
    /**
     * 获取牌类型名称
     */
    public String getName() {
        switch (this) {
            case MANZU:
                return "万";
            case PINZU:
                return "筒";
            case SOUZU:
                return "索";
            case WIND:
                return "风";
            case DRAGON:
                return "三元";
            default:
                return "";
        }
    }
} 