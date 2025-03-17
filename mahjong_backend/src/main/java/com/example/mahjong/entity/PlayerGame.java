package com.example.mahjong.entity;

import lombok.Data;
import java.util.Date;

@Data
public class PlayerGame {
    private Long id;
    private Long gameId;
    private Long userId;
    private Integer position;
    private Boolean isDealer;
    private Integer score;
    private Boolean isRiichi;
    private Boolean isDoubleRiichi;
    private Boolean isIppatsu;
    private Boolean isFuriten;
    private Boolean isTemporaryFuriten;
    private Boolean isMenzen;
    private Date createTime;
    private Date updateTime;
} 