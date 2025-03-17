package com.example.mahjong.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Game {
    private Long id;
    private Long roomId;
    private Integer roundWind;
    private Integer dealerPosition;
    private Integer currentPosition;
    private Integer honbaCount;
    private Integer riichiStickCount;
    private String wallTiles;
    private String doraIndicators;
    private String uraDoraIndicators;
    private String kanDoraIndicators;
    private Integer wallPosition;
    private Integer status;
    private Date startTime;
    private Date endTime;
    private Date createTime;
    private Date updateTime;
} 