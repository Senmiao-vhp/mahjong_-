package com.example.mahjong.websocket;

import com.example.mahjong.service.GameService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
public class GameWebSocketHandler {

    @Autowired
    private GameService gameService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ObjectMapper objectMapper;

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

        Map<String, Object> result = gameService.riichi(gameId, userId, tile);
        return result;
    }

    /**
     * 广播游戏状态更新
     */
    public void broadcastGameState(Long gameId, Map<String, Object> gameState) {
        messagingTemplate.convertAndSend("/topic/game/" + gameId, gameState);
    }

    /**
     * 广播玩家手牌更新
     */
    public void broadcastPlayerHand(Long gameId, Long userId, Map<String, Object> handInfo) {
        messagingTemplate.convertAndSend("/queue/game/" + gameId + "/player/" + userId, handInfo);
    }

    /**
     * 广播游戏结算信息
     */
    public void broadcastGameSettlement(Long gameId, Map<String, Object> settlement) {
        messagingTemplate.convertAndSend("/topic/game/" + gameId + "/settlement", settlement);
    }

    /**
     * 广播游戏日志
     */
    public void broadcastGameLog(Long gameId, Map<String, Object> log) {
        messagingTemplate.convertAndSend("/topic/game/" + gameId + "/logs", log);
    }
} 