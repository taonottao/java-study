package com.example.springbootdemo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/17 21:42
 */

@Controller
@RestController // @Controller(当前类为控制器) + @ResponseBody(返回的是数据而非页面)
public class TestController {

//    @Value("mytest")  // 意味着把字符串 "mytest" 赋值给 mytest 变量
    @Value("${mytest}") // 从配置文件中拿 mytest
    private String mytest;

    @RequestMapping("/hi") // url 路由注册
    public String sayHi(String name){
//        if(name == null || "".equals(name)){
        if(!StringUtils.hasLength(name)){// 等价于上面的条件
            // 设置默认值
            name = "张三";
        }
        return "你好: " + name;
    }

    @RequestMapping("/getconf")
    public String getConfig(){
        return mytest;
    }
}
