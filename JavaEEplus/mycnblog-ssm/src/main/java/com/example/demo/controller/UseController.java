package com.example.demo.controller;

import com.example.demo.common.AjaxResult;
import com.example.demo.entity.Userinfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/25 23:47
 */
@RestController
@RequestMapping("/user")
public class UseController {

    @RequestMapping("/reg")
    public AjaxResult reg(Userinfo userinfo){
        // 非空校验
    }

}
