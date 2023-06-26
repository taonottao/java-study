package com.example.demo.mapper;

import com.example.demo.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/25 14:18
 */
@Mapper
public interface UserMapper {

    int add(UserInfo userInfo);

}
