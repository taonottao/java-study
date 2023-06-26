package com.example.demo.service;

import com.example.demo.entity.UserInfo;
import com.example.demo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/25 14:23
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Transactional(propagation = Propagation.REQUIRED)
    public Integer add(UserInfo userInfo) {
        int result = userMapper.add(userInfo);
        System.out.println("用户添加：" + result);
        return result;
    }

}
