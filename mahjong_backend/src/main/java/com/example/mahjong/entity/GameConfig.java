package com.example.mahjong.entity;

import lombok.Data;
import java.io.Serializable;

/**
 * 游戏配置实体类
 */
@Data
public class GameConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 房间类型（0: 一般场, 1: 上级场）
     */
    private Integer roomType;
    
    /**
     * 游戏类型（0: 东风战, 1: 半庄战）
     */
    private Integer gameType;
    
    /**
     * 是否使用赤宝牌
     */
    private Boolean hasRedDora;
    
    /**
     * 是否允许食断
     */
    private Boolean hasOpenTanyao;
    
    /**
     * 是否为AI游戏
     */
    private Boolean isAIGame;
    
    /**
     * AI难度等级（1-3）
     */
    private Integer aiDifficultyLevel;
    
    /**
     * AI玩家数量
     */
    private Integer aiPlayerCount;
    
    /**
     * 初始点数
     */
    private Integer initialScore;
    
    /**
     * 场供点数
     */
    private Integer riichiStickPoints;
    
    /**
     * 本场点数
     */
    private Integer honbaPoints;
    
    /**
     * 最大本场数
     */
    private Integer maxHonbaCount;
    
    /**
     * 最大立直棒数
     */
    private Integer maxRiichiSticks;
    
    /**
     * 默认构造函数，设置默认值
     */
    public GameConfig() {
        this.roomType = 0;
        this.gameType = 0;
        this.hasRedDora = true;
        this.hasOpenTanyao = true;
        this.isAIGame = false;
        this.aiDifficultyLevel = 2;
        this.aiPlayerCount = 0;
        this.initialScore = 25000;
        this.riichiStickPoints = 1000;
        this.honbaPoints = 300;
        this.maxHonbaCount = 8;
        this.maxRiichiSticks = 8;
    }
    
    /**
     * 验证配置是否有效
     */
    public boolean isValid() {
        // 检查房间类型
        if (roomType != 0 && roomType != 1) {
            return false;
        }
        
        // 检查游戏类型
        if (gameType != 0 && gameType != 1) {
            return false;
        }
        
        // 检查AI相关配置
        if (isAIGame) {
            if (aiDifficultyLevel < 1 || aiDifficultyLevel > 3) {
                return false;
            }
            if (aiPlayerCount < 1 || aiPlayerCount > 3) {
                return false;
            }
        }
        
        // 检查点数相关配置
        if (initialScore < 25000) {
            return false;
        }
        if (riichiStickPoints <= 0 || honbaPoints <= 0) {
            return false;
        }
        if (maxHonbaCount <= 0 || maxRiichiSticks <= 0) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 获取游戏类型名称
     */
    public String getGameTypeName() {
        return gameType == 0 ? "东风战" : "半庄战";
    }
    
    /**
     * 获取房间类型名称
     */
    public String getRoomTypeName() {
        return roomType == 0 ? "一般场" : "上级场";
    }
    
    /**
     * 获取AI难度名称
     */
    public String getAIDifficultyName() {
        switch (aiDifficultyLevel) {
            case 1:
                return "简单";
            case 2:
                return "中等";
            case 3:
                return "困难";
            default:
                return "未知";
        }
    }
} 