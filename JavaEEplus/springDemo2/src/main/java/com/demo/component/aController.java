package com.demo.component;

import org.springframework.stereotype.Controller;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/14 23:33
 */
@Controller
public class aController {
    public String sayHi(){
        return "hi, Controller";
    }
}
