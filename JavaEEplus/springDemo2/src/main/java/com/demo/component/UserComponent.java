package com.demo.component;

import org.springframework.stereotype.Component;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/15 10:40
 */
@Component
public class UserComponent {
    public String sayHi(){
        return "hi, Component";
    }
}
