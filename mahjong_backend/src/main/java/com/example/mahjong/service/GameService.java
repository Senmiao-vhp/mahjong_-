package com.example.mahjong.service;

import com.example.mahjong.entity.*;

import java.util.*;

public interface GameService {
    Game createGame(Long roomId);
    Game getGameById(Long id);
    List<PlayerGame> getPlayersByGameId(Long gameId);
    PlayerGame getPlayerGameByGameIdAndUserId(Long gameId, Long userId);
    Map<String, Object> getGameDetails(Long gameId);
    Map<String, Object> getPlayerHand(Long gameId, Long userId);
    List<Map<String, Object>> getDiscardPiles(Long gameId);
    Map<String, Object> discardTile(Long gameId, Long userId, String tile, boolean isRiichi);
    Map<String, Object> performAction(Long gameId, Long userId, String actionType, List<String> tiles);
    Map<String, Object> riichi(Long gameId, Long userId, String tile);
    Map<String, Object> getGameSettlement(Long gameId);
    List<Map<String, Object>> getGameLogs(Long gameId, int page, int pageSize);
} 