package com.example.demo.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/24 18:16
 */
@ControllerAdvice
@ResponseBody
public class MyExHander {

    /**
     * 拦截所有的空指针异常，进行统一的数据返回
     * @param e
     * @return
     */
    @ExceptionHandler(NullPointerException.class)
    public HashMap<String, Object> nullException(NullPointerException e){
        HashMap<String, Object> result = new HashMap<>();
        result.put("code","-1");
        result.put("msg","空指针异常:" + e.getMessage());// 错误码的描述信息
        result.put("data", null);

        return result;
    }

    @ExceptionHandler(Exception.class)
    public HashMap<String, Object> exception(Exception e){
        HashMap<String, Object> result = new HashMap<>();
        result.put("code","-1");
        result.put("msg","异常:" + e.getMessage());// 错误码的描述信息
        result.put("data", null);

        return result;
    }

}
