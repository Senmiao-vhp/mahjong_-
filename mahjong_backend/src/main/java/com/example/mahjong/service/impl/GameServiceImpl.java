package com.example.mahjong.service.impl;

import com.example.mahjong.entity.*;
import com.example.mahjong.service.GameService;
import com.example.mahjong.service.GameLogicService;
import com.example.mahjong.service.AIService;
import com.example.mahjong.mapper.GameMapper;
import com.example.mahjong.mapper.PlayerGameMapper;
import com.example.mahjong.websocket.GameWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GameServiceImpl implements GameService {

    @Autowired
    private GameMapper gameMapper;

    @Autowired
    private PlayerGameMapper playerGameMapper;

    @Autowired
    private GameWebSocketHandler webSocketHandler;

    @Autowired
    private GameLogicService gameLogicService;

    @Autowired
    private AIService aiService;

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
    public List<PlayerGame> getPlayersByGameId(Long gameId) {
        return playerGameMapper.selectByGameId(gameId);
    }

    @Override
    public PlayerGame getPlayerGameByGameIdAndUserId(Long gameId, Long userId) {
        return playerGameMapper.selectByGameIdAndUserId(gameId, userId);
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
            // 这里需要从用户服务获取昵称，暂时使用ID代替
            player.put("nickname", "用户" + pg.getUserId());
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
        
        // 这里需要从另一个表获取玩家手牌信息
        // 暂时模拟一些数据
        List<String> tiles = Arrays.asList("1m", "1m", "2m", "3m", "4m", "5m", "6p", "7p", "8p", "1s", "2s", "3s");
        result.put("tiles", tiles);
        
        String drawnTile = "4s";
        result.put("drawn_tile", drawnTile);
        
        // 计算听牌
        List<String> tenpaiTiles = Arrays.asList("5m", "9p");
        result.put("tenpai_tiles", tenpaiTiles);
        
        // 获取副露信息
        List<Map<String, Object>> melds = new ArrayList<>();
        // 暂时模拟一个碰的数据
        Map<String, Object> meld = new HashMap<>();
        meld.put("type", 1); // 碰
        meld.put("tiles", Arrays.asList("7s", "7s", "7s"));
        meld.put("from", 2); // 来自西家
        melds.add(meld);
        
        result.put("melds", melds);
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getDiscardPiles(Long gameId) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 获取游戏中的所有玩家
        List<PlayerGame> players = playerGameMapper.selectByGameId(gameId);
        
        // 为每个位置创建牌河信息
        for (int i = 0; i < 4; i++) {
            Map<String, Object> discardPile = new HashMap<>();
            discardPile.put("position", i);
            
            // 这里需要从另一个表获取牌河信息
            // 暂时模拟一些数据
            List<Map<String, Object>> tiles = new ArrayList<>();
            
            if (i == 0) {
                // 东家牌河
                Map<String, Object> tile1 = new HashMap<>();
                tile1.put("tile", "1p");
                tile1.put("is_riichi", false);
                tile1.put("is_tsumogiri", false);
                tile1.put("is_furiten", false);
                tiles.add(tile1);
                
                Map<String, Object> tile2 = new HashMap<>();
                tile2.put("tile", "9s");
                tile2.put("is_riichi", true);
                tile2.put("is_tsumogiri", true);
                tile2.put("is_furiten", false);
                tiles.add(tile2);
            } else if (i == 1) {
                // 南家牌河
                Map<String, Object> tile = new HashMap<>();
                tile.put("tile", "3m");
                tile.put("is_riichi", false);
                tile.put("is_tsumogiri", false);
                tile.put("is_furiten", false);
                tiles.add(tile);
            } else if (i == 3) {
                // 北家牌河
                Map<String, Object> tile = new HashMap<>();
                tile.put("tile", "5p");
                tile.put("is_riichi", false);
                tile.put("is_tsumogiri", true);
                tile.put("is_furiten", false);
                tiles.add(tile);
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
            webSocketHandler.broadcastGameState(game.getId(), gameState);
        }
    }

    @Override
    @Transactional
    public Map<String, Object> discardTile(Long gameId, Long userId, String tile, boolean isRiichi) {
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
        
        Map<String, Object> result = new HashMap<>();
        
        // 这里应该实现打牌的逻辑，包括：
        // 1. 从玩家手牌中移除打出的牌
        // 2. 添加到牌河
        // 3. 如果立直，更新玩家状态和立直棒数
        // 4. 计算其他玩家可用的操作（吃/碰/杠/和）
        // 5. 更新游戏状态
        
        // 由于我们没有实现完整的游戏逻辑，这里简化处理
        
        // 更新当前行动位置（轮到下一个玩家）
        int nextPosition = (game.getCurrentPosition() + 1) % 4;
        game.setCurrentPosition(nextPosition);
        
        // 如果立直，更新玩家状态和立直棒数
        if (isRiichi) {
            player.setIsRiichi(true);
            player.setScore(player.getScore() - 1000); // 立直需要支付1000点
            game.setRiichiSticks(game.getRiichiSticks() + 1);
            
            playerGameMapper.updateById(player);
        }
        
        game.setUpdateTime(new Date());
        gameMapper.updateById(game);
        
        // 记录操作日志（实际应该保存到数据库）
        
        // 构建返回结果
        result.put("next_position", nextPosition);
        
        // 模拟其他玩家可用的操作
        List<Map<String, Object>> availableActions = new ArrayList<>();
        
        // 假设下家可以吃
        if (nextPosition == 1) {
            Map<String, Object> action = new HashMap<>();
            action.put("position", 1);
            action.put("actions", Arrays.asList("chi"));
            availableActions.add(action);
        }
        
        // 假设对家可以碰
        if (nextPosition != 2) {
            Map<String, Object> action = new HashMap<>();
            action.put("position", 2);
            action.put("actions", Arrays.asList("pon"));
            availableActions.add(action);
        }
        
        result.put("available_actions", availableActions);
        
        // 广播游戏状态更新
        webSocketHandler.broadcastGameState(gameId, result);
        
        // 广播玩家手牌更新
        Map<String, Object> handInfo = getPlayerHand(gameId, userId);
        webSocketHandler.broadcastPlayerHand(gameId, userId, handInfo);
        
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
        webSocketHandler.broadcastGameState(gameId, result);
        
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> riichi(Long gameId, Long userId, String tile) {
        Map<String, Object> result = discardTile(gameId, userId, tile, true);
        
        // 广播游戏状态更新
        webSocketHandler.broadcastGameState(gameId, result);
        
        // 广播玩家手牌更新
        Map<String, Object> handInfo = getPlayerHand(gameId, userId);
        webSocketHandler.broadcastPlayerHand(gameId, userId, handInfo);
        
        return result;
    }

    @Override
    public Map<String, Object> getGameSettlement(Long gameId) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取游戏信息
        Game game = gameMapper.selectById(gameId);
        if (game == null || game.getStatus() != GameState.FINISHED) { // 只有已结束的游戏才有结算信息
            return null;
        }
        
        // 获取玩家信息
        List<PlayerGame> playerGames = playerGameMapper.selectByGameId(gameId);
        
        // 设置结算基本信息
        result.put("settlement_type", 0); // 0-和牌，1-流局
        result.put("settlement_time", game.getEndTime());
        result.put("honba_count", game.getHonbaCount());
        result.put("riichi_stick_count", game.getRiichiSticks());
        
        // 设置玩家结算信息
        List<Map<String, Object>> players = new ArrayList<>();
        for (PlayerGame pg : playerGames) {
            Map<String, Object> player = new HashMap<>();
            player.put("position", pg.getPosition());
            player.put("user_id", pg.getUserId());
            player.put("nickname", "用户" + pg.getUserId()); // 实际应该从用户服务获取
            
            // 这里应该计算点数变化，暂时模拟
            int scoreChange = 0;
            if (pg.getPosition() == 0) {
                scoreChange = -8000;
            } else if (pg.getPosition() == 1) {
                scoreChange = 8000;
            }
            
            player.put("score_change", scoreChange);
            player.put("final_score", pg.getScore());
            
            players.add(player);
        }
        result.put("players", players);
        
        // 设置和牌信息（如果是和牌结束）
        if ((Integer) result.get("settlement_type") == 0) {
            List<Map<String, Object>> winningHands = new ArrayList<>();
            
            // 假设是1号位玩家和牌
            Map<String, Object> winningHand = new HashMap<>();
            winningHand.put("position", 1);
            winningHand.put("win_type", 1); // 荣和
            winningHand.put("from_position", 0); // 从东家荣和
            winningHand.put("win_tile", "5m");
            winningHand.put("hand_tiles", Arrays.asList("1m", "1m", "2m", "3m", "4m", "6p", "7p", "8p", "1s", "2s", "3s", "4s"));
            winningHand.put("melds", new ArrayList<>());
            winningHand.put("dora_count", 1);
            winningHand.put("ura_dora_count", 0);
            winningHand.put("aka_dora_count", 0);
            winningHand.put("fu", 40);
            winningHand.put("han", 3);
            winningHand.put("score", 8000);
            
            // 设置役种
            List<Map<String, Object>> yaku = new ArrayList<>();
            Map<String, Object> yaku1 = new HashMap<>();
            yaku1.put("name", "立直");
            yaku1.put("han", 1);
            yaku.add(yaku1);
            
            Map<String, Object> yaku2 = new HashMap<>();
            yaku2.put("name", "门前清自摸和");
            yaku2.put("han", 1);
            yaku.add(yaku2);
            
            Map<String, Object> yaku3 = new HashMap<>();
            yaku3.put("name", "平和");
            yaku3.put("han", 1);
            yaku.add(yaku3);
            
            winningHand.put("yaku", yaku);
            
            winningHands.add(winningHand);
            result.put("winning_hands", winningHands);
        }
        
        // 广播游戏结算信息
        webSocketHandler.broadcastGameSettlement(gameId, result);
        
        return result;
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
        webSocketHandler.broadcastGameLog(gameId, logData);
        
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