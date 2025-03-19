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
import java.util.logging.Logger;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = Logger.getLogger(AuthController.class.getName());

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 响应OPTIONS预检请求
     */
    @RequestMapping(value = "/**", method = RequestMethod.OPTIONS)
    public ResponseEntity<?> handleOptions() {
        logger.info("处理OPTIONS预检请求");
        return ResponseEntity.ok().build();
    }

    /**
     * 使用昵称登录
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, Object> params) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 支持使用ID或昵称登录
            if (params.containsKey("id")) {
                Long userId = Long.valueOf(params.get("id").toString());
                return loginById(userId);
            } else if (params.containsKey("nickname")) {
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
                data.put("id", user.getId());
                data.put("nickname", user.getNickname());
                
                response.put("code", 200);
                response.put("msg", "登录成功");
                response.put("data", data);
                
                return ResponseEntity.ok(response);
            } else {
                response.put("code", 400);
                response.put("msg", "请提供用户ID或昵称");
                response.put("data", null);
                
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            logger.severe("登录失败：" + e.getMessage());
            e.printStackTrace();
            
            response.put("code", 500);
            response.put("msg", "登录失败：" + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 使用ID登录
     */
    private ResponseEntity<Map<String, Object>> loginById(Long userId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 查询用户
            User user = userService.getUserById(userId);
            
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
            data.put("id", user.getId());
            data.put("nickname", user.getNickname());
            
            response.put("code", 200);
            response.put("msg", "登录成功");
            response.put("data", data);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.severe("ID登录失败：" + e.getMessage());
            e.printStackTrace();
            
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
            data.put("id", user.getId());
            data.put("nickname", user.getNickname());
            
            response.put("code", 200);
            response.put("msg", "注册成功");
            response.put("data", data);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.severe("注册失败：" + e.getMessage());
            e.printStackTrace();
            
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
            logger.info("开始处理游客登录请求");
            
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
            logger.info("游客用户创建成功，ID: " + user.getId());
            
            // 生成JWT令牌
            String token = jwtUtil.generateToken(user);
            
            // 构建响应
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("id", user.getId());
            data.put("nickname", user.getNickname());
            
            response.put("code", 200);
            response.put("msg", "游客登录成功");
            response.put("data", data);
            
            logger.info("游客登录成功，正在返回响应");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.severe("游客登录失败：" + e.getMessage());
            e.printStackTrace();
            
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
            logger.severe("检查昵称失败：" + e.getMessage());
            e.printStackTrace();
            
            response.put("code", 500);
            response.put("msg", "检查昵称失败：" + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.status(500).body(response);
        }
    }
} 