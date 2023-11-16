package com.example.demofile.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/11/15 23:49
 */
@Component
public class AliOSSUtils {
    public static void main(String[] args) {
        String str = "aaa.bbb.ccc";
        int i = str.lastIndexOf(".");
        System.out.println(i);
    }
    // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
    private String endpoint = "https://oss-cn-beijing.aliyuncs.com";
    // 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
//        EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
    private String accessKeyId = "LTAI5t9MuESzmq62wZSWtd4r";
    private String accessKeySecret = "8jTwwXD3XgrWWNThQpqMQkmMUuRgnV";
    // 填写Bucket名称，例如examplebucket。
    private String bucketName = "t-wang-demo";

    public String upload(MultipartFile file) throws IOException {
        // 获取输入文件的输入流
        InputStream inputStream = file.getInputStream();
        // 避免文件覆盖
        String originalFilename = file.getOriginalFilename();
        int index = originalFilename.lastIndexOf(".");
        String fileName = UUID.randomUUID().toString() + originalFilename.substring(index);
        // 上传文件到 OSS
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ossClient.putObject(bucketName, fileName, inputStream);
        // 文件的访问路径
        String url = endpoint.split("//")[0] + "//" + bucketName + "." + endpoint.split("//")[1] + "/" + fileName;

        return url;
    }
}
