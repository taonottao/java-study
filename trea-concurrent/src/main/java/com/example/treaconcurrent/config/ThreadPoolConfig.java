package com.example.treaconcurrent.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;

/**
 * 线程池配置管理类
 * 提供各种类型的线程池配置和创建方法，包含详细的参数说明
 * 
 * @author SOLO Coding
 * @since 2024-01-20
 */
@Slf4j
@Configuration
@Data
public class ThreadPoolConfig {

    /**
     * 核心线程数 - 线程池中始终保持的线程数量
     * 即使这些线程处于空闲状态，也不会被回收
     */
    private int corePoolSize = 5;

    /**
     * 最大线程数 - 线程池中允许的最大线程数量
     * 当工作队列满了之后，会创建新线程直到达到最大线程数
     */
    private int maximumPoolSize = 10;

    /**
     * 线程空闲时间 - 非核心线程的最大空闲时间
     * 超过这个时间的空闲线程会被回收
     */
    private long keepAliveTime = 60L;

    /**
     * 时间单位 - keepAliveTime的时间单位
     */
    private TimeUnit timeUnit = TimeUnit.SECONDS;

    /**
     * 工作队列容量 - 用于存放等待执行任务的队列大小
     */
    private int queueCapacity = 100;

    /**
     * 线程名称前缀 - 便于线程识别和调试
     */
    private String threadNamePrefix = "async-task-";

    /**
     * 拒绝策略 - 当线程池和队列都满时的处理策略
     */
    private RejectedExecutionHandler rejectedExecutionHandler = new ThreadPoolExecutor.CallerRunsPolicy();

    /**
     * 创建自定义线程池执行器
     * 适用于Spring环境下的异步任务执行
     * 
     * @return ThreadPoolTaskExecutor Spring线程池执行器
     */
    @Bean(name = "customTaskExecutor")
    public ThreadPoolTaskExecutor customTaskExecutor() {
        log.info("创建自定义线程池执行器...");
        
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // 设置核心线程数
        executor.setCorePoolSize(corePoolSize);
        log.info("核心线程数设置为：{}", corePoolSize);
        
        // 设置最大线程数
        executor.setMaxPoolSize(maximumPoolSize);
        log.info("最大线程数设置为：{}", maximumPoolSize);
        
        // 设置队列容量
        executor.setQueueCapacity(queueCapacity);
        log.info("队列容量设置为：{}", queueCapacity);
        
        // 设置线程空闲时间
        executor.setKeepAliveSeconds((int) keepAliveTime);
        log.info("线程空闲时间设置为：{} 秒", keepAliveTime);
        
        // 设置线程名称前缀
        executor.setThreadNamePrefix(threadNamePrefix);
        log.info("线程名称前缀设置为：{}", threadNamePrefix);
        
        // 设置拒绝策略
        executor.setRejectedExecutionHandler(rejectedExecutionHandler);
        log.info("拒绝策略设置为：{}", rejectedExecutionHandler.getClass().getSimpleName());
        
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        
        // 等待时间（秒）
        executor.setAwaitTerminationSeconds(60);
        
        // 初始化线程池
        executor.initialize();
        
        log.info("自定义线程池执行器创建完成");
        return executor;
    }

    /**
     * 创建固定大小的线程池
     * 适用于负载比较重的服务器，为了资源的合理利用
     * 
     * @param nThreads 线程数量
     * @return ExecutorService 线程池服务
     */
    public static ExecutorService createFixedThreadPool(int nThreads) {
        log.info("创建固定大小线程池，线程数：{}", nThreads);
        return Executors.newFixedThreadPool(nThreads, r -> {
            Thread thread = new Thread(r);
            thread.setName("fixed-pool-" + thread.getId());
            thread.setDaemon(false);
            return thread;
        });
    }

    /**
     * 创建缓存线程池
     * 适用于执行很多短期异步的小程序或者负载较轻的服务器
     * 
     * @return ExecutorService 线程池服务
     */
    public static ExecutorService createCachedThreadPool() {
        log.info("创建缓存线程池");
        return Executors.newCachedThreadPool(r -> {
            Thread thread = new Thread(r);
            thread.setName("cached-pool-" + thread.getId());
            thread.setDaemon(false);
            return thread;
        });
    }

    /**
     * 创建单线程池
     * 适用于需要保证顺序执行各个任务的场景
     * 
     * @return ExecutorService 线程池服务
     */
    public static ExecutorService createSingleThreadExecutor() {
        log.info("创建单线程池");
        return Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r);
            thread.setName("single-pool-" + thread.getId());
            thread.setDaemon(false);
            return thread;
        });
    }

    /**
     * 创建定时任务线程池
     * 适用于需要定时执行任务的场景
     * 
     * @param corePoolSize 核心线程数
     * @return ScheduledExecutorService 定时任务线程池服务
     */
    public static ScheduledExecutorService createScheduledThreadPool(int corePoolSize) {
        log.info("创建定时任务线程池，核心线程数：{}", corePoolSize);
        return Executors.newScheduledThreadPool(corePoolSize, r -> {
            Thread thread = new Thread(r);
            thread.setName("scheduled-pool-" + thread.getId());
            thread.setDaemon(false);
            return thread;
        });
    }

    /**
     * 创建自定义ThreadPoolExecutor
     * 提供最大的灵活性，可以精确控制各个参数
     * 
     * @return ThreadPoolExecutor 自定义线程池执行器
     */
    public ThreadPoolExecutor createCustomThreadPoolExecutor() {
        log.info("创建自定义ThreadPoolExecutor");
        
        // 创建工作队列
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(queueCapacity);
        
        // 创建线程工厂
        ThreadFactory threadFactory = r -> {
            Thread thread = new Thread(r);
            thread.setName(threadNamePrefix + thread.getId());
            thread.setDaemon(false);
            return thread;
        };
        
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                timeUnit,
                workQueue,
                threadFactory,
                rejectedExecutionHandler
        );
        
        log.info("自定义ThreadPoolExecutor创建完成，参数：核心线程数={}, 最大线程数={}, 空闲时间={}秒, 队列容量={}",
                corePoolSize, maximumPoolSize, keepAliveTime, queueCapacity);
        
        return executor;
    }

    /**
     * 线程池参数说明方法
     * 详细解释各个参数的作用和选择策略
     */
    public static void explainThreadPoolParameters() {
        log.info("\n=== 线程池参数详细说明 ===");
        
        log.info("\n1. corePoolSize（核心线程数）：");
        log.info("   - 线程池中始终保持的线程数量，即使处于空闲状态也不会被回收");
        log.info("   - 建议值：CPU密集型任务 = CPU核心数，IO密集型任务 = CPU核心数 * 2");
        
        log.info("\n2. maximumPoolSize（最大线程数）：");
        log.info("   - 线程池中允许的最大线程数量");
        log.info("   - 当工作队列满了之后，会创建新线程直到达到最大线程数");
        log.info("   - 建议值：根据系统负载和内存情况设置，通常为核心线程数的2-4倍");
        
        log.info("\n3. keepAliveTime（线程空闲时间）：");
        log.info("   - 非核心线程的最大空闲时间，超过这个时间的空闲线程会被回收");
        log.info("   - 建议值：30-300秒，根据任务频率调整");
        
        log.info("\n4. workQueue（工作队列）：");
        log.info("   - ArrayBlockingQueue：有界队列，防止内存溢出");
        log.info("   - LinkedBlockingQueue：无界队列，可能导致内存溢出");
        log.info("   - SynchronousQueue：直接提交队列，不存储任务");
        log.info("   - PriorityBlockingQueue：优先级队列，支持任务优先级");
        
        log.info("\n5. threadFactory（线程工厂）：");
        log.info("   - 用于创建新线程，可以设置线程名称、优先级、守护线程等属性");
        log.info("   - 建议：自定义线程名称，便于问题排查和监控");
        
        log.info("\n6. rejectedExecutionHandler（拒绝策略）：");
        log.info("   - AbortPolicy：直接抛出异常（默认策略）");
        log.info("   - CallerRunsPolicy：由调用线程执行任务");
        log.info("   - DiscardPolicy：直接丢弃任务");
        log.info("   - DiscardOldestPolicy：丢弃队列中最老的任务");
        
        log.info("\n=== 参数说明结束 ===");
    }
}