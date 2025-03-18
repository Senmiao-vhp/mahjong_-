package com.example.mahjong.service.impl;

import com.example.mahjong.entity.*;
import com.example.mahjong.service.GameService;
import com.example.mahjong.service.GameLogicService;
import com.example.mahjong.service.AIService;
import com.example.mahjong.service.GameWebSocketService;
import com.example.mahjong.mapper.GameMapper;
import com.example.mahjong.mapper.PlayerGameMapper;
import com.example.mahjong.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class GameServiceImpl implements GameService {

    private static final Logger logger = Logger.getLogger(GameServiceImpl.class.getName());
    
    private final GameMapper gameMapper;
    private final PlayerGameMapper playerGameMapper;
    private final GameWebSocketService webSocketService;
    private final GameLogicService gameLogicService;
    private final AIService aiService;
    private final ObjectMapper objectMapper;
    private final UserService userService;

    @Autowired
    public GameServiceImpl(GameMapper gameMapper,
                         PlayerGameMapper playerGameMapper,
                         GameWebSocketService webSocketService,
                         GameLogicService gameLogicService,
                         AIService aiService,
                         ObjectMapper objectMapper,
                         UserService userService) {
        this.gameMapper = gameMapper;
        this.playerGameMapper = playerGameMapper;
        this.webSocketService = webSocketService;
        this.gameLogicService = gameLogicService;
        this.aiService = aiService;
        this.objectMapper = objectMapper;
        this.userService = userService;
        logger.info("GameServiceImpl初始化完成");
    }

    @Override
    @Transactional
    public Game createGame(Long roomId) {
        // 创建新游戏
        Game game = new Game();
        game.setRoomId(roomId);
        game.setRoundWind(Wind.EAST); // 东风
        game.setDealerPosition(0); // 东家为庄
        game.setCurrentPosition(0); // 从东家开始
        game.setHonbaCount(0);
        game.setRiichiSticks(0);
        
        // 初始化牌山和宝牌指示牌
        game.initializeWall();
        
        game.setStatus(GameState.READY); // 准备中
        game.setStartTime(new Date());
        game.setCreateTime(new Date());
        game.setUpdateTime(new Date());
        
        // 保存游戏
        gameMapper.insert(game);
        
        return game;
    }

    @Transactional
    public Game createGame(Long roomId, Long userId, GameConfig config) {
        // 创建新游戏
        Game game = createGame(roomId);
        
        // 设置AI相关配置
        if (config != null && config.getIsAIGame()) {
            game.setIsAIGame(true);
            game.setAiDifficultyLevel(config.getAiDifficultyLevel());
            
            // 初始化AI玩家
            List<AIPlayer> aiPlayers = new ArrayList<>();
            for (int i = 0; i < config.getAiPlayerCount(); i++) {
                AIPlayer aiPlayer = new AIPlayer();
                aiPlayer.setGameId(game.getId());
                aiPlayer.setPosition(i + 1); // 从1号位开始
                aiPlayer.setIsDealer(false);
                aiPlayer.setDifficultyLevel(config.getAiDifficultyLevel());
                aiPlayers.add(aiPlayer);
                
                // 初始化AI玩家
                aiService.initializeAIPlayer(aiPlayer, game);
            }
            game.setAiPlayers(aiPlayers);
        }
        
        // 更新游戏
        gameMapper.updateById(game);
        
        return game;
    }

    @Override
    public Game getGameById(Long id) {
        return gameMapper.selectById(id);
    }

    @Override
    public List<Game> getGamesByRoomId(Long roomId) {
        return gameMapper.selectByRoomId(roomId);
    }

    @Override
    public List<PlayerGame> getPlayersByGameId(Long gameId) {
        return playerGameMapper.selectByGameId(gameId);
    }

    @Override
    public PlayerGame getPlayerGameByGameIdAndUserId(Long gameId, Long userId) {
        return playerGameMapper.selectByGameIdAndUserId(gameId, userId);
    }

    @Override
    public boolean updatePlayerGame(PlayerGame playerGame) {
        try {
            playerGameMapper.updateById(playerGame);
            return true;
        } catch (Exception e) {
            logger.severe("更新PlayerGame失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Map<String, Object> getGameDetails(Long gameId) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取游戏基本信息
        Game game = gameMapper.selectById(gameId);
        if (game == null) {
            return null;
        }
        
        result.put("id", game.getId());
        result.put("room_id", game.getRoomId());
        result.put("round_wind", game.getRoundWind());
        result.put("dealer_position", game.getDealerPosition());
        result.put("current_position", game.getCurrentPosition());
        result.put("honba_count", game.getHonbaCount());
        result.put("riichi_stick_count", game.getRiichiSticks());
        
        // 获取宝牌指示牌
        List<String> doraIndicators = parseDoraIndicators(game.getDoraIndicators());
        result.put("dora_indicators", doraIndicators);
        
        result.put("status", game.getStatus());
        result.put("start_time", game.getStartTime());
        
        // 获取玩家信息
        List<Map<String, Object>> players = new ArrayList<>();
        List<PlayerGame> playerGames = playerGameMapper.selectByGameId(gameId);
        
        for (PlayerGame pg : playerGames) {
            Map<String, Object> player = new HashMap<>();
            player.put("user_id", pg.getUserId());
            // 从用户服务获取昵称
            User user = userService.getUserById(pg.getUserId());
            String nickname = (user != null) ? user.getNickname() : "用户" + pg.getUserId();
            player.put("nickname", nickname);
            player.put("position", pg.getPosition());
            player.put("score", pg.getScore());
            player.put("is_dealer", pg.getIsDealer());
            player.put("is_riichi", pg.getIsRiichi());
            players.add(player);
        }
        
        result.put("players", players);
        
        return result;
    }

    @Override
    public Map<String, Object> getPlayerHand(Long gameId, Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取玩家在游戏中的信息
        PlayerGame playerGame = playerGameMapper.selectByGameIdAndUserId(gameId, userId);
        if (playerGame == null) {
            return null;
        }
        
        try {
            // 从hand表获取玩家手牌
            String handTilesJson = playerGameMapper.getPlayerHandTiles(gameId, userId);
            if (handTilesJson != null && !handTilesJson.isEmpty()) {
                List<Tile> handTiles = objectMapper.readValue(handTilesJson, 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Tile.class));
                
                // 转换为前端需要的格式
                List<String> tileStrings = handTiles.stream()
                    .map(Tile::toString)
                    .collect(Collectors.toList());
                
                result.put("tiles", tileStrings);
        
        // 计算听牌
                // 计算听牌
                List<String> waitingTileStrings = new ArrayList<>();
                try {
                    // 使用gameLogicService计算听牌
                    List<Tile> waitingTiles = gameLogicService.getPossibleWaitingTiles(playerGame);
                    if (waitingTiles != null && !waitingTiles.isEmpty()) {
                        waitingTileStrings = waitingTiles.stream()
                            .map(tile -> tile.getType().name() + tile.getNumber())
                        .collect(Collectors.toList());
                        logger.info("玩家" + userId + "的听牌: " + String.join(", ", waitingTileStrings));
                    } else {
                        logger.info("玩家" + userId + "没有听牌");
                    }
                } catch (Exception e) {
                    logger.warning("计算听牌失败: " + e.getMessage());
                    e.printStackTrace();
                }
                
                result.put("tenpai_tiles", waitingTileStrings);
            } else {
                result.put("tiles", new ArrayList<>());
                result.put("tenpai_tiles", new ArrayList<>());
            }
        
        // 获取副露信息
            List<String> meldJsons = playerGameMapper.getPlayerMelds(gameId, userId);
        List<Map<String, Object>> melds = new ArrayList<>();
            
            if (meldJsons != null && !meldJsons.isEmpty()) {
                for (String meldJson : meldJsons) {
                    Meld meld = objectMapper.readValue(meldJson, Meld.class);
                    Map<String, Object> meldMap = new HashMap<>();
                    meldMap.put("type", meld.getType().ordinal());
                    meldMap.put("tiles", meld.getTiles().stream()
                        .map(Tile::toString)
                        .collect(Collectors.toList()));
                    meldMap.put("from", meld.getFromPlayer());
                    melds.add(meldMap);
                }
            }
        
        result.put("melds", melds);
            
        } catch (Exception e) {
            logger.severe("获取玩家手牌失败: " + e.getMessage());
            result.put("tiles", new ArrayList<>());
            result.put("tenpai_tiles", new ArrayList<>());
            result.put("melds", new ArrayList<>());
        }
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getDiscardPiles(Long gameId) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 获取游戏中的所有玩家
        List<PlayerGame> playerGames = playerGameMapper.selectByGameId(gameId);
        
        // 为每个位置创建牌河信息
        for (int i = 0; i < 4; i++) {
            Map<String, Object> discardPile = new HashMap<>();
            discardPile.put("position", i);
            
            // 获取该位置玩家的弃牌信息
            List<Map<String, Object>> tiles = new ArrayList<>();
            
            // 查找该位置的玩家
            PlayerGame playerAtPosition = null;
            for (PlayerGame pg : playerGames) {
                if (pg.getPosition() == i) {
                    playerAtPosition = pg;
                    break;
                }
            }
            
            if (playerAtPosition != null && playerAtPosition.getDiscardTiles() != null) {
                // 转换弃牌为前端需要的格式
                for (Tile tile : playerAtPosition.getDiscardTiles()) {
                    Map<String, Object> tileMap = new HashMap<>();
                    tileMap.put("type", tile.getType().name());
                    tileMap.put("number", tile.getNumber());
                    tileMap.put("is_red_dora", tile.getIsRedDora());
                    // 如果有立直信息，添加立直标记
                    boolean isRiichiTile = playerAtPosition.getIsRiichi() && 
                                        playerAtPosition.getDiscardTiles().indexOf(tile) == playerAtPosition.getDiscardTiles().size() - 1;
                    tileMap.put("is_riichi_tile", isRiichiTile);
                    tiles.add(tileMap);
                }
            }
            
            discardPile.put("tiles", tiles);
            result.add(discardPile);
        }
        
        return result;
    }
    
    /**
     * 解析宝牌指示牌
     */
    private List<String> parseDoraIndicators(List<Tile> doraIndicators) {
        if (doraIndicators == null || doraIndicators.isEmpty()) {
            return new ArrayList<>();
        }
        return doraIndicators.stream()
                .map(Tile::getName)
                .collect(Collectors.toList());
    }

    /**
     * 检查并处理操作超时
     */
    private void checkAndHandleTimeout(Game game) {
        if (game.isActionTimeout()) {
            // 获取当前玩家
            PlayerGame currentPlayer = game.getCurrentPlayer();
            
            // 如果是AI玩家，执行AI操作
            if (isAIPlayer(game, currentPlayer.getUserId())) {
                performAIAction(game, currentPlayer.getUserId(), "skip");
            } else {
                // 如果是真实玩家，自动跳过
                handleSkipAction(game, currentPlayer, new HashMap<>());
            }
            
            // 广播游戏状态更新
            Map<String, Object> gameState = new HashMap<>();
            gameState.put("status", "timeout");
            gameState.put("message", "玩家操作超时，自动跳过");
            webSocketService.broadcastGameState(game.getId(), gameState);
        }
    }

    @Override
    @Transactional
    public Map<String, Object> discardTile(Long gameId, Long userId, String tileStr, boolean isRiichi) {
        Map<String, Object> result = new HashMap<>();
        
        // 检查游戏是否存在
        Game game = gameMapper.selectById(gameId);
        if (game == null) {
            result.put("success", false);
            result.put("message", "游戏不存在");
            return result;
        }
        
        // 检查是否是当前玩家的回合
        PlayerGame player = playerGameMapper.selectByGameIdAndUserId(gameId, userId);
        if (player == null || !player.getPosition().equals(game.getCurrentPosition())) {
            result.put("success", false);
            result.put("message", "不是当前玩家的回合");
            return result;
        }
        
        // 检查操作超时
        checkAndHandleTimeout(game);
        
        try {
            // 将字符串转换为Tile对象
            Tile tile = Tile.fromString(tileStr);
            
            // 从玩家手牌中移除这张牌
            String handTilesJson = playerGameMapper.getPlayerHandTiles(gameId, userId);
            List<Tile> handTiles = objectMapper.readValue(handTilesJson, 
                objectMapper.getTypeFactory().constructCollectionType(List.class, Tile.class));
            
            if (!handTiles.contains(tile)) {
                result.put("success", false);
                result.put("message", "玩家手牌中没有这张牌");
                return result;
            }
            
            handTiles.remove(tile);
            
            // 更新玩家手牌
            playerGameMapper.updatePlayerHandTiles(gameId, userId, 
                objectMapper.writeValueAsString(handTiles));
            
            // 添加到玩家牌河
            String discardTilesJson = playerGameMapper.getPlayerDiscardTiles(gameId, userId);
            List<Tile> discardTiles;
            
            if (discardTilesJson != null && !discardTilesJson.isEmpty()) {
                discardTiles = objectMapper.readValue(discardTilesJson, 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Tile.class));
            } else {
                discardTiles = new ArrayList<>();
            }
            
            discardTiles.add(tile);
            
            // 更新玩家牌河
            playerGameMapper.updatePlayerDiscardTiles(gameId, userId, 
                objectMapper.writeValueAsString(discardTiles));
            
            // 如果是立直，更新玩家状态
            if (isRiichi) {
                player.setIsRiichi(true);
                playerGameMapper.updateById(player);
                
                // 增加立直棒
                game.incrementRiichiSticks();
                gameMapper.updateById(game);
            }
            
            // 更新游戏状态
            game.setCurrentPosition((game.getCurrentPosition() + 1) % 4); // 轮到下一个玩家
            game.updateLastActionTime(); // 更新最后操作时间
            gameMapper.updateById(game);
            
            // 广播游戏状态更新
            Map<String, Object> gameState = getGameDetails(gameId);
            webSocketService.broadcastGameState(gameId, gameState);
            
            // 返回成功结果
            result.put("success", true);
            result.put("message", "打牌成功");
            
        } catch (Exception e) {
            logger.severe("打牌失败: " + e.getMessage());
            result.put("success", false);
            result.put("message", "打牌失败: " + e.getMessage());
        }
        
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> performAction(Long gameId, Long userId, String actionType, List<String> tiles) {
        Game game = getGameById(gameId);
        if (game == null) {
            return null;
        }
        
        // 检查是否超时
        checkAndHandleTimeout(game);
        
        // 检查是否是当前玩家的回合
        PlayerGame player = game.getCurrentPlayer();
        if (player == null || !player.getUserId().equals(userId)) {
            return null;
        }
        
        // 清除等待操作状态
        game.clearWaitingForAction();
        
        // 处理玩家操作
        Map<String, Object> result = new HashMap<>();
        switch (actionType) {
            case "chi":
                handleChiAction(game, player, tiles, result);
                break;
            case "pon":
                handlePonAction(game, player, tiles, result);
                break;
            case "kan":
                handleKanAction(game, player, tiles, result);
                break;
            case "ron":
                handleRonAction(game, player, tiles, result);
                break;
            case "tsumo":
                handleTsumoAction(game, player, result);
                break;
            case "skip":
                handleSkipAction(game, player, result);
                break;
            default:
                return null;
        }
        
        // 更新游戏状态
        gameMapper.updateById(game);
        
        // 广播游戏状态
        webSocketService.broadcastGameState(gameId, result);
        
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> riichi(Long gameId, Long userId, String tile) {
        Map<String, Object> result = discardTile(gameId, userId, tile, true);
        
        // 广播游戏状态更新
        webSocketService.broadcastGameState(gameId, result);
        
        // 广播玩家手牌更新
        Map<String, Object> handInfo = getPlayerHand(gameId, userId);
        webSocketService.broadcastPlayerHand(gameId, userId, handInfo);
        
        return result;
    }

    @Override
    public Map<String, Object> getGameSettlement(Long gameId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
        // 获取游戏信息
        Game game = gameMapper.selectById(gameId);
            if (game == null) {
            return null;
        }
        
            // 基本信息
            result.put("game_id", gameId);
            result.put("status", game.getStatus().ordinal());
            result.put("round", game.getRoundWind().ordinal());
            result.put("honba", game.getHonbaCount());
            
            // 结算类型：0=和牌结束，1=流局，2=中断
            int settlementType = 0;
            if (game.getStatus() == GameState.FINISHED) {
                // 检查是否是中断结束
                if (game.isNormalEnd()) {
                    // 检查是否有赢家
                    if (game.getWinner() != null) {
                        // 如果有赢家，表示和牌结束
                        settlementType = 0;
                    } else {
                        // 如果没有赢家，表示流局
                        settlementType = 1;
                    }
                } else {
                    // 中断结束
                    settlementType = 2;
                }
            }
            result.put("settlement_type", settlementType);
            
            // 获取玩家信息
            List<PlayerGame> playerGames = playerGameMapper.selectByGameId(gameId);
        List<Map<String, Object>> players = new ArrayList<>();
            
        for (PlayerGame pg : playerGames) {
            Map<String, Object> player = new HashMap<>();
            player.put("user_id", pg.getUserId());
                
                // 获取用户昵称
                User user = userService.getUserById(pg.getUserId());
                String nickname = (user != null) ? user.getNickname() : "用户" + pg.getUserId();
                player.put("nickname", nickname);
                
                player.put("position", pg.getPosition());
                player.put("score", pg.getScore());
                player.put("is_dealer", pg.getIsDealer());
            players.add(player);
        }
            
        result.put("players", players);
        
        // 设置和牌信息（如果是和牌结束）
            if (settlementType == 0) {
            List<Map<String, Object>> winningHands = new ArrayList<>();
            
                // 获取获胜玩家
                PlayerGame winningPlayer = null;
                if (game.getWinner() != null) {
                    for (PlayerGame pg : playerGames) {
                        if (pg.getUserId().equals(game.getWinner())) {
                            winningPlayer = pg;
                            break;
                        }
                    }
                }
                
                if (winningPlayer != null) {
            Map<String, Object> winningHand = new HashMap<>();
                    winningHand.put("user_id", winningPlayer.getUserId());
                    
                    // 获取用户昵称
                    User user = userService.getUserById(winningPlayer.getUserId());
                    String nickname = (user != null) ? user.getNickname() : "用户" + winningPlayer.getUserId();
                    winningHand.put("nickname", nickname);
                    
                    winningHand.put("position", winningPlayer.getPosition());
                    winningHand.put("score", winningPlayer.getScore());
                    
                    // 和牌详情
                    Map<String, Object> handDetails = new HashMap<>();
                    
                    // 从数据库获取和牌信息
                    try {
                        // 获取手牌
                        String handTilesJson = playerGameMapper.getPlayerHandTiles(gameId, winningPlayer.getUserId());
                        List<Tile> handTiles = objectMapper.readValue(handTilesJson, 
                            objectMapper.getTypeFactory().constructCollectionType(List.class, Tile.class));
                        
                        // 获取副露
                        List<String> meldJsons = playerGameMapper.getPlayerMelds(gameId, winningPlayer.getUserId());
                        List<Meld> melds = new ArrayList<>();
                        
                        if (meldJsons != null && !meldJsons.isEmpty()) {
                            for (String meldJson : meldJsons) {
                                Meld meld = objectMapper.readValue(meldJson, Meld.class);
                                melds.add(meld);
                            }
                        }
                        
                        // 计算役种和分数
                        List<Yaku> yakus = gameLogicService.calculateYaku(winningPlayer, game);
                        int basePoints = calculateBasePoints(handTiles, melds, game.getDoraIndicators()); // 计算符数
                        boolean isDealer = winningPlayer.getIsDealer();
                        
                        // 判断是否自摸
                        boolean isTsumo = game.getIsTsumo();
                        
                        int score = gameLogicService.calculateScore(yakus, basePoints, isDealer, isTsumo);
                        
                        handDetails.put("han", yakus.stream().mapToInt(Yaku::getHan).sum());
                        handDetails.put("fu", basePoints);
                        handDetails.put("score", score);
                        handDetails.put("yakus", yakus.stream().map(Yaku::getName).collect(Collectors.toList()));
                        
                        // 添加手牌信息
                        List<Map<String, Object>> tiles = new ArrayList<>();
                        for (Tile tile : handTiles) {
                            Map<String, Object> tileMap = new HashMap<>();
                            tileMap.put("type", tile.getType().name());
                            tileMap.put("number", tile.getNumber());
                            tileMap.put("is_red_dora", tile.getIsRedDora());
                            tiles.add(tileMap);
                        }
                        handDetails.put("tiles", tiles);
                        
                        // 添加副露信息
                        List<Map<String, Object>> meldList = new ArrayList<>();
                        for (Meld meld : melds) {
                            Map<String, Object> meldMap = new HashMap<>();
                            meldMap.put("type", meld.getType().name());
                            
                            List<Map<String, Object>> meldTiles = new ArrayList<>();
                            for (Tile tile : meld.getTiles()) {
                                Map<String, Object> tileMap = new HashMap<>();
                                tileMap.put("type", tile.getType().name());
                                tileMap.put("number", tile.getNumber());
                                tileMap.put("is_red_dora", tile.getIsRedDora());
                                meldTiles.add(tileMap);
                            }
                            meldMap.put("tiles", meldTiles);
                            meldList.add(meldMap);
                        }
                        handDetails.put("melds", meldList);
                        
                        // 添加宝牌信息
                        List<Map<String, Object>> doraIndicators = new ArrayList<>();
                        for (Tile tile : game.getDoraIndicators()) {
                            Map<String, Object> tileMap = new HashMap<>();
                            tileMap.put("type", tile.getType().name());
                            tileMap.put("number", tile.getNumber());
                            tileMap.put("is_red_dora", tile.getIsRedDora());
                            doraIndicators.add(tileMap);
                        }
                        handDetails.put("dora_indicators", doraIndicators);
                        
                    } catch (Exception e) {
                        logger.severe("获取和牌信息失败: " + e.getMessage());
                        e.printStackTrace();
                    }
                    
                    winningHand.put("hand", handDetails);
            winningHands.add(winningHand);
                }
                
            result.put("winning_hands", winningHands);
        }
        
            // 宝牌指示牌
            result.put("dora_indicators", parseDoraIndicators(game.getDoraIndicators()));
        
        return result;
            
        } catch (Exception e) {
            logger.severe("获取游戏结算信息失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 计算和牌的符数
     */
    private int calculateBasePoints(List<Tile> handTiles, List<Meld> melds, List<Tile> doraIndicators) {
        // 基本符数规则
        int basePoints = 20; // 初始符数
        
        // 门清状态判断
        boolean isConcealed = melds == null || melds.isEmpty() || melds.stream().allMatch(meld -> !meld.isOpen());
        if (isConcealed) {
            basePoints += 10; // 门清加符
        }
        
        // 雀头计算
        for (int i = 0; i < handTiles.size() - 1; i++) {
            Tile tile1 = handTiles.get(i);
            for (int j = i + 1; j < handTiles.size(); j++) {
                Tile tile2 = handTiles.get(j);
                if (tile1.getType() == tile2.getType() && tile1.getNumber() == tile2.getNumber()) {
                    // 雀头为役牌加符
                    if (tile1.getType() == TileType.DRAGON) {
                        basePoints += 2; // 三元牌雀头加符
                    } else if (tile1.getType() == TileType.WIND) {
                        basePoints += 2; // 风牌雀头加符
                    }
                    break;
                }
            }
        }
        
        // 面子计算
        if (melds != null) {
            for (Meld meld : melds) {
                if (meld.getType() == MeldType.PON) {
                    Tile tile = meld.getTiles().get(0);
                    if (tile.getType() == TileType.DRAGON) {
                        basePoints += 4; // 明刻三元牌加符
                    } else if (tile.getType() == TileType.WIND) {
                        basePoints += 4; // 明刻风牌加符
                    } else if (tile.getNumber() == 1 || tile.getNumber() == 9) {
                        basePoints += 2; // 明刻幺九牌加符
                    }
                } else if (meld.getType() == MeldType.KAN) {
                    Tile tile = meld.getTiles().get(0);
                    if (tile.getType() == TileType.DRAGON) {
                        basePoints += 16; // 明杠三元牌加符
                    } else if (tile.getType() == TileType.WIND) {
                        basePoints += 16; // 明杠风牌加符
                    } else if (tile.getNumber() == 1 || tile.getNumber() == 9) {
                        basePoints += 8; // 明杠幺九牌加符
                    } else {
                        basePoints += 4; // 明杠中张牌加符
                    }
                }
            }
        }
        
        // 上限符数为110符
        return Math.min(110, basePoints);
    }

    @Override
    public List<Map<String, Object>> getGameLogs(Long gameId, int page, int pageSize) {
        // 获取游戏信息
        Game game = gameMapper.selectById(gameId);
        if (game == null) {
            return null;
        }
        
        // 获取玩家信息
        List<PlayerGame> playerGames = playerGameMapper.selectByGameId(gameId);
        if (playerGames == null || playerGames.isEmpty()) {
            return null;
        }
        
        // 构建日志列表
        List<Map<String, Object>> logs = new ArrayList<>();
        
        // 添加游戏开始日志
        Map<String, Object> startLog = new HashMap<>();
        startLog.put("type", "game_start");
        startLog.put("time", game.getStartTime());
        startLog.put("players", playerGames.stream()
                .map(pg -> {
                    Map<String, Object> player = new HashMap<>();
                    player.put("user_id", pg.getUserId());
                    player.put("seat_wind", pg.getSeatWind());
                    player.put("is_dealer", pg.getIsDealer());
                    return player;
                })
                .collect(Collectors.toList()));
        logs.add(startLog);
        
        // 添加游戏结束日志
        if (game.getEndTime() != null) {
            Map<String, Object> endLog = new HashMap<>();
            endLog.put("type", "game_end");
            endLog.put("time", game.getEndTime());
            endLog.put("players", playerGames.stream()
                    .map(pg -> {
                        Map<String, Object> player = new HashMap<>();
                        player.put("user_id", pg.getUserId());
                        player.put("score", pg.getScore());
                        return player;
                    })
                    .collect(Collectors.toList()));
            logs.add(endLog);
        }
        
        // 分页处理
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, logs.size());
        List<Map<String, Object>> subLogs = logs.subList(start, end);
        
        // 广播游戏日志
        Map<String, Object> logData = new HashMap<>();
        logData.put("logs", subLogs);
        logData.put("page", page);
        logData.put("page_size", pageSize);
        webSocketService.broadcastGameLog(gameId, logData);
        
        return subLogs;
    }

    /**
     * 执行AI行动
     */
    private Map<String, Object> performAIAction(Game game, Long userId, String actionType) {
        Map<String, Object> result = new HashMap<>();
        AIPlayer aiPlayer = getAIPlayer(game, userId);
        
        if (aiPlayer == null) {
            throw new RuntimeException("AI player not found");
        }
        
        switch (actionType) {
            case "discard":
                // AI决定打出一张牌
                Tile discardTile = aiService.decideDiscard(aiPlayer, game);
                if (discardTile != null) {
                    return discardTile(game.getId(), userId, discardTile.toString(), false);
                }
                break;
                
            case "riichi":
                // AI决定是否立直
                if (aiService.decideRiichi(aiPlayer, game)) {
                    Tile riichiTile = aiService.decideDiscard(aiPlayer, game);
                    if (riichiTile != null) {
                        return riichi(game.getId(), userId, riichiTile.toString());
                    }
                }
                break;
                
            case "chi":
            case "pon":
            case "kan":
                // AI决定是否副露
                PlayerGame currentPlayer = game.getCurrentPlayer();
                if (currentPlayer != null && !currentPlayer.getId().equals(userId)) {
                    Tile lastDiscard = currentPlayer.getDiscardTiles().get(currentPlayer.getDiscardTiles().size() - 1);
                    Meld meld = aiService.decideMeld(aiPlayer, game, lastDiscard);
                    if (meld != null) {
                        List<String> tiles = meld.getTiles().stream()
                                .map(Tile::toString)
                                .collect(Collectors.toList());
                        return performAction(game.getId(), userId, actionType, tiles);
                    }
                }
                break;
                
            case "ron":
                // AI决定是否荣和
                PlayerGame lastPlayer = game.getCurrentPlayer();
                if (lastPlayer != null && !lastPlayer.getId().equals(userId)) {
                    Tile lastDiscard = lastPlayer.getDiscardTiles().get(lastPlayer.getDiscardTiles().size() - 1);
                    if (aiService.decideRon(aiPlayer, game, lastDiscard)) {
                        List<String> tiles = Collections.singletonList(lastDiscard.toString());
                        return performAction(game.getId(), userId, "ron", tiles);
                    }
                }
                break;
                
            case "tsumo":
                // AI决定是否自摸
                if (aiService.decideTsumo(aiPlayer, game)) {
                    return performAction(game.getId(), userId, "tsumo", Collections.emptyList());
                }
                break;
        }
        
        // 如果AI没有执行任何行动，则跳过
        return performAction(game.getId(), userId, "skip", Collections.emptyList());
    }

    /**
     * 判断是否是AI玩家
     */
    private boolean isAIPlayer(Game game, Long userId) {
        if (!game.getIsAIGame()) {
            return false;
        }
        return game.getAiPlayers().stream()
                .anyMatch(ai -> ai.getId().equals(userId));
    }

    /**
     * 获取AI玩家
     */
    private AIPlayer getAIPlayer(Game game, Long userId) {
        if (!game.getIsAIGame()) {
            return null;
        }
        return game.getAiPlayers().stream()
                .filter(ai -> ai.getId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    private void handleChiAction(Game game, PlayerGame player, List<String> tiles, Map<String, Object> result) {
        // 吃牌后轮到该玩家打牌，解除振听状态
        player.setIsFuriten(false);
        player.setIsTemporaryFuriten(false);
        game.setCurrentPosition(player.getPosition());

        // 创建吃牌副露
        List<Tile> meldTiles = tiles.stream()
                .map(Tile::fromString)
                .collect(Collectors.toList());
        Meld meld = new Meld(MeldType.CHI, meldTiles, true, (Long)null);
        player.getMelds().add(meld);

        // 从手牌中移除用于吃牌的牌
        for (Tile tile : meldTiles) {
            if (!tile.equals(game.getLastDiscardedTile())) {
                player.getHandTiles().remove(tile);
            }
        }

        result.put("success", true);
        result.put("message", "吃成功，请打出一张牌");
    }

    private void handlePonAction(Game game, PlayerGame player, List<String> tiles, Map<String, Object> result) {
        // 碰牌后轮到该玩家打牌，解除振听状态
        player.setIsFuriten(false);
        player.setIsTemporaryFuriten(false);
        game.setCurrentPosition(player.getPosition());

        // 创建碰牌副露
        List<Tile> meldTiles = tiles.stream()
                .map(Tile::fromString)
                .collect(Collectors.toList());
        Meld meld = new Meld(MeldType.PON, meldTiles, true, (Long)null);
        player.getMelds().add(meld);

        // 从手牌中移除用于碰牌的牌
        for (Tile tile : meldTiles) {
            if (!tile.equals(game.getLastDiscardedTile())) {
                player.getHandTiles().remove(tile);
            }
        }

        result.put("success", true);
        result.put("message", "碰成功，请打出一张牌");
    }

    private void handleKanAction(Game game, PlayerGame player, List<String> tiles, Map<String, Object> result) {
        // 杠牌后需要摸一张牌，仍然轮到该玩家，解除振听状态
        player.setIsFuriten(false);
        player.setIsTemporaryFuriten(false);
        game.setCurrentPosition(player.getPosition());

        // 创建杠牌副露
        List<Tile> meldTiles = tiles.stream()
                .map(Tile::fromString)
                .collect(Collectors.toList());
        Meld meld = new Meld(MeldType.KAN, meldTiles, true, (Long)null);
        player.getMelds().add(meld);

        // 从手牌中移除用于杠牌的牌
        for (Tile tile : meldTiles) {
            if (!tile.equals(game.getLastDiscardedTile())) {
                player.getHandTiles().remove(tile);
            }
        }

        // 补牌
        Tile replacementTile = game.drawTile();
        player.getHandTiles().add(replacementTile);

        result.put("success", true);
        result.put("message", "杠成功，请打出一张牌");
    }

    private void handleRonAction(Game game, PlayerGame player, List<String> tiles, Map<String, Object> result) {
        // 荣和后游戏结束
        game.setStatus(GameState.FINISHED);
        game.setEndTime(new Date());

        // 计算和牌分数
        List<Yaku> yakus = gameLogicService.calculateYaku(player, game);
        int basePoints = 30; // 基本符数
        boolean isDealer = player.getIsDealer();
        boolean isTsumo = false;
        int score = gameLogicService.calculateScore(yakus, basePoints, isDealer, isTsumo);

        // 更新玩家分数
        PlayerGame lastPlayer = game.getCurrentPlayer();
        if (lastPlayer != null) {
            lastPlayer.setScore(lastPlayer.getScore() - score);
        }
        player.setScore(player.getScore() + score);

        result.put("success", true);
        result.put("message", "荣和成功，游戏结束");
        result.put("score", score);
        result.put("yakus", yakus);
    }

    private void handleTsumoAction(Game game, PlayerGame player, Map<String, Object> result) {
        // 自摸后游戏结束
        game.setStatus(GameState.FINISHED);
        game.setEndTime(new Date());

        // 计算和牌分数
        List<Yaku> yakus = gameLogicService.calculateYaku(player, game);
        int basePoints = 30; // 基本符数
        boolean isDealer = player.getIsDealer();
        boolean isTsumo = true;
        int score = gameLogicService.calculateScore(yakus, basePoints, isDealer, isTsumo);

        // 更新所有玩家分数
        for (PlayerGame p : game.getPlayers()) {
            if (p.getId().equals(player.getId())) {
                p.setScore(p.getScore() + score * 3);
            } else {
                p.setScore(p.getScore() - score);
            }
        }

        result.put("success", true);
        result.put("message", "自摸成功，游戏结束");
        result.put("score", score);
        result.put("yakus", yakus);
    }

    private void handleSkipAction(Game game, PlayerGame player, Map<String, Object> result) {
        // 跳过操作，轮到下一个玩家
        game.setCurrentPosition((game.getCurrentPosition() + 1) % 4);
        
        result.put("success", true);
        result.put("message", "跳过操作");
    }
} 