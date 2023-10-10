package com.wt.demo.dao;

import com.wt.demo.entity.User;
import org.springframework.stereotype.Repository;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/10/7 23:30
 */
@Repository
public class UserDAO {

    public int add() {
        System.out.println("do userdao");
        return 1;
    }

}
