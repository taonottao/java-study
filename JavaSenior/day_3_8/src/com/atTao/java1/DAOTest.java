package com.atTao.java1;

import org.junit.Test;

import java.util.List;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/3/10 23:17
 */
public class DAOTest {
    @Test
    public void test1(){
        CustomerDAO dao1 = new CustomerDAO();

        dao1.add(new Customer());
        List<Customer> list = dao1.getForList(10);


        StudentDAO dao2 = new StudentDAO();
        Student student = dao2.getIndex(1);
    }
}
