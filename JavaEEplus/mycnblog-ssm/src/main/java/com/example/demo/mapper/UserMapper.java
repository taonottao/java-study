package com.example.demo.mapper;

import com.example.demo.entity.Userinfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/25 23:49
 */
@Mapper
public interface UserMapper {

    // 注册
    int reg(Userinfo userinfo);

    // 登录(根据用户名查询 userinfo 对象)
    Userinfo getUserByName(@Param("username") String username);

    Userinfo getUserById(@Param("id")Integer id);

}
