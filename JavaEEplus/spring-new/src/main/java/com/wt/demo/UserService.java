package com.wt.demo;

import org.springframework.stereotype.Controller;


/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/10/6 15:33
 */
//@Controller(value = "userInfo")
@Controller
public class UserService {

    public UserService() {
        System.out.println("userservice init");
    }

    public void sayHi() {
        System.out.println("hi1");
    }

}
