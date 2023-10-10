package com.wt.demo;

import com.wt.demo.controller.UserController;
import com.wt.demo.controller.UserController2;
import com.wt.demo.controller.UserController3;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/10/8 15:35
 */
public class app {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
//        UserController userController = context.getBean("userController", UserController.class);
//        userController.add();
        UserController2 userController2 = context.getBean("userController2", UserController2.class);
        UserController3 userController3 = context.getBean("userController3", UserController3.class);
        userController2.doMethod();
        userController3.doMethod();
    }

}
