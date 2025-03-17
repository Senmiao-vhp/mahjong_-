package com.example.mahjong.mapper;

import com.example.mahjong.entity.Game;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface GameMapper {
    
    @Insert("INSERT INTO game (room_id, status, round_wind, dealer_position, current_position, " +
            "honba_count, riichi_stick_count, wall_tiles, dora_indicators, ura_dora_indicators, " +
            "kan_dora_indicators, wall_position) " +
            "VALUES (#{roomId}, #{status}, #{roundWind}, #{dealerPosition}, #{currentPosition}, " +
            "#{honbaCount}, #{riichiSticks}, " +
            "#{wallTiles,jdbcType=VARCHAR}, #{doraIndicators,jdbcType=VARCHAR}, " +
            "#{uraDoraIndicators,jdbcType=VARCHAR}, #{kanDoraIndicators,jdbcType=VARCHAR}, " +
            "#{wallPosition})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Game game);
    
    @Select("SELECT * FROM game WHERE id = #{id}")
    Game selectById(Long id);
    
    @Select("SELECT * FROM game WHERE room_id = #{roomId}")
    List<Game> selectByRoomId(Long roomId);
    
    @Update("UPDATE game SET status = #{status}, round_wind = #{roundWind}, " +
            "honba_count = #{honbaCount}, riichi_stick_count = #{riichiSticks}, " +
            "dealer_position = #{dealerPosition}, current_position = #{currentPosition}, " +
            "wall_position = #{wallPosition}, " +
            "wall_tiles = #{wallTiles,jdbcType=VARCHAR}, " +
            "dora_indicators = #{doraIndicators,jdbcType=VARCHAR}, " +
            "ura_dora_indicators = #{uraDoraIndicators,jdbcType=VARCHAR}, " +
            "kan_dora_indicators = #{kanDoraIndicators,jdbcType=VARCHAR}, " +
            "start_time = #{startTime}, end_time = #{endTime}, update_time = NOW() " +
            "WHERE id = #{id}")
    void updateById(Game game);
    
    @Delete("DELETE FROM game WHERE id = #{id}")
    void delete(Long id);
} 