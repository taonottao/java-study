package com.example.demo.controller;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/7/2 14:47
 */
@RestController
public class RedisAnnController {

    /**
     * 存入或读取缓存
     * @param name
     * @param pnumber
     * @return
     */
    @RequestMapping("/ann-get")
    @Cacheable(value = "spring.cache", key = "#name+'-'+#pnumber")
    public String get(String name, String pnumber) {
        if (!StringUtils.hasLength(name) || !StringUtils.hasLength(pnumber)) {
            return null;
        }
        System.out.println("执行了 get 方法");
        return "name=" + name + "|pnumber=" + pnumber;
    }

    /**
     * 更新缓存
     * @param name
     * @param pnumber
     * @return
     */
    @RequestMapping("/put")
    @CachePut(value = "spring.cache", key = "#name+'-'+#pnumber")
    public String put(String name, String pnumber) {
        if (!StringUtils.hasLength(name) || !StringUtils.hasLength(pnumber)) {
            return null;
        }
        System.out.println("执行了 put 方法");
        return "[name=" + name + " <-> pnumber=" + pnumber + "]";
    }

    /**
     * redis 缓存删除
     * @param name
     * @param pnumber
     */
    @RequestMapping("/del")
    @CacheEvict(value = "spring.cache", key = "#name+'-'+#pnumber")
    public void delete(String name, String pnumber) {
        System.out.println("执行了缓存删除");
    }


}
