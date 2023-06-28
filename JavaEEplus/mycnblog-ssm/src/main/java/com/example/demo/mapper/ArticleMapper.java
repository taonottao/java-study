package com.example.demo.mapper;

import com.example.demo.entity.Articleinfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/26 15:00
 */
@Mapper
public interface ArticleMapper {

    int getArtCountByUid(@Param("uid") Integer uid);

    List<Articleinfo> getMyList(@Param("uid") Integer uid);

    int del(@Param("id") Integer id, @Param("uid") Integer uid);

    Articleinfo getDetail(@Param("id")Integer id);

    int incrRCount(@Param("id")Integer id);

    int add(Articleinfo articleinfo);

    int update(Articleinfo articleinfo);

}
