package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/19 20:57
 */
@Controller
@RequestMapping("/resp")
public class RespController {

    @RequestMapping("/hi")
    public String sayHi(){
        return "/index.html";
    }
}
