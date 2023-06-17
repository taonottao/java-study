package com.demo.controller;

import com.demo.model.Student;
import com.demo.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/15 15:10
 */
@Controller
public class StudentController {

    //    private final int number;
//
//    public StudentController(int number){
//        this.number = number;
//    }


    // 1. 使用属性注入的方式获取 Bean
//    @Autowired
//    @Resource
//    private StudentService studentService;


    // 2. set 注入
//    private StudentService studentService;
//
//    @Autowired
//    public void setStudentService(StudentService studentService) {
//        this.studentService = studentService;
//    }

    // 3. 构造方法注入
    // 可以注入一个不可变的对象
//    private final StudentService studentService;
//
//    @Autowired// 只有一个构造方法时可以省略这个注解
//    private StudentController(StudentService studentService) {
//        this.studentService = studentService;
//    }


//    public void sayHi(){
//        // 调用 service 方法
//        studentService.sayHi();
//    }

//    @Resource(name = "getStudent2")
    @Autowired
    @Qualifier("getStudent1")
    private Student student;

    public void sayHi(){
        System.out.println(student.toString());
    }

}
