package com.demo.controller;

import com.demo.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/15 15:10
 */
@Controller
public class StudentController {

    // 1. 使用属性注入的方式获取 Bean
    @Autowired
    private StudentService studentService;

//    private final int number;
//
//    public StudentController(int number){
//        this.number = number;
//    }

    public void sayHi(){
        // 调用 service 方法
        studentService.sayHi();
    }

}
