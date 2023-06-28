package com.example.demo.controller;

import com.example.demo.common.AjaxResult;
import com.example.demo.common.UserSessionUtils;
import com.example.demo.entity.Articleinfo;
import com.example.demo.entity.Userinfo;
import com.example.demo.service.ArticleService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/26 17:29
 */
@RestController
@RequestMapping("/art")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @RequestMapping("/mylist")
    public AjaxResult getMyList(HttpServletRequest request){
        Userinfo userinfo = UserSessionUtils.getUser(request);
        if (userinfo == null) {
            return AjaxResult.fail(-1,"非法请求");
        }
        List<Articleinfo> list = articleService.getMyList(userinfo.getId());
        return AjaxResult.success(list);
    }

    @RequestMapping("/del")
    public AjaxResult del(HttpServletRequest request, Integer id){
        if(id==null || id < 0){
            // 参数有误
            return AjaxResult.fail(-1, "参数异常");
        }
        Userinfo userinfo = UserSessionUtils.getUser(request);
        if (userinfo == null) {
            return AjaxResult.fail(-2, "用户未登录");
        }
        return AjaxResult.success(articleService.del(id, userinfo.getId()));
    }

    @RequestMapping("/detail")
    public AjaxResult getDetail(Integer id) {
        if (id == null || id <= 0) {
            return AjaxResult.fail(-1, "参数非法");
        }
        return AjaxResult.success(articleService.getDetail(id));
    }

    @RequestMapping("/incr-rcount")
    public AjaxResult incrRCount(Integer id){
        if(id != null && id > 0){
            return AjaxResult.success(articleService.incrRCount(id));
        }
        return AjaxResult.fail(-1, "未知错误");
    }

    @RequestMapping("/add")
    public AjaxResult add(HttpServletRequest request, Articleinfo articleinfo) {
        // 1. 非空校验
        if (articleinfo == null || !StringUtils.hasLength(articleinfo.getTitle())
                || !StringUtils.hasLength(articleinfo.getContent())) {
            return AjaxResult.fail(-1, "非法参数");
        }
        // 2. 数据库添加操作
        // 2.1 得到当前登录用户的 UID
        Userinfo userinfo = UserSessionUtils.getUser(request);
        if (userinfo == null || userinfo.getId() <= 0) {
            // 无效的登录用户
            return AjaxResult.fail(-2,"无效的登录用户");
        }
        articleinfo.setUid(userinfo.getId());
        articleinfo.setUpdatetime(LocalDateTime.now());
        // 2.2 添加到数据库并返回结果
        if (articleinfo.getContent().length() > 200){
            articleinfo.setContent(articleinfo.getContent().substring(0, 200));
        }
        return AjaxResult.success(articleService.add(articleinfo));
    }

    @RequestMapping("/update")
    public AjaxResult update(HttpServletRequest request, Articleinfo articleinfo) {
        // 非空校验
        if (articleinfo == null || !StringUtils.hasLength(articleinfo.getTitle())
                || !StringUtils.hasLength(articleinfo.getContent()) ||
                articleinfo.getId() == null) {
            return AjaxResult.fail(-1, "非法参数");
        }
        // 得到当前登录用户的 id
        Userinfo userinfo = UserSessionUtils.getUser(request);
        if (userinfo == null || userinfo.getId() == null) {
            // 无效的登录用户
            return AjaxResult.fail(-2,"无效用户");
        }
        // 很核心的代码（解决了修改文章归属人判定的问题）
        articleinfo.setUid(userinfo.getId());
        // 更新修改时间
        articleinfo.setUpdatetime(LocalDateTime.now());
        return AjaxResult.success(articleService.update(articleinfo));
    }

}
