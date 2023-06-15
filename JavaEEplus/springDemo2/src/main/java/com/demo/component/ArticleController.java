package com.demo.component;

import org.springframework.stereotype.Controller;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/14 17:24
 */
@Controller
public class ArticleController {
    public String sayHello(){
        return "hello controller";
    }
}
