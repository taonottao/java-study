package com.example.ssmdemo.service;

import com.example.ssmdemo.entity.Userinfo;
import com.example.ssmdemo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/21 22:50
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public Userinfo getUserById(Integer id){
        return userMapper.getUserById(id);
    }

}
