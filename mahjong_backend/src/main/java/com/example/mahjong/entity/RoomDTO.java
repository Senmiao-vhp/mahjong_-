package com.example.mahjong.entity;

import lombok.Data;
import java.util.Date;

@Data
public class RoomDTO {
    private Long id;
    private String roomName;
    private Integer roomType;
    private Integer gameType;
    private Boolean hasRedDora;
    private Boolean hasOpenTanyao;
    private Integer status;
    private Integer maxPlayerCount;
    private Integer currentPlayerCount;
    private Long creatorId;
    private String creatorName;
    private Date createTime;
} 