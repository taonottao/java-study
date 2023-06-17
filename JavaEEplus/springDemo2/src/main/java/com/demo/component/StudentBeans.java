package com.demo.component;

import com.demo.model.Student;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/15 14:23
 */
@Component
public class StudentBeans {

//    @Bean(name = {"s1", "s2"})
    @Bean
    public Student getStudent1(){
        // 伪代码, 构建对象
        Student student = new Student();
        student.setId(1);
        student.setName("张三");
        student.setAge(18);
        return student;
    }

    @Bean
    public Student getStudent2(){
        // 伪代码, 构建对象
        Student student = new Student();
        student.setId(2);
        student.setName("李四");
        student.setAge(20);
        return student;
    }

}
