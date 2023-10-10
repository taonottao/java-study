package com.wt.demo.service;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/10/7 23:41
 */

class UserServiceTest2 {

    @org.junit.jupiter.api.Test
    void add() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        UserService2 userService2 = context.getBean("userService2", UserService2.class);
        userService2.sayHi();
    }
}