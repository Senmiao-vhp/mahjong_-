package com.example.mahjong.mapper;

import com.example.mahjong.entity.Game;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface GameMapper {
    
    @Insert("INSERT INTO game (room_id, state, current_player_id, round_wind, honba, riichi_sticks) " +
            "VALUES (#{roomId}, #{state}, #{currentPlayerId}, #{roundWind}, #{honba}, #{riichiSticks})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Game game);
    
    @Select("SELECT * FROM game WHERE id = #{id}")
    Game selectById(Long id);
    
    @Select("SELECT * FROM game WHERE room_id = #{roomId}")
    List<Game> selectByRoomId(Long roomId);
    
    @Update("UPDATE game SET state = #{state}, current_player_id = #{currentPlayerId}, " +
            "round_wind = #{roundWind}, honba = #{honba}, riichi_sticks = #{riichiSticks}, " +
            "dealer_position = #{dealerPosition}, current_position = #{currentPosition}, " +
            "wall_position = #{wallPosition}, wall_tiles = #{wallTiles}, " +
            "dora_indicators = #{doraIndicators}, ura_dora_indicators = #{uraDoraIndicators}, " +
            "kan_dora_indicators = #{kanDoraIndicators}, start_time = #{startTime}, " +
            "end_time = #{endTime} WHERE id = #{id}")
    void updateById(Game game);
    
    @Delete("DELETE FROM game WHERE id = #{id}")
    void delete(Long id);
} 