package com.example.mahjong.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.mahjong.service.impl.RoomServiceImpl;
import com.example.mahjong.common.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * 数据迁移控制器
 * 用于执行系统升级时的数据迁移任务
 * 注意：此控制器仅供管理员使用，生产环境中应当有更严格的权限控制
 */
@RestController
@RequestMapping("/admin/migration")
public class MigrationController {
    
    private static final Logger logger = Logger.getLogger(MigrationController.class.getName());
    
    @Autowired
    private RoomServiceImpl roomService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 迁移指定房间的玩家数据到room_player表
     */
    @PostMapping("/room/{roomId}/players")
    public ResponseEntity<Map<String, Object>> migrateRoomPlayers(
            @PathVariable Long roomId,
            HttpServletRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 从请求上下文中获取用户ID
            Long userId = (Long) request.getAttribute("userId");
            
            // 检查是否为管理员（这里简化处理，实际应该查询用户角色）
            if (userId == null || userId <= 0) {
                response.put("code", 403);
                response.put("msg", "无权执行此操作");
                response.put("data", null);
                
                return ResponseEntity.status(403).body(response);
            }
            
            // 执行迁移
            roomService.migratePlayersToRoomPlayerTable(roomId);
            
            // 构建响应
            response.put("code", 200);
            response.put("msg", "房间玩家数据迁移成功");
            response.put("data", new HashMap<>());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.severe("迁移房间玩家数据失败: " + e.getMessage());
            
            response.put("code", 500);
            response.put("msg", "迁移失败: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 迁移所有房间的玩家数据
     */
    @PostMapping("/all-rooms/players")
    public ResponseEntity<Map<String, Object>> migrateAllRoomPlayers(
            HttpServletRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 从请求上下文中获取用户ID
            Long userId = (Long) request.getAttribute("userId");
            
            // 检查是否为管理员（这里简化处理，实际应该查询用户角色）
            if (userId == null || userId <= 0) {
                response.put("code", 403);
                response.put("msg", "无权执行此操作");
                response.put("data", null);
                
                return ResponseEntity.status(403).body(response);
            }
            
            // 这里应该实现迁移所有房间的逻辑
            // 为避免进行大量工作而导致请求超时，实际项目中应该使用异步任务
            response.put("code", 501);
            response.put("msg", "批量迁移功能尚未实现");
            response.put("data", null);
            
            return ResponseEntity.status(501).body(response);
        } catch (Exception e) {
            logger.severe("迁移所有房间玩家数据失败: " + e.getMessage());
            
            response.put("code", 500);
            response.put("msg", "迁移失败: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.status(500).body(response);
        }
    }
} 