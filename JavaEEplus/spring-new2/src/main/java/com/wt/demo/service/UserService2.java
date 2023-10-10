package com.wt.demo.service;

import com.wt.demo.dao.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/10/8 13:19
 */
@Service
public class UserService2 {

    private UserDAO userDAO;

//    @Autowired
    @Resource
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
    public void sayHi() {
        System.out.println("do userservice2");
        userDAO.add();
    }
}
