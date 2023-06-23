package com.example.ssmdemo.mapper;

import com.example.ssmdemo.entity.vo.ArticleinfoVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/23 8:47
 */
@SpringBootTest
class ArticleMapperTest {

    @Autowired
    private ArticleMapper articleMapper;

    @Test
    void getById() {
        ArticleinfoVO articleinfoVO = articleMapper.getById(1);
        System.out.println(articleinfoVO);
    }
}