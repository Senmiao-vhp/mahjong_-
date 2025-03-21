package com.example.mahjong.entity;

import java.io.Serializable;

/**
 * 游戏状态枚举类
 */
public enum GameState implements Serializable {
    /**
     * 等待中
     */
    WAITING("等待中"),
    
    /**
     * 准备中
     */
    READY("准备中"),
    
    /**
     * 游戏中
     */
    PLAYING("游戏中"),
    
    /**
     * 回合中
     */
    TURN_IN_PROGRESS("回合中"),
    
    /**
     * 等待玩家操作
     */
    WAITING_FOR_ACTION("等待操作"),
    
    /**
     * 等待玩家操作超时
     */
    WAITING_FOR_ACTION_TIMEOUT("操作超时"),
    
    /**
     * 游戏结束
     */
    GAME_OVER("游戏结束"),
    
    /**
     * 已结束
     */
    FINISHED("已结束");
    
    private final String displayName;
    
    /**
     * 构造函数
     * 
     * @param displayName 显示名称
     */
    GameState(String displayName) {
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