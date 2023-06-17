package com.demo.controller;

import com.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * @version 1.0
 * @Author 李四
 * @Date 2023/6/16 18:57
 */
@Controller
public class UserController {

    @Autowired
    private User user1;

    public void getUser(){
        System.out.println("user1: " + user1);
        User u = user1;
        u.setName("李四");
        System.out.println("u: " + u);
    }

}
