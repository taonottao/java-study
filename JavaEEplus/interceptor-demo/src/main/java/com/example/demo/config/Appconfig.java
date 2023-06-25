package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/24 16:54
 */
@Configuration
public class Appconfig implements WebMvcConfigurer {
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new LoginInterceptor())
//                .addPathPatterns("/**") //拦截所有请求
//                .excludePathPatterns("/user/login") // 排除的url地址（不拦截的url地址）
//                .excludePathPatterns("/user/reg")
//                .excludePathPatterns("/**/*.html");
//    }

    // 统一价前缀
//    @Override
//    public void configurePathMatch(PathMatchConfigurer configurer) {
//        configurer.addPathPrefix("/zhangsan", c ->true);
//    }
}
