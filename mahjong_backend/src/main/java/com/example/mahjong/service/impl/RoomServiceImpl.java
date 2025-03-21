package com.example.mahjong.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mahjong.entity.Game;
import com.example.mahjong.entity.GameState;
import com.example.mahjong.entity.PlayerGame;
import com.example.mahjong.entity.Room;
import com.example.mahjong.entity.RoomDTO;
import com.example.mahjong.entity.User;
import com.example.mahjong.entity.RoomPlayer;
import com.example.mahjong.mapper.RoomMapper;
import com.example.mahjong.mapper.RoomPlayerMapper;
import com.example.mahjong.service.GameService;
import com.example.mahjong.service.RoomService;
import com.example.mahjong.service.UserService;

import java.util.*;
import java.util.Comparator;
import java.util.logging.Logger;

@Service
public class RoomServiceImpl implements RoomService {

    private static final Logger logger = Logger.getLogger(RoomServiceImpl.class.getName());

    @Autowired
    private RoomMapper roomMapper;
    
    @Autowired
    private GameService gameService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private RoomPlayerMapper roomPlayerMapper;

    @Override
    @Transactional
    public RoomDTO createRoom(Room room) {
        // 设置默认值
        if (room.getRoomType() == null) {
            room.setRoomType(0); // 默认普通房间
        }
        if (room.getGameType() == null) {
            room.setGameType(0); // 默认东风战
        }
        if (room.getHasRedDora() == null) {
            room.setHasRedDora(true); // 默认有赤宝牌
        }
        if (room.getHasOpenTanyao() == null) {
            room.setHasOpenTanyao(true); // 默认有食断
        }
        
        room.setStatus(0); // 等待中
        room.setMaxPlayerCount(4); // 麻将固定4人
        room.setCurrentPlayerCount(1); // 创建时只有房主一人
        room.setCreateTime(new Date());
        room.setUpdateTime(new Date());
        
        roomMapper.insert(room);
        
        // 转换为DTO
        RoomDTO roomDTO = new RoomDTO();
        BeanUtils.copyProperties(room, roomDTO);
        
        User creator = userService.getUserById(room.getCreatorId());
        if (creator != null) {
            roomDTO.setCreatorName(creator.getNickname());
        } else {
            roomDTO.setCreatorName("未知用户");
        }
        
        return roomDTO;
    }

    @Override
    public Room getRoomById(Long id) {
        return roomMapper.selectById(id);
    }

    @Override
    public Map<String, Object> getRoomList(Integer status, int page, int pageSize) {
        Map<String, Object> result = new HashMap<>();
        
        // 计算分页参数
        int offset = (page - 1) * pageSize;
        
        // 查询房间列表
        List<Room> rooms = roomMapper.selectByStatus(status, offset, pageSize);
        
        // 查询总数
        int total = roomMapper.countByStatus(status);
        
        // 转换为前端需要的格式
        List<Map<String, Object>> roomList = new ArrayList<>();
        for (Room room : rooms) {
            Map<String, Object> roomMap = new HashMap<>();
            roomMap.put("id", room.getId());
            roomMap.put("room_name", room.getRoomName());
            roomMap.put("game_type", room.getGameType());
            roomMap.put("status", room.getStatus());
            roomMap.put("player_count", room.getCurrentPlayerCount());
            
            // 从用户服务获取创建者昵称
            User creator = userService.getUserById(room.getCreatorId());
            if (creator != null) {
                roomMap.put("creator_name", creator.getNickname());
            } else {
                roomMap.put("creator_name", "未知用户");
            }
            roomMap.put("create_time", room.getCreateTime());
            
            roomList.add(roomMap);
        }
        
        result.put("total", total);
        result.put("list", roomList);
        
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> joinRoom(Long roomId, Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        // 检查房间是否存在
        Room room = roomMapper.selectById(roomId);
        if (room == null) {
            result.put("success", false);
            result.put("message", "房间不存在");
            return result;
        }
        
        // 检查房间状态
        if (room.getStatus() != 0) {
            result.put("success", false);
            result.put("message", "房间已开始游戏或已关闭");
            return result;
        }
        
        // 检查房间是否已满
        int playerCount = roomPlayerMapper.countByRoomId(roomId);
        if (playerCount >= room.getMaxPlayerCount()) {
            result.put("success", false);
            result.put("message", "房间已满");
            return result;
        }
        
        // 检查玩家是否已在房间中
        if (roomPlayerMapper.isPlayerInRoom(roomId, userId) > 0) {
            // 如果已经在房间中，返回座位信息
            RoomPlayer playerInfo = roomPlayerMapper.selectByRoomIdAndUserId(roomId, userId);
            result.put("success", true);
            result.put("message", "你已经在房间中");
            result.put("position", playerInfo.getPosition());
            return result;
        }
        
        // 分配空闲座位
        Integer position = null;
        
        // 查找所有已占用的座位
        List<Map<String, Object>> existingPlayers = roomPlayerMapper.selectByRoomId(roomId);
        Set<Integer> occupiedPositions = new HashSet<>();
        for (Map<String, Object> player : existingPlayers) {
            occupiedPositions.add((Integer) player.get("position"));
        }
        
        // 寻找一个未占用的座位号
        for (int i = 0; i < room.getMaxPlayerCount(); i++) {
            if (!occupiedPositions.contains(i)) {
                position = i;
                break;
            }
        }
        
        if (position == null) {
            result.put("success", false);
            result.put("message", "没有可用座位");
            return result;
        }
        
        // 将玩家加入房间
        RoomPlayer roomPlayer = new RoomPlayer();
        roomPlayer.setRoomId(roomId);
        roomPlayer.setUserId(userId);
        roomPlayer.setPosition(position);
        roomPlayer.setIsReady(position == 0); // 房主默认准备
        
        roomPlayerMapper.insert(roomPlayer);
        
        // 更新房间玩家数量
        room.setCurrentPlayerCount(playerCount + 1);
        room.setUpdateTime(new Date());
        roomMapper.updateById(room);
        
        result.put("success", true);
        result.put("message", "加入房间成功");
        result.put("position", position);
        
        return result;
    }

    @Override
    public boolean readyInRoom(Long roomId, Long userId, boolean isReady) {
        // 获取房间信息
        Room room = roomMapper.selectById(roomId);
        if (room == null || room.getStatus() != 0) {
            return false; // 房间不存在或不在等待状态
        }
        
        // 检查玩家是否在房间中
        RoomPlayer playerInfo = roomPlayerMapper.selectByRoomIdAndUserId(roomId, userId);
        if (playerInfo == null) {
            return false; // 玩家不在房间中
        }
        
        // 获取玩家位置
        Integer position = playerInfo.getPosition();
        
        // 检查是否有进行中的游戏
        Game currentGame = null;
        List<Game> games = gameService.getGamesByRoomId(roomId);
        if (games != null && !games.isEmpty()) {
            // 找出最新的游戏
            currentGame = games.stream()
                .filter(g -> g.getStatus() != GameState.FINISHED)
                .sorted(Comparator.comparing(Game::getCreateTime).reversed())
                .findFirst()
                .orElse(null);
        }
        
        // 如果已有进行中的游戏，不能更改准备状态
        if (currentGame != null && currentGame.getStatus() != GameState.WAITING && currentGame.getStatus() != GameState.READY) {
            return false;
        }
        
        // 如果游戏已经创建但还未开始
        if (currentGame != null) {
            // 通过player_game表更新准备状态
            PlayerGame playerGame = gameService.getPlayerGameByGameIdAndUserId(currentGame.getId(), userId);
            if (playerGame != null) {
                playerGame.setIsRiichi(isReady);
                
                try {
                    // 更新PlayerGame对象到数据库
                    return gameService.updatePlayerGame(playerGame);
                } catch (Exception e) {
                    logger.severe("更新玩家准备状态失败: " + e.getMessage());
                    return false;
                }
            }
        } else {
            // 使用room_player表更新准备状态
            try {
                int rows = roomPlayerMapper.updateReadyStatus(roomId, userId, isReady);
                logger.info("更新玩家准备状态: roomId=" + roomId + ", userId=" + userId + ", position=" + position + ", isReady=" + isReady);
                
                // 检查是否所有玩家都已准备，如果是，可以自动开始游戏
                if (isReady && areAllPlayersReady(roomId)) {
                    logger.info("房间" + roomId + "的所有玩家都已准备好");
                }
                
                return rows > 0;
            } catch (Exception e) {
                logger.severe("更新玩家准备状态失败: " + e.getMessage());
                return false;
            }
        }
        
        return false;
    }
    
    /**
     * 检查房间中所有玩家是否都已准备
     */
    private boolean areAllPlayersReady(Long roomId) {
        // 获取房间中玩家数量
        int playerCount = roomPlayerMapper.countByRoomId(roomId);
        
        // 如果房间为空或只有一个玩家，返回false
        if (playerCount <= 1) {
            return false;
        }
        
        // 获取准备好的玩家数量
        int readyCount = roomPlayerMapper.countReadyPlayersByRoomId(roomId);
        
        // 所有玩家都已准备
        return readyCount == playerCount;
    }

    @Override
    public List<Map<String, Object>> getPlayersInRoom(Long roomId) {
        Room room = roomMapper.selectById(roomId);
        if (room == null) {
            return new ArrayList<>();
        }
        
        // 查找与该房间关联的游戏
        Game currentGame = null;
        List<Game> games = gameService.getGamesByRoomId(roomId);
        if (games != null && !games.isEmpty()) {
            // 找出最新的游戏
            currentGame = games.stream()
                .filter(g -> g.getStatus() != GameState.FINISHED)
                .sorted(Comparator.comparing(Game::getCreateTime).reversed())
                .findFirst()
                .orElse(null);
        }
        
        List<Map<String, Object>> players = new ArrayList<>();
        
        // 如果有进行中的游戏，从player_game表获取玩家信息
        if (currentGame != null) {
            List<PlayerGame> playerGames = gameService.getPlayersByGameId(currentGame.getId());
            for (PlayerGame pg : playerGames) {
                User user = userService.getUserById(pg.getUserId());
                Map<String, Object> player = new HashMap<>();
                player.put("user_id", pg.getUserId());
                player.put("nickname", user != null ? user.getNickname() : "用户" + pg.getUserId());
                player.put("position", pg.getPosition());
                player.put("is_ready", true); // 游戏已经开始，所有玩家都视为准备好
                player.put("score", pg.getScore());
                players.add(player);
            }
        } else {
            // 如果没有进行中的游戏，使用room_player表获取玩家信息
            List<RoomPlayer> roomPlayers = roomPlayerMapper.getRoomPlayersByRoomId(roomId);
            for (RoomPlayer rp : roomPlayers) {
                Long userId = rp.getUserId();
                User user = userService.getUserById(userId);
                
                Map<String, Object> player = new HashMap<>();
                player.put("user_id", userId);
                player.put("nickname", user != null ? user.getNickname() : "用户" + userId);
                player.put("position", rp.getPosition());
                player.put("is_ready", rp.getIsReady());
                player.put("join_time", rp.getJoinTime());
                players.add(player);
            }
        }
        
        return players;
    }

    @Override
    @Transactional
    public Long startGame(Long roomId) {
        // 获取房间信息
        Room room = roomMapper.selectById(roomId);
        if (room == null) {
            return null; // 房间不存在
        }
        
        // 检查房间状态
        if (room.getStatus() != 0) {
            return null; // 房间不在等待状态
        }
        
        // 从room_player表获取玩家数量
        int playerCount = roomPlayerMapper.countByRoomId(roomId);
        
        // 检查玩家数量
        if (playerCount < 4) {
            return null; // 玩家不足4人
        }
        
        // 检查玩家准备状态
        if (!areAllPlayersReady(roomId)) {
            return null; // 不是所有玩家都已准备
        }
        
        // 更新房间状态
        room.setStatus(1); // 游戏中
        room.setUpdateTime(new Date());
        roomMapper.updateById(room);
        
        // 创建游戏
        Game game = gameService.createGame(roomId);
        
        return game.getId();
    }

    /**
     * 从旧系统迁移玩家数据到room_player表
     * 这是一个临时方法，用于处理系统升级时的数据迁移
     */
    @Transactional
    public void migratePlayersToRoomPlayerTable(Long roomId) {
        Room room = roomMapper.selectById(roomId);
        if (room == null) {
            logger.warning("尝试迁移不存在的房间: " + roomId);
            return;
        }
        
        // 检查是否已有room_player记录
        int existingPlayers = roomPlayerMapper.countByRoomId(roomId);
        if (existingPlayers > 0) {
            logger.info("房间" + roomId + "已有玩家记录，无需迁移");
            return;
        }
        
        // 迁移房主
        RoomPlayer roomPlayerCreator = new RoomPlayer();
        roomPlayerCreator.setRoomId(roomId);
        roomPlayerCreator.setUserId(room.getCreatorId());
        roomPlayerCreator.setPosition(0); // 房主默认在东家位置
        roomPlayerCreator.setIsReady(true); // 房主默认准备
        roomPlayerMapper.insert(roomPlayerCreator);
        
        // 检查是否有进行中的游戏
        List<Game> games = gameService.getGamesByRoomId(roomId);
        Game currentGame = null;
        if (games != null && !games.isEmpty()) {
            // 找出最新的游戏
            currentGame = games.stream()
                .filter(g -> g.getStatus() != GameState.FINISHED)
                .sorted(Comparator.comparing(Game::getCreateTime).reversed())
                .findFirst()
                .orElse(null);
        }
        
        // 如果有游戏，从player_game表迁移其他玩家
        if (currentGame != null) {
            List<PlayerGame> playerGames = gameService.getPlayersByGameId(currentGame.getId());
            for (PlayerGame pg : playerGames) {
                // 跳过房主，因为已经添加过了
                if (pg.getUserId().equals(room.getCreatorId())) {
                    continue;
                }
                
                RoomPlayer roomPlayer = new RoomPlayer();
                roomPlayer.setRoomId(roomId);
                roomPlayer.setUserId(pg.getUserId());
                roomPlayer.setPosition(pg.getPosition());
                roomPlayer.setIsReady(true); // 游戏已创建的玩家默认准备
                roomPlayerMapper.insert(roomPlayer);
            }
        }
        
        // 更新房间的玩家数量
        int newPlayerCount = roomPlayerMapper.countByRoomId(roomId);
        room.setCurrentPlayerCount(newPlayerCount);
        room.setUpdateTime(new Date());
        roomMapper.updateById(room);
        
        logger.info("成功将" + newPlayerCount + "名玩家迁移到房间" + roomId + "的room_player表");
    }
} 