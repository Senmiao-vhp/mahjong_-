package com.example.mahjong.entity;

import lombok.Data;

@Data
public class Yaku {
    private String name;
    private int han;
    
    public Yaku(String name, int han) {
        this.name = name;
        this.han = han;
    }
} 