package com.wt.demo.entity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/10/8 15:54
 */
@Component
public class Users {

    /**
     * 公共对象
     * @return
     */
    @Bean("user")
    @Scope("prototype")
    public User getUser() {
        User user = new User();
        user.setId(1);
        user.setName("张三");
        return user;
    }

}
