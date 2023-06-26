package com.example.demo.config;

import com.example.demo.common.AjaxResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 实现统一数据返回的保底类
 * 说明：在返回数据之前检测数据的类型是否为统一的对象，如果不是，就封装成统一的对象
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/25 21:47
 */
@ControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 开关，如果为 true 才会调用 beforeBodyWrite
     */
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    /**
     * 对数据格式进行校验和封装
     */
    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if(body instanceof AjaxResult) return body;

        if(body instanceof String){
            return objectMapper.writeValueAsString(AjaxResult.success(body));
        }
        return AjaxResult.success(body);
    }
}
