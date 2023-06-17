package com.demo.component;

import com.demo.model.User;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @version 1.0
 * @Author 张三
 * @Date 2023/6/16 17:16
 */
@Component
public class UserBeans {

//    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Scope("prototype")
    @Bean
    public User user1(){
        User user = new User();
        user.setId(1);
        user.setName("张三");
        return user;
    };
}
