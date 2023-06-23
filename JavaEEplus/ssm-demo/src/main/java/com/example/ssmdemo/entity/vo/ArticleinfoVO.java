package com.example.ssmdemo.entity.vo;

import com.example.ssmdemo.entity.Articleinfo;
import lombok.Data;

import java.io.Serializable;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/23 8:40
 */
@Data
public class ArticleinfoVO extends Articleinfo implements Serializable {

    private final  long serializableId = 1l;

    private String username;
//    private int id;
//    private String title;
//    private String createtime;
//    private String updatetime;
//    private int uid;
//    private int rcount;
//    private int state;


    @Override
    public String toString() {
        return "ArticleinfoVO{" +
                "username='" + username + '\'' +
                "} " + super.toString();
    }
}
