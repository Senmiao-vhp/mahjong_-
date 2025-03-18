package com.example.mahjong.mapper;

import com.example.mahjong.entity.RoomPlayer;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

/**
 * 房间玩家关系Mapper接口
 */
@Mapper
public interface RoomPlayerMapper {
    
    /**
     * 添加玩家到房间
     */
    @Insert("INSERT INTO room_player (room_id, user_id, position, is_ready) VALUES (#{roomId}, #{userId}, #{position}, #{isReady})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(RoomPlayer roomPlayer);
    
    /**
     * 更新玩家准备状态
     */
    @Update("UPDATE room_player SET is_ready = #{isReady}, update_time = NOW() WHERE room_id = #{roomId} AND user_id = #{userId}")
    int updateReadyStatus(@Param("roomId") Long roomId, @Param("userId") Long userId, @Param("isReady") Boolean isReady);
    
    /**
     * 获取房间中所有玩家
     */
    @Select("SELECT rp.*, u.nickname FROM room_player rp LEFT JOIN user u ON rp.user_id = u.id WHERE rp.room_id = #{roomId}")
    List<Map<String, Object>> selectByRoomId(Long roomId);
    
    /**
     * 根据房间ID获取所有RoomPlayer对象
     */
    @Select("SELECT * FROM room_player WHERE room_id = #{roomId}")
    List<RoomPlayer> getRoomPlayersByRoomId(Long roomId);
    
    /**
     * 获取特定玩家在房间中的信息
     */
    @Select("SELECT * FROM room_player WHERE room_id = #{roomId} AND user_id = #{userId}")
    RoomPlayer selectByRoomIdAndUserId(@Param("roomId") Long roomId, @Param("userId") Long userId);
    
    /**
     * 获取特定位置的玩家
     */
    @Select("SELECT * FROM room_player WHERE room_id = #{roomId} AND position = #{position}")
    RoomPlayer selectByRoomIdAndPosition(@Param("roomId") Long roomId, @Param("position") Integer position);
    
    /**
     * 检查指定位置是否已被占用
     */
    @Select("SELECT COUNT(*) FROM room_player WHERE room_id = #{roomId} AND position = #{position}")
    int countByRoomIdAndPosition(@Param("roomId") Long roomId, @Param("position") Integer position);
    
    /**
     * 获取房间中玩家数量
     */
    @Select("SELECT COUNT(*) FROM room_player WHERE room_id = #{roomId}")
    int countByRoomId(Long roomId);
    
    /**
     * 检查玩家是否已在房间中
     */
    @Select("SELECT COUNT(*) FROM room_player WHERE room_id = #{roomId} AND user_id = #{userId}")
    int isPlayerInRoom(@Param("roomId") Long roomId, @Param("userId") Long userId);
    
    /**
     * 获取玩家在房间中的位置
     */
    @Select("SELECT position FROM room_player WHERE room_id = #{roomId} AND user_id = #{userId}")
    Integer getPlayerPosition(@Param("roomId") Long roomId, @Param("userId") Long userId);
    
    /**
     * 删除玩家从房间
     */
    @Delete("DELETE FROM room_player WHERE room_id = #{roomId} AND user_id = #{userId}")
    void deleteByRoomIdAndUserId(@Param("roomId") Long roomId, @Param("userId") Long userId);
    
    /**
     * 清空房间中所有玩家
     */
    @Delete("DELETE FROM room_player WHERE room_id = #{roomId}")
    void deleteByRoomId(Long roomId);
    
    /**
     * 获取房间中所有准备好的玩家数量
     */
    @Select("SELECT COUNT(*) FROM room_player WHERE room_id = #{roomId} AND is_ready = true")
    int countReadyPlayersByRoomId(Long roomId);
    
    /**
     * 获取房间中所有玩家的准备状态
     */
    @Select("SELECT user_id, is_ready FROM room_player WHERE room_id = #{roomId}")
    List<Map<String, Object>> getPlayersReadyStatus(Long roomId);
    
    /**
     * 更新RoomPlayer对象
     */
    @Update("UPDATE room_player SET position = #{position}, is_ready = #{isReady}, update_time = NOW() WHERE id = #{id}")
    int updateById(RoomPlayer roomPlayer);
} 