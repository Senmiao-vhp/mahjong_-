package com.example.mahjong.websocket;

import com.example.mahjong.service.GameService;
import com.example.mahjong.service.GameWebSocketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Controller
public class GameWebSocketHandler {
    
    private static final Logger logger = Logger.getLogger(GameWebSocketHandler.class.getName());
    
    private final GameService gameService;
    private final GameWebSocketService webSocketService;
    private final ObjectMapper objectMapper;
    
    @Autowired
    public GameWebSocketHandler(GameService gameService, 
                              GameWebSocketService webSocketService,
                              ObjectMapper objectMapper) {
        this.gameService = gameService;
        this.webSocketService = webSocketService;
        this.objectMapper = objectMapper;
        logger.info("GameWebSocketHandler初始化完成");
    }

    /**
     * 处理打牌消息
     */
    @MessageMapping("/game.discard")
    @SendTo("/topic/game/{gameId}")
    public Map<String, Object> handleDiscard(Map<String, Object> message) {
        Long gameId = Long.valueOf(message.get("game_id").toString());
        Long userId = Long.valueOf(message.get("user_id").toString());
        String tile = (String) message.get("tile");
        boolean isRiichi = Boolean.valueOf(message.get("is_riichi").toString());

        logger.info("接收到打牌消息: gameId=" + gameId + ", userId=" + userId + ", tile=" + tile + ", isRiichi=" + isRiichi);
        Map<String, Object> result = gameService.discardTile(gameId, userId, tile, isRiichi);
        return result;
    }

    /**
     * 处理动作消息（吃、碰、杠等）
     */
    @MessageMapping("/game.action")
    @SendTo("/topic/game/{gameId}")
    public Map<String, Object> handleAction(Map<String, Object> message) {
        Long gameId = Long.valueOf(message.get("game_id").toString());
        Long userId = Long.valueOf(message.get("user_id").toString());
        String actionType = (String) message.get("action_type");
        @SuppressWarnings("unchecked")
        List<String> tiles = (List<String>) message.get("tiles");

        logger.info("接收到动作消息: gameId=" + gameId + ", userId=" + userId + ", actionType=" + actionType);
        Map<String, Object> result = gameService.performAction(gameId, userId, actionType, tiles);
        return result;
    }

    /**
     * 处理立直消息
     */
    @MessageMapping("/game.riichi")
    @SendTo("/topic/game/{gameId}")
    public Map<String, Object> handleRiichi(Map<String, Object> message) {
        Long gameId = Long.valueOf(message.get("game_id").toString());
        Long userId = Long.valueOf(message.get("user_id").toString());
        String tile = (String) message.get("tile");

        logger.info("接收到立直消息: gameId=" + gameId + ", userId=" + userId + ", tile=" + tile);
        Map<String, Object> result = gameService.riichi(gameId, userId, tile);
        return result;
    }

    // 以下方法委托给GameWebSocketService处理
    
    /**
     * 广播游戏状态更新
     */
    public void broadcastGameState(Long gameId, Map<String, Object> gameState) {
        webSocketService.broadcastGameState(gameId, gameState);
    }

    /**
     * 广播玩家手牌更新
     */
    public void broadcastPlayerHand(Long gameId, Long userId, Map<String, Object> handInfo) {
        webSocketService.broadcastPlayerHand(gameId, userId, handInfo);
    }

    /**
     * 广播游戏结算信息
     */
    public void broadcastGameSettlement(Long gameId, Map<String, Object> settlement) {
        webSocketService.broadcastGameSettlement(gameId, settlement);
    }

    /**
     * 广播游戏日志
     */
    public void broadcastGameLog(Long gameId, Map<String, Object> log) {
        webSocketService.broadcastGameLog(gameId, log);
    }

    /**
     * 广播操作超时消息
     */
    public void broadcastTimeoutMessage(Long gameId, String message) {
        webSocketService.broadcastTimeoutMessage(gameId, message);
    }
    
    /**
     * 广播剩余操作时间
     */
    public void broadcastRemainingTime(Long gameId, long remainingTime) {
        webSocketService.broadcastRemainingTime(gameId, remainingTime);
    }
} 