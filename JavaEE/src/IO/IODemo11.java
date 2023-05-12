package IO;

import java.io.File;
import java.util.Scanner;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/12 22:06
 */
public class IODemo11 {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // 让用户输入一个指定搜索的目录
//        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入要搜索的路径");
        String basePath = scanner.next();

        // 针对用户输入, 进行一个简单判定
        File root = new File(basePath);
        if(!root.isDirectory()){
            // 路径不存在, 或者只是一个普通文件, 此时无法进行搜索.
            System.out.println("输入的目录有误!!!");
            return;
        }

        // 在让用户输入一个要删除的文件名
        System.out.println("请输入要删除的文件名");
        // 此处使用 next, 而不要使用nextLine!!!
        String nameToDelete = scanner.next();

        // 针对指定路径进行扫描, 递归操作
        // 先从根目录出发,  (root)
        // 先判定一下, 当前目录里, 是否包含咱们要删除的文件, 如果是, 就删除; 否则就跳过下一个
        // 如果当前这里包含了一些目录, 在针对子目录进行递归.

        scanDir(root, nameToDelete);
    }

    private static void scanDir(File root, String nameToDelete) {
        System.out.println("[scanDir]" + root.getAbsolutePath());
        // 1. 先列出当前路径下包含的内容
        File[] files = root.listFiles();
        if(files == null){
            // 当前root目录下没东西, 是一个空目录
            // 结束继续递归
            return;
        }
        //2. 遍历当前的列出结果
        for(File f : files){
            if(f.isDirectory()){
                // 如果是一个目录, 就进一步递归
                scanDir(f, nameToDelete);
            }else{
                // 如果是普通文件, 就判定是否删除
                if(f.getName().contains(nameToDelete)){
                    System.out.println("确认是否要删除" + f.getAbsolutePath() + "吗?");
                    String choice = scanner.next();
                    if(choice.equals("y") || choice.equals("Y")){
                        f.delete();
                        System.out.println("删除成功");
                    }else {
                        System.out.println("删除取消");
                    }
                }
            }
        }

    }


}
