package IO;

import java.io.*;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/12 22:40
 */
public class IODemo12 {
    public static void main(String[] args) {
        // 输入两个路径
        // 源 和 目标 (从哪里, 拷贝到那里)
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入要拷贝那个文件:");
        String srcPath = scanner.next();
        System.out.println("请输入要被拷贝到那里:");
        String destPath = scanner.next();

        File srcFile = new File(srcPath);
        if(!srcFile.isFile()){
            // 如果源不是一个文件(是个目录或者不存在)
            // 此时就不做任何操作
            System.out.println("您当前输入的原路径有误!!!");
            return;
        }
        File destFile = new File(destPath);
        if(destFile.isFile()){
            // 如果已经存在, 认为也不能拷贝
            System.out.println("您当前输入的原路径有误!!!");
            return;
        }

        // 进行拷贝操作
        try(InputStream inputStream = new FileInputStream(srcFile);
            OutputStream outputStream = new FileOutputStream(destFile)){

            while (true){
                // 进行读操作
                int b = inputStream.read();
                if(b == -1){
                    break;
                }
                outputStream.write(b);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
