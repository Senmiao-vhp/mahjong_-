package com.example.mahjong.controller;

import com.example.mahjong.entity.*;
import com.example.mahjong.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

@Tag(name = "游戏管理", description = "提供游戏创建、查询、操作等功能")
@RestController
@RequestMapping("/games")
public class GameController {

    @Autowired
    private GameService gameService;

    /**
     * 获取游戏详情
     */
    @Operation(summary = "获取游戏详情", description = "根据游戏ID获取游戏详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "404", description = "游戏不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/{gameId}")
    public ResponseEntity<Map<String, Object>> getGameDetails(
            @Parameter(description = "游戏ID", required = true, example = "1") 
            @PathVariable Long gameId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> gameDetails = gameService.getGameDetails(gameId);
            
            if (gameDetails == null) {
                response.put("code", 404);
                response.put("msg", "游戏不存在");
                response.put("data", null);
                return ResponseEntity.status(404).body(response);
            }
            
            response.put("code", 200);
            response.put("msg", "成功");
            response.put("data", gameDetails);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("msg", "获取游戏详情失败：" + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取玩家手牌
     */
    @Operation(summary = "获取玩家手牌", description = "获取当前玩家在指定游戏中的手牌")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，需要先登录"),
        @ApiResponse(responseCode = "403", description = "权限不足，只能查看自己的手牌"),
        @ApiResponse(responseCode = "404", description = "游戏不存在或玩家不在游戏中"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/{gameId}/hand")
    public ResponseEntity<Map<String, Object>> getPlayerHand(
            @Parameter(description = "游戏ID", required = true, example = "1") 
            @PathVariable Long gameId,
            HttpServletRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 从请求上下文中获取用户ID
            Long userId = (Long) request.getAttribute("userId");
            
            Map<String, Object> handInfo = gameService.getPlayerHand(gameId, userId);
            
            if (handInfo == null) {
                response.put("code", 404);
                response.put("msg", "获取手牌失败");
                response.put("data", null);
                return ResponseEntity.status(404).body(response);
            }
            
            response.put("code", 200);
            response.put("msg", "成功");
            response.put("data", handInfo);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("msg", "获取手牌失败：" + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取所有玩家的弃牌区
     */
    @Operation(summary = "获取弃牌堆", description = "获取指定游戏中所有玩家的弃牌堆")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "404", description = "游戏不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/{gameId}/discards")
    public ResponseEntity<Map<String, Object>> getDiscardPiles(
            @Parameter(description = "游戏ID", required = true, example = "1") 
            @PathVariable Long gameId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Map<String, Object>> discardPiles = gameService.getDiscardPiles(gameId);
            
            if (discardPiles == null) {
                response.put("code", 404);
                response.put("msg", "获取弃牌区失败");
                response.put("data", null);
                return ResponseEntity.status(404).body(response);
            }
            
            response.put("code", 200);
            response.put("msg", "成功");
            response.put("data", discardPiles);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("msg", "获取弃牌区失败：" + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 打出一张牌
     */
    @Operation(summary = "打出牌", description = "玩家在游戏中打出一张牌")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "操作成功"),
        @ApiResponse(responseCode = "400", description = "无效的操作或参数"),
        @ApiResponse(responseCode = "401", description = "未授权，需要先登录"),
        @ApiResponse(responseCode = "403", description = "权限不足，不是当前玩家的回合"),
        @ApiResponse(responseCode = "404", description = "游戏不存在或玩家不在游戏中"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/{gameId}/discard")
    public ResponseEntity<Map<String, Object>> discardTile(
            @Parameter(description = "游戏ID", required = true, example = "1") 
            @PathVariable Long gameId,
            @Parameter(description = "牌信息", required = true) 
            @RequestBody Map<String, Object> params,
            HttpServletRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 从请求上下文中获取用户ID
            Long userId = (Long) request.getAttribute("userId");
            
            // 获取参数
            String tile = (String) params.get("tile");
            Boolean isRiichi = (Boolean) params.get("is_riichi");
            
            if (tile == null) {
                response.put("code", 400);
                response.put("msg", "缺少必要参数");
                response.put("data", null);
                return ResponseEntity.status(400).body(response);
            }
            
            Map<String, Object> result = gameService.discardTile(gameId, userId, tile, isRiichi != null && isRiichi);
            
            if (result == null) {
                response.put("code", 400);
                response.put("msg", "打牌失败");
                response.put("data", null);
                return ResponseEntity.status(400).body(response);
            }
            
            response.put("code", 200);
            response.put("msg", "成功");
            response.put("data", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("msg", "打牌失败：" + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 执行动作（吃、碰、杠等）
     */
    @PostMapping("/{gameId}/action")
    public ResponseEntity<Map<String, Object>> performAction(
            @PathVariable Long gameId,
            @RequestBody Map<String, Object> params,
            HttpServletRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 从请求上下文中获取用户ID
            Long userId = (Long) request.getAttribute("userId");
            
            // 获取参数
            String actionType = (String) params.get("action_type");
            @SuppressWarnings("unchecked")
            List<String> tiles = (List<String>) params.get("tiles");
            
            if (actionType == null || tiles == null) {
                response.put("code", 400);
                response.put("msg", "缺少必要参数");
                response.put("data", null);
                return ResponseEntity.status(400).body(response);
            }
            
            Map<String, Object> result = gameService.performAction(gameId, userId, actionType, tiles);
            
            if (result == null) {
                response.put("code", 400);
                response.put("msg", "执行动作失败");
                response.put("data", null);
                return ResponseEntity.status(400).body(response);
            }
            
            response.put("code", 200);
            response.put("msg", "成功");
            response.put("data", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("msg", "执行动作失败：" + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 立直
     */
    @PostMapping("/{gameId}/riichi")
    public ResponseEntity<Map<String, Object>> riichi(
            @PathVariable Long gameId,
            @RequestBody Map<String, Object> params,
            HttpServletRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 从请求上下文中获取用户ID
            Long userId = (Long) request.getAttribute("userId");
            
            // 获取参数
            String tile = (String) params.get("tile");
            
            if (tile == null) {
                response.put("code", 400);
                response.put("msg", "缺少必要参数");
                response.put("data", null);
                return ResponseEntity.status(400).body(response);
            }
            
            Map<String, Object> result = gameService.riichi(gameId, userId, tile);
            
            if (result == null) {
                response.put("code", 400);
                response.put("msg", "立直失败");
                response.put("data", null);
                return ResponseEntity.status(400).body(response);
            }
            
            response.put("code", 200);
            response.put("msg", "成功");
            response.put("data", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("msg", "立直失败：" + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取游戏结算信息
     */
    @GetMapping("/{gameId}/settlement")
    public ResponseEntity<Map<String, Object>> getGameSettlement(@PathVariable Long gameId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> settlement = gameService.getGameSettlement(gameId);
            
            if (settlement == null) {
                response.put("code", 404);
                response.put("msg", "获取结算信息失败");
                response.put("data", null);
                return ResponseEntity.status(404).body(response);
            }
            
            response.put("code", 200);
            response.put("msg", "成功");
            response.put("data", settlement);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("msg", "获取结算信息失败：" + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取游戏日志
     */
    @GetMapping("/{gameId}/logs")
    public ResponseEntity<Map<String, Object>> getGameLogs(
            @PathVariable Long gameId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Map<String, Object>> logs = gameService.getGameLogs(gameId, page, pageSize);
            
            if (logs == null) {
                response.put("code", 404);
                response.put("msg", "获取游戏日志失败");
                response.put("data", null);
                return ResponseEntity.status(404).body(response);
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("logs", logs);
            data.put("page", page);
            data.put("page_size", pageSize);
            
            response.put("code", 200);
            response.put("msg", "成功");
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("msg", "获取游戏日志失败：" + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }
} 