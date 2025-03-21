package com.example.mahjong.service;

import com.example.mahjong.entity.AIPlayer;
import com.example.mahjong.entity.Game;
import com.example.mahjong.entity.Tile;
import com.example.mahjong.entity.Meld;
import java.util.List;

/**
 * AI服务接口
 */
public interface AIService {
    
    /**
     * 初始化AI玩家
     */
    void initializeAIPlayer(AIPlayer aiPlayer, Game game);
    
    /**
     * AI决策打出一张牌
     */
    Tile decideDiscard(AIPlayer aiPlayer, Game game);
    
    /**
     * AI决策是否立直
     */
    boolean decideRiichi(AIPlayer aiPlayer, Game game);
    
    /**
     * AI决策是否吃碰杠
     */
    Meld decideMeld(AIPlayer aiPlayer, Game game, Tile discardedTile);
    
    /**
     * AI决策是否和牌
     */
    boolean decideWin(AIPlayer aiPlayer, Game game, Tile winningTile);
    
    /**
     * AI决策是否荣和
     */
    boolean decideRon(AIPlayer aiPlayer, Game game, Tile discardedTile);
    
    /**
     * AI决策是否自摸
     */
    boolean decideTsumo(AIPlayer aiPlayer, Game game);
    
    /**
     * 计算手牌评分
     */
    double evaluateHand(AIPlayer aiPlayer, Game game);
    
    /**
     * 计算等待牌
     */
    List<Tile> calculateWaitingTiles(AIPlayer aiPlayer, Game game);
    
    /**
     * 计算和牌概率
     */
    double calculateWinProbability(AIPlayer aiPlayer, Game game);
    
    /**
     * 根据难度等级调整决策
     */
    void adjustDecisionByDifficulty(AIPlayer aiPlayer);
} 