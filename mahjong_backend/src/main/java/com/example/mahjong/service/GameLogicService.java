package com.example.mahjong.service;

import com.example.mahjong.entity.*;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 游戏逻辑服务接口
 * 负责处理麻将游戏的核心逻辑，如和牌判断、牌型分析、得分计算等
 */
public interface GameLogicService {

    /**
     * 判断是否可以和牌
     * @param handTiles 手牌列表
     * @param melds 副露列表
     * @return 是否可以和牌
     */
    boolean canWin(List<Tile> handTiles, List<Meld> melds);
    
    /**
     * 检查是否听牌
     * @param handTiles 手牌列表
     * @param melds 副露列表
     * @return 是否听牌
     */
    boolean isTenpai(List<Tile> handTiles, List<Meld> melds);
    
    /**
     * 获取可能的待牌列表
     * @param playerGame 玩家游戏对象
     * @return 可能的待牌列表
     */
    List<Tile> getPossibleWaitingTiles(PlayerGame playerGame);
    
    /**
     * 获取可能的副露
     * @param handTiles 手牌列表
     * @param discardedTile 被打出的牌
     * @return 可能的副露列表
     */
    List<Meld> getPossibleMelds(List<Tile> handTiles, Tile discardedTile);
    
    /**
     * 计算役种
     * @param playerGame 玩家游戏对象
     * @param game 游戏对象
     * @return 役种列表
     */
    List<Yaku> calculateYaku(PlayerGame playerGame, Game game);
    
    /**
     * 计算得分
     * @param yakus 役种列表
     * @param basePoints 基本符数
     * @param isDealer 是否为庄家
     * @param isTsumo 是否为自摸
     * @return 得分
     */
    int calculateScore(List<Yaku> yakus, int basePoints, boolean isDealer, boolean isTsumo);
    
    /**
     * 检查是否可以吃牌
     * @param handTiles 手牌列表
     * @param discardedTile 被打出的牌
     * @return 是否可以吃牌
     */
    boolean canChi(List<Tile> handTiles, Tile discardedTile);
    
    /**
     * 检查是否可以碰牌
     * @param handTiles 手牌列表
     * @param discardedTile 被打出的牌
     * @return 是否可以碰牌
     */
    boolean canPon(List<Tile> handTiles, Tile discardedTile);
    
    /**
     * 检查是否可以杠牌
     * @param handTiles 手牌列表
     * @param discardedTile 被打出的牌
     * @return 是否可以杠牌
     */
    boolean canKan(List<Tile> handTiles, Tile discardedTile);
    
    /**
     * 检查是否可以暗杠
     * @param handTiles 手牌列表
     * @return 是否可以暗杠
     */
    boolean canAnkan(List<Tile> handTiles);
    
    /**
     * 检查是否可以加杠
     * @param handTiles 手牌列表
     * @param melds 副露列表
     * @return 是否可以加杠
     */
    boolean canKakan(List<Tile> handTiles, List<Meld> melds);
    
    /**
     * 检查是否可以立直
     * @param playerGame 玩家游戏对象
     * @return 是否可以立直
     */
    boolean canRiichi(PlayerGame playerGame);
    
    /**
     * 判断是否振听
     * @param playerGame 玩家游戏对象
     * @param discardTiles 弃牌列表
     * @return 是否振听
     */
    boolean isFuriten(PlayerGame playerGame, List<Tile> discardTiles);
} 