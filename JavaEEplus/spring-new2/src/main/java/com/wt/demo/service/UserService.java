package com.wt.demo.service;

import com.wt.demo.dao.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/10/7 23:29
 */
@Service
public class UserService {

    @Autowired
    private UserDAO userDAO;

    public int add() {
        System.out.println("do userService");
        return userDAO.add();
    }

}
