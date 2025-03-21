package com.example.mahjong.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.mahjong.entity.Room;
import com.example.mahjong.entity.RoomDTO;
import com.example.mahjong.service.RoomService;
import com.example.mahjong.common.JwtUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Tag(name = "房间管理", description = "提供房间的创建、加入、准备、开始游戏等操作")
@RestController
@RequestMapping("/rooms")
public class RoomController {

    // 添加日志记录器
    private static final Logger logger = Logger.getLogger(RoomController.class.getName());

    @Autowired
    private RoomService roomService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 创建房间
     */
    @Operation(summary = "创建新房间", description = "创建一个新的麻将游戏房间")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "创建成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要先登录"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping
    public ResponseEntity<Map<String, Object>> createRoom(
            @Parameter(description = "房间参数", required = true) @RequestBody Map<String, Object> params, 
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("收到创建房间请求: " + params);
            
            // 从请求上下文中获取用户ID
            Long userId = (Long) request.getAttribute("userId");
            logger.info("从请求属性中获取的用户ID: " + userId);
            
            // 检查请求头中的Authorization
            String authHeader = request.getHeader("Authorization");
            logger.info("Authorization头: " + (authHeader != null ? authHeader.substring(0, Math.min(20, authHeader.length())) + "..." : "null"));
            
            // 如果userId为null，尝试从Authorization头中提取
            if (userId == null && authHeader != null && authHeader.startsWith("Bearer ")) {
                logger.info("尝试从Authorization头中直接提取用户ID");
                
                try {
                    // 提取token内容
                    String token = authHeader.substring(7);
                    
                    // 使用JwtUtil解析token
                    userId = jwtUtil.getUserIdFromToken(token);
                    logger.info("从Authorization头中提取的用户ID: " + userId);
                    
                    // 如果成功提取到userId，将其存储到请求属性中
                    if (userId != null) {
                        request.setAttribute("userId", userId);
                        logger.info("已将用户ID存储到请求属性中: " + userId);
                    }
                } catch (Exception e) {
                    logger.severe("从Authorization头中提取用户ID失败: " + e.getMessage());
                }
            }
            
            // 检查userId是否为null
            if (userId == null) {
                logger.warning("用户ID为null，认证失败");
                response.put("code", 401);
                response.put("msg", "未授权，请先登录");
                response.put("data", null);
                
                return ResponseEntity.status(401).body(response);
            }
            
            // 从请求参数中获取房间信息
            Room room = new Room();
            room.setRoomName((String) params.get("room_name"));
            logger.info("房间名称: " + room.getRoomName());
            
            // 设置可选参数
            if (params.containsKey("room_type")) {
                room.setRoomType((Integer) params.get("room_type"));
                logger.info("房间类型: " + room.getRoomType());
            }
            if (params.containsKey("game_type")) {
                room.setGameType((Integer) params.get("game_type"));
                logger.info("游戏类型: " + room.getGameType());
            }
            if (params.containsKey("has_red_dora")) {
                room.setHasRedDora((Boolean) params.get("has_red_dora"));
                logger.info("是否有赤宝牌: " + room.getHasRedDora());
            }
            if (params.containsKey("has_open_tanyao")) {
                room.setHasOpenTanyao((Boolean) params.get("has_open_tanyao"));
                logger.info("是否有食断: " + room.getHasOpenTanyao());
            }
            
            // 设置创建者ID
            room.setCreatorId(userId);
            logger.info("设置创建者ID: " + userId);
            
            // 创建房间
            logger.info("调用roomService.createRoom方法");
            RoomDTO roomDTO = roomService.createRoom(room);
            logger.info("房间创建成功，ID: " + roomDTO.getId());
            
            // 构建响应
            response.put("code", 200);
            response.put("msg", "成功");
            response.put("data", roomDTO);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.severe("创建房间失败: " + e.getMessage());
            e.printStackTrace();
            
            response.put("code", 500);
            response.put("msg", "创建房间失败：" + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取房间列表
     */
    @Operation(summary = "获取房间列表", description = "分页获取符合条件的房间列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping
    public ResponseEntity<Map<String, Object>> getRoomList(
            @Parameter(description = "房间状态(0:等待中, 1:游戏中)", example = "0") 
            @RequestParam(required = false) Integer status,
            @Parameter(description = "页码", example = "1") 
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量", example = "10") 
            @RequestParam(defaultValue = "10") int page_size) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("收到获取房间列表请求: status=" + status + ", page=" + page + ", page_size=" + page_size);
            
            // 获取房间列表
            Map<String, Object> roomList = roomService.getRoomList(status, page, page_size);
            logger.info("成功获取房间列表，总数: " + (roomList.containsKey("total") ? roomList.get("total") : "未知"));
            
            // 构建响应
            response.put("code", 200);
            response.put("msg", "成功");
            response.put("data", roomList);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.severe("获取房间列表失败: " + e.getMessage());
            e.printStackTrace();
            
            response.put("code", 500);
            response.put("msg", "获取房间列表失败：" + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取房间信息
     */
    @Operation(summary = "获取房间详情", description = "根据房间ID获取房间详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "404", description = "房间不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getRoomById(
            @Parameter(description = "房间ID", required = true, example = "1") 
            @PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 获取房间信息
            Room room = roomService.getRoomById(id);
            
            if (room == null) {
                response.put("code", 404);
                response.put("msg", "房间不存在");
                response.put("data", null);
                
                return ResponseEntity.status(404).body(response);
            }
            
            // 构建响应
            response.put("code", 200);
            response.put("msg", "成功");
            response.put("data", room);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("msg", "获取房间信息失败：" + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 加入房间
     */
    @Operation(summary = "加入房间", description = "玩家加入指定ID的房间")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "加入成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要先登录"),
        @ApiResponse(responseCode = "404", description = "房间不存在"),
        @ApiResponse(responseCode = "409", description = "房间已满或状态不允许加入"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/{roomId}/join")
    public ResponseEntity<Map<String, Object>> joinRoom(
            @Parameter(description = "房间ID", required = true, example = "1") 
            @PathVariable Long roomId, 
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 从请求上下文中获取用户ID
            Long userId = (Long) request.getAttribute("userId");
            
            // 加入房间
            Map<String, Object> result = roomService.joinRoom(roomId, userId);
            
            if (result == null) {
                response.put("code", 409);
                response.put("msg", "加入房间失败，房间可能不存在、已满或已开始游戏");
                response.put("data", null);
                
                return ResponseEntity.status(409).body(response);
            }
            
            // 构建响应
            response.put("code", 200);
            response.put("msg", "成功");
            response.put("data", result);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("msg", "加入房间失败：" + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 准备/取消准备
     */
    @Operation(summary = "更新准备状态", description = "玩家在房间中更新准备状态")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要先登录"),
        @ApiResponse(responseCode = "404", description = "房间不存在"),
        @ApiResponse(responseCode = "409", description = "房间状态不允许更改准备状态"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/{roomId}/ready")
    public ResponseEntity<Map<String, Object>> readyInRoom(
            @Parameter(description = "房间ID", required = true, example = "1") 
            @PathVariable Long roomId,
            @Parameter(description = "准备状态参数", required = true) 
            @RequestBody Map<String, Object> params,
            HttpServletRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 从请求上下文中获取用户ID
            Long userId = (Long) request.getAttribute("userId");
            
            // 获取准备状态
            Boolean isReady = (Boolean) params.get("is_ready");
            
            // 更新准备状态
            boolean result = roomService.readyInRoom(roomId, userId, isReady);
            
            if (!result) {
                response.put("code", 409);
                response.put("msg", "准备失败，房间可能不存在或已开始游戏");
                response.put("data", null);
                
                return ResponseEntity.status(409).body(response);
            }
            
            // 构建响应
            response.put("code", 200);
            response.put("msg", "成功");
            response.put("data", new HashMap<>());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("msg", "准备失败：" + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 开始游戏
     */
    @Operation(summary = "开始游戏", description = "房主开始游戏，所有玩家必须已准备就绪")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "游戏开始成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要先登录"),
        @ApiResponse(responseCode = "403", description = "权限不足，只有房主能开始游戏"),
        @ApiResponse(responseCode = "404", description = "房间不存在"),
        @ApiResponse(responseCode = "409", description = "房间状态不允许开始游戏或玩家未准备完成"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/{roomId}/start")
    public ResponseEntity<Map<String, Object>> startGame(
            @Parameter(description = "房间ID", required = true, example = "1") 
            @PathVariable Long roomId, 
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 从请求上下文中获取用户ID
            Long userId = (Long) request.getAttribute("userId");
            
            // 开始游戏
            Long gameId = roomService.startGame(roomId);
            
            if (gameId == null) {
                response.put("code", 409);
                response.put("msg", "开始游戏失败，房间可能不存在、玩家不足或未全部准备");
                response.put("data", null);
                
                return ResponseEntity.status(409).body(response);
            }
            
            // 构建响应
            Map<String, Object> data = new HashMap<>();
            data.put("game_id", gameId);
            
            response.put("code", 200);
            response.put("msg", "成功");
            response.put("data", data);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("msg", "开始游戏失败：" + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取房间内玩家列表
     */
    @Operation(summary = "获取房间玩家", description = "获取指定房间内的所有玩家信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "404", description = "房间不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/{roomId}/players")
    public ResponseEntity<Map<String, Object>> getPlayersInRoom(
            @Parameter(description = "房间ID", required = true, example = "1") 
            @PathVariable Long roomId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 获取房间内的玩家
            List<Map<String, Object>> players = roomService.getPlayersInRoom(roomId);
            
            // 构建响应
            response.put("code", 200);
            response.put("msg", "成功");
            response.put("data", players);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("msg", "获取房间玩家失败：" + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.status(500).body(response);
        }
    }
} 