package com.example.mahjong.mapper;

import org.apache.ibatis.annotations.*;

import com.example.mahjong.entity.PlayerGame;

import java.util.List;

@Mapper
public interface PlayerGameMapper {

    /**
     * 创建玩家对局记录
     */
    @Insert("INSERT INTO player_game(game_id, user_id, position, is_dealer, score, is_riichi, " +
            "is_double_riichi, is_ippatsu, is_furiten, is_temporary_furiten, is_menzen, " +
            "create_time, update_time) " +
            "VALUES(#{gameId}, #{userId}, #{position}, #{isDealer}, #{score}, #{isRiichi}, " +
            "#{isDoubleRiichi}, #{isIppatsu}, #{isFuriten}, #{isTemporaryFuriten}, #{isMenzen}, " +
            "#{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PlayerGame playerGame);

    /**
     * 根据ID查询玩家对局记录
     */
    @Select("SELECT * FROM player_game WHERE id = #{id}")
    PlayerGame selectById(Long id);

    /**
     * 根据对局ID查询所有玩家
     */
    @Select("SELECT * FROM player_game WHERE game_id = #{gameId}")
    List<PlayerGame> selectByGameId(Long gameId);

    /**
     * 根据对局ID和用户ID查询玩家对局记录
     */
    @Select("SELECT * FROM player_game WHERE game_id = #{gameId} AND user_id = #{userId}")
    PlayerGame selectByGameIdAndUserId(@Param("gameId") Long gameId, @Param("userId") Long userId);

    /**
     * 根据对局ID和位置查询玩家对局记录
     */
    @Select("SELECT * FROM player_game WHERE game_id = #{gameId} AND position = #{position}")
    PlayerGame selectByGameIdAndPosition(@Param("gameId") Long gameId, @Param("position") Integer position);

    /**
     * 更新玩家对局记录
     */
    @Update("UPDATE player_game SET score = #{score}, is_riichi = #{isRiichi}, " +
            "is_double_riichi = #{isDoubleRiichi}, is_ippatsu = #{isIppatsu}, " +
            "is_furiten = #{isFuriten}, is_temporary_furiten = #{isTemporaryFuriten}, " +
            "is_menzen = #{isMenzen}, update_time = #{updateTime} " +
            "WHERE id = #{id}")
    int updateById(PlayerGame playerGame);
} 