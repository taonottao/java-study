package com.demo.controller;

import com.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

/**
 * @version 1.0
 * @Author 王五
 * @Date 2023/6/16 19:01
 */
@Controller
public class UserAdviceController {

    @Autowired
    private User user1;

    public void getUser(){
        System.out.println("王五 | user1: " + user1);
    }

}
