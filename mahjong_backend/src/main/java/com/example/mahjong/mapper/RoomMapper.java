package com.example.mahjong.mapper;

import org.apache.ibatis.annotations.*;

import com.example.mahjong.entity.Room;

import java.util.List;

@Mapper
public interface RoomMapper {

    /**
     * 创建房间
     */
    @Insert("INSERT INTO room(room_name, room_type, game_type, has_red_dora, has_open_tanyao, " +
            "status, max_player_count, current_player_count, creator_id, create_time, update_time) " +
            "VALUES(#{roomName}, #{roomType}, #{gameType}, #{hasRedDora}, #{hasOpenTanyao}, " +
            "#{status}, #{maxPlayerCount}, #{currentPlayerCount}, #{creatorId}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Room room);

    /**
     * 根据ID查询房间
     */
    @Select("SELECT * FROM room WHERE id = #{id}")
    Room selectById(Long id);

    /**
     * 根据状态查询房间列表
     */
    @Select("<script>" +
            "SELECT * FROM room " +
            "<if test='status != null'>WHERE status = #{status}</if> " +
            "ORDER BY create_time DESC " +
            "LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<Room> selectByStatus(@Param("status") Integer status, @Param("offset") int offset, @Param("pageSize") int pageSize);

    /**
     * 根据状态统计房间数量
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM room " +
            "<if test='status != null'>WHERE status = #{status}</if>" +
            "</script>")
    int countByStatus(@Param("status") Integer status);

    /**
     * 更新房间信息
     */
    @Update("UPDATE room SET status = #{status}, current_player_count = #{currentPlayerCount}, " +
            "update_time = #{updateTime} WHERE id = #{id}")
    int updateById(Room room);
} 