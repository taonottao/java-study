package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching // 开启全局注解缓存
public class SpringbootRedisDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootRedisDemoApplication.class, args);
    }

}
