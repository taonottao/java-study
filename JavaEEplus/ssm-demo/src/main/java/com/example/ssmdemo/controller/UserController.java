package com.example.ssmdemo.controller;

import com.example.ssmdemo.entity.Userinfo;
import com.example.ssmdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/21 23:04
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/byid")
    public Userinfo getUserById(Integer id){
        if(id == null){
            return null;
        }

        return userService.getUserById(id);
    }

}
