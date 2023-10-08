package com.wt.demo;

import com.wt.demo.entity.ArticleInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/10/7 21:10
 */
@Component
public class Articles {

    @Bean("article") // 将该方法返回的对象存储到 IoC 容器中
    public ArticleInfo getBean() {
        ArticleInfo articleInfo = new ArticleInfo();
        articleInfo.setAid(1);
        articleInfo.setTitle("日记");
        articleInfo.setContent("这是一篇日记");
        return articleInfo;
    }
    @Bean // 将该方法返回的对象存储到 IoC 容器中
    public ArticleInfo getArticle() {
        ArticleInfo articleInfo = new ArticleInfo();
        articleInfo.setAid(1);
        articleInfo.setTitle("日记");
        articleInfo.setContent("这是一篇日记");
        return articleInfo;
    }

    public void sayHi() {
        System.out.println("article");
    }

}
