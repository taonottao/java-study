package com.example.demo.controller;

import com.example.demo.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/18 16:51
 */
@RequestMapping("/test") // 路由注册
//@ResponseBody // 告诉程序我返回的是一个数据而不是一个页面
@Controller // 让框架启动的时候加载当前类(只有加载的类, 别人蔡锷能访问[使用])
@RestController
@Slf4j
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

    // demo 版
    @RequestMapping("/upfile")
    public String upfile(@RequestPart("myfile") MultipartFile file) throws IOException {
        String path = "E:\\workSpace\\JavaEEplus\\img.png";
        // 保存文件
        file.transferTo(new File(path));
        return path;
    }

    //最终版
    @RequestMapping("/finalupfile")
    public String finalUpFile(@RequestPart("myfile") MultipartFile file) throws IOException {
        // 根目录
        String path = "E:\\workSpace\\JavaEEplus\\";
        // 根目录 + 唯一文件名
        path += UUID.randomUUID().toString().replace("-", "");
        // 根目录 + 唯一文件名 + 文件的后缀
        path += file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        file.transferTo(new File(path));
        return path;
    }

    // SpringMVC(Spring Web) 内置了 HttpServletRequest 和 HttpServletResponse
    @GetMapping("/getparam")
    public String getParam(HttpServletRequest req, HttpServletResponse res) {
        return req.getParameter("username");
    }

    // 获取所有的 cookie (servlet 写法)
    @RequestMapping("/getck")
    public String getCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        for (Cookie ck : cookies) {
            log.error(ck.getName() + ": " + ck.getValue());
        }
        return "get cookie";
    }

    // 获取单个cookie
    @RequestMapping("/getck2")
    public String getCookie2(@CookieValue("zhangsan") String val) {
        return "Cookie Value: " + val;
    }

    @RequestMapping("/getua")
    public String getUA(@RequestHeader("User-Agent") String userAgent) {
        return userAgent;
    }

    // 存 session 信息
    @RequestMapping("/setsess")
    public String setSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute("userinfo", "userinfo");
        return "Set Session Success";
    }

    // 读 session
    @RequestMapping("/getsess")
    public String getSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userinfo") != null) {
            return (String) session.getAttribute("userinfo");
        } else {
            return "暂无 Session 信息";
        }
    }

    @RequestMapping("/getsess2")
    public String getSession2(@SessionAttribute(value = "userinfo", required = false) String userinfo) {
        return userinfo;
    }
}
