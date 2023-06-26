package com.example.demo.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/25 23:53
 */
@Data
public class Userinfo {
//    private int id;
    private Integer id;
    private String username;
    private String password;
    private String photo;
    private LocalDateTime createtime;
    private LocalDateTime updatetime;
    private Integer state;

}
