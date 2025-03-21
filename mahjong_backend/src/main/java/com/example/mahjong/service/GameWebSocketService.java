package com.example.mahjong.service;

import java.util.Map;

/**
 * 游戏WebSocket服务接口
 * 负责处理所有WebSocket消息的发送
 */
public interface GameWebSocketService {
    
    /**
     * 广播游戏状态更新
     */
    void broadcastGameState(Long gameId, Map<String, Object> gameState);
    
    /**
     * 广播玩家手牌更新
     */
    void broadcastPlayerHand(Long gameId, Long userId, Map<String, Object> handInfo);
    
    /**
     * 广播游戏结算信息
     */
    void broadcastGameSettlement(Long gameId, Map<String, Object> settlement);
    
    /**
     * 广播游戏日志
     */
    void broadcastGameLog(Long gameId, Map<String, Object> log);
    
    /**
     * 广播操作超时消息
     */
    void broadcastTimeoutMessage(Long gameId, String message);
    
    /**
     * 广播剩余操作时间
     */
    void broadcastRemainingTime(Long gameId, long remainingTime);
} 