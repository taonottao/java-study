package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/24 8:11
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @RequestMapping("/eat")
    public String eat(String food){
        System.out.println("执行了 eat 方法");
        return "吃" + food;
    }

    @RequestMapping("/play")
    public String playGame(){
        System.out.println("执行了 play 方法");
        return "玩游戏";
    }

}
