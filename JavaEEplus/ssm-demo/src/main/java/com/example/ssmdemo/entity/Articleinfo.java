package com.example.ssmdemo.entity;

import lombok.Data;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/23 8:27
 */
@Data
public class Articleinfo {
    private int id;
    private String title;
    private String content;
    private String createtime;
    private String updatetime;
    private int uid;
    private int rcount;
    private int state;
}
