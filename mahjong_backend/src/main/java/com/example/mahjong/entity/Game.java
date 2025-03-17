package com.example.mahjong.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 游戏实体类
 */
public class Game implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * ID
     */
    private Long id;
    
    /**
     * 房间ID
     */
    private Long roomId;
    
    /**
     * 游戏状态
     */
    private GameState status;
    
    /**
     * 场风
     */
    private Wind roundWind;
    
    /**
     * 本场数
     */
    private Integer honbaCount;
    
    /**
     * 立直棒数
     */
    private Integer riichiSticks;
    
    /**
     * 庄家位置
     */
    private Integer dealerPosition;
    
    /**
     * 当前玩家位置
     */
    private Integer currentPosition;
    
    /**
     * 牌山位置
     */
    private Integer wallPosition;
    
    /**
     * 牌山
     */
    private List<Tile> wallTiles;
    
    /**
     * 宝牌指示牌
     */
    private List<Tile> doraIndicators;
    
    /**
     * 里宝牌指示牌
     */
    private List<Tile> uraDoraIndicators;
    
    /**
     * 杠宝牌指示牌
     */
    private List<Tile> kanDoraIndicators;
    
    /**
     * 玩家列表
     */
    private List<PlayerGame> players;
    
    /**
     * 开始时间
     */
    private Date startTime;
    
    /**
     * 结束时间
     */
    private Date endTime;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
    /**
     * 默认构造函数
     */
    public Game() {
        this.status = GameState.WAITING;
        this.honbaCount = 0;
        this.riichiSticks = 0;
        this.wallTiles = new ArrayList<>();
        this.doraIndicators = new ArrayList<>();
        this.uraDoraIndicators = new ArrayList<>();
        this.kanDoraIndicators = new ArrayList<>();
        this.players = new ArrayList<>();
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
     * 获取房间ID
     */
    public Long getRoomId() {
        return roomId;
    }
    
    /**
     * 设置房间ID
     */
    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }
    
    /**
     * 获取游戏状态
     */
    public GameState getStatus() {
        return status;
    }
    
    /**
     * 设置游戏状态
     */
    public void setStatus(GameState status) {
        this.status = status;
    }
    
    /**
     * 获取场风
     */
    public Wind getRoundWind() {
        return roundWind;
    }
    
    /**
     * 设置场风
     */
    public void setRoundWind(Wind roundWind) {
        this.roundWind = roundWind;
    }
    
    /**
     * 获取本场数
     */
    public Integer getHonbaCount() {
        return honbaCount;
    }
    
    /**
     * 设置本场数
     */
    public void setHonbaCount(Integer honbaCount) {
        this.honbaCount = honbaCount;
    }
    
    /**
     * 获取立直棒数
     */
    public Integer getRiichiSticks() {
        return riichiSticks;
    }
    
    /**
     * 设置立直棒数
     */
    public void setRiichiSticks(Integer riichiSticks) {
        this.riichiSticks = riichiSticks;
    }
    
    /**
     * 获取庄家位置
     */
    public Integer getDealerPosition() {
        return dealerPosition;
    }
    
    /**
     * 设置庄家位置
     */
    public void setDealerPosition(Integer dealerPosition) {
        this.dealerPosition = dealerPosition;
    }
    
    /**
     * 获取当前玩家位置
     */
    public Integer getCurrentPosition() {
        return currentPosition;
    }
    
    /**
     * 设置当前玩家位置
     */
    public void setCurrentPosition(Integer currentPosition) {
        this.currentPosition = currentPosition;
    }
    
    /**
     * 获取牌山位置
     */
    public Integer getWallPosition() {
        return wallPosition;
    }
    
    /**
     * 设置牌山位置
     */
    public void setWallPosition(Integer wallPosition) {
        this.wallPosition = wallPosition;
    }
    
    /**
     * 获取牌山
     */
    public List<Tile> getWallTiles() {
        return wallTiles;
    }
    
    /**
     * 设置牌山
     */
    public void setWallTiles(List<Tile> wallTiles) {
        this.wallTiles = wallTiles;
    }
    
    /**
     * 获取宝牌指示牌
     */
    public List<Tile> getDoraIndicators() {
        return doraIndicators;
    }
    
    /**
     * 设置宝牌指示牌
     */
    public void setDoraIndicators(List<Tile> doraIndicators) {
        this.doraIndicators = doraIndicators;
    }
    
    /**
     * 获取里宝牌指示牌
     */
    public List<Tile> getUraDoraIndicators() {
        return uraDoraIndicators;
    }
    
    /**
     * 设置里宝牌指示牌
     */
    public void setUraDoraIndicators(List<Tile> uraDoraIndicators) {
        this.uraDoraIndicators = uraDoraIndicators;
    }
    
    /**
     * 获取杠宝牌指示牌
     */
    public List<Tile> getKanDoraIndicators() {
        return kanDoraIndicators;
    }
    
    /**
     * 设置杠宝牌指示牌
     */
    public void setKanDoraIndicators(List<Tile> kanDoraIndicators) {
        this.kanDoraIndicators = kanDoraIndicators;
    }
    
    /**
     * 获取玩家列表
     */
    public List<PlayerGame> getPlayers() {
        return players;
    }
    
    /**
     * 设置玩家列表
     */
    public void setPlayers(List<PlayerGame> players) {
        this.players = players;
    }
    
    /**
     * 获取开始时间
     */
    public Date getStartTime() {
        return startTime;
    }
    
    /**
     * 设置开始时间
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    
    /**
     * 获取结束时间
     */
    public Date getEndTime() {
        return endTime;
    }
    
    /**
     * 设置结束时间
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
    
    /**
     * 获取创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }
    
    /**
     * 设置创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    
    /**
     * 获取更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }
    
    /**
     * 设置更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    
    /**
     * 初始化牌山
     */
    public void initializeWall() {
        wallTiles.clear();
        wallTiles.addAll(createAllTiles());
        shuffle();
        setupDoraIndicators();
    }
    
    /**
     * 创建所有牌
     */
    private List<Tile> createAllTiles() {
        List<Tile> tiles = new ArrayList<>();
        
        // 数牌
        for (TileType type : TileType.values()) {
            if (type.isNumberTile()) {
                for (int number = 1; number <= 9; number++) {
                    for (int i = 0; i < 4; i++) {
                        tiles.add(new Tile(type, number));
                    }
                }
            }
        }
        
        // 字牌
        for (TileType type : TileType.values()) {
            if (type.isHonorTile()) {
                for (int i = 0; i < 4; i++) {
                    tiles.add(new Tile(type, 0));
                }
            }
        }
        
        return tiles;
    }
    
    /**
     * 洗牌
     */
    private void shuffle() {
        Collections.shuffle(wallTiles, new Random());
    }
    
    /**
     * 设置宝牌指示牌
     */
    private void setupDoraIndicators() {
        doraIndicators.clear();
        uraDoraIndicators.clear();
        kanDoraIndicators.clear();
        
        // 设置第一张宝牌指示牌
        addNewDoraIndicator();
    }
    
    /**
     * 添加新的宝牌指示牌
     */
    public void addNewDoraIndicator() {
        if (wallTiles.size() >= 14) { // 确保牌山还有足够的牌
            doraIndicators.add(wallTiles.remove(wallTiles.size() - 1));
            uraDoraIndicators.add(wallTiles.remove(wallTiles.size() - 1));
        }
    }
    
    /**
     * 添加新的杠宝牌指示牌
     */
    public void addNewKanDoraIndicator() {
        if (wallTiles.size() >= 2) { // 确保牌山还有足够的牌
            kanDoraIndicators.add(wallTiles.remove(wallTiles.size() - 1));
        }
    }
    
    /**
     * 摸牌
     */
    public Tile draw() {
        if (wallTiles.isEmpty()) {
            return null;
        }
        return wallTiles.remove(wallTiles.size() - 1);
    }
    
    /**
     * 从王牌摸牌
     */
    public Tile drawFromDeadWall() {
        if (wallTiles.size() < 14) { // 确保还有王牌
            return wallTiles.remove(wallTiles.size() - 1);
        }
        return null;
    }
    
    /**
     * 检查牌山是否为空
     */
    public boolean isWallEmpty() {
        return wallTiles.isEmpty();
    }
    
    /**
     * 获取当前玩家
     */
    public PlayerGame getCurrentPlayer() {
        if (currentPosition >= 0 && currentPosition < players.size()) {
            return players.get(currentPosition);
        }
        return null;
    }
    
    /**
     * 获取指定位置的玩家
     */
    public PlayerGame getPlayerAt(int position) {
        if (position >= 0 && position < players.size()) {
            return players.get(position);
        }
        return null;
    }
    
    /**
     * 获取庄家
     */
    public PlayerGame getDealer() {
        if (dealerPosition >= 0 && dealerPosition < players.size()) {
            return players.get(dealerPosition);
        }
        return null;
    }
    
    /**
     * 增加本场数
     */
    public void incrementHonba() {
        this.honbaCount++;
    }
    
    /**
     * 重置本场数
     */
    public void resetHonba() {
        this.honbaCount = 0;
    }
    
    /**
     * 增加立直棒数
     */
    public void incrementRiichiSticks() {
        this.riichiSticks++;
    }
    
    /**
     * 重置立直棒数
     */
    public void resetRiichiSticks() {
        this.riichiSticks = 0;
    }
    
    /**
     * 开始游戏
     */
    public void start() {
        this.status = GameState.PLAYING;
        this.startTime = new Date();
        initializeWall();
        dealInitialTiles();
    }
    
    /**
     * 发初始牌
     */
    private void dealInitialTiles() {
        // 每个玩家发13张牌
        for (int i = 0; i < 13; i++) {
            for (PlayerGame player : players) {
                Tile tile = draw();
                if (tile != null) {
                    player.draw(tile);
                }
            }
        }
    }
    
    /**
     * 结束游戏
     */
    public void end() {
        this.status = GameState.FINISHED;
        this.endTime = new Date();
    }
    
    /**
     * 重置游戏
     */
    public void reset() {
        this.status = GameState.WAITING;
        this.honbaCount = 0;
        this.riichiSticks = 0;
        this.wallTiles.clear();
        this.doraIndicators.clear();
        this.uraDoraIndicators.clear();
        this.kanDoraIndicators.clear();
        for (PlayerGame player : players) {
            player.reset();
        }
    }
} 