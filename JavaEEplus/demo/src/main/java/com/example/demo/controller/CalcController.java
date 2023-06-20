package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/19 21:09
 */
@Controller
@ResponseBody
public class CalcController {

    @RequestMapping("/calc")
    public String calc(Integer num1, Integer num2) {
        if (num1 == null || num2 == null) {
            return "参数错误";
        }
        return "结果为: " + (num1 + num2);
    }

    @RequestMapping("/json")
    public HashMap<String, String> method() {
        HashMap<String, String> map = new HashMap<>();
        map.put("Java", "java value");
        map.put("mysql", "mysql value");
        return map;
    }
}
