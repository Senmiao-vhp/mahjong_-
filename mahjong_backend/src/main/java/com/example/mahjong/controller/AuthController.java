package com.example.mahjong.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.mahjong.common.JwtUtil;
import com.example.mahjong.entity.User;
import com.example.mahjong.service.UserService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, Object> params) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String nickname = (String) params.get("nickname");
            
            // 查询用户
            User user = userService.getUserByNickname(nickname);
            
            // 如果用户不存在，返回错误
            if (user == null) {
                response.put("code", 404);
                response.put("msg", "用户不存在");
                response.put("data", null);
                
                return ResponseEntity.status(404).body(response);
            }
            
            // 更新最后登录时间
            user.setLastLoginTime(LocalDateTime.now());
            userService.updateUser(user);
            
            // 生成JWT令牌
            String token = jwtUtil.generateToken(user);
            
            // 构建响应
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user_id", user.getId());
            data.put("nickname", user.getNickname());
            
            response.put("code", 200);
            response.put("msg", "登录成功");
            response.put("data", data);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("msg", "登录失败：" + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, Object> params) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String nickname = (String) params.get("nickname");
            
            // 检查昵称是否已存在
            User existingUser = userService.getUserByNickname(nickname);
            if (existingUser != null) {
                response.put("code", 409);
                response.put("msg", "昵称已被使用");
                response.put("data", null);
                
                return ResponseEntity.status(409).body(response);
            }
            
            // 创建新用户
            User user = new User();
            user.setNickname(nickname);
            user.setIsGuest(false);
            user.setCreateTime(LocalDateTime.now());
            user.setLastLoginTime(LocalDateTime.now());
            user.setStatus(1);
            
            // 保存用户
            userService.createUser(user);
            
            // 生成JWT令牌
            String token = jwtUtil.generateToken(user);
            
            // 构建响应
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user_id", user.getId());
            data.put("nickname", user.getNickname());
            
            response.put("code", 200);
            response.put("msg", "注册成功");
            response.put("data", data);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("msg", "注册失败：" + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 游客登录
     */
    @PostMapping("/guest")
    public ResponseEntity<Map<String, Object>> guestLogin() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 生成游客昵称
            String nickname = "游客" + System.currentTimeMillis() % 10000;
            
            // 创建游客用户
            User user = new User();
            user.setNickname(nickname);
            user.setIsGuest(true);
            user.setCreateTime(LocalDateTime.now());
            user.setLastLoginTime(LocalDateTime.now());
            user.setStatus(1);
            
            // 保存用户
            userService.createUser(user);
            
            // 生成JWT令牌
            String token = jwtUtil.generateToken(user);
            
            // 构建响应
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user_id", user.getId());
            data.put("nickname", user.getNickname());
            
            response.put("code", 200);
            response.put("msg", "游客登录成功");
            response.put("data", data);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("msg", "游客登录失败：" + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 检查昵称是否可用
     */
    @GetMapping("/check-nickname/{nickname}")
    public ResponseEntity<Map<String, Object>> checkNickname(@PathVariable String nickname) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 检查昵称是否可用
            boolean available = userService.checkNicknameAvailable(nickname);
            
            response.put("code", 200);
            response.put("msg", "成功");
            response.put("data", available);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("msg", "检查昵称失败：" + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.status(500).body(response);
        }
    }
} 