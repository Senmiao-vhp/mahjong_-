package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.entity.UserDTO;
import com.example.hello.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 游客登录
     */
    @PostMapping("/guest")
    public Result<UserDTO> guestLogin() {
        UserDTO userDTO = userService.createGuestUser();
        return Result.success(userDTO);
    }

    /**
     * 检查昵称是否可用
     */
    @GetMapping("/check-nickname/{nickname}")
    public Result<Boolean> checkNickname(@PathVariable String nickname) {
        boolean available = userService.checkNicknameAvailable(nickname);
        return Result.success(available);
    }

    /**
     * 注册新用户
     */
    @PostMapping
    public Result<UserDTO> register(@RequestBody Map<String, String> params) {
        String nickname = params.get("nickname");

        // 参数校验
        if (nickname == null || nickname.trim().isEmpty()) {
            return Result.error(400, "昵称不能为空");
        }

        if (nickname.length() > 8) {
            return Result.error(400, "昵称最多8个字符");
        }

        // 检查昵称是否可用
        if (!userService.checkNicknameAvailable(nickname)) {
            return Result.error(409, "昵称已被占用");
        }

        // 注册用户
        UserDTO userDTO = userService.registerUser(nickname);
        return Result.success(userDTO);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<UserDTO> login(@RequestBody Map<String, Object> params) {
        // 参数校验
        if (!params.containsKey("id")) {
            return Result.error(400, "用户ID不能为空");
        }

        Long id;
        try {
            id = Long.valueOf(params.get("id").toString());
        } catch (NumberFormatException e) {
            return Result.error(400, "用户ID格式错误");
        }

        // 登录
        UserDTO userDTO = userService.login(id);
        if (userDTO == null) {
            return Result.error(404, "用户不存在或已被禁用");
        }

        return Result.success(userDTO);
    }
}