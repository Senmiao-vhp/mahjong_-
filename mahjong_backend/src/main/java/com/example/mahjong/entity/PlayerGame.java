package com.example.mahjong.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 玩家对局关联实体类
 */
public class PlayerGame implements Serializable {
    
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
     * 用户ID
     */
    private Long userId;
    
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
     * 默认构造函数
     */
    public PlayerGame() {
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
    }
    
    /**
     * 获取ID
     */
    public Long getId() {
        return id;
    }
    
    /**
     * 设置ID
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * 获取游戏ID
     */
    public Long getGameId() {
        return gameId;
    }
    
    /**
     * 设置游戏ID
     */
    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }
    
    /**
     * 获取用户ID
     */
    public Long getUserId() {
        return userId;
    }
    
    /**
     * 设置用户ID
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    /**
     * 获取座位位置
     */
    public Integer getPosition() {
        return position;
    }
    
    /**
     * 设置座位位置
     */
    public void setPosition(Integer position) {
        this.position = position;
    }
    
    /**
     * 获取是否庄家
     */
    public Boolean getIsDealer() {
        return isDealer;
    }
    
    /**
     * 设置是否庄家
     */
    public void setIsDealer(Boolean dealer) {
        isDealer = dealer;
    }
    
    /**
     * 获取分数
     */
    public Integer getScore() {
        return score;
    }
    
    /**
     * 设置分数
     */
    public void setScore(Integer score) {
        this.score = score;
    }
    
    /**
     * 获取是否立直
     */
    public Boolean getIsRiichi() {
        return isRiichi;
    }
    
    /**
     * 设置是否立直
     */
    public void setIsRiichi(Boolean riichi) {
        isRiichi = riichi;
    }
    
    /**
     * 获取是否双立直
     */
    public Boolean getIsDoubleRiichi() {
        return isDoubleRiichi;
    }
    
    /**
     * 设置是否双立直
     */
    public void setIsDoubleRiichi(Boolean doubleRiichi) {
        isDoubleRiichi = doubleRiichi;
    }
    
    /**
     * 获取是否一发
     */
    public Boolean getIsIppatsu() {
        return isIppatsu;
    }
    
    /**
     * 设置是否一发
     */
    public void setIsIppatsu(Boolean ippatsu) {
        isIppatsu = ippatsu;
    }
    
    /**
     * 获取是否振听
     */
    public Boolean getIsFuriten() {
        return isFuriten;
    }
    
    /**
     * 设置是否振听
     */
    public void setIsFuriten(Boolean furiten) {
        isFuriten = furiten;
    }
    
    /**
     * 获取是否临时振听
     */
    public Boolean getIsTemporaryFuriten() {
        return isTemporaryFuriten;
    }
    
    /**
     * 设置是否临时振听
     */
    public void setIsTemporaryFuriten(Boolean temporaryFuriten) {
        isTemporaryFuriten = temporaryFuriten;
    }
    
    /**
     * 获取是否门前清
     */
    public Boolean getIsMenzen() {
        return isMenzen;
    }
    
    /**
     * 设置是否门前清
     */
    public void setIsMenzen(Boolean menzen) {
        isMenzen = menzen;
    }
    
    /**
     * 获取是否第一巡
     */
    public Boolean getIsFirstTurn() {
        return isFirstTurn;
    }
    
    /**
     * 设置是否第一巡
     */
    public void setIsFirstTurn(Boolean firstTurn) {
        isFirstTurn = firstTurn;
    }
    
    /**
     * 获取自风
     */
    public Wind getSeatWind() {
        return seatWind;
    }
    
    /**
     * 设置自风
     */
    public void setSeatWind(Wind seatWind) {
        this.seatWind = seatWind;
    }
    
    /**
     * 获取手牌
     */
    public List<Tile> getHandTiles() {
        return handTiles;
    }
    
    /**
     * 设置手牌
     */
    public void setHandTiles(List<Tile> handTiles) {
        this.handTiles = handTiles;
    }
    
    /**
     * 获取弃牌
     */
    public List<Tile> getDiscardTiles() {
        return discardTiles;
    }
    
    /**
     * 设置弃牌
     */
    public void setDiscardTiles(List<Tile> discardTiles) {
        this.discardTiles = discardTiles;
    }
    
    /**
     * 获取副露
     */
    public List<Meld> getMelds() {
        return melds;
    }
    
    /**
     * 设置副露
     */
    public void setMelds(List<Meld> melds) {
        this.melds = melds;
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
     * 重置玩家状态
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