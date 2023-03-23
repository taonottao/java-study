package com.atTao.exer1;

import java.util.List;

/**
 * 定义一个测试类：
 * 创建 DAO 类的对象， 分别调用其 save、get、update、list、delete 方法来操作 User 对象，
 * 使用 Junit 单元测试类进行测试。
 *
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/3/11 20:12
 */
public class DAOTest {
    public static void main(String[] args) {
        DAO<User> dao = new DAO<>();

        dao.save("1001",new User(1001,35,"周杰伦"));
        dao.save("1002",new User(1002,30,"昆凌"));
        dao.save("1003",new User(1003,28,"蔡依林"));

        dao.update("1003",new User(1003,31,"方文山"));

        dao.delete("1002");

        List<User> list = dao.list();
//        System.out.println(list);
        list.forEach(System.out::println);

    }
}
