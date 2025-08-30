package com.example.treaconcurrent.basics;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.example.treaconcurrent.config.ThreadPoolConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ExecutorService线程池演示类
 * 展示各种线程池的配置、使用方法和适用场景
 * 
 * @author SOLO Coding
 * @since 2024-01-20
 */
@Slf4j
public class ExecutorServiceDemo {

    private static final AtomicInteger taskCounter = new AtomicInteger(0);

    public static void main(String[] args) {
        log.info("=== ExecutorService线程池演示开始 ===");
        
        // 首先解释线程池参数
        ThreadPoolConfig.explainThreadPoolParameters();
        
        // 1. 演示固定大小线程池
        demonstrateFixedThreadPoolStatic();
        
        // 2. 演示缓存线程池
        demonstrateCachedThreadPoolStatic();
        
        // 3. 演示单线程池
        demonstrateSingleThreadExecutorStatic();
        
        // 4. 演示定时任务线程池
        demonstrateScheduledThreadPoolStatic();
        
        // 5. 演示自定义线程池
        demonstrateCustomThreadPoolStatic();
        
        // 6. 演示线程池监控
        demonstrateThreadPoolMonitoringStatic();
        
        log.info("=== ExecutorService线程池演示结束 ===");
    }

    /**
     * 演示固定大小线程池
     * 特点：线程数量固定，适用于负载比较重的服务器
     */
    public void demonstrateFixedThreadPool() {
        demonstrateFixedThreadPoolStatic();
    }
    
    /**
     * 演示固定大小线程池（静态方法）
     * 特点：线程数量固定，适用于负载比较重的服务器
     */
    private static void demonstrateFixedThreadPoolStatic() {
        log.info("\n--- 1. 固定大小线程池演示 ---");
        
        ExecutorService fixedPool = ThreadPoolConfig.createFixedThreadPool(3);
        
        try {
            // 提交多个任务
            List<Future<String>> futures = new ArrayList<>();
            for (int i = 1; i <= 6; i++) {
                final int taskId = i;
                Future<String> future = fixedPool.submit(() -> {
                    String threadName = Thread.currentThread().getName();
                    log.info("固定线程池 - 任务{} 开始执行，线程：{}", taskId, threadName);
                    
                    // 模拟任务执行
                    ThreadUtil.sleep(2000);
                    
                    String result = String.format("任务%d执行完成，线程：%s", taskId, threadName);
                    log.info("固定线程池 - {}", result);
                    return result;
                });
                futures.add(future);
            }
            
            // 获取所有任务结果
            for (Future<String> future : futures) {
                try {
                    String result = future.get(5, TimeUnit.SECONDS);
                    log.info("固定线程池 - 获取到结果：{}", result);
                } catch (TimeoutException e) {
                    log.error("固定线程池 - 任务执行超时", e);
                }
            }
            
        } catch (Exception e) {
            log.error("固定线程池执行异常", e);
        } finally {
            shutdownExecutor(fixedPool, "固定线程池");
        }
    }

    /**
     * 演示缓存线程池
     * 特点：线程数量可变，适用于执行很多短期异步的小程序
     */
    public void demonstrateCachedThreadPool() {
        demonstrateCachedThreadPoolStatic();
    }
    
    /**
     * 演示缓存线程池（静态方法）
     * 特点：线程数量可变，适用于执行很多短期异步的小程序
     */
    private static void demonstrateCachedThreadPoolStatic() {
        log.info("\n--- 2. 缓存线程池演示 ---");
        
        ExecutorService cachedPool = ThreadPoolConfig.createCachedThreadPool();
        
        try {
            // 提交大量短期任务
            for (int i = 1; i <= 8; i++) {
                final int taskId = i;
                cachedPool.submit(() -> {
                    String threadName = Thread.currentThread().getName();
                    log.info("缓存线程池 - 任务{} 开始执行，线程：{}", taskId, threadName);
                    
                    // 模拟短期任务
                    ThreadUtil.sleep(500);
                    
                    log.info("缓存线程池 - 任务{} 执行完成，线程：{}", taskId, threadName);
                });
                
                // 间隔提交任务
                ThreadUtil.sleep(100);
            }
            
            // 等待所有任务完成
            ThreadUtil.sleep(3000);
            
        } catch (Exception e) {
            log.error("缓存线程池执行异常", e);
        } finally {
            shutdownExecutor(cachedPool, "缓存线程池");
        }
    }

    /**
     * 演示单线程池
     * 特点：只有一个线程，保证任务按顺序执行
     */
    public void demonstrateSingleThreadExecutor() {
        demonstrateSingleThreadExecutorStatic();
    }
    
    /**
     * 演示单线程池（静态方法）
     * 特点：只有一个线程，保证任务按顺序执行
     */
    private static void demonstrateSingleThreadExecutorStatic() {
        log.info("\n--- 3. 单线程池演示 ---");
        
        ExecutorService singlePool = ThreadPoolConfig.createSingleThreadExecutor();
        
        try {
            // 提交多个任务，验证顺序执行
            for (int i = 1; i <= 5; i++) {
                final int taskId = i;
                singlePool.submit(() -> {
                    String threadName = Thread.currentThread().getName();
                    log.info("单线程池 - 任务{} 开始执行，线程：{}，时间：{}", 
                            taskId, threadName, DateUtil.now());
                    
                    // 模拟任务执行
                    ThreadUtil.sleep(800);
                    
                    log.info("单线程池 - 任务{} 执行完成，线程：{}，时间：{}", 
                            taskId, threadName, DateUtil.now());
                });
            }
            
            // 等待所有任务完成
            ThreadUtil.sleep(5000);
            
        } catch (Exception e) {
            log.error("单线程池执行异常", e);
        } finally {
            shutdownExecutor(singlePool, "单线程池");
        }
    }

    /**
     * 演示定时任务线程池
     * 特点：支持定时和周期性任务执行
     */
    public void demonstrateScheduledThreadPool() {
        demonstrateScheduledThreadPoolStatic();
    }
    
    /**
     * 演示定时任务线程池（静态方法）
     * 特点：支持定时和周期性任务执行
     */
    private static void demonstrateScheduledThreadPoolStatic() {
        log.info("\n--- 4. 定时任务线程池演示 ---");
        
        ScheduledExecutorService scheduledPool = ThreadPoolConfig.createScheduledThreadPool(2);
        
        try {
            // 1. 延迟执行任务
            log.info("提交延迟执行任务，3秒后执行");
            ScheduledFuture<?> delayedTask = scheduledPool.schedule(() -> {
                log.info("延迟任务执行，当前时间：{}", DateUtil.now());
            }, 3, TimeUnit.SECONDS);
            
            // 2. 固定频率执行任务
            log.info("提交固定频率任务，每2秒执行一次");
            ScheduledFuture<?> fixedRateTask = scheduledPool.scheduleAtFixedRate(() -> {
                int count = taskCounter.incrementAndGet();
                log.info("固定频率任务执行第{}次，当前时间：{}", count, DateUtil.now());
                if (count >= 3) {
                    log.info("固定频率任务达到执行次数限制，停止执行");
                    throw new RuntimeException("任务完成");
                }
            }, 1, 2, TimeUnit.SECONDS);
            
            // 3. 固定延迟执行任务
            log.info("提交固定延迟任务，每次执行完成后延迟1.5秒再执行");
            AtomicInteger delayCounter = new AtomicInteger(0);
            ScheduledFuture<?> fixedDelayTask = scheduledPool.scheduleWithFixedDelay(() -> {
                int count = delayCounter.incrementAndGet();
                log.info("固定延迟任务执行第{}次，当前时间：{}", count, DateUtil.now());
                
                // 模拟任务执行时间
                ThreadUtil.sleep(1000);
                
                if (count >= 3) {
                    log.info("固定延迟任务达到执行次数限制，停止执行");
                    throw new RuntimeException("任务完成");
                }
            }, 2, 1500, TimeUnit.MILLISECONDS);
            
            // 等待任务执行
            ThreadUtil.sleep(12000);
            
        } catch (Exception e) {
            log.error("定时任务线程池执行异常", e);
        } finally {
            shutdownExecutor(scheduledPool, "定时任务线程池");
        }
    }

    /**
     * 演示自定义线程池
     * 特点：可以精确控制各个参数，提供最大的灵活性
     */
    public void demonstrateCustomThreadPool() {
        demonstrateCustomThreadPoolStatic();
    }
    
    /**
     * 演示自定义线程池（静态方法）
     * 特点：可以精确控制各个参数，提供最大的灵活性
     */
    private static void demonstrateCustomThreadPoolStatic() {
        log.info("\n--- 5. 自定义线程池演示 ---");
        
        ThreadPoolConfig config = new ThreadPoolConfig();
        config.setCorePoolSize(2);
        config.setMaximumPoolSize(4);
        config.setKeepAliveTime(30L);
        config.setQueueCapacity(5);
        config.setThreadNamePrefix("custom-demo-");
        
        ThreadPoolExecutor customPool = config.createCustomThreadPoolExecutor();
        
        try {
            // 提交任务测试线程池行为
            for (int i = 1; i <= 10; i++) {
                final int taskId = i;
                try {
                    customPool.submit(() -> {
                        String threadName = Thread.currentThread().getName();
                        log.info("自定义线程池 - 任务{} 开始执行，线程：{}，当前时间：{}", 
                                taskId, threadName, DateUtil.now());
                        
                        // 模拟任务执行
                        ThreadUtil.sleep(3000);
                        
                        log.info("自定义线程池 - 任务{} 执行完成，线程：{}", taskId, threadName);
                    });
                    
                    // 打印线程池状态
                    log.info("提交任务{}后 - 活跃线程数：{}，队列大小：{}，已完成任务数：{}",
                            taskId, customPool.getActiveCount(), 
                            customPool.getQueue().size(), customPool.getCompletedTaskCount());
                    
                    ThreadUtil.sleep(500);
                    
                } catch (RejectedExecutionException e) {
                    log.warn("任务{}被拒绝执行：{}", taskId, e.getMessage());
                }
            }
            
            // 等待任务执行完成
            ThreadUtil.sleep(15000);
            
        } catch (Exception e) {
            log.error("自定义线程池执行异常", e);
        } finally {
            shutdownExecutor(customPool, "自定义线程池");
        }
    }

    /**
     * 演示线程池监控
     * 展示如何监控线程池的运行状态和性能指标
     */
    public void demonstrateThreadPoolMonitoring() {
        demonstrateThreadPoolMonitoringStatic();
    }
    
    /**
     * 演示线程池监控（静态方法）
     * 展示如何监控线程池的运行状态和性能指标
     */
    private static void demonstrateThreadPoolMonitoringStatic() {
        log.info("\n--- 6. 线程池监控演示 ---");
        
        ThreadPoolExecutor monitorPool = new ThreadPoolExecutor(
                2, 4, 60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(3),
                r -> new Thread(r, "monitor-" + r.hashCode()),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        
        try {
            // 启动监控线程
            ScheduledExecutorService monitorExecutor = Executors.newSingleThreadScheduledExecutor();
            ScheduledFuture<?> monitorTask = monitorExecutor.scheduleAtFixedRate(() -> {
                log.info("=== 线程池监控信息 ===");
                log.info("核心线程数：{}", monitorPool.getCorePoolSize());
                log.info("最大线程数：{}", monitorPool.getMaximumPoolSize());
                log.info("当前线程数：{}", monitorPool.getPoolSize());
                log.info("活跃线程数：{}", monitorPool.getActiveCount());
                log.info("队列大小：{}", monitorPool.getQueue().size());
                log.info("已提交任务数：{}", monitorPool.getTaskCount());
                log.info("已完成任务数：{}", monitorPool.getCompletedTaskCount());
                log.info("是否正在关闭：{}", monitorPool.isShutdown());
                log.info("是否已终止：{}", monitorPool.isTerminated());
                log.info("========================");
            }, 1, 2, TimeUnit.SECONDS);
            
            // 提交任务
            for (int i = 1; i <= 8; i++) {
                final int taskId = i;
                monitorPool.submit(() -> {
                    log.info("监控演示 - 任务{} 开始执行，线程：{}", 
                            taskId, Thread.currentThread().getName());
                    ThreadUtil.sleep(4000);
                    log.info("监控演示 - 任务{} 执行完成", taskId);
                });
                ThreadUtil.sleep(800);
            }
            
            // 等待任务执行完成
            ThreadUtil.sleep(20000);
            
            // 停止监控
            monitorTask.cancel(true);
            monitorExecutor.shutdown();
            
        } catch (Exception e) {
            log.error("线程池监控演示异常", e);
        } finally {
            shutdownExecutor(monitorPool, "监控线程池");
        }
    }

    /**
     * 优雅关闭线程池
     * 
     * @param executor 要关闭的线程池
     * @param poolName 线程池名称
     */
    private static void shutdownExecutor(ExecutorService executor, String poolName) {
        log.info("开始关闭{}...", poolName);
        
        // 停止接收新任务
        executor.shutdown();
        
        try {
            // 等待已提交的任务完成
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                log.warn("{}未能在10秒内正常关闭，强制关闭", poolName);
                // 强制关闭
                executor.shutdownNow();
                
                // 再次等待
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    log.error("{}无法正常关闭", poolName);
                }
            }
        } catch (InterruptedException e) {
            log.error("等待{}关闭时被中断", poolName, e);
            // 强制关闭
            executor.shutdownNow();
            // 恢复中断状态
            Thread.currentThread().interrupt();
        }
        
        log.info("{}已关闭", poolName);
    }
    
    /**
     * 运行所有演示
     */
    public void runAllDemonstrations() {
        log.info("=== 开始运行所有ExecutorService演示 ===");
        
        demonstrateFixedThreadPool();
        ThreadUtil.sleep(2000);
        
        demonstrateCachedThreadPool();
        ThreadUtil.sleep(2000);
        
        demonstrateSingleThreadExecutor();
        ThreadUtil.sleep(2000);
        
        demonstrateScheduledThreadPool();
        ThreadUtil.sleep(2000);
        
        demonstrateCustomThreadPool();
        ThreadUtil.sleep(2000);
        
        demonstrateThreadPoolMonitoring();
        
        log.info("=== 所有ExecutorService演示完成 ===");
    }
}