package com.example.mahjong.entity;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

@Data
@Schema(description = "麻将房间信息")
public class Room {
    @Schema(description = "房间ID", example = "1")
    private Long id;
    @Schema(description = "房间名称", example = "欢乐麻将室")
    private String roomName;
    @Schema(description = "房间类型", example = "0", allowableValues = {"0", "1"})
    private Integer roomType;
    @Schema(description = "游戏类型", example = "0", allowableValues = {"0", "1"})
    private Integer gameType;
    @Schema(description = "是否有赤宝牌", example = "true")
    private Boolean hasRedDora;
    @Schema(description = "是否允许食断", example = "true")
    private Boolean hasOpenTanyao;
    @Schema(description = "房间状态", example = "0", allowableValues = {"0", "1", "2"})
    private Integer status;
    @Schema(description = "最大玩家数", example = "4")
    private Integer maxPlayerCount;
    @Schema(description = "当前玩家数", example = "2")
    private Integer currentPlayerCount;
    @Schema(description = "创建者ID", example = "10001")
    private Long creatorId;
    @Schema(description = "创建时间")
    private Date createTime;
    @Schema(description = "更新时间")
    private Date updateTime;
} 