package com.example.mahjong.mapper;

import com.example.mahjong.entity.PlayerGame;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PlayerGameMapper {
    
    @Insert("INSERT INTO player_game (game_id, user_id, position, is_dealer, score, is_riichi, " +
            "is_double_riichi, is_ippatsu, is_furiten, is_temporary_furiten, is_menzen) " +
            "VALUES (#{gameId}, #{userId}, #{position}, #{isDealer}, #{score}, #{isRiichi}, " +
            "#{isDoubleRiichi}, #{isIppatsu}, #{isFuriten}, #{isTemporaryFuriten}, #{isMenzen})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(PlayerGame playerGame);
    
    @Select("SELECT * FROM player_game WHERE game_id = #{gameId}")
    List<PlayerGame> selectByGameId(Long gameId);
    
    @Select("SELECT * FROM player_game WHERE game_id = #{gameId} AND user_id = #{userId}")
    PlayerGame selectByGameIdAndUserId(Long gameId, Long userId);
    
    @Update("UPDATE player_game SET score = #{score}, is_riichi = #{isRiichi}, " +
            "is_double_riichi = #{isDoubleRiichi}, is_ippatsu = #{isIppatsu}, " +
            "is_furiten = #{isFuriten}, is_temporary_furiten = #{isTemporaryFuriten}, " +
            "is_menzen = #{isMenzen}, update_time = NOW() " +
            "WHERE id = #{id}")
    void updateById(PlayerGame playerGame);
    
    @Delete("DELETE FROM player_game WHERE id = #{id}")
    void delete(Long id);
    
    /**
     * 获取玩家手牌
     */
    @Select("SELECT h.tiles FROM hand h " +
            "JOIN player_game pg ON h.player_game_id = pg.id " +
            "WHERE pg.game_id = #{gameId} AND pg.user_id = #{userId}")
    String getPlayerHandTiles(@Param("gameId") Long gameId, @Param("userId") Long userId);
    
    /**
     * 更新玩家手牌
     */
    @Update("UPDATE hand SET tiles = #{tiles,jdbcType=VARCHAR}, update_time = NOW() " +
            "WHERE player_game_id = (SELECT id FROM player_game WHERE game_id = #{gameId} AND user_id = #{userId})")
    void updatePlayerHandTiles(@Param("gameId") Long gameId, @Param("userId") Long userId, @Param("tiles") String tiles);
    
    /**
     * 获取玩家牌河
     */
    @Select("SELECT d.tiles FROM discard_pile d " +
            "JOIN player_game pg ON d.player_game_id = pg.id " +
            "WHERE pg.game_id = #{gameId} AND pg.user_id = #{userId}")
    String getPlayerDiscardTiles(@Param("gameId") Long gameId, @Param("userId") Long userId);
    
    /**
     * 更新玩家牌河
     */
    @Update("UPDATE discard_pile SET tiles = #{tiles,jdbcType=VARCHAR}, update_time = NOW() " +
            "WHERE player_game_id = (SELECT id FROM player_game WHERE game_id = #{gameId} AND user_id = #{userId})")
    void updatePlayerDiscardTiles(@Param("gameId") Long gameId, @Param("userId") Long userId, @Param("tiles") String tiles);
    
    /**
     * 获取玩家副露
     */
    @Select("SELECT m.* FROM meld m " +
            "JOIN player_game pg ON m.player_game_id = pg.id " +
            "WHERE pg.game_id = #{gameId} AND pg.user_id = #{userId}")
    List<String> getPlayerMelds(@Param("gameId") Long gameId, @Param("userId") Long userId);
    
    /**
     * 添加玩家副露
     */
    @Insert("INSERT INTO meld (player_game_id, meld_type, tiles, from_position, called_tile) " +
            "VALUES ((SELECT id FROM player_game WHERE game_id = #{gameId} AND user_id = #{userId}), " +
            "#{meldType}, #{tiles,jdbcType=VARCHAR}, #{fromPosition}, #{calledTile})")
    void addPlayerMeld(@Param("gameId") Long gameId, @Param("userId") Long userId, 
                       @Param("meldType") Integer meldType, @Param("tiles") String tiles, 
                       @Param("fromPosition") Integer fromPosition, @Param("calledTile") String calledTile);
    
    /**
     * 初始化玩家手牌
     */
    @Insert("INSERT INTO hand (player_game_id, tiles) " +
            "VALUES ((SELECT id FROM player_game WHERE game_id = #{gameId} AND user_id = #{userId}), " +
            "#{tiles,jdbcType=VARCHAR})")
    void initPlayerHandTiles(@Param("gameId") Long gameId, @Param("userId") Long userId, @Param("tiles") String tiles);
    
    /**
     * 初始化玩家牌河
     */
    @Insert("INSERT INTO discard_pile (player_game_id, tiles) " +
            "VALUES ((SELECT id FROM player_game WHERE game_id = #{gameId} AND user_id = #{userId}), " +
            "'[]')")
    void initPlayerDiscardPile(@Param("gameId") Long gameId, @Param("userId") Long userId);
} 