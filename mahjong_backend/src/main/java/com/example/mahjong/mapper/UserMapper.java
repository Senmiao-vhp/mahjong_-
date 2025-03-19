package com.example.mahjong.mapper;

import org.apache.ibatis.annotations.*;

import com.example.mahjong.entity.User;

@Mapper
public interface UserMapper {

    /**
     * 创建用户
     */
    @Insert("INSERT INTO user(nickname, is_guest, create_time, last_login_time, status) " +
            "VALUES(#{nickname}, #{isGuest}, #{createTime}, #{lastLoginTime}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    /**
     * 根据ID查询用户
     */
    @Select("SELECT * FROM user WHERE id = #{id}")
    User selectById(Long id);

    /**
     * 根据昵称查询用户
     */
    @Select("SELECT * FROM user WHERE nickname = #{nickname}")
    User selectByNickname(String nickname);

    /**
     * 更新用户信息
     */
    @Update("UPDATE user SET last_login_time = #{lastLoginTime}, status = #{status} WHERE id = #{id}")
    int updateById(User user);
}