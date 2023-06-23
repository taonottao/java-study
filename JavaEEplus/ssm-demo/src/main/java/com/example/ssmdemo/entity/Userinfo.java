package com.example.ssmdemo.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/20 23:22
 */
@Data
public class Userinfo {
    private int id;
    private String name;
    private String password;
    private String photo;
    private LocalDateTime createtime;
    private LocalDateTime updatetime;
    private int state;
}
