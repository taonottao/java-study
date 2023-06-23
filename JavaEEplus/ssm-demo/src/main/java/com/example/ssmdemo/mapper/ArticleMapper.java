package com.example.ssmdemo.mapper;

import com.example.ssmdemo.entity.vo.ArticleinfoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/23 8:41
 */
@Mapper
public interface ArticleMapper {

    ArticleinfoVO getById(@Param("id")Integer id);

}
