package com.example.mahjong.service.impl;

import com.example.mahjong.service.GameWebSocketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * 游戏WebSocket服务实现类
 */
@Service
public class GameWebSocketServiceImpl implements GameWebSocketService {
    
    private static final Logger logger = Logger.getLogger(GameWebSocketServiceImpl.class.getName());
    
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;
    
    @Autowired
    public GameWebSocketServiceImpl(SimpMessagingTemplate messagingTemplate, ObjectMapper objectMapper) {
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = objectMapper;
        logger.info("GameWebSocketServiceImpl初始化完成");
    }
    
    @Override
    public void broadcastGameState(Long gameId, Map<String, Object> gameState) {
        logger.fine("广播游戏状态更新: gameId=" + gameId);
        messagingTemplate.convertAndSend("/topic/game/" + gameId, gameState);
    }
    
    @Override
    public void broadcastPlayerHand(Long gameId, Long userId, Map<String, Object> handInfo) {
        logger.fine("广播玩家手牌更新: gameId=" + gameId + ", userId=" + userId);
        messagingTemplate.convertAndSend("/queue/game/" + gameId + "/player/" + userId, handInfo);
    }
    
    @Override
    public void broadcastGameSettlement(Long gameId, Map<String, Object> settlement) {
        logger.fine("广播游戏结算信息: gameId=" + gameId);
        messagingTemplate.convertAndSend("/topic/game/" + gameId + "/settlement", settlement);
    }
    
    @Override
    public void broadcastGameLog(Long gameId, Map<String, Object> log) {
        logger.fine("广播游戏日志: gameId=" + gameId);
        messagingTemplate.convertAndSend("/topic/game/" + gameId + "/logs", log);
    }
    
    @Override
    public void broadcastTimeoutMessage(Long gameId, String message) {
        logger.fine("广播操作超时消息: gameId=" + gameId + ", message=" + message);
        Map<String, Object> timeoutMessage = new HashMap<>();
        timeoutMessage.put("type", "TIMEOUT");
        timeoutMessage.put("message", message);
        timeoutMessage.put("timestamp", System.currentTimeMillis());
        
        messagingTemplate.convertAndSend("/topic/game/" + gameId + "/timeout", timeoutMessage);
    }
    
    @Override
    public void broadcastRemainingTime(Long gameId, long remainingTime) {
        logger.fine("广播剩余操作时间: gameId=" + gameId + ", remainingTime=" + remainingTime);
        Map<String, Object> timeMessage = new HashMap<>();
        timeMessage.put("type", "REMAINING_TIME");
        timeMessage.put("remaining_time", remainingTime);
        
        messagingTemplate.convertAndSend("/topic/game/" + gameId + "/time", timeMessage);
    }
} 