package com.demo.controller;

import com.demo.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/16 15:53
 */
@Controller
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    public void teach(){
        teacherService.teach();
    }
}
