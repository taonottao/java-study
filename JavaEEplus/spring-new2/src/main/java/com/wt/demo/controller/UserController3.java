package com.wt.demo.controller;

import com.wt.demo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * @version 2.0
 * @Author 李四
 * @Date 2023/10/8 15:59
 */
@Controller
public class UserController3 {
    @Autowired
    private User user;

    public void doMethod() {
        System.out.println("userController3 ->" + user);
    }
}
