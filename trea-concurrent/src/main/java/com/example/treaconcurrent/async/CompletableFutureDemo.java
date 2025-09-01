package com.example.treaconcurrent.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * CompletableFuture演示类
 * 展示CompletableFuture的各种用法和特性
 * 
 * @author SOLO Coding
 * @since 2024-01-01
 */
@Slf4j
@Component
public class CompletableFutureDemo {
    
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    
    /**
     * 演示CompletableFuture基础用法
     */
    public void demonstrateBasicUsage() {
        log.info("=== CompletableFuture基础用法演示开始 ===");
        
        try {
            // 1. 创建已完成的CompletableFuture
            CompletableFuture<String> completedFuture = CompletableFuture.completedFuture("已完成的任务");
            log.info("已完成的任务结果: {}", completedFuture.get());
            
            // 2. 使用supplyAsync创建异步任务
            CompletableFuture<String> asyncFuture = CompletableFuture.supplyAsync(() -> {
                log.info("异步任务执行中...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return "异步任务完成";
            }, executor);
            
            log.info("异步任务结果: {}", asyncFuture.get());
            
            // 3. 使用runAsync创建无返回值的异步任务
            CompletableFuture<Void> runAsyncFuture = CompletableFuture.runAsync(() -> {
                log.info("无返回值的异步任务执行");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                log.info("无返回值的异步任务完成");
            }, executor);
            
            runAsyncFuture.get();
            
        } catch (Exception e) {
            log.error("基础用法演示异常", e);
        }
        
        log.info("=== CompletableFuture基础用法演示结束 ===");
    }
    
    /**
     * 演示CompletableFuture异常处理
     * 
     * @return 异常处理演示的CompletableFuture
     */
    public CompletableFuture<String> demonstrateExceptionHandling() {
        log.info("=== CompletableFuture异常处理演示开始 ===");
        
        try {
            // 1. 使用handle处理异常和正常结果
            CompletableFuture<String> futureWithHandle = CompletableFuture.supplyAsync(() -> {
                log.info("可能抛出异常的任务");
                if (Math.random() > 0.5) {
                    throw new RuntimeException("随机异常");
                }
                return "正常结果";
            }, executor).handle((result, throwable) -> {
                if (throwable != null) {
                    log.error("handle捕获异常: {}", throwable.getMessage());
                    return "异常处理后的结果";
                } else {
                    log.info("handle处理正常结果: {}", result);
                    return result;
                }
            });
            
            log.info("handle处理结果: {}", futureWithHandle.get());
            
            // 2. 使用exceptionally处理异常
            CompletableFuture<String> futureWithExceptionally = CompletableFuture.<String>supplyAsync(() -> {
                log.info("必定抛出异常的任务");
                throw new RuntimeException("必定异常");
            }, executor).exceptionally(throwable -> {
                log.error("exceptionally捕获异常: {}", throwable.getMessage());
                return "异常恢复值";
            });
            
            log.info("exceptionally处理结果: {}", futureWithExceptionally.get());
            
            // 3. 使用whenComplete进行最终处理
            CompletableFuture<String> futureWithWhenComplete = CompletableFuture.supplyAsync(() -> {
                log.info("whenComplete演示任务");
                return "正常结果";
            }, executor);
            
            // 添加whenComplete处理，但不改变返回类型
            futureWithWhenComplete.whenComplete((result, throwable) -> {
                if (throwable != null) {
                    log.error("whenComplete检测到异常: {}", throwable.getMessage());
                } else {
                    log.info("whenComplete检测到正常结果: {}", result);
                }
            });
            
            log.info("whenComplete处理结果: {}", futureWithWhenComplete.get());
            
            // 返回最后一个CompletableFuture作为演示结果
            return futureWithExceptionally; // 返回exceptionally处理的结果，确保类型正确
            
        } catch (Exception e) {
            log.error("异常处理演示异常", e);
            // 异常情况下返回一个包含错误信息的CompletableFuture
            return CompletableFuture.completedFuture("异常处理演示失败: " + e.getMessage());
        } finally {
            log.info("=== CompletableFuture异常处理演示结束 ===");
        }
    }
    
    /**
     * 演示CompletableFuture组合操作
     */
    public void demonstrateComposition() {
        log.info("=== CompletableFuture组合操作演示开始 ===");
        
        try {
            // 1. thenApply - 对结果进行转换
            CompletableFuture<Integer> thenApplyFuture = CompletableFuture.supplyAsync(() -> {
                log.info("thenApply源任务执行");
                return 10;
            }, executor).thenApply(result -> {
                log.info("thenApply转换操作，输入: {}", result);
                return result * 2;
            });
            
            log.info("thenApply结果: {}", thenApplyFuture.get());
            
            // 2. thenCompose - 组合两个CompletableFuture
            CompletableFuture<String> thenComposeFuture = CompletableFuture.supplyAsync(() -> {
                log.info("thenCompose第一个任务");
                return "Hello";
            }, executor).thenCompose(result -> {
                log.info("thenCompose组合操作，第一个结果: {}", result);
                return CompletableFuture.supplyAsync(() -> {
                    log.info("thenCompose第二个任务");
                    return result + " World";
                }, executor);
            });
            
            log.info("thenCompose结果: {}", thenComposeFuture.get());
            
            // 3. thenCombine - 合并两个独立的CompletableFuture
            CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
                log.info("thenCombine任务1执行");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return "任务1";
            }, executor);
            
            CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
                log.info("thenCombine任务2执行");
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return "任务2";
            }, executor);
            
            CompletableFuture<String> combinedFuture = future1.thenCombine(future2, (result1, result2) -> {
                log.info("thenCombine合并操作，结果1: {}, 结果2: {}", result1, result2);
                return result1 + " + " + result2;
            });
            
            log.info("thenCombine结果: {}", combinedFuture.get());
            
        } catch (Exception e) {
            log.error("组合操作演示异常", e);
        }
        
        log.info("=== CompletableFuture组合操作演示结束 ===");
    }
    
    /**
     * 演示CompletableFuture链式调用
     */
    public void demonstrateChaining() {
        log.info("=== CompletableFuture链式调用演示开始 ===");
        
        try {
            // 复杂的链式调用示例
            CompletableFuture<String> chainedFuture = CompletableFuture
                .supplyAsync(() -> {
                    log.info("链式调用 - 步骤1: 获取用户ID");
                    return "user123";
                }, executor)
                .thenApplyAsync(userId -> {
                    log.info("链式调用 - 步骤2: 根据用户ID获取用户信息, userId: {}", userId);
                    try {
                        Thread.sleep(500); // 模拟数据库查询
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return "用户信息:" + userId;
                }, executor)
                .thenApplyAsync(userInfo -> {
                    log.info("链式调用 - 步骤3: 处理用户信息, userInfo: {}", userInfo);
                    return userInfo + ",已处理";
                }, executor)
                .thenApplyAsync(processedInfo -> {
                    log.info("链式调用 - 步骤4: 格式化输出, processedInfo: {}", processedInfo);
                    return "[" + processedInfo + "]";
                }, executor)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        log.error("链式调用过程中发生异常", throwable);
                    } else {
                        log.info("链式调用完成，最终结果: {}", result);
                    }
                });
            
            String finalResult = chainedFuture.get();
            log.info("链式调用最终结果: {}", finalResult);
            
        } catch (Exception e) {
            log.error("链式调用演示异常", e);
        }
        
        log.info("=== CompletableFuture链式调用演示结束 ===");
    }
    
    /**
     * 演示CompletableFuture批量处理
     */
    public void demonstrateBatchProcessing() {
        log.info("=== CompletableFuture批量处理演示开始 ===");
        
        try {
            // 创建多个异步任务
            List<CompletableFuture<String>> futures = Arrays.asList(
                CompletableFuture.supplyAsync(() -> processTask("任务1", 1000), executor),
                CompletableFuture.supplyAsync(() -> processTask("任务2", 800), executor),
                CompletableFuture.supplyAsync(() -> processTask("任务3", 1200), executor),
                CompletableFuture.supplyAsync(() -> processTask("任务4", 600), executor)
            );
            
            // 等待所有任务完成
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
            );
            
            // 收集所有结果
            CompletableFuture<List<String>> allResults = allFutures.thenApply(v -> 
                futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList())
            );
            
            List<String> results = allResults.get();
            log.info("所有任务完成，结果: {}", results);
            
            // 演示anyOf - 任意一个完成即返回
            List<CompletableFuture<String>> anyOfFutures = Arrays.asList(
                CompletableFuture.supplyAsync(() -> processTask("快速任务1", 300), executor),
                CompletableFuture.supplyAsync(() -> processTask("快速任务2", 500), executor),
                CompletableFuture.supplyAsync(() -> processTask("快速任务3", 200), executor)
            );
            
            // 注意：anyOf返回的是Object类型，需要进行类型转换
            CompletableFuture<Object> anyResult = CompletableFuture.anyOf(
                anyOfFutures.toArray(new CompletableFuture[0])
            );
            
            Object firstResult = anyResult.get();
            log.info("最快完成的任务结果: {}", firstResult);
            
        } catch (Exception e) {
            log.error("批量处理演示异常", e);
        }
        
        log.info("=== CompletableFuture批量处理演示结束 ===");
    }
    
    /**
     * 模拟任务处理
     * 
     * @param taskName 任务名称
     * @param delayMs 延迟毫秒数
     * @return 处理结果
     */
    private String processTask(String taskName, long delayMs) {
        try {
            log.info("开始处理{}, 预计耗时{}ms", taskName, delayMs);
            Thread.sleep(delayMs);
            String result = taskName + "处理完成";
            log.info("{}", result);
            return result;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return taskName + "被中断";
        }
    }
    
    /**
     * 关闭线程池
     */
    public void shutdown() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * 运行所有演示
     */
    public void runAllDemonstrations() {
        log.info("=== 开始运行所有CompletableFuture演示 ===");
        
        demonstrateBasicUsage();
        demonstrateExceptionHandling();
        demonstrateComposition();
        demonstrateChaining();
        demonstrateBatchProcessing();
        
        log.info("=== 所有CompletableFuture演示完成 ===");
    }
    
    /**
     * 主方法，用于独立运行演示
     */
    public static void main(String[] args) {
        CompletableFutureDemo demo = new CompletableFutureDemo();
        try {
            demo.runAllDemonstrations();
        } finally {
            demo.shutdown();
        }
    }
}