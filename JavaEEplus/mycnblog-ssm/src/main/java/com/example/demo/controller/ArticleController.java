package com.example.demo.controller;

import com.example.demo.common.AjaxResult;
import com.example.demo.common.UserSessionUtils;
import com.example.demo.entity.Articleinfo;
import com.example.demo.entity.Userinfo;
import com.example.demo.service.ArticleService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
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

}
