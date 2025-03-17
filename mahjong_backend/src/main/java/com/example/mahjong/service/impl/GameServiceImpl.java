package com.example.mahjong.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mahjong.entity.Game;
import com.example.mahjong.entity.PlayerGame;
import com.example.mahjong.mapper.GameMapper;
import com.example.mahjong.mapper.PlayerGameMapper;
import com.example.mahjong.service.GameService;

import java.util.*;

@Service
public class GameServiceImpl implements GameService {

    @Autowired
    private GameMapper gameMapper;

    @Autowired
    private PlayerGameMapper playerGameMapper;

    @Override
    @Transactional
    public Game createGame(Long roomId) {
        // 创建新游戏
        Game game = new Game();
        game.setRoomId(roomId);
        game.setRoundWind(0); // 东风
        game.setDealerPosition(0); // 东家为庄
        game.setCurrentPosition(0); // 从东家开始
        game.setHonbaCount(0);
        game.setRiichiStickCount(0);
        
        // 初始化牌山和宝牌指示牌
        initializeWallAndDora(game);
        
        game.setWallPosition(14); // 从第14张牌开始摸牌
        game.setStatus(0); // 准备中
        game.setStartTime(new Date());
        game.setCreateTime(new Date());
        game.setUpdateTime(new Date());
        
        gameMapper.insert(game);
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
        result.put("riichi_stick_count", game.getRiichiStickCount());
        
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
     * 初始化牌山和宝牌指示牌
     */
    private void initializeWallAndDora(Game game) {
        // 这里应该实现洗牌和牌山初始化的逻辑
        // 暂时使用随机字符串模拟
        StringBuilder wallTiles = new StringBuilder();
        String[] tileTypes = {"m", "p", "s", "z"};
        Random random = new Random();
        
        for (int i = 0; i < 136; i++) {
            int number = random.nextInt(9) + 1;
            if (number > 7 && tileTypes[i % 4].equals("z")) {
                number = 7; // 字牌只有1-7
            }
            wallTiles.append(number).append(tileTypes[i % 4]).append(",");
        }
        
        game.setWallTiles(wallTiles.toString());
        
        // 设置宝牌指示牌
        game.setDoraIndicators("5m");
        game.setUraDoraIndicators("3p");
        game.setKanDoraIndicators("");
    }
    
    /**
     * 解析宝牌指示牌字符串
     */
    private List<String> parseDoraIndicators(String doraIndicators) {
        if (doraIndicators == null || doraIndicators.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(doraIndicators.split(","));
    }

    @Override
    @Transactional
    public Map<String, Object> discardTile(Long gameId, Long userId, String tile, boolean isRiichi) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取游戏信息
        Game game = gameMapper.selectById(gameId);
        if (game == null) {
            return null;
        }
        
        // 获取玩家在游戏中的信息
        PlayerGame playerGame = playerGameMapper.selectByGameIdAndUserId(gameId, userId);
        if (playerGame == null) {
            return null;
        }
        
        // 检查是否轮到该玩家行动
        if (!playerGame.getPosition().equals(game.getCurrentPosition())) {
            return null;
        }
        
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
            playerGame.setIsRiichi(true);
            playerGame.setScore(playerGame.getScore() - 1000); // 立直需要支付1000点
            game.setRiichiStickCount(game.getRiichiStickCount() + 1);
            
            playerGameMapper.updateById(playerGame);
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
        
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> performAction(Long gameId, Long userId, String actionType, List<String> tiles) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取游戏信息
        Game game = gameMapper.selectById(gameId);
        if (game == null) {
            return null;
        }
        
        // 获取玩家在游戏中的信息
        PlayerGame playerGame = playerGameMapper.selectByGameIdAndUserId(gameId, userId);
        if (playerGame == null) {
            return null;
        }
        
        // 这里应该实现各种操作的逻辑，包括：
        // 1. 吃：从牌河获取上家打出的牌，与手牌组成顺子
        // 2. 碰：从牌河获取任何玩家打出的牌，与手牌组成刻子
        // 3. 杠：明杠、暗杠、加杠的处理
        // 4. 和：荣和、自摸的判定和得分计算
        // 5. 跳过：不进行任何操作
        
        // 由于我们没有实现完整的游戏逻辑，这里简化处理
        
        // 根据操作类型更新游戏状态
        String actionResult = "";
        int nextPosition = game.getCurrentPosition();
        
        switch (actionType) {
            case "chi":
                // 吃牌后轮到该玩家打牌
                nextPosition = playerGame.getPosition();
                actionResult = "吃成功，请打出一张牌";
                break;
            case "pon":
                // 碰牌后轮到该玩家打牌
                nextPosition = playerGame.getPosition();
                actionResult = "碰成功，请打出一张牌";
                break;
            case "kan":
                // 杠牌后需要摸一张牌，仍然轮到该玩家
                nextPosition = playerGame.getPosition();
                actionResult = "杠成功，请打出一张牌";
                break;
            case "ron":
                // 荣和后游戏结束
                game.setStatus(2); // 已结束
                game.setEndTime(new Date());
                actionResult = "荣和成功，游戏结束";
                break;
            case "tsumo":
                // 自摸后游戏结束
                game.setStatus(2); // 已结束
                game.setEndTime(new Date());
                actionResult = "自摸成功，游戏结束";
                break;
            case "skip":
                // 跳过操作，轮到下一个玩家
                nextPosition = (game.getCurrentPosition() + 1) % 4;
                actionResult = "跳过操作";
                break;
        }
        
        game.setCurrentPosition(nextPosition);
        game.setUpdateTime(new Date());
        gameMapper.updateById(game);
        
        // 记录操作日志（实际应该保存到数据库）
        
        // 构建返回结果
        result.put("next_position", nextPosition);
        result.put("action_result", actionResult);
        
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> riichi(Long gameId, Long userId, String tile) {
        // 立直实际上是一种特殊的打牌操作，可以调用discardTile方法
        return discardTile(gameId, userId, tile, true);
    }

    @Override
    public Map<String, Object> getGameSettlement(Long gameId) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取游戏信息
        Game game = gameMapper.selectById(gameId);
        if (game == null || game.getStatus() != 2) { // 只有已结束的游戏才有结算信息
            return null;
        }
        
        // 获取玩家信息
        List<PlayerGame> playerGames = playerGameMapper.selectByGameId(gameId);
        
        // 设置结算基本信息
        result.put("settlement_type", 0); // 0-和牌，1-流局
        result.put("settlement_time", game.getEndTime());
        result.put("honba_count", game.getHonbaCount());
        result.put("riichi_stick_count", game.getRiichiStickCount());
        
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
        
        return result;
    }

    @Override
    public Map<String, Object> getGameLogs(Long gameId, int page, int pageSize) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取游戏信息
        Game game = gameMapper.selectById(gameId);
        if (game == null) {
            return null;
        }
        
        // 这里应该从数据库中获取操作日志
        // 由于我们没有实现日志表，这里模拟一些数据
        
        // 设置总记录数
        result.put("total", 30);
        
        // 生成日志列表
        List<Map<String, Object>> logs = new ArrayList<>();
        
        // 模拟一些操作日志
        for (int i = 1; i <= 3; i++) {
            Map<String, Object> log = new HashMap<>();
            log.put("id", i);
            
            if (i == 1) {
                log.put("position", 0);
                log.put("action_type", 0); // 摸牌
                Map<String, Object> actionData = new HashMap<>();
                actionData.put("tile", "4m");
                log.put("action_data", actionData);
                log.put("action_time", new Date(System.currentTimeMillis() - 50000));
            } else if (i == 2) {
                log.put("position", 0);
                log.put("action_type", 1); // 打牌
                Map<String, Object> actionData = new HashMap<>();
                actionData.put("tile", "1p");
                actionData.put("is_tsumogiri", false);
                log.put("action_data", actionData);
                log.put("action_time", new Date(System.currentTimeMillis() - 45000));
            } else if (i == 3) {
                log.put("position", 1);
                log.put("action_type", 0); // 摸牌
                Map<String, Object> actionData = new HashMap<>();
                actionData.put("tile", "7s");
                log.put("action_data", actionData);
                log.put("action_time", new Date(System.currentTimeMillis() - 40000));
            }
            
            logs.add(log);
        }
        
        result.put("list", logs);
        
        return result;
    }
} 