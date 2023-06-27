package com.example.demo.entity.vo;

import com.example.demo.entity.Userinfo;
import lombok.Data;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/26 15:10
 */
@Data
public class UserinfoVO extends Userinfo {
    private Integer artCount; // 文章总数
}
