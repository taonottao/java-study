package com.demo.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/16 17:18
 */

//@Setter
//@Getter
@Data
public class User {
    private int id;
    private String name;
    private String password;
}