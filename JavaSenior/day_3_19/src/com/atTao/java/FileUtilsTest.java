package com.atTao.java;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/3/19 23:40
 */
public class FileUtilsTest {

    public static void main(String[] args) {
        File srcFile = new File("day_3_19\\迦南.png");
        File  destFile = new File("day_3_19\\迦南2.png");

        try {
            FileUtils.copyFile(srcFile,destFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
