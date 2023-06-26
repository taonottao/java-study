package com.example.demo.service;

import com.example.demo.entity.Userinfo;
import com.example.demo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/26 0:01
 */
@Service
public class UserService {

//    @Autowired
    @Resource
    private UserMapper userMapper;

    public int reg(Userinfo userinfo){
        return userMapper.reg(userinfo);
    }

}
