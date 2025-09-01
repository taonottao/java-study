package com.example.treaconcurrent.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * 异步业务场景演示类
 * 展示如何在实际业务场景中使用异步编程
 * 
 * @author 系统
 * @since 1.0
 */
@Slf4j
@Component
public class AsyncBusinessDemo {

    @Autowired
    private AsyncService asyncService;
    
    /**
     * 默认构造函数
     */
    public AsyncBusinessDemo() {
    }
    
    /**
     * 带参数的构造函数，用于测试
     * @param asyncService 异步服务实例
     */
    public AsyncBusinessDemo(AsyncService asyncService) {
        this.asyncService = asyncService;
    }

    /**
     * 演示用户订单处理场景
     * 包含用户信息获取、订单处理、邮件通知等异步操作
     */
    public void demonstrateUserOrderProcessing() {
        log.info("=== 用户订单处理异步场景演示开始 ===");
        
        String userId = "user001";
        String orderId = "order001";
        String userEmail = "user001@example.com";
        
        try {
            // 并行执行用户信息获取和订单处理
            CompletableFuture<String> userInfoFuture = asyncService.getUserInfoAsync(userId);
            CompletableFuture<String> orderProcessFuture = asyncService.processOrderAsync(orderId);
            
            // 当用户信息和订单处理都完成后，发送邮件通知
            CompletableFuture<Boolean> emailFuture = userInfoFuture
                .thenCombine(orderProcessFuture, (userInfo, orderResult) -> {
                    log.info("用户信息和订单处理完成，准备发送邮件");
                    log.info("用户信息: {}", userInfo);
                    log.info("订单结果: {}", orderResult);
                    return orderResult;
                })
                .thenCompose(orderResult -> {
                    String subject = "订单处理完成通知";
                    String content = "您的订单已处理完成：" + orderResult;
                    return asyncService.sendEmailAsync(userEmail, subject, content);
                })
                .exceptionally(throwable -> {
                    log.error("订单处理流程异常", throwable);
                    // 发送异常通知邮件
                    try {
                        return asyncService.sendEmailAsync(userEmail, "订单处理异常", 
                            "订单处理过程中发生异常：" + throwable.getMessage()).get();
                    } catch (Exception e) {
                        log.error("发送异常通知邮件失败", e);
                        return false;
                    }
                });
            
            // 等待整个流程完成
            Boolean emailSent = emailFuture.get();
            log.info("用户订单处理流程完成，邮件发送结果: {}", emailSent);
            
        } catch (Exception e) {
            log.error("用户订单处理演示异常", e);
        }
        
        log.info("=== 用户订单处理异步场景演示结束 ===\n");
    }

    /**
     * 演示批量数据处理和报告生成场景
     */
    public void demonstrateBatchProcessingAndReporting() {
        log.info("=== 批量数据处理和报告生成异步场景演示开始 ===");
        
        List<String> dataList = Arrays.asList(
            "数据项1", "数据项2", "数据项3", "数据项4", "数据项5"
        );
        
        try {
            // 并行执行批量数据处理和报告生成
            CompletableFuture<List<String>> batchProcessFuture = 
                asyncService.batchProcessDataAsync(dataList);
            
            CompletableFuture<String> reportFuture = 
                asyncService.generateReportAsync("月度数据报告", "2024-01-01", "2024-01-31");
            
            // 当批量处理完成后，进行数据同步
            CompletableFuture<Boolean> syncFuture = batchProcessFuture
                .thenCompose(processedData -> {
                    log.info("批量处理完成，开始数据同步");
                    return asyncService.syncDataAsync("batch_source", "batch_target");
                });
            
            // 等待所有任务完成
            CompletableFuture<Void> allTasks = CompletableFuture.allOf(
                batchProcessFuture, reportFuture, syncFuture
            );
            
            allTasks.thenRun(() -> {
                try {
                    List<String> processedData = batchProcessFuture.get();
                    String report = reportFuture.get();
                    Boolean syncResult = syncFuture.get();
                    
                    log.info("所有任务完成:");
                    log.info("处理数据数量: {}", processedData.size());
                    log.info("报告生成状态: 完成");
                    log.info("数据同步结果: {}", syncResult);
                    
                } catch (Exception e) {
                    log.error("获取任务结果异常", e);
                }
            }).get();
            
        } catch (Exception e) {
            log.error("批量处理和报告生成演示异常", e);
        }
        
        log.info("=== 批量数据处理和报告生成异步场景演示结束 ===\n");
    }

    /**
     * 演示文件上传和复杂计算场景
     * 
     * @param fileName 文件名
     */
    public void demonstrateFileUploadAndCalculation(String fileName) {
        log.info("=== 文件上传和复杂计算异步场景演示开始 ===");
        
        try {
            // 并行执行多个文件上传
            List<CompletableFuture<String>> uploadFutures = Arrays.asList(
                asyncService.uploadFileAsync(fileName + ".pdf", 1024000L),
                asyncService.uploadFileAsync(fileName + ".jpg", 512000L),
                asyncService.uploadFileAsync(fileName + ".csv", 256000L)
            );
            
            // 并行执行复杂计算
            List<CompletableFuture<Integer>> calculationFutures = Arrays.asList(
                asyncService.complexCalculationAsync(10),
                asyncService.complexCalculationAsync(20),
                asyncService.complexCalculationAsync(30)
            );
            
            // 等待所有上传完成
            CompletableFuture<List<String>> allUploadsFuture = CompletableFuture
                .allOf(uploadFutures.toArray(new CompletableFuture[0]))
                .thenApply(v -> uploadFutures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList()));
            
            // 等待所有计算完成
            CompletableFuture<List<Integer>> allCalculationsFuture = CompletableFuture
                .allOf(calculationFutures.toArray(new CompletableFuture[0]))
                .thenApply(v -> calculationFutures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList()));
            
            // 合并上传和计算结果
            CompletableFuture<String> combinedResultFuture = allUploadsFuture
                .thenCombine(allCalculationsFuture, (uploadResults, calculationResults) -> {
                    log.info("文件上传和计算都完成，合并结果");
                    
                    StringBuilder result = new StringBuilder();
                    result.append("=== 任务执行结果汇总 ===\n");
                    result.append("文件上传结果:\n");
                    uploadResults.forEach(path -> result.append("  - ").append(path).append("\n"));
                    result.append("计算结果:\n");
                    calculationResults.forEach(calc -> result.append("  - ").append(calc).append("\n"));
                    
                    return result.toString();
                });
            
            // 获取最终结果
            String finalResult = combinedResultFuture.get();
            log.info("文件上传和计算场景完成:\n{}", finalResult);
            
        } catch (Exception e) {
            log.error("文件上传和计算演示异常", e);
        }
        
        log.info("=== 文件上传和复杂计算异步场景演示结束 ===\n");
    }

    /**
     * 演示异步任务的超时和重试机制
     */
    public void demonstrateTimeoutAndRetry() {
        log.info("=== 异步任务超时和重试机制演示开始 ===");
        
        try {
            // 演示带超时的异步任务
            CompletableFuture<String> timeoutTask = asyncService.getUserInfoAsync("timeout_user")
                .orTimeout(2, java.util.concurrent.TimeUnit.SECONDS)
                .exceptionally(throwable -> {
                    if (throwable instanceof java.util.concurrent.TimeoutException) {
                        log.warn("任务超时，使用默认值");
                        return "默认用户信息（超时）";
                    } else {
                        log.error("任务执行异常", throwable);
                        return "默认用户信息（异常）";
                    }
                });
            
            String timeoutResult = timeoutTask.get();
            log.info("超时处理结果: {}", timeoutResult);
            
            // 演示重试机制
            CompletableFuture<String> retryTask = retryAsync(
                () -> asyncService.processOrderAsync("retry_order"),
                3, // 最大重试次数
                1000 // 重试间隔毫秒
            );
            
            String retryResult = retryTask.get();
            log.info("重试机制结果: {}", retryResult);
            
        } catch (Exception e) {
            log.error("超时和重试演示异常", e);
        }
        
        log.info("=== 异步任务超时和重试机制演示结束 ===\n");
    }

    /**
     * 异步任务重试工具方法
     */
    private <T> CompletableFuture<T> retryAsync(
            java.util.function.Supplier<CompletableFuture<T>> taskSupplier,
            int maxRetries,
            long delayMs) {
        
        return taskSupplier.get().exceptionally(throwable -> {
            log.warn("任务执行失败，准备重试。异常: {}", throwable.getMessage());
            return null;
        }).thenCompose(result -> {
            if (result != null) {
                return CompletableFuture.completedFuture(result);
            }
            
            if (maxRetries <= 0) {
                return CompletableFuture.failedFuture(
                    new RuntimeException("重试次数已用完"));
            }
            
            log.info("等待 {}ms 后重试，剩余重试次数: {}", delayMs, maxRetries - 1);
            
            return CompletableFuture
                .runAsync(() -> {
                    try {
                        Thread.sleep(delayMs);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                })
                .thenCompose(v -> retryAsync(taskSupplier, maxRetries - 1, delayMs));
        });
    }

    /**
     * 运行所有业务场景演示
     */
    public void runAllBusinessDemonstrations() {
        log.info("=== 开始异步业务场景全部演示 ===\n");
        
        demonstrateUserOrderProcessing();
        sleep(2000);
        
        demonstrateBatchProcessingAndReporting();
        sleep(2000);
        
        demonstrateFileUploadAndCalculation("test.txt");
        sleep(2000);
        
        demonstrateTimeoutAndRetry();
        
        log.info("=== 异步业务场景全部演示完成 ===\n");
    }

    /**
     * 辅助方法：线程休眠
     */
    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * 关闭资源，用于测试清理
     */
    public void shutdown() {
        // 这里可以添加资源清理逻辑
        // 由于AsyncService是接口，具体的资源清理由实现类负责
        log.info("AsyncBusinessDemo shutdown completed");
    }
}