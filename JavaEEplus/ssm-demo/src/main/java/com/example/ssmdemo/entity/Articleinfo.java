package com.example.ssmdemo.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/23 8:27
 */
@Data
public class Articleinfo implements Serializable {
    private final  long serializableId = 1l;
    private int id;
    private String title;
    private String content;
    private String createtime;
    private String updatetime;
    private int uid;
    private int rcount;
    private int state;
}
