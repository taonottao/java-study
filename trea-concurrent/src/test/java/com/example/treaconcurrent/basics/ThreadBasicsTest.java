package com.example.treaconcurrent.basics;

import com.example.treaconcurrent.utils.ThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 线程基础功能测试类
 * 验证ThreadBasicsDemo的各种功能
 * 
 * @author SOLO Coding
 * @since 2024-01-20
 */
@Slf4j
@SpringBootTest
class ThreadBasicsTest {
    
    /**
     * 测试Thread基础功能演示
     */
    @Test
    void testThreadBasicsDemo() {
        log.info("=== 开始测试Thread基础功能 ===");
        
        ThreadBasicsDemo demo = new ThreadBasicsDemo();
        
        try {
            // 测试基础线程创建和执行
            demo.demonstrateBasicThreadCreation();
            
            // 等待一段时间确保所有线程执行完成
            ThreadUtils.sleep(2000);
            
            log.info("Thread基础功能测试完成");
            
        } catch (Exception e) {
            log.error("Thread基础功能测试失败: {}", e.getMessage(), e);
            throw e;
        }
        
        log.info("=== Thread基础功能测试结束 ===");
    }
    
    /**
     * 测试线程状态演示
     */
    @Test
    void testThreadStatesDemo() {
        log.info("=== 开始测试线程状态演示 ===");
        
        ThreadBasicsDemo demo = new ThreadBasicsDemo();
        
        try {
            demo.demonstrateThreadStates();
            
            // 等待演示完成
            ThreadUtils.sleep(3000);
            
            log.info("线程状态演示测试完成");
            
        } catch (Exception e) {
            log.error("线程状态演示测试失败: {}", e.getMessage(), e);
            throw e;
        }
        
        log.info("=== 线程状态演示测试结束 ===");
    }
    
    /**
     * 测试线程优先级演示
     */
    @Test
    void testThreadPriorityDemo() {
        log.info("=== 开始测试线程优先级演示 ===");
        
        ThreadBasicsDemo demo = new ThreadBasicsDemo();
        
        try {
            demo.demonstrateThreadPriority();
            
            // 等待演示完成
            ThreadUtils.sleep(2000);
            
            log.info("线程优先级演示测试完成");
            
        } catch (Exception e) {
            log.error("线程优先级演示测试失败: {}", e.getMessage(), e);
            throw e;
        }
        
        log.info("=== 线程优先级演示测试结束 ===");
    }
    
    /**
     * 测试守护线程演示
     */
    @Test
    void testDaemonThreadDemo() {
        log.info("=== 开始测试守护线程演示 ===");
        
        ThreadBasicsDemo demo = new ThreadBasicsDemo();
        
        try {
            demo.demonstrateDaemonThread();
            
            // 等待演示完成
            ThreadUtils.sleep(3000);
            
            log.info("守护线程演示测试完成");
            
        } catch (Exception e) {
            log.error("守护线程演示测试失败: {}", e.getMessage(), e);
            throw e;
        }
        
        log.info("=== 守护线程演示测试结束 ===");
    }
    
    /**
     * 测试线程中断演示
     */
    @Test
    void testThreadInterruptDemo() {
        log.info("=== 开始测试线程中断演示 ===");
        
        ThreadBasicsDemo demo = new ThreadBasicsDemo();
        
        try {
            demo.demonstrateThreadInterrupt();
            
            // 等待演示完成
            ThreadUtils.sleep(2000);
            
            log.info("线程中断演示测试完成");
            
        } catch (Exception e) {
            log.error("线程中断演示测试失败: {}", e.getMessage(), e);
            throw e;
        }
        
        log.info("=== 线程中断演示测试结束 ===");
    }
    
    /**
     * 测试线程同步演示
     */
    @Test
    void testThreadSynchronizationDemo() {
        log.info("=== 开始测试线程同步演示 ===");
        
        ThreadBasicsDemo demo = new ThreadBasicsDemo();
        
        try {
            demo.demonstrateThreadSynchronization();
            
            // 等待演示完成
            ThreadUtils.sleep(3000);
            
            log.info("线程同步演示测试完成");
            
        } catch (Exception e) {
            log.error("线程同步演示测试失败: {}", e.getMessage(), e);
            throw e;
        }
        
        log.info("=== 线程同步演示测试结束 ===");
    }
    
    /**
     * 测试所有演示功能
     */
    @Test
    void testAllDemonstrations() {
        log.info("=== 开始测试所有Thread演示功能 ===");
        
        ThreadBasicsDemo demo = new ThreadBasicsDemo();
        
        try {
            // 运行所有演示
            demo.runAllDemonstrations();
            
            // 等待所有演示完成
            ThreadUtils.sleep(5000);
            
            log.info("所有Thread演示功能测试完成");
            
        } catch (Exception e) {
            log.error("Thread演示功能测试失败: {}", e.getMessage(), e);
            throw e;
        }
        
        log.info("=== 所有Thread演示功能测试结束 ===");
    }
    
    /**
     * 测试并发执行多个线程
     */
    @Test
    void testConcurrentExecution() throws InterruptedException {
        log.info("=== 开始测试并发执行 ===");
        
        int threadCount = 5;
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        // 创建多个线程并发执行
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            Thread thread = new Thread(() -> {
                try {
                    log.info("线程{}开始执行", threadIndex);
                    ThreadUtils.printCurrentThreadInfo();
                    
                    // 模拟一些工作
                    ThreadUtils.sleep(1000 + threadIndex * 200);
                    
                    log.info("线程{}执行完成", threadIndex);
                } finally {
                    latch.countDown();
                }
            }, "TestThread-" + threadIndex);
            
            thread.start();
        }
        
        // 等待所有线程完成
        boolean completed = latch.await(10, TimeUnit.SECONDS);
        
        if (completed) {
            log.info("所有线程执行完成");
        } else {
            log.warn("部分线程未在规定时间内完成");
        }
        
        log.info("=== 并发执行测试结束 ===");
    }
    
    /**
     * 测试线程工具类功能
     */
    @Test
    void testThreadUtils() {
        log.info("=== 开始测试线程工具类 ===");
        
        try {
            // 测试获取当前线程信息
            ThreadUtils.printCurrentThreadInfo();
            
            // 测试系统信息
            ThreadUtils.printSystemInfo();
            
            // 测试任务ID生成
            String taskId1 = ThreadUtils.generateTaskId();
            String taskId2 = ThreadUtils.generateTaskId("TEST");
            
            log.info("生成的任务ID1: {}", taskId1);
            log.info("生成的任务ID2: {}", taskId2);
            
            // 测试线程池大小计算
            int cpuIntensiveSize = ThreadUtils.calculateOptimalThreadPoolSize(0.9);
            int ioIntensiveSize = ThreadUtils.calculateOptimalThreadPoolSize(0.1);
            int mixedSize = ThreadUtils.calculateOptimalThreadPoolSize(0.5);
            
            log.info("CPU密集型任务建议线程池大小: {}", cpuIntensiveSize);
            log.info("IO密集型任务建议线程池大小: {}", ioIntensiveSize);
            log.info("混合型任务建议线程池大小: {}", mixedSize);
            
            log.info("线程工具类测试完成");
            
        } catch (Exception e) {
            log.error("线程工具类测试失败: {}", e.getMessage(), e);
            throw e;
        }
        
        log.info("=== 线程工具类测试结束 ===");
    }
}