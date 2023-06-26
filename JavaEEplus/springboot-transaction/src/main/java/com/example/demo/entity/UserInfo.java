package com.example.demo.entity;

import lombok.Data;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/25 14:09
 */
@Data
public class UserInfo {
    private int id;
    private String username;
    private String password;
    private String photo;
    private String createtime;
    private String updatetime;
    private int state;
}
