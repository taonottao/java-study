package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/25 19:07
 */
@Service
public class LogService {

    @Transactional(propagation = Propagation.REQUIRED)// 这个也是默认的
    public int add(){
        try {
            int num = 10 / 0;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return 1;
    }

}
