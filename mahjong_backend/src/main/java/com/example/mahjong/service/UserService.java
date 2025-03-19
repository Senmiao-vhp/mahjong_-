package com.example.mahjong.service;

import com.example.mahjong.entity.User;
import com.example.mahjong.entity.UserDTO;

public interface UserService {

    /**
     * 创建用户
     */
    User createUser(User user);

    /**
     * 根据ID查询用户
     */
    User getUserById(Long id);

    /**
     * 根据昵称查询用户
     */
    User getUserByNickname(String nickname);

    /**
     * 更新用户信息
     */
    boolean updateUser(User user);

    /**
     * 创建游客账号
     */
    UserDTO createGuestUser();

    /**
     * 检查昵称是否可用
     */
    boolean checkNicknameAvailable(String nickname);

    /**
     * 注册新用户
     */
    UserDTO registerUser(String nickname);

    /**
     * 用户登录
     */
    UserDTO login(Long id);
}