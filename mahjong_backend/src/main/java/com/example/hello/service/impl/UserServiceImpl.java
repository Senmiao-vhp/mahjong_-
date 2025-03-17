package com.example.hello.service.impl;

import com.example.hello.common.JwtUtil;
import com.example.hello.entity.User;
import com.example.hello.entity.UserDTO;
import com.example.hello.exception.BusinessException;
import com.example.hello.mapper.UserMapper;
import com.example.hello.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
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
        userMapper.updateLastLoginTime(user);
        
        // 再次查询确保数据已写入
        User savedUser = userMapper.findById(user.getId());
        if (savedUser == null) {
            throw new BusinessException(500, "创建游客账号失败，请重试");
        }

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
        User existingUser = userMapper.findByNickname(nickname);
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
        User user = userMapper.findById(id);
        if (user == null || !Objects.equals(user.getStatus(), 1)) {
            return null;
        }

        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateLastLoginTime(user);

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
    public UserDTO getUserInfo(Long id) {
        // 查询用户
        User user = userMapper.findById(id);
        if (user == null || !Objects.equals(user.getStatus(), 1)) {
            return null;
        }

        // 构建返回对象
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setNickname(user.getNickname());
        
        return userDTO;
    }
}