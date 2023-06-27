package com.example.demo.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/26 15:06
 */
@SpringBootTest
class ArticleMapperTest {

    @Resource
    private ArticleMapper articleMapper;

    @Test
    void getArtCountByUid() {
        int ret = articleMapper.getArtCountByUid(1);
        System.out.println("文章总数："+ ret);
    }
}