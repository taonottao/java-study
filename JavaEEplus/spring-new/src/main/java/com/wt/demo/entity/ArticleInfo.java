package com.wt.demo.entity;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/10/7 21:09
 */

public class ArticleInfo {

    private int aid;
    private String title;
    private String content;

    @Override
    public String toString() {
        return "ArticleInfo{" +
                "aid=" + aid +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
