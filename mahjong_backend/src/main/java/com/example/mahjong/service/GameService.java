package com.example.mahjong.service;

import com.example.mahjong.entity.*;

import java.util.*;

public interface GameService {
    /**
     * 创建新游戏
     * @param roomId 房间ID
     * @return 创建的游戏对象
     */
    Game createGame(Long roomId);
    
    /**
     * 根据ID获取游戏
     * @param id 游戏ID
     * @return 游戏对象
     */
    Game getGameById(Long id);
    
    /**
     * 获取指定房间的所有游戏
     * @param roomId 房间ID
     * @return 游戏列表
     */
    List<Game> getGamesByRoomId(Long roomId);
    
    /**
     * 获取游戏中的所有玩家
     * @param gameId 游戏ID
     * @return 玩家游戏关系列表
     */
    List<PlayerGame> getPlayersByGameId(Long gameId);
    
    /**
     * 获取特定玩家在特定游戏中的信息
     * @param gameId 游戏ID
     * @param userId 用户ID
     * @return 玩家游戏关系对象
     */
    PlayerGame getPlayerGameByGameIdAndUserId(Long gameId, Long userId);
    
    /**
     * 更新玩家游戏信息
     * @param playerGame 玩家游戏关系对象
     * @return 更新是否成功
     */
    boolean updatePlayerGame(PlayerGame playerGame);
    
    /**
     * 获取游戏详情
     * @param gameId 游戏ID
     * @return 游戏详情信息
     */
    Map<String, Object> getGameDetails(Long gameId);
    
    /**
     * 获取玩家手牌
     * @param gameId 游戏ID
     * @param userId 用户ID
     * @return 玩家手牌信息
     */
    Map<String, Object> getPlayerHand(Long gameId, Long userId);
    
    /**
     * 获取所有玩家的弃牌堆
     * @param gameId 游戏ID
     * @return 弃牌堆信息列表
     */
    List<Map<String, Object>> getDiscardPiles(Long gameId);
    
    /**
     * 玩家弃牌
     * @param gameId 游戏ID
     * @param userId 用户ID
     * @param tile 弃牌
     * @param isRiichi 是否立直
     * @return 弃牌结果信息
     */
    Map<String, Object> discardTile(Long gameId, Long userId, String tile, boolean isRiichi);
    
    /**
     * 执行游戏动作（如吃、碰、杠等）
     * @param gameId 游戏ID
     * @param userId 用户ID
     * @param actionType 动作类型
     * @param tiles 相关牌
     * @return 动作执行结果
     */
    Map<String, Object> performAction(Long gameId, Long userId, String actionType, List<String> tiles);
    
    /**
     * 玩家立直
     * @param gameId 游戏ID
     * @param userId 用户ID
     * @param tile 立直时打出的牌
     * @return 立直结果信息
     */
    Map<String, Object> riichi(Long gameId, Long userId, String tile);
    
    /**
     * 获取游戏结算信息
     * @param gameId 游戏ID
     * @return 结算信息
     */
    Map<String, Object> getGameSettlement(Long gameId);
    
    /**
     * 获取游戏日志
     * @param gameId 游戏ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 游戏日志列表
     */
    List<Map<String, Object>> getGameLogs(Long gameId, int page, int pageSize);
} 