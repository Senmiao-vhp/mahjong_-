package com.example.hello.service;

import com.example.hello.entity.User;
import com.example.hello.entity.UserDTO;

public interface UserService {

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

    /**
     * 获取用户信息
     */
    UserDTO getUserInfo(Long id);
}