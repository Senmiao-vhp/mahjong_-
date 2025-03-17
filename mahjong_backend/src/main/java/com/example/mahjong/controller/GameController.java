package com.example.mahjong.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.mahjong.service.GameService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/games")
public class GameController {

    @Autowired
    private GameService gameService;

    /**
     * 获取对局信息
     */
    @GetMapping("/{gameId}")
    public ResponseEntity<Map<String, Object>> getGameById(@PathVariable Long gameId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 获取对局详情
            Map<String, Object> gameDetails = gameService.getGameDetails(gameId);
            
            if (gameDetails == null) {
                response.put("code", 404);
                response.put("msg", "对局不存在");
                response.put("data", null);
                
                return ResponseEntity.status(404).body(response);
            }
            
            // 构建响应
            response.put("code", 200);
            response.put("msg", "成功");
            response.put("data", gameDetails);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("msg", "获取对局信息失败：" + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取玩家手牌
     */
    @GetMapping("/{gameId}/hand")
    public ResponseEntity<Map<String, Object>> getPlayerHand(@PathVariable Long gameId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 从请求上下文中获取用户ID
            Long userId = (Long) request.getAttribute("userId");
            
            // 获取玩家手牌
            Map<String, Object> handInfo = gameService.getPlayerHand(gameId, userId);
            
            if (handInfo == null) {
                response.put("code", 404);
                response.put("msg", "对局不存在或您不是该对局的玩家");
                response.put("data", null);
                
                return ResponseEntity.status(404).body(response);
            }
            
            // 构建响应
            response.put("code", 200);
            response.put("msg", "成功");
            response.put("data", handInfo);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("msg", "获取手牌信息失败：" + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取牌河信息
     */
    @GetMapping("/{gameId}/river")
    public ResponseEntity<Map<String, Object>> getDiscardPiles(@PathVariable Long gameId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 获取牌河信息
            List<Map<String, Object>> discardPiles = gameService.getDiscardPiles(gameId);
            
            // 构建响应
            response.put("code", 200);
            response.put("msg", "成功");
            response.put("data", discardPiles);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("msg", "获取牌河信息失败：" + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 打牌
     */
    @PostMapping("/{gameId}/discard")
    public ResponseEntity<Map<String, Object>> discardTile(
            @PathVariable Long gameId,
            @RequestBody Map<String, Object> params,
            HttpServletRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 从请求上下文中获取用户ID
            Long userId = (Long) request.getAttribute("userId");
            
            // 获取打出的牌
            String tile = (String) params.get("tile");
            
            // 获取是否立直
            Boolean riichi = params.containsKey("riichi") ? (Boolean) params.get("riichi") : false;
            
            // 调用游戏服务的打牌方法
            Map<String, Object> result = gameService.discardTile(gameId, userId, tile, riichi);
            
            if (result == null) {
                response.put("code", 403);
                response.put("msg", "打牌失败，可能不是您的回合或对局不存在");
                response.put("data", null);
                
                return ResponseEntity.status(403).body(response);
            }
            
            // 构建响应
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
     * 吃/碰/杠/和
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
            
            // 获取操作类型
            String actionType = (String) params.get("action_type");
            
            // 获取操作的牌组
            List<String> tiles = (List<String>) params.get("tiles");
            
            // 调用游戏服务的操作方法
            Map<String, Object> result = gameService.performAction(gameId, userId, actionType, tiles);
            
            if (result == null) {
                response.put("code", 403);
                response.put("msg", "操作失败，可能不是您的回合或对局不存在");
                response.put("data", null);
                
                return ResponseEntity.status(403).body(response);
            }
            
            // 构建响应
            response.put("code", 200);
            response.put("msg", "成功");
            response.put("data", result);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("msg", "操作失败：" + e.getMessage());
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
            
            // 获取打出的牌
            String tile = (String) params.get("tile");
            
            // 调用游戏服务的立直方法
            Map<String, Object> result = gameService.riichi(gameId, userId, tile);
            
            if (result == null) {
                response.put("code", 403);
                response.put("msg", "立直失败，可能不是您的回合或对局不存在");
                response.put("data", null);
                
                return ResponseEntity.status(403).body(response);
            }
            
            // 构建响应
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
     * 获取对局结算信息
     */
    @GetMapping("/{gameId}/settlement")
    public ResponseEntity<Map<String, Object>> getGameSettlement(@PathVariable Long gameId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 获取对局结算信息
            Map<String, Object> settlement = gameService.getGameSettlement(gameId);
            
            if (settlement == null) {
                response.put("code", 404);
                response.put("msg", "对局不存在或尚未结束");
                response.put("data", null);
                
                return ResponseEntity.status(404).body(response);
            }
            
            // 构建响应
            response.put("code", 200);
            response.put("msg", "成功");
            response.put("data", settlement);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("msg", "获取对局结算信息失败：" + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取对局操作日志
     */
    @GetMapping("/{gameId}/logs")
    public ResponseEntity<Map<String, Object>> getGameLogs(
            @PathVariable Long gameId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int page_size) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 获取对局操作日志
            Map<String, Object> logs = gameService.getGameLogs(gameId, page, page_size);
            
            if (logs == null) {
                response.put("code", 404);
                response.put("msg", "对局不存在");
                response.put("data", null);
                
                return ResponseEntity.status(404).body(response);
            }
            
            // 构建响应
            response.put("code", 200);
            response.put("msg", "成功");
            response.put("data", logs);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("msg", "获取对局操作日志失败：" + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.status(500).body(response);
        }
    }
} 