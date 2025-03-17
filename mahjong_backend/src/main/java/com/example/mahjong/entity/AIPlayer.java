package com.example.mahjong.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * AI玩家实体类
 */
public class AIPlayer implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * ID
     */
    private Long id;
    
    /**
     * 游戏ID
     */
    private Long gameId;
    
    /**
     * 座位位置
     */
    private Integer position;
    
    /**
     * 是否庄家
     */
    private Boolean isDealer;
    
    /**
     * 分数
     */
    private Integer score;
    
    /**
     * 是否立直
     */
    private Boolean isRiichi;
    
    /**
     * 是否双立直
     */
    private Boolean isDoubleRiichi;
    
    /**
     * 是否一发
     */
    private Boolean isIppatsu;
    
    /**
     * 是否振听
     */
    private Boolean isFuriten;
    
    /**
     * 是否临时振听
     */
    private Boolean isTemporaryFuriten;
    
    /**
     * 是否门前清
     */
    private Boolean isMenzen;
    
    /**
     * 是否第一巡
     */
    private Boolean isFirstTurn;
    
    /**
     * 自风
     */
    private Wind seatWind;
    
    /**
     * 手牌
     */
    private List<Tile> handTiles;
    
    /**
     * 弃牌
     */
    private List<Tile> discardTiles;
    
    /**
     * 副露
     */
    private List<Meld> melds;
    
    /**
     * AI难度等级（1-3）
     */
    private Integer difficultyLevel;
    
    /**
     * 默认构造函数
     */
    public AIPlayer() {
        this.score = 25000; // 初始分数
        this.handTiles = new ArrayList<>();
        this.discardTiles = new ArrayList<>();
        this.melds = new ArrayList<>();
        this.isRiichi = false;
        this.isDoubleRiichi = false;
        this.isIppatsu = false;
        this.isFuriten = false;
        this.isTemporaryFuriten = false;
        this.isMenzen = true;
        this.isFirstTurn = true;
        this.difficultyLevel = 2; // 默认中等难度
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getGameId() {
        return gameId;
    }
    
    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }
    
    public Integer getPosition() {
        return position;
    }
    
    public void setPosition(Integer position) {
        this.position = position;
    }
    
    public Boolean getIsDealer() {
        return isDealer;
    }
    
    public void setIsDealer(Boolean dealer) {
        isDealer = dealer;
    }
    
    public Integer getScore() {
        return score;
    }
    
    public void setScore(Integer score) {
        this.score = score;
    }
    
    public Boolean getIsRiichi() {
        return isRiichi;
    }
    
    public void setIsRiichi(Boolean riichi) {
        isRiichi = riichi;
    }
    
    public Boolean getIsDoubleRiichi() {
        return isDoubleRiichi;
    }
    
    public void setIsDoubleRiichi(Boolean doubleRiichi) {
        isDoubleRiichi = doubleRiichi;
    }
    
    public Boolean getIsIppatsu() {
        return isIppatsu;
    }
    
    public void setIsIppatsu(Boolean ippatsu) {
        isIppatsu = ippatsu;
    }
    
    public Boolean getIsFuriten() {
        return isFuriten;
    }
    
    public void setIsFuriten(Boolean furiten) {
        isFuriten = furiten;
    }
    
    public Boolean getIsTemporaryFuriten() {
        return isTemporaryFuriten;
    }
    
    public void setIsTemporaryFuriten(Boolean temporaryFuriten) {
        isTemporaryFuriten = temporaryFuriten;
    }
    
    public Boolean getIsMenzen() {
        return isMenzen;
    }
    
    public void setIsMenzen(Boolean menzen) {
        isMenzen = menzen;
    }
    
    public Boolean getIsFirstTurn() {
        return isFirstTurn;
    }
    
    public void setIsFirstTurn(Boolean firstTurn) {
        isFirstTurn = firstTurn;
    }
    
    public Wind getSeatWind() {
        return seatWind;
    }
    
    public void setSeatWind(Wind seatWind) {
        this.seatWind = seatWind;
    }
    
    public List<Tile> getHandTiles() {
        return handTiles;
    }
    
    public void setHandTiles(List<Tile> handTiles) {
        this.handTiles = handTiles;
    }
    
    public List<Tile> getDiscardTiles() {
        return discardTiles;
    }
    
    public void setDiscardTiles(List<Tile> discardTiles) {
        this.discardTiles = discardTiles;
    }
    
    public List<Meld> getMelds() {
        return melds;
    }
    
    public void setMelds(List<Meld> melds) {
        this.melds = melds;
    }
    
    public Integer getDifficultyLevel() {
        return difficultyLevel;
    }
    
    public void setDifficultyLevel(Integer difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }
    
    /**
     * 增加分数
     */
    public void addScore(int amount) {
        this.score += amount;
    }
    
    /**
     * 减少分数
     */
    public void subtractScore(int amount) {
        this.score -= amount;
    }
    
    /**
     * 打出一张牌
     */
    public boolean discard(Tile tile) {
        if (handTiles.remove(tile)) {
            discardTiles.add(tile);
            return true;
        }
        return false;
    }
    
    /**
     * 摸一张牌
     */
    public void draw(Tile tile) {
        if (tile != null) {
            handTiles.add(tile);
        }
    }
    
    /**
     * 清空打出的牌
     */
    public void clearDiscards() {
        discardTiles.clear();
    }
    
    /**
     * 重置AI玩家状态
     */
    public void reset() {
        handTiles.clear();
        discardTiles.clear();
        melds.clear();
        isRiichi = false;
        isDoubleRiichi = false;
        isIppatsu = false;
        isFuriten = false;
        isTemporaryFuriten = false;
        isMenzen = true;
        isFirstTurn = true;
    }
} 