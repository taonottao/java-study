package com.wt.demo.controller;

import com.wt.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/10/7 23:28
 */
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    public void add() {
        System.out.println("do userController");
        userService.add();
    }

}
