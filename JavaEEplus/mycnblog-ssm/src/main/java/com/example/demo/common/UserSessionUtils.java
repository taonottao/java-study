package com.example.demo.common;

import com.example.demo.entity.Userinfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 当前登录用户相关的操作
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/26 16:10
 */
public class UserSessionUtils {

    /**
     * 得到当前的登录用户
     * @param request
     * @return
     */
    public static Userinfo getUser(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if(session!=null && session.getAttribute(AppVariable.USER_SESSION_KEY)!=null){
            // 当前用户已登录
            return (Userinfo) session.getAttribute(AppVariable.USER_SESSION_KEY);
        }
        return null;
    }

}
