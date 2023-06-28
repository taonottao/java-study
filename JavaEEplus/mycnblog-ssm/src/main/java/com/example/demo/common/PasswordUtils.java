package com.example.demo.common;

import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/28 19:31
 */
public class PasswordUtils {

    public static void main(String[] args) {
        String password = "123456";
        String finalPassword = encrypt(password);
        System.out.println("加密：" + finalPassword);
        // 对比
        String inputPassword = "12345";
        System.out.println("对比：" + check(inputPassword, finalPassword));
        String inputPassword2 = "123456";
        System.out.println("对比: " + check(inputPassword2, finalPassword));
    }


    /** 1. 加盐并生成密码
     *
     * @param password 明文密码
     * @return 保存到数据库中的密码
     */
    public static String encrypt(String password) {
        // 1.1 生成盐值  32 位
        String salt = UUID.randomUUID().toString().replace("-","");
        // 1.2 生成加盐之后的密码
        String saltPassword = DigestUtils.md5DigestAsHex((salt + password).getBytes());
        // 1.3 生成最终密码（保存到数据库中的密码） [约定格式：32位盐值+$+32位加盐之后的密码]
        String finalPassword = salt + "$" + saltPassword;
        return finalPassword;
    }

    /** 2. 生成加盐的密码（方法1的重载，主要用于下面的验证密码）
     *
     * @param password 明文
     * @param salt 盐值
     * @return 最终密码
     */
    public static String encrypt(String password, String salt){
        // 1. 生成一个加盐之后的密码
        String saltPassword = DigestUtils.md5DigestAsHex((salt + password).getBytes());
        // 2. 生成最终密码
        String finalPassword = salt + "$" + saltPassword;
        return finalPassword;
    }

    /** 3. 验证密码
     *
     * @param inputPassword 用户输入的明文密码
     * @param finalPassword 数据库保存的最终密码
     * @return
     */
    public static boolean check(String inputPassword, String finalPassword) {
        if (StringUtils.hasLength(inputPassword) && StringUtils.hasLength(finalPassword)
        && finalPassword.length() == 65) {
            // 1. 得到盐值
            String salt = finalPassword.split("\\$")[0];
            // 2. 使用之前加密的步骤，将明文密码和已经得到的盐值进行加密，生成最终密码
            String confimPassword = encrypt(inputPassword, salt);
            // 3. 对比两个密码是否相同
            return confimPassword.equals(finalPassword);
        }
        return false;
    }
}
