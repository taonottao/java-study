package com.example.demo.service;

import com.example.demo.entity.Articleinfo;
import com.example.demo.mapper.ArticleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/26 15:08
 */
@Service
public class ArticleService {
    @Autowired
    private ArticleMapper articleMapper;

    public int getArtCountByUid(Integer uid){
        return articleMapper.getArtCountByUid(uid);
    }

    public List<Articleinfo> getMyList(Integer uid){
        return articleMapper.getMyList(uid);
    }

    public int del(Integer id, Integer uid) {
        return articleMapper.del(id, uid);
    }

    public Articleinfo getDetail(Integer id) {
        return articleMapper.getDetail(id);
    }
}
