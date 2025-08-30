package com.example.treaconcurrent.utils;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程工具类
 * 提供线程相关的常用工具方法
 * 
 * @author SOLO Coding
 * @since 2024-01-20
 */
@Slf4j
public class ThreadUtils {
    
    /**
     * 私有构造函数，防止实例化
     */
    private ThreadUtils() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }
    
    /**
     * 安全地休眠指定时间
     * 忽略InterruptedException异常
     * 
     * @param millis 休眠时间（毫秒）
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // 恢复中断状态
            Thread.currentThread().interrupt();
            log.warn("线程休眠被中断: {}", e.getMessage());
        }
    }
    
    /**
     * 安全地休眠指定时间
     * 可以选择是否在被中断时抛出异常
     * 
     * @param millis 休眠时间（毫秒）
     * @param throwOnInterrupt 被中断时是否抛出异常
     * @throws InterruptedException 当throwOnInterrupt为true且线程被中断时抛出
     */
    public static void sleep(long millis, boolean throwOnInterrupt) throws InterruptedException {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            if (throwOnInterrupt) {
                throw e;
            }
            log.warn("线程休眠被中断: {}", e.getMessage());
        }
    }
    
    /**
     * 获取当前线程信息
     * 
     * @return 线程信息字符串
     */
    public static String getCurrentThreadInfo() {
        Thread currentThread = Thread.currentThread();
        return String.format("线程[%s] ID=%d, 优先级=%d, 状态=%s, 是否守护线程=%s", 
                currentThread.getName(), 
                currentThread.getId(), 
                currentThread.getPriority(), 
                currentThread.getState(), 
                currentThread.isDaemon());
    }
    
    /**
     * 打印当前线程信息
     */
    public static void printCurrentThreadInfo() {
        log.info("当前{}", getCurrentThreadInfo());
    }
    
    /**
     * 获取线程池状态信息
     * 
     * @param executor 线程池执行器
     * @return 状态信息字符串
     */
    public static String getThreadPoolStatus(ThreadPoolExecutor executor) {
        if (executor == null) {
            return "线程池为null";
        }
        
        return String.format("线程池状态: 核心线程数=%d, 最大线程数=%d, 当前线程数=%d, 活跃线程数=%d, " +
                        "队列大小=%d, 已完成任务数=%d, 总任务数=%d, 是否关闭=%s, 是否终止=%s", 
                executor.getCorePoolSize(), 
                executor.getMaximumPoolSize(), 
                executor.getPoolSize(), 
                executor.getActiveCount(), 
                executor.getQueue().size(), 
                executor.getCompletedTaskCount(), 
                executor.getTaskCount(), 
                executor.isShutdown(), 
                executor.isTerminated());
    }
    
    /**
     * 打印线程池状态信息
     * 
     * @param executor 线程池执行器
     */
    public static void printThreadPoolStatus(ThreadPoolExecutor executor) {
        log.info(getThreadPoolStatus(executor));
    }
    
    /**
     * 优雅地关闭线程池
     * 
     * @param executor 线程池执行器
     * @param timeoutSeconds 等待超时时间（秒）
     * @return true表示成功关闭，false表示超时
     */
    public static boolean shutdownGracefully(ExecutorService executor, int timeoutSeconds) {
        if (executor == null || executor.isShutdown()) {
            return true;
        }
        
        try {
            log.info("开始关闭线程池...");
            
            // 停止接收新任务
            executor.shutdown();
            
            // 等待现有任务完成
            if (!executor.awaitTermination(timeoutSeconds, TimeUnit.SECONDS)) {
                log.warn("线程池在{}秒内未能正常关闭，强制关闭", timeoutSeconds);
                
                // 强制关闭
                executor.shutdownNow();
                
                // 再次等待
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    log.error("线程池无法正常关闭");
                    return false;
                }
            }
            
            log.info("线程池已成功关闭");
            return true;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("关闭线程池时被中断: {}", e.getMessage());
            executor.shutdownNow();
            return false;
        }
    }
    
    /**
     * 创建自定义线程工厂
     * 
     * @param namePrefix 线程名称前缀
     * @param daemon 是否为守护线程
     * @return 线程工厂
     */
    public static ThreadFactory createThreadFactory(String namePrefix, boolean daemon) {
        return new CustomThreadFactory(namePrefix, daemon);
    }
    
    /**
     * 创建自定义线程工厂（非守护线程）
     * 
     * @param namePrefix 线程名称前缀
     * @return 线程工厂
     */
    public static ThreadFactory createThreadFactory(String namePrefix) {
        return createThreadFactory(namePrefix, false);
    }
    
    /**
     * 生成唯一的任务ID
     * 
     * @return 任务ID
     */
    public static String generateTaskId() {
        return IdUtil.fastSimpleUUID();
    }
    
    /**
     * 生成带前缀的任务ID
     * 
     * @param prefix 前缀
     * @return 任务ID
     */
    public static String generateTaskId(String prefix) {
        return prefix + "-" + IdUtil.fastSimpleUUID();
    }
    
    /**
     * 检查线程是否被中断
     * 
     * @throws InterruptedException 如果线程被中断则抛出异常
     */
    public static void checkInterrupted() throws InterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException("线程已被中断");
        }
    }
    
    /**
     * 等待所有Future完成
     * 
     * @param futures Future列表
     * @param timeoutSeconds 超时时间（秒）
     * @return true表示所有任务都完成，false表示超时
     */
    public static boolean waitForAll(java.util.List<Future<?>> futures, int timeoutSeconds) {
        long deadline = System.currentTimeMillis() + timeoutSeconds * 1000L;
        
        for (Future<?> future : futures) {
            try {
                long remaining = deadline - System.currentTimeMillis();
                if (remaining <= 0) {
                    return false;
                }
                
                future.get(remaining, TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                log.warn("等待任务完成超时");
                return false;
            } catch (Exception e) {
                log.error("任务执行异常: {}", e.getMessage());
            }
        }
        
        return true;
    }
    
    /**
     * 自定义线程工厂
     */
    private static class CustomThreadFactory implements ThreadFactory {
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;
        private final boolean daemon;
        
        CustomThreadFactory(String namePrefix, boolean daemon) {
            this.namePrefix = namePrefix;
            this.daemon = daemon;
        }
        
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, namePrefix + "-" + threadNumber.getAndIncrement());
            thread.setDaemon(daemon);
            
            // 设置默认优先级
            if (thread.getPriority() != Thread.NORM_PRIORITY) {
                thread.setPriority(Thread.NORM_PRIORITY);
            }
            
            return thread;
        }
    }
    
    /**
     * 计算合适的线程池大小
     * 
     * @param cpuIntensiveRatio CPU密集型任务比例（0.0-1.0）
     * @return 建议的线程池大小
     */
    public static int calculateOptimalThreadPoolSize(double cpuIntensiveRatio) {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        
        if (cpuIntensiveRatio >= 0.8) {
            // CPU密集型任务，线程数接近CPU核心数
            return availableProcessors;
        } else if (cpuIntensiveRatio <= 0.2) {
            // IO密集型任务，线程数可以是CPU核心数的2-3倍
            return availableProcessors * 3;
        } else {
            // 混合型任务，根据比例计算
            return (int) (availableProcessors * (1 + (1 - cpuIntensiveRatio)));
        }
    }
    
    /**
     * 获取系统信息
     * 
     * @return 系统信息字符串
     */
    public static String getSystemInfo() {
        Runtime runtime = Runtime.getRuntime();
        return String.format("系统信息: CPU核心数=%d, 最大内存=%dMB, 总内存=%dMB, 空闲内存=%dMB", 
                runtime.availableProcessors(), 
                runtime.maxMemory() / 1024 / 1024, 
                runtime.totalMemory() / 1024 / 1024, 
                runtime.freeMemory() / 1024 / 1024);
    }
    
    /**
     * 打印系统信息
     */
    public static void printSystemInfo() {
        log.info(getSystemInfo());
    }
}