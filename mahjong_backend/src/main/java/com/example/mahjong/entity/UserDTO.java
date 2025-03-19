package com.example.mahjong.entity;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String nickname;
    private String token;
}