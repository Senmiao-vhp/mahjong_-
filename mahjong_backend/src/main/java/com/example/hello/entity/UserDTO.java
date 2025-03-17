package com.example.hello.entity;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String nickname;
    private String token;
}