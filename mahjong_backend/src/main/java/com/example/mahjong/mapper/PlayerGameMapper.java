package com.example.mahjong.mapper;

import com.example.mahjong.entity.PlayerGame;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PlayerGameMapper {
    
    @Insert("INSERT INTO player_game (game_id, user_id, seat_wind, is_dealer, score, hand_tiles, " +
            "discard_tiles, melds, is_riichi) " +
            "VALUES (#{gameId}, #{userId}, #{seatWind}, #{isDealer}, #{score}, #{handTiles}, " +
            "#{discardTiles}, #{melds}, #{isRiichi})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(PlayerGame playerGame);
    
    @Select("SELECT * FROM player_game WHERE game_id = #{gameId}")
    List<PlayerGame> selectByGameId(Long gameId);
    
    @Select("SELECT * FROM player_game WHERE game_id = #{gameId} AND user_id = #{userId}")
    PlayerGame selectByGameIdAndUserId(Long gameId, Long userId);
    
    @Update("UPDATE player_game SET score = #{score}, hand_tiles = #{handTiles}, " +
            "discard_tiles = #{discardTiles}, melds = #{melds}, is_riichi = #{isRiichi} " +
            "WHERE id = #{id}")
    void updateById(PlayerGame playerGame);
    
    @Delete("DELETE FROM player_game WHERE id = #{id}")
    void delete(Long id);
} 