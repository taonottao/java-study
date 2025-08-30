package com.example.treaconcurrent.basics;

import com.example.treaconcurrent.config.ThreadPoolConfig;
import com.example.treaconcurrent.utils.ThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;

/**
 * ExecutorService功能测试类
 * 验证ExecutorServiceDemo和ThreadPoolConfig的各种功能
 * 
 * @author SOLO Coding
 * @since 2024-01-20
 */
@Slf4j
@SpringBootTest
class ExecutorServiceTest {
    
    /**
     * 测试ExecutorService基础功能演示
     */
    @Test
    void testExecutorServiceDemo() {
        log.info("=== 开始测试ExecutorService基础功能 ===");
        
        ExecutorServiceDemo demo = new ExecutorServiceDemo();
        
        try {
            // 测试各种线程池演示
            demo.demonstrateFixedThreadPool();
            ThreadUtils.sleep(1000);
            
            demo.demonstrateCachedThreadPool();
            ThreadUtils.sleep(1000);
            
            demo.demonstrateSingleThreadExecutor();
            ThreadUtils.sleep(1000);
            
            demo.demonstrateScheduledThreadPool();
            ThreadUtils.sleep(3000);
            
            demo.demonstrateCustomThreadPool();
            ThreadUtils.sleep(2000);
            
            log.info("ExecutorService基础功能测试完成");
            
        } catch (Exception e) {
            log.error("ExecutorService基础功能测试失败: {}", e.getMessage(), e);
            throw e;
        }
        
        log.info("=== ExecutorService基础功能测试结束 ===");
    }
    
    /**
     * 测试线程池监控功能
     */
    @Test
    void testThreadPoolMonitoring() {
        log.info("=== 开始测试线程池监控功能 ===");
        
        ExecutorServiceDemo demo = new ExecutorServiceDemo();
        
        try {
            demo.demonstrateThreadPoolMonitoring();
            
            // 等待监控演示完成
            ThreadUtils.sleep(5000);
            
            log.info("线程池监控功能测试完成");
            
        } catch (Exception e) {
            log.error("线程池监控功能测试失败: {}", e.getMessage(), e);
            throw e;
        }
        
        log.info("=== 线程池监控功能测试结束 ===");
    }
    
    /**
     * 测试线程池配置类
     */
    @Test
    void testThreadPoolConfig() {
        log.info("=== 开始测试线程池配置类 ===");
        
        try {
            ThreadPoolConfig config = new ThreadPoolConfig();
            
            // 测试创建各种类型的线程池
            ThreadPoolExecutor customExecutor = config.createCustomThreadPoolExecutor();
            ExecutorService fixedExecutor = ThreadPoolConfig.createFixedThreadPool(5);
            ExecutorService cachedExecutor = ThreadPoolConfig.createCachedThreadPool();
            ExecutorService singleExecutor = ThreadPoolConfig.createSingleThreadExecutor();
            ScheduledExecutorService scheduledExecutor = ThreadPoolConfig.createScheduledThreadPool(3);
            
            // 测试自定义ThreadPoolExecutor
            ThreadPoolExecutor customThreadPool = config.createCustomThreadPoolExecutor();
            
            log.info("创建的线程池: {}", customThreadPool.getClass().getSimpleName());
            ThreadUtils.printThreadPoolStatus(customThreadPool);
            
            // 提交一些测试任务
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                final int taskIndex = i;
                Future<?> future = customThreadPool.submit(() -> {
                    log.info("执行测试任务{}", taskIndex);
                    ThreadUtils.sleep(500);
                    log.info("测试任务{}完成", taskIndex);
                });
                futures.add(future);
            }
            
            // 等待任务完成
            ThreadUtils.waitForAll(futures, 10);
            
            // 打印最终状态
            ThreadUtils.printThreadPoolStatus(customThreadPool);
            
            // 关闭线程池
            ThreadUtils.shutdownGracefully(customExecutor, 5);
            ThreadUtils.shutdownGracefully(fixedExecutor, 5);
            ThreadUtils.shutdownGracefully(cachedExecutor, 5);
            ThreadUtils.shutdownGracefully(singleExecutor, 5);
            ThreadUtils.shutdownGracefully(scheduledExecutor, 5);
            ThreadUtils.shutdownGracefully(customThreadPool, 5);
            
            log.info("线程池配置类测试完成");
            
        } catch (Exception e) {
            log.error("线程池配置类测试失败: {}", e.getMessage(), e);
            throw e;
        }
        
        log.info("=== 线程池配置类测试结束 ===");
    }
    
    /**
     * 测试线程池参数解释功能
     */
    @Test
    void testThreadPoolParametersExplanation() {
        log.info("=== 开始测试线程池参数解释功能 ===");
        
        try {
            ThreadPoolConfig config = new ThreadPoolConfig();
            
            // 调用参数解释方法
            config.explainThreadPoolParameters();
            
            log.info("线程池参数解释功能测试完成");
            
        } catch (Exception e) {
            log.error("线程池参数解释功能测试失败: {}", e.getMessage(), e);
            throw e;
        }
        
        log.info("=== 线程池参数解释功能测试结束 ===");
    }
    
    /**
     * 测试线程池性能对比
     */
    @Test
    void testThreadPoolPerformanceComparison() {
        log.info("=== 开始测试线程池性能对比 ===");
        
        try {
            ThreadPoolConfig config = new ThreadPoolConfig();
            
            // 创建不同类型的线程池
            ExecutorService fixedPool = ThreadPoolConfig.createFixedThreadPool(5);
            ExecutorService cachedPool = ThreadPoolConfig.createCachedThreadPool();
            ThreadPoolExecutor customPool = config.createCustomThreadPoolExecutor();
            
            int taskCount = 20;
            
            // 测试固定线程池性能
            long fixedStartTime = System.currentTimeMillis();
            List<Future<?>> fixedFutures = submitTestTasks(fixedPool, taskCount, "Fixed");
            ThreadUtils.waitForAll(fixedFutures, 30);
            long fixedEndTime = System.currentTimeMillis();
            
            ThreadUtils.sleep(1000); // 间隔
            
            // 测试缓存线程池性能
            long cachedStartTime = System.currentTimeMillis();
            List<Future<?>> cachedFutures = submitTestTasks(cachedPool, taskCount, "Cached");
            ThreadUtils.waitForAll(cachedFutures, 30);
            long cachedEndTime = System.currentTimeMillis();
            
            ThreadUtils.sleep(1000); // 间隔
            
            // 测试自定义线程池性能
            long customStartTime = System.currentTimeMillis();
            List<Future<?>> customFutures = submitTestTasks(customPool, taskCount, "Custom");
            ThreadUtils.waitForAll(customFutures, 30);
            long customEndTime = System.currentTimeMillis();
            
            // 输出性能对比结果
            log.info("=== 线程池性能对比结果 ===");
            log.info("固定线程池执行{}个任务耗时: {}ms", taskCount, fixedEndTime - fixedStartTime);
            log.info("缓存线程池执行{}个任务耗时: {}ms", taskCount, cachedEndTime - cachedStartTime);
            log.info("自定义线程池执行{}个任务耗时: {}ms", taskCount, customEndTime - customStartTime);
            
            // 关闭线程池
            ThreadUtils.shutdownGracefully(fixedPool, 5);
            ThreadUtils.shutdownGracefully(cachedPool, 5);
            ThreadUtils.shutdownGracefully(customPool, 5);
            
            log.info("线程池性能对比测试完成");
            
        } catch (Exception e) {
            log.error("线程池性能对比测试失败: {}", e.getMessage(), e);
            throw e;
        }
        
        log.info("=== 线程池性能对比测试结束 ===");
    }
    
    /**
     * 提交测试任务
     * 
     * @param executor 线程池
     * @param taskCount 任务数量
     * @param poolType 线程池类型
     * @return Future列表
     */
    private List<Future<?>> submitTestTasks(ExecutorService executor, int taskCount, String poolType) {
        List<Future<?>> futures = new ArrayList<>();
        
        for (int i = 0; i < taskCount; i++) {
            final int taskIndex = i;
            Future<?> future = executor.submit(() -> {
                log.debug("{}线程池执行任务{}", poolType, taskIndex);
                
                // 模拟一些计算工作
                long sum = 0;
                for (int j = 0; j < 1000000; j++) {
                    sum += j;
                }
                
                // 模拟一些IO等待
                ThreadUtils.sleep(100);
                
                log.debug("{}线程池任务{}完成，计算结果: {}", poolType, taskIndex, sum);
            });
            futures.add(future);
        }
        
        return futures;
    }
    
    /**
     * 测试线程池异常处理
     */
    @Test
    void testThreadPoolExceptionHandling() {
        log.info("=== 开始测试线程池异常处理 ===");
        
        try {
            ThreadPoolConfig config = new ThreadPoolConfig();
            ThreadPoolExecutor executor = config.createCustomThreadPoolExecutor();
            
            // 提交会抛出异常的任务
            Future<?> future1 = executor.submit(() -> {
                log.info("执行正常任务");
                ThreadUtils.sleep(500);
                log.info("正常任务完成");
            });
            
            Future<?> future2 = executor.submit(() -> {
                log.info("执行异常任务");
                ThreadUtils.sleep(200);
                throw new RuntimeException("模拟任务执行异常");
            });
            
            Future<?> future3 = executor.submit(() -> {
                log.info("执行另一个正常任务");
                ThreadUtils.sleep(300);
                log.info("另一个正常任务完成");
            });
            
            // 获取任务结果，处理异常
            try {
                future1.get(2, TimeUnit.SECONDS);
                log.info("任务1执行成功");
            } catch (Exception e) {
                log.error("任务1执行失败: {}", e.getMessage());
            }
            
            try {
                future2.get(2, TimeUnit.SECONDS);
                log.info("任务2执行成功");
            } catch (Exception e) {
                log.error("任务2执行失败: {}", e.getMessage());
            }
            
            try {
                future3.get(2, TimeUnit.SECONDS);
                log.info("任务3执行成功");
            } catch (Exception e) {
                log.error("任务3执行失败: {}", e.getMessage());
            }
            
            ThreadUtils.printThreadPoolStatus(executor);
            ThreadUtils.shutdownGracefully(executor, 5);
            
            log.info("线程池异常处理测试完成");
            
        } catch (Exception e) {
            log.error("线程池异常处理测试失败: {}", e.getMessage(), e);
            throw e;
        }
        
        log.info("=== 线程池异常处理测试结束 ===");
    }
    
    /**
     * 测试所有ExecutorService演示功能
     */
    @Test
    void testAllExecutorServiceDemonstrations() {
        log.info("=== 开始测试所有ExecutorService演示功能 ===");
        
        ExecutorServiceDemo demo = new ExecutorServiceDemo();
        
        try {
            // 运行所有演示
            demo.runAllDemonstrations();
            
            // 等待所有演示完成
            ThreadUtils.sleep(8000);
            
            log.info("所有ExecutorService演示功能测试完成");
            
        } catch (Exception e) {
            log.error("ExecutorService演示功能测试失败: {}", e.getMessage(), e);
            throw e;
        }
        
        log.info("=== 所有ExecutorService演示功能测试结束 ===");
    }
}