package com.example.springbootsessionredisdemo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/7/2 17:08
 */
@RestController
public class UserController {

    private static final String SESSION_KEY = "USER_SESSION_KEY";

    @RequestMapping("/login")
    public String login(HttpSession session){
        // 存储 session 到 redis
        session.setAttribute(SESSION_KEY, "zhangsan");
        return "login success";
    }

    @RequestMapping("/get")
    public String get(HttpServletRequest request) {
        String username = "暂无";
        HttpSession session = request.getSession(false);
        if (session != null) {
            // 从 redis 中获取 session 用户
            Object userinfo = session.getAttribute(SESSION_KEY);
            if (userinfo != null) {
                return userinfo.toString();
            }
        }
        return username;
    }

}
