package com.example.demo.controller;

import com.example.demo.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/18 16:51
 */
@RequestMapping("/test") // 路由注册
//@ResponseBody // 告诉程序我返回的是一个数据而不是一个页面
@Controller // 让框架启动的时候加载当前类(只有加载的类, 别人蔡锷能访问[使用])
@RestController
public class TestController {

    //    @RequestMapping("/hi")
//    @RequestMapping(value = "/hi", method = RequestMethod.POST)
//    @PostMapping("/hi")
    @GetMapping("/hi")
    public String sayHi(String name, int v) {
        return "hi, " + name + " | v= " + v;
    }

    @GetMapping("/num")
    public String getNum(Integer num) {
        return "num=" + num;
    }

    // 接收普通对象
    @GetMapping("/show-user")
    public String showUser(User user) {
        return user.toString();
    }

    // 接收 json 对象(和第三方系统通讯时常见的场景)
    @PostMapping("show-json-user")
    public String showJsonUser(@RequestBody User user) {
        return user.toString();
    }

    @GetMapping("/show-time")
//    public String showTime(String t, String t2){
    public String showTime(@RequestParam(value = "t", required = false) String startTime,
                           @RequestParam(value = "t2") String endTime) {

        return "开始时间: " + startTime + " | 结束时间: " + endTime;
    }

    @RequestMapping("/login/{username}/{password}")
    public String login(@PathVariable("username") String username,
                        @PathVariable("password") String password) {
        return username + ": " + password;
    }

    @RequestMapping("/show/{username}/and/{password}")
    public String showInfo(@PathVariable("password") String pwd,
                           @PathVariable("username") String un) {
        return un + ": " + pwd;
    }

    @RequestMapping("/upfile")
    public String upfile(@RequestPart("myfile")MultipartFile file) throws IOException {
        String path = "E:\\workSpace\\JavaEEplus\\img.png";
        // 保存文件
        file.transferTo(new File(path));
        return path;
    }
}
