package com.example.demofile.controller;

import com.example.demofile.utils.AliOSSUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/11/16 0:21
 */
@RestController
@Slf4j
public class UploadController {

    @Autowired
    private AliOSSUtils aliOSSUtils;

    @RequestMapping("/upload")
    public String upload(MultipartFile image) throws IOException {
        log.info("文件上传，文件名：{}" + image.getOriginalFilename());

        String url = aliOSSUtils.upload(image);

        return url;
    }
}
