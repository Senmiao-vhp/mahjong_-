package com.example.mahjong.mapper;

import org.apache.ibatis.annotations.*;

import com.example.mahjong.entity.Game;

@Mapper
public interface GameMapper {

    /**
     * 创建对局
     */
    @Insert("INSERT INTO game(room_id, round_wind, dealer_position, current_position, honba_count, " +
            "riichi_stick_count, wall_tiles, dora_indicators, ura_dora_indicators, kan_dora_indicators, " +
            "wall_position, status, start_time, create_time, update_time) " +
            "VALUES(#{roomId}, #{roundWind}, #{dealerPosition}, #{currentPosition}, #{honbaCount}, " +
            "#{riichiStickCount}, #{wallTiles}, #{doraIndicators}, #{uraDoraIndicators}, #{kanDoraIndicators}, " +
            "#{wallPosition}, #{status}, #{startTime}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Game game);

    /**
     * 根据ID查询对局
     */
    @Select("SELECT * FROM game WHERE id = #{id}")
    Game selectById(Long id);

    /**
     * 根据房间ID查询对局
     */
    @Select("SELECT * FROM game WHERE room_id = #{roomId} ORDER BY create_time DESC LIMIT 1")
    Game selectByRoomId(Long roomId);

    /**
     * 更新对局信息
     */
    @Update("UPDATE game SET current_position = #{currentPosition}, honba_count = #{honbaCount}, " +
            "riichi_stick_count = #{riichiStickCount}, wall_position = #{wallPosition}, " +
            "status = #{status}, end_time = #{endTime}, update_time = #{updateTime} " +
            "WHERE id = #{id}")
    int updateById(Game game);
} 