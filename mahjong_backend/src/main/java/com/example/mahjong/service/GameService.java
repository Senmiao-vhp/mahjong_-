package com.example.mahjong.service;

import java.util.List;
import java.util.Map;

import com.example.mahjong.entity.Game;
import com.example.mahjong.entity.PlayerGame;

public interface GameService {

    /**
     * 创建对局
     */
    Game createGame(Long roomId);

    /**
     * 获取对局信息
     */
    Game getGameById(Long id);

    /**
     * 获取对局中的玩家
     */
    List<PlayerGame> getPlayersByGameId(Long gameId);

    /**
     * 获取玩家在对局中的信息
     */
    PlayerGame getPlayerGameByGameIdAndUserId(Long gameId, Long userId);

    /**
     * 获取对局详情
     */
    Map<String, Object> getGameDetails(Long gameId);

    /**
     * 获取玩家手牌
     */
    Map<String, Object> getPlayerHand(Long gameId, Long userId);

    /**
     * 获取牌河信息
     */
    List<Map<String, Object>> getDiscardPiles(Long gameId);

    /**
     * 打出一张牌
     */
    Map<String, Object> discardTile(Long gameId, Long userId, String tile, boolean isRiichi);

    /**
     * 执行操作（吃/碰/杠/和/跳过）
     */
    Map<String, Object> performAction(Long gameId, Long userId, String actionType, List<String> tiles);

    /**
     * 立直
     */
    Map<String, Object> riichi(Long gameId, Long userId, String tile);

    /**
     * 获取对局结算信息
     */
    Map<String, Object> getGameSettlement(Long gameId);

    /**
     * 获取对局操作日志
     */
    Map<String, Object> getGameLogs(Long gameId, int page, int pageSize);
} 