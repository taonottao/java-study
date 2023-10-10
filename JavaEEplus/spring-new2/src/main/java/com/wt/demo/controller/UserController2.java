package com.wt.demo.controller;

import com.wt.demo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * @version 1.0
 * @Author 张三
 * @Date 2023/10/8 15:59
 */
@Controller
public class UserController2 {

    @Autowired
    private User user;

    public void doMethod() {
        User user1 = user;
        System.out.println("userController2 修改前 ->" + user);
        user1.setName("皮卡丘");
        System.out.println("userController2 修改后 ->" + user);
    }

}
