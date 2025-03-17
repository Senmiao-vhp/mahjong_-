package com.example.hello.mapper;

import com.example.hello.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    /**
     * 根据ID查询用户
     */
    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Long id);

    /**
     * 根据昵称查询用户
     */
    @Select("SELECT * FROM user WHERE nickname = #{nickname}")
    User findByNickname(String nickname);

    /**
     * 插入新用户
     */
    @Insert("INSERT INTO user(nickname, is_guest, create_time, status) VALUES(#{nickname}, #{isGuest}, #{createTime}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    /**
     * 更新用户最后登录时间
     */
    @Update("UPDATE user SET last_login_time = #{lastLoginTime} WHERE id = #{id}")
    int updateLastLoginTime(User user);
}