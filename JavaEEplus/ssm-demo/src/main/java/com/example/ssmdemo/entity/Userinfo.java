package com.example.ssmdemo.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/20 23:22
 */
@Data
public class Userinfo implements Serializable {
    private final  long serializableId = 1l;
    private int id;
    private String username;
    private String password;
    private String photo;
    private LocalDateTime createtime;
    private LocalDateTime updatetime;
    private int state;
}
