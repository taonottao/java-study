package com.demo.component;

import org.springframework.stereotype.Controller;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/14 23:37
 */
@Controller
public class BController {
    public String sayF(){
        return "F, Controller";
    }
}
