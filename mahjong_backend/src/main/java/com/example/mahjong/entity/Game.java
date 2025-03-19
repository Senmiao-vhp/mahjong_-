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
     * 操作超时时间（毫秒）
     */
    private static final long ACTION_TIMEOUT = 15000;
    
    /**
     * 最后操作时间
     */
    private Date lastActionTime;
    
    /**
     * 是否正在等待玩家操作
     */
    private Boolean isWaitingForAction;
    
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
     * 是否AI局
     */
    private Boolean isAIGame;
    
    /**
     * AI难度等级（1-3）
     */
    private Integer aiDifficultyLevel;
    
    /**
     * AI玩家列表
     */
    private List<AIPlayer> aiPlayers;
    
    /**
     * 是否自摸
     */
    private Boolean isTsumo;
    
    /**
     * 赢家用户ID
     */
    private Long winner;
    
    /**
     * 是否正常结束
     */
    private Boolean normalEnd;
    
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
     * 获取是否AI局
     */
    public Boolean getIsAIGame() {
        return isAIGame;
    }
    
    /**
     * 设置是否AI局
     */
    public void setIsAIGame(Boolean aiGame) {
        isAIGame = aiGame;
    }
    
    /**
     * 获取AI难度等级
     */
    public Integer getAiDifficultyLevel() {
        return aiDifficultyLevel;
    }
    
    /**
     * 设置AI难度等级
     */
    public void setAiDifficultyLevel(Integer aiDifficultyLevel) {
        this.aiDifficultyLevel = aiDifficultyLevel;
    }
    
    /**
     * 获取AI玩家列表
     */
    public List<AIPlayer> getAiPlayers() {
        return aiPlayers;
    }
    
    /**
     * 设置AI玩家列表
     */
    public void setAiPlayers(List<AIPlayer> aiPlayers) {
        this.aiPlayers = aiPlayers;
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
    
    /**
     * 获取所有弃牌
     */
    public List<Tile> getDiscardedTiles() {
        List<Tile> discardedTiles = new ArrayList<>();
        for (PlayerGame player : players) {
            discardedTiles.addAll(player.getDiscardTiles());
        }
        return discardedTiles;
    }
    
    /**
     * 获取当前回合的弃牌
     */
    public List<Tile> getCurrentTurnDiscards() {
        List<Tile> currentTurnDiscards = new ArrayList<>();
        for (PlayerGame player : players) {
            if (player.getPosition() != currentPosition) {
                List<Tile> discards = player.getDiscardTiles();
                if (!discards.isEmpty()) {
                    currentTurnDiscards.add(discards.get(discards.size() - 1));
                }
            }
        }
        return currentTurnDiscards;
    }

    public Tile getLastDiscardedTile() {
        if (players == null || players.isEmpty()) {
            return null;
        }

        // 获取当前玩家的上一家
        int previousPosition = (currentPosition + 3) % 4;
        PlayerGame previousPlayer = players.stream()
                .filter(p -> p.getPosition() == previousPosition)
                .findFirst()
                .orElse(null);

        if (previousPlayer == null || previousPlayer.getDiscardTiles() == null || 
            previousPlayer.getDiscardTiles().isEmpty()) {
            return null;
        }

        // 返回上一家最后打出的牌
        return previousPlayer.getDiscardTiles().get(previousPlayer.getDiscardTiles().size() - 1);
    }

    public Tile drawTile() {
        if (wallTiles.isEmpty()) {
            throw new IllegalStateException("No tiles left in wall");
        }
        return wallTiles.remove(wallTiles.size() - 1);
    }

    /**
     * 获取最后操作时间
     */
    public Date getLastActionTime() {
        return lastActionTime;
    }
    
    /**
     * 设置最后操作时间
     */
    public void setLastActionTime(Date lastActionTime) {
        this.lastActionTime = lastActionTime;
    }
    
    /**
     * 获取是否正在等待玩家操作
     */
    public Boolean getIsWaitingForAction() {
        return isWaitingForAction;
    }
    
    /**
     * 设置是否正在等待玩家操作
     */
    public void setIsWaitingForAction(Boolean waitingForAction) {
        isWaitingForAction = waitingForAction;
    }
    
    /**
     * 检查是否操作超时
     */
    public boolean isActionTimeout() {
        if (!isWaitingForAction || lastActionTime == null) {
            return false;
        }
        return System.currentTimeMillis() - lastActionTime.getTime() > ACTION_TIMEOUT;
    }
    
    /**
     * 更新最后操作时间
     */
    public void updateLastActionTime() {
        this.lastActionTime = new Date();
        this.isWaitingForAction = true;
    }
    
    /**
     * 清除等待操作状态
     */
    public void clearWaitingForAction() {
        this.isWaitingForAction = false;
        this.lastActionTime = null;
    }
    
    /**
     * 获取是否正常结束
     */
    public Boolean isNormalEnd() {
        return normalEnd != null && normalEnd;
    }
    
    /**
     * 设置是否正常结束
     */
    public void setNormalEnd(Boolean normalEnd) {
        this.normalEnd = normalEnd;
    }
    
    /**
     * 获取赢家用户ID
     */
    public Long getWinner() {
        return winner;
    }
    
    /**
     * 设置赢家用户ID
     */
    public void setWinner(Long winner) {
        this.winner = winner;
    }
    
    /**
     * 获取是否自摸
     */
    public Boolean getIsTsumo() {
        return isTsumo != null && isTsumo;
    }
    
    /**
     * 设置是否自摸
     */
    public void setIsTsumo(Boolean isTsumo) {
        this.isTsumo = isTsumo;
    }
} 