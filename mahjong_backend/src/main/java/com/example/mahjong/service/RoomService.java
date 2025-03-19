package com.example.mahjong.service;

import java.util.List;
import java.util.Map;

import com.example.mahjong.entity.Room;
import com.example.mahjong.entity.RoomDTO;

public interface RoomService {

    /**
     * 创建房间
     */
    RoomDTO createRoom(Room room);

    /**
     * 获取房间信息
     */
    Room getRoomById(Long id);

    /**
     * 获取房间列表
     */
    Map<String, Object> getRoomList(Integer status, int page, int pageSize);

    /**
     * 加入房间
     */
    Map<String, Object> joinRoom(Long roomId, Long userId);

    /**
     * 准备/取消准备
     */
    boolean readyInRoom(Long roomId, Long userId, boolean isReady);

    /**
     * 获取房间内的玩家
     */
    List<Map<String, Object>> getPlayersInRoom(Long roomId);

    /**
     * 开始游戏
     */
    Long startGame(Long roomId);
} 