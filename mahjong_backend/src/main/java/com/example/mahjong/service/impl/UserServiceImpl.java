package com.example.mahjong.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mahjong.common.JwtUtil;
import com.example.mahjong.entity.User;
import com.example.mahjong.entity.UserDTO;
import com.example.mahjong.mapper.UserMapper;
import com.example.mahjong.service.UserService;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public UserDTO createGuestUser() {
        // 创建游客用户
        User user = new User();
        user.setIsGuest(true);
        user.setCreateTime(LocalDateTime.now());
        user.setLastLoginTime(LocalDateTime.now());
        user.setStatus(1);

        // 先设置一个临时昵称
        String tempNickname = "游客" + UUID.randomUUID().toString().substring(0, 8);
        user.setNickname(tempNickname);

        // 插入数据库
        userMapper.insert(user);

        // 设置正式的游客昵称（使用ID）
        String guestNickname = "游客" + user.getId();
        user.setNickname(guestNickname);

        // 更新数据库中的昵称
        userMapper.updateById(user);

        // 生成JWT令牌
        String token = jwtUtil.generateToken(user);

        // 构建返回对象
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setNickname(guestNickname);
        userDTO.setToken(token);

        return userDTO;
    }

    @Override
    public boolean checkNicknameAvailable(String nickname) {
        // 检查昵称是否已被占用
        User existingUser = userMapper.selectByNickname(nickname);
        return existingUser == null;
    }

    @Override
    public UserDTO registerUser(String nickname) {
        // 检查昵称是否已被占用
        if (!checkNicknameAvailable(nickname)) {
            return null;
        }

        // 创建新用户
        User user = new User();
        user.setNickname(nickname);
        user.setIsGuest(false);
        user.setCreateTime(LocalDateTime.now());
        user.setLastLoginTime(LocalDateTime.now());
        user.setStatus(1);

        // 插入数据库
        userMapper.insert(user);

        // 生成JWT令牌
        String token = jwtUtil.generateToken(user);

        // 构建返回对象
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setNickname(nickname);
        userDTO.setToken(token);

        return userDTO;
    }

    @Override
    public UserDTO login(Long id) {
        // 查询用户
        User user = userMapper.selectById(id);
        if (user == null || !Objects.equals(user.getStatus(), 1)) {
            return null;
        }

        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);

        // 生成JWT令牌
        String token = jwtUtil.generateToken(user);

        // 构建返回对象
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setNickname(user.getNickname());
        userDTO.setToken(token);

        return userDTO;
    }

    @Override
    @Transactional
    public User createUser(User user) {
        userMapper.insert(user);
        return user;
    }

    @Override
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public User getUserByNickname(String nickname) {
        return userMapper.selectByNickname(nickname);
    }

    @Override
    @Transactional
    public boolean updateUser(User user) {
        return userMapper.updateById(user) > 0;
    }
}