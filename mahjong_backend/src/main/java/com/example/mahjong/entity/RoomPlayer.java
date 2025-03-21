package com.example.mahjong.entity;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

/**
 * 房间玩家关系实体类
 */
@Data
@Schema(description = "房间玩家关系信息")
public class RoomPlayer {
    /**
     * 主键ID
     */
    @Schema(description = "主键ID", example = "1")
    private Long id;
    
    /**
     * 房间ID
     */
    @Schema(description = "房间ID", example = "100")
    private Long roomId;
    
    /**
     * 用户ID
     */
    @Schema(description = "用户ID", example = "1001")
    private Long userId;
    
    /**
     * 座位位置
     */
    @Schema(description = "座位位置", example = "0")
    private Integer position;
    
    /**
     * 准备状态
     */
    @Schema(description = "准备状态", example = "true")
    private Boolean isReady;
    
    /**
     * 加入时间
     */
    @Schema(description = "加入时间")
    private Date joinTime;
    
    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private Date updateTime;
} 