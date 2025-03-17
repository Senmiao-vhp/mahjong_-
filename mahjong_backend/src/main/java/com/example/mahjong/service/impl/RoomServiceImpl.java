package com.example.mahjong.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mahjong.entity.Game;
import com.example.mahjong.entity.Room;
import com.example.mahjong.entity.RoomDTO;
import com.example.mahjong.mapper.RoomMapper;
import com.example.mahjong.service.GameService;
import com.example.mahjong.service.RoomService;

import java.util.*;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomMapper roomMapper;
    
    @Autowired
    private GameService gameService;

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
        
        // 这里应该从用户服务获取创建者昵称，暂时使用ID代替
        roomDTO.setCreatorName("用户" + room.getCreatorId());
        
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
            
            // 这里应该从用户服务获取创建者昵称，暂时使用ID代替
            roomMap.put("creator_name", "用户" + room.getCreatorId());
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
        
        // 获取房间信息
        Room room = roomMapper.selectById(roomId);
        if (room == null) {
            return null; // 房间不存在
        }
        
        // 检查房间是否已满
        if (room.getCurrentPlayerCount() >= room.getMaxPlayerCount()) {
            return null; // 房间已满
        }
        
        // 检查房间状态
        if (room.getStatus() != 0) {
            return null; // 房间不在等待状态
        }
        
        // 更新房间人数
        room.setCurrentPlayerCount(room.getCurrentPlayerCount() + 1);
        room.setUpdateTime(new Date());
        roomMapper.updateById(room);
        
        // 分配座位位置（这里简化处理，实际应该检查已有玩家的位置）
        int position = room.getCurrentPlayerCount() - 1;
        
        // 获取房间内所有玩家信息
        List<Map<String, Object>> players = getPlayersInRoom(roomId);
        
        result.put("room_id", roomId);
        result.put("position", position);
        result.put("players", players);
        
        return result;
    }

    @Override
    public boolean readyInRoom(Long roomId, Long userId, boolean isReady) {
        // 这里应该更新玩家在房间中的准备状态
        // 由于我们没有定义房间玩家关系表，这里简化处理
        return true;
    }

    @Override
    public List<Map<String, Object>> getPlayersInRoom(Long roomId) {
        // 这里应该查询房间玩家关系表获取所有玩家
        // 由于我们没有定义该表，这里模拟一些数据
        List<Map<String, Object>> players = new ArrayList<>();
        
        Room room = roomMapper.selectById(roomId);
        if (room == null) {
            return players;
        }
        
        // 模拟房主信息
        Map<String, Object> creator = new HashMap<>();
        creator.put("user_id", room.getCreatorId());
        creator.put("nickname", "用户" + room.getCreatorId());
        creator.put("position", 0);
        creator.put("is_ready", true);
        players.add(creator);
        
        // 如果有其他玩家，模拟其他玩家信息
        for (int i = 1; i < room.getCurrentPlayerCount(); i++) {
            Map<String, Object> player = new HashMap<>();
            long userId = room.getCreatorId() + i; // 模拟用户ID
            player.put("user_id", userId);
            player.put("nickname", "用户" + userId);
            player.put("position", i);
            player.put("is_ready", i % 2 == 0); // 随机准备状态
            players.add(player);
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
        
        // 检查玩家数量
        if (room.getCurrentPlayerCount() < 4) {
            return null; // 玩家不足4人
        }
        
        // 检查玩家准备状态（这里简化处理，实际应该检查每个玩家）
        
        // 更新房间状态
        room.setStatus(1); // 游戏中
        room.setUpdateTime(new Date());
        roomMapper.updateById(room);
        
        // 创建游戏
        Game game = gameService.createGame(roomId);
        
        return game.getId();
    }
} 