package com.example.mahjong.entity;

import lombok.Data;

@Data
public class Yaku {
    private String name;
    private int han;
    private int points;
    
    public Yaku(String name, int han) {
        this.name = name;
        this.han = han;
        this.points = han * 2; // 默认点数为番数的2倍
    }
    
    public int getPoints() {
        return points;
    }
    
    public void setPoints(int points) {
        this.points = points;
    }
} 