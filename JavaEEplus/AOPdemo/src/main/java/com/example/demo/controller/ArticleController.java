package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/24 8:28
 */
@RestController
@RequestMapping("/art")
public class ArticleController {

    @RequestMapping("/sleep")
    public String sleep(){
        return "sleep";
    }

}
