package com.example.hello.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String nickname;
    private Boolean isGuest;
    private LocalDateTime createTime;
    private LocalDateTime lastLoginTime;
    private Integer status;
}