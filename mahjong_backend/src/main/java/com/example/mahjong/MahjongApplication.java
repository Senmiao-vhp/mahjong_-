package com.example.mahjong;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
@MapperScan("com.example.mahjong.mapper")
public class MahjongApplication {

    public static void main(String[] args) {
        SpringApplication.run(MahjongApplication.class, args);
    }
} 