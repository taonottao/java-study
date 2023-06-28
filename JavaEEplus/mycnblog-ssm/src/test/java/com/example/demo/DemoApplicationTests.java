package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class DemoApplicationTests {

    @Test
    void contextLoads() {
        String password = "123456";
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String finalPassword = passwordEncoder.encode(password);
        System.out.println("第1次加密：" + finalPassword);
        System.out.println("第2次加密：" + passwordEncoder.encode(password));
        System.out.println("第3次加密：" + passwordEncoder.encode(password));
        // 验证
        String inputPassword = "12345";
        System.out.println("错误：" + passwordEncoder.matches(inputPassword, finalPassword));
        String inputPassword2 = "123456";
        System.out.println("正确：" + passwordEncoder.matches(inputPassword2, finalPassword));

    }

}
