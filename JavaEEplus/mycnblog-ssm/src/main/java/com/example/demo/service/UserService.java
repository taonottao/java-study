package com.example.demo.service;

import com.example.demo.entity.Userinfo;
import com.example.demo.mapper.ArticleMapper;
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

    public Userinfo getUserByName(String username){
        return userMapper.getUserByName(username);
    }

    public Userinfo getUserById(Integer id) {
        return userMapper.getUserById(id);
    }



}
