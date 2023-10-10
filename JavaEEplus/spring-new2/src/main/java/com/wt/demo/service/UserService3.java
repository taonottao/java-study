package com.wt.demo.service;

import com.wt.demo.dao.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/10/8 13:32
 */
@Service
public class UserService3 {

    private UserDAO userDAO;

    @Autowired
//    @Resource
    public UserService3(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void sayHi() {
        System.out.println("do userService3");
        userDAO.add();
    }

}
