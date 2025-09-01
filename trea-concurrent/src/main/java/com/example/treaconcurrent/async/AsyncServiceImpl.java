package com.example.treaconcurrent.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.Random;

/**
 * 异步服务实现类
 * 提供各种异步业务操作的具体实现
 * 
 * @author 系统
 * @since 1.0
 */
@Slf4j
@Service
public class AsyncServiceImpl implements AsyncService {

    /**
     * 异步任务执行线程池
     */
    private final ExecutorService asyncExecutor = Executors.newFixedThreadPool(8, r -> {
        Thread thread = new Thread(r);
        thread.setName("async-service-" + thread.getId());
        thread.setDaemon(false);
        return thread;
    });

    /**
     * 随机数生成器，用于模拟不同的执行时间
     */
    private final Random random = new Random();

    @Override
    public CompletableFuture<String> getUserInfoAsync(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("开始异步获取用户信息，用户ID: {}, 线程: {}", userId, Thread.currentThread().getName());
            
            try {
                // 模拟数据库查询耗时
                int delay = 500 + random.nextInt(1000);
                Thread.sleep(delay);
                
                // 模拟偶尔的查询失败
                if (random.nextDouble() < 0.1) {
                    throw new RuntimeException("用户信息查询失败: " + userId);
                }
                
                String userInfo = String.format("用户信息{id=%s, name=用户%s, email=%s@example.com}", 
                    userId, userId, userId);
                
                log.info("用户信息获取完成: {}", userInfo);
                return userInfo;
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("用户信息获取被中断", e);
            }
        }, asyncExecutor);
    }

    @Override
    public CompletableFuture<String> processOrderAsync(String orderId) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("开始异步处理订单，订单ID: {}, 线程: {}", orderId, Thread.currentThread().getName());
            
            try {
                // 模拟订单处理的多个步骤
                log.info("步骤1: 验证订单 {}", orderId);
                Thread.sleep(200 + random.nextInt(300));
                
                log.info("步骤2: 库存检查 {}", orderId);
                Thread.sleep(300 + random.nextInt(400));
                
                log.info("步骤3: 支付处理 {}", orderId);
                Thread.sleep(400 + random.nextInt(500));
                
                // 模拟偶尔的处理失败
                if (random.nextDouble() < 0.15) {
                    throw new RuntimeException("订单处理失败: " + orderId);
                }
                
                String result = "订单处理成功: " + orderId + ", 状态: 已完成";
                log.info("订单处理完成: {}", result);
                return result;
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("订单处理被中断", e);
            }
        }, asyncExecutor);
    }

    @Override
    public CompletableFuture<Boolean> sendEmailAsync(String email, String subject, String content) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("开始异步发送邮件，收件人: {}, 主题: {}, 线程: {}", 
                email, subject, Thread.currentThread().getName());
            
            try {
                // 模拟邮件发送耗时
                int delay = 800 + random.nextInt(1200);
                Thread.sleep(delay);
                
                // 模拟偶尔的发送失败
                if (random.nextDouble() < 0.05) {
                    throw new RuntimeException("邮件发送失败: " + email);
                }
                
                log.info("邮件发送成功: {} -> {}", email, subject);
                return true;
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("邮件发送被中断", e);
            }
        }, asyncExecutor);
    }

    @Override
    public CompletableFuture<List<String>> batchProcessDataAsync(List<String> dataList) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("开始异步批量处理数据，数据量: {}, 线程: {}", 
                dataList.size(), Thread.currentThread().getName());
            
            try {
                // 并行处理每个数据项
                List<CompletableFuture<String>> futures = dataList.stream()
                    .map(data -> CompletableFuture.supplyAsync(() -> {
                        try {
                            // 模拟单个数据处理耗时
                            Thread.sleep(100 + random.nextInt(200));
                            return "处理完成: " + data;
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return "处理中断: " + data;
                        }
                    }, asyncExecutor))
                    .collect(Collectors.toList());
                
                // 等待所有处理完成
                CompletableFuture<Void> allOf = CompletableFuture.allOf(
                    futures.toArray(new CompletableFuture[0]));
                
                List<String> results = allOf.thenApply(v -> 
                    futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList())
                ).get();
                
                log.info("批量数据处理完成，结果数量: {}", results.size());
                return results;
                
            } catch (Exception e) {
                throw new RuntimeException("批量数据处理失败", e);
            }
        }, asyncExecutor);
    }

    @Override
    public CompletableFuture<Integer> complexCalculationAsync(Integer input) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("开始异步复杂计算，输入: {}, 线程: {}", input, Thread.currentThread().getName());
            
            try {
                // 模拟复杂计算过程
                int result = input;
                for (int i = 0; i < 5; i++) {
                    log.info("计算步骤 {}: 当前值 = {}", i + 1, result);
                    Thread.sleep(200 + random.nextInt(300));
                    result = result * 2 + i;
                }
                
                log.info("复杂计算完成，最终结果: {}", result);
                return result;
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("复杂计算被中断", e);
            }
        }, asyncExecutor);
    }

    @Override
    public CompletableFuture<String> uploadFileAsync(String fileName, Long fileSize) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("开始异步文件上传，文件名: {}, 大小: {} bytes, 线程: {}", 
                fileName, fileSize, Thread.currentThread().getName());
            
            try {
                // 模拟文件上传进度
                long uploadedSize = 0;
                long chunkSize = Math.max(fileSize / 10, 1);
                
                while (uploadedSize < fileSize) {
                    Thread.sleep(100 + random.nextInt(200));
                    uploadedSize += chunkSize;
                    if (uploadedSize > fileSize) {
                        uploadedSize = fileSize;
                    }
                    
                    int progress = (int) ((uploadedSize * 100) / fileSize);
                    log.info("文件上传进度: {}% ({}/{})", progress, uploadedSize, fileSize);
                }
                
                // 模拟偶尔的上传失败
                if (random.nextDouble() < 0.08) {
                    throw new RuntimeException("文件上传失败: " + fileName);
                }
                
                String uploadPath = "/uploads/" + System.currentTimeMillis() + "_" + fileName;
                log.info("文件上传成功: {}", uploadPath);
                return uploadPath;
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("文件上传被中断", e);
            }
        }, asyncExecutor);
    }

    @Override
    public CompletableFuture<Boolean> syncDataAsync(String sourceId, String targetId) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("开始异步数据同步，源: {}, 目标: {}, 线程: {}", 
                sourceId, targetId, Thread.currentThread().getName());
            
            try {
                // 模拟数据同步的多个阶段
                log.info("阶段1: 读取源数据 {}", sourceId);
                Thread.sleep(300 + random.nextInt(400));
                
                log.info("阶段2: 数据转换和验证");
                Thread.sleep(400 + random.nextInt(500));
                
                log.info("阶段3: 写入目标数据 {}", targetId);
                Thread.sleep(200 + random.nextInt(300));
                
                log.info("阶段4: 同步验证");
                Thread.sleep(100 + random.nextInt(200));
                
                // 模拟偶尔的同步失败
                if (random.nextDouble() < 0.12) {
                    throw new RuntimeException("数据同步失败: " + sourceId + " -> " + targetId);
                }
                
                log.info("数据同步成功: {} -> {}", sourceId, targetId);
                return true;
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("数据同步被中断", e);
            }
        }, asyncExecutor);
    }

    @Override
    public CompletableFuture<String> generateReportAsync(String reportType, String startDate, String endDate) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("开始异步生成报告，类型: {}, 时间范围: {} - {}, 线程: {}", 
                reportType, startDate, endDate, Thread.currentThread().getName());
            
            try {
                // 模拟报告生成的多个步骤
                log.info("步骤1: 数据收集");
                Thread.sleep(600 + random.nextInt(800));
                
                log.info("步骤2: 数据分析");
                Thread.sleep(800 + random.nextInt(1000));
                
                log.info("步骤3: 报告格式化");
                Thread.sleep(400 + random.nextInt(600));
                
                log.info("步骤4: 报告生成");
                Thread.sleep(300 + random.nextInt(400));
                
                // 模拟偶尔的生成失败
                if (random.nextDouble() < 0.1) {
                    throw new RuntimeException("报告生成失败: " + reportType);
                }
                
                String reportContent = String.format(
                    "报告类型: %s\n时间范围: %s 至 %s\n生成时间: %s\n数据行数: %d\n状态: 成功", 
                    reportType, startDate, endDate, 
                    java.time.LocalDateTime.now().toString(),
                    100 + random.nextInt(900)
                );
                
                log.info("报告生成完成: {}", reportType);
                return reportContent;
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("报告生成被中断", e);
            }
        }, asyncExecutor);
    }

    /**
     * 关闭线程池
     */
    public void shutdown() {
        if (asyncExecutor != null && !asyncExecutor.isShutdown()) {
            asyncExecutor.shutdown();
            try {
                if (!asyncExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                    asyncExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                asyncExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}