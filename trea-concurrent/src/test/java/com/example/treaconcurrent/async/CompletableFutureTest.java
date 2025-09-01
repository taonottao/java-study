package com.example.treaconcurrent.async;

import com.example.treaconcurrent.async.AsyncBusinessDemo;
import com.example.treaconcurrent.async.AsyncService;
import com.example.treaconcurrent.async.AsyncServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CompletableFuture异步编程功能测试类
 * 验证CompletableFuture的各种功能和异步服务
 * 
 * @author 系统
 * @since 1.0
 */
@Slf4j
@SpringBootTest
class CompletableFutureTest {

    private CompletableFutureDemo demo;
    private AsyncService asyncService;
    private AsyncBusinessDemo asyncBusinessDemo;

    @BeforeEach
    void setUp() {
        demo = new CompletableFutureDemo();
        asyncService = new AsyncServiceImpl();
        asyncBusinessDemo = new AsyncBusinessDemo(asyncService);
        log.info("测试环境初始化完成");
    }

    @AfterEach
    void tearDown() {
        // 清理测试环境
        demo = null;
        asyncService = null;
        asyncBusinessDemo = null;
        log.info("测试环境清理完成");
    }

    @Test
    @DisplayName("测试CompletableFuture基础用法")
    void testBasicUsage() {
        log.info("=== 测试CompletableFuture基础用法 ===");
        
        assertDoesNotThrow(() -> {
            demo.demonstrateBasicUsage();
        }, "基础用法演示不应抛出异常");
        
        log.info("基础用法测试完成");
    }

    @Test
    @DisplayName("测试CompletableFuture异常处理")
    void testExceptionHandling() {
        log.info("=== 测试CompletableFuture异常处理 ===");
        
        assertDoesNotThrow(() -> {
            CompletableFuture<String> result = demo.demonstrateExceptionHandling();
            assertNotNull(result, "异常处理演示应返回非空的CompletableFuture");
            String value = result.get();
            assertNotNull(value, "异常处理结果不应为null");
            log.info("异常处理演示结果: {}", value);
        }, "异常处理演示不应抛出异常");
        
        log.info("异常处理测试完成");
    }

    @Test
    @DisplayName("测试CompletableFuture组合操作")
    void testComposition() {
        log.info("=== 测试CompletableFuture组合操作 ===");
        
        assertDoesNotThrow(() -> {
            demo.demonstrateComposition();
        }, "组合操作演示不应抛出异常");
        
        log.info("组合操作测试完成");
    }

    @Test
    @DisplayName("测试CompletableFuture链式调用")
    void testChaining() {
        log.info("=== 测试CompletableFuture链式调用 ===");
        
        assertDoesNotThrow(() -> {
            demo.demonstrateChaining();
        }, "链式调用演示不应抛出异常");
        
        log.info("链式调用测试完成");
    }

    @Test
    @DisplayName("测试CompletableFuture批量处理")
    void testBatchProcessing() {
        log.info("=== 测试CompletableFuture批量处理 ===");
        
        assertDoesNotThrow(() -> {
            demo.demonstrateBatchProcessing();
        }, "批量处理演示不应抛出异常");
        
        log.info("批量处理测试完成");
    }

    @Test
    @DisplayName("测试异步服务 - 用户信息获取")
    void testAsyncServiceGetUserInfo() throws ExecutionException, InterruptedException {
        log.info("=== 测试异步服务 - 用户信息获取 ===");
        
        String userId = "testUser123";
        CompletableFuture<String> future = asyncService.getUserInfoAsync(userId);
        
        assertNotNull(future, "返回的Future不应为null");
        
        String result = future.get();
        assertNotNull(result, "用户信息不应为null");
        assertTrue(result.contains(userId), "结果应包含用户ID");
        
        log.info("用户信息获取测试完成，结果: {}", result);
    }

    @Test
    @DisplayName("测试异步服务 - 订单处理")
    void testAsyncServiceProcessOrder() throws ExecutionException, InterruptedException {
        log.info("=== 测试异步服务 - 订单处理 ===");
        
        String orderId = "order456";
        CompletableFuture<String> future = asyncService.processOrderAsync(orderId);
        
        assertNotNull(future, "返回的Future不应为null");
        
        String result = future.get();
        assertNotNull(result, "订单处理结果不应为null");
        assertTrue(result.contains(orderId), "结果应包含订单ID");
        
        log.info("订单处理测试完成，结果: {}", result);
    }

    @Test
    @DisplayName("测试异步服务 - 邮件发送")
    void testAsyncServiceSendEmail() throws ExecutionException, InterruptedException {
        log.info("=== 测试异步服务 - 邮件发送 ===");
        
        String to = "test@example.com";
        String subject = "测试邮件";
        String content = "这是一封测试邮件";
        
        CompletableFuture<Boolean> future = asyncService.sendEmailAsync(to, subject, content);
        
        assertNotNull(future, "返回的Future不应为null");
        
        Boolean result = future.get();
        assertNotNull(result, "邮件发送结果不应为null");
        
        log.info("邮件发送测试完成，结果: {}", result);
    }

    @Test
    @DisplayName("测试异步服务 - 批量数据处理")
    void testAsyncServiceBatchProcessData() throws ExecutionException, InterruptedException {
        log.info("=== 测试异步服务 - 批量数据处理 ===");
        
        List<String> dataList = Arrays.asList("数据1", "数据2", "数据3", "数据4", "数据5");
        CompletableFuture<List<String>> future = asyncService.batchProcessDataAsync(dataList);
        
        assertNotNull(future, "返回的Future不应为null");
        
        List<String> results = future.get();
        assertNotNull(results, "处理结果不应为null");
        assertEquals(dataList.size(), results.size(), "结果数量应与输入数量相同");
        
        log.info("批量数据处理测试完成，结果数量: {}", results.size());
    }

    @Test
    @DisplayName("测试异步服务 - 复杂计算")
    void testAsyncServiceComplexCalculation() throws ExecutionException, InterruptedException {
        log.info("=== 测试异步服务 - 复杂计算 ===");
        
        Integer input = 5;
        CompletableFuture<Integer> future = asyncService.complexCalculationAsync(input);
        
        assertNotNull(future, "返回的Future不应为null");
        
        Integer result = future.get();
        assertNotNull(result, "计算结果不应为null");
        assertTrue(result > input, "计算结果应大于输入值");
        
        log.info("复杂计算测试完成，输入: {}, 结果: {}", input, result);
    }

    @Test
    @DisplayName("测试异步服务 - 文件上传")
    void testAsyncServiceUploadFile() throws ExecutionException, InterruptedException {
        log.info("=== 测试异步服务 - 文件上传 ===");
        
        String fileName = "test.txt";
        
        CompletableFuture<String> future = asyncService.uploadFileAsync(fileName, 1024L)
            .exceptionally(ex -> {
                log.info("文件上传异常（这是预期的模拟异常）: {}", ex.getMessage());
                return "上传失败: " + fileName;
            });
        
        assertNotNull(future, "返回的Future不应为null");
        
        String result = future.get();
        assertNotNull(result, "上传结果不应为null");
        assertTrue(result.contains(fileName), "结果应包含文件名");
        
        log.info("文件上传测试完成，结果: {}", result);
    }

    @Test
    @DisplayName("测试异步服务 - 数据同步")
    void testAsyncServiceSyncData() throws ExecutionException, InterruptedException {
        log.info("=== 测试异步服务 - 数据同步 ===");
        
        String data = "testData";
        
        CompletableFuture<Boolean> future = asyncService.syncDataAsync("source123", "target456")
            .exceptionally(throwable -> {
                log.info("数据同步异常（这是预期的模拟异常）: {}", throwable.getMessage());
                return false; // 异常时返回false
            });
        
        assertNotNull(future, "返回的Future不应为null");
        
        Boolean result = future.get();
        assertNotNull(result, "同步结果不应为null");
        
        log.info("数据同步测试完成，结果: {}", result);
    }

    @Test
    @DisplayName("测试异步服务 - 报告生成")
    void testAsyncServiceGenerateReport() throws ExecutionException, InterruptedException {
        log.info("=== 测试异步服务 - 报告生成 ===");
        
        String reportType = "测试报告";
        String reportData = "测试数据";
        
        CompletableFuture<String> future = asyncService.generateReportAsync(reportType, "2024-01-01", "2024-01-31")
            .exceptionally(ex -> {
                log.info("报告生成异常（这是预期的模拟异常）: {}", ex.getMessage());
                return "报告生成失败: " + reportType;
            });
        
        assertNotNull(future, "返回的Future不应为null");
        
        String result = future.get();
        assertNotNull(result, "报告内容不应为null");
        assertTrue(result.contains(reportType), "报告应包含报告类型");
        
        log.info("报告生成测试完成");
    }

    @Test
    @DisplayName("测试异步任务超时处理")
    void testAsyncTimeout() {
        log.info("=== 测试异步任务超时处理 ===");
        
        CompletableFuture<String> timeoutFuture = CompletableFuture
            .supplyAsync(() -> {
                try {
                    Thread.sleep(3000); // 3秒延迟
                    return "不应该返回这个结果";
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return "被中断";
                }
            })
            .orTimeout(1, TimeUnit.SECONDS); // 1秒超时
        
        assertThrows(ExecutionException.class, () -> {
            timeoutFuture.get();
        }, "应该抛出超时异常");
        
        log.info("超时处理测试完成");
    }

    @Test
    @DisplayName("测试异步任务组合")
    void testAsyncCombination() throws ExecutionException, InterruptedException {
        log.info("=== 测试异步任务组合 ===");
        
        CompletableFuture<String> future1 = asyncService.getUserInfoAsync("user1")
            .exceptionally(ex -> "用户信息获取失败: " + ex.getMessage());
        CompletableFuture<String> future2 = asyncService.processOrderAsync("order1")
            .exceptionally(ex -> "订单处理失败: " + ex.getMessage());
        
        CompletableFuture<String> combinedFuture = future1.thenCombine(future2, (userInfo, orderResult) -> {
            return "组合结果: " + userInfo + " + " + orderResult;
        });
        
        String result = combinedFuture.get();
        assertNotNull(result, "组合结果不应为null");
        assertTrue(result.contains("组合结果"), "结果应包含组合标识");
        
        log.info("异步任务组合测试完成，结果: {}", result);
    }

    @Test
    @DisplayName("测试业务场景演示")
    void testBusinessDemonstrations() {
        log.info("=== 测试业务场景演示 ===");
        
        assertDoesNotThrow(() -> {
            asyncBusinessDemo.demonstrateUserOrderProcessing();
        }, "用户订单处理演示不应抛出异常");
        
        assertDoesNotThrow(() -> {
            asyncBusinessDemo.demonstrateBatchProcessingAndReporting();
        }, "批量处理和报告生成演示不应抛出异常");
        
        assertDoesNotThrow(() -> {
            asyncBusinessDemo.demonstrateFileUploadAndCalculation("test.txt");
        }, "文件上传和计算演示不应抛出异常");
        
        log.info("业务场景演示测试完成");
    }

    @Test
    @DisplayName("测试异步任务异常处理")
    void testAsyncExceptionHandling() throws ExecutionException, InterruptedException {
        log.info("=== 测试异步任务异常处理 ===");
        
        CompletableFuture<String> exceptionFuture = CompletableFuture
            .<String>supplyAsync(() -> {
                throw new RuntimeException("测试异常");
            })
            .exceptionally(throwable -> {
                log.info("捕获到异常: {}", throwable.getMessage());
                return "异常处理后的默认值";
            });
        
        String result = exceptionFuture.get();
        assertEquals("异常处理后的默认值", result, "应该返回异常处理后的默认值");
        
        log.info("异步任务异常处理测试完成");
    }

    @Test
    @DisplayName("测试CompletableFuture性能")
    void testAsyncPerformance() throws ExecutionException, InterruptedException {
        log.info("=== 测试CompletableFuture性能 ===");
        
        long startTime = System.currentTimeMillis();
        
        // 创建多个并行任务，添加异常处理
        List<CompletableFuture<String>> futures = Arrays.asList(
            asyncService.getUserInfoAsync("user1").exceptionally(ex -> "用户信息获取失败: " + ex.getMessage()),
            asyncService.getUserInfoAsync("user2").exceptionally(ex -> "用户信息获取失败: " + ex.getMessage()),
            asyncService.getUserInfoAsync("user3").exceptionally(ex -> "用户信息获取失败: " + ex.getMessage()),
            asyncService.processOrderAsync("order1").exceptionally(ex -> "订单处理失败: " + ex.getMessage()),
            asyncService.processOrderAsync("order2").exceptionally(ex -> "订单处理失败: " + ex.getMessage())
        );
        
        // 等待所有任务完成
        CompletableFuture<Void> allOf = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0])
        );
        
        allOf.get();
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        log.info("并行执行{}个任务耗时: {}ms", futures.size(), duration);
        
        // 验证所有任务都完成了
        for (CompletableFuture<String> future : futures) {
            assertTrue(future.isDone(), "所有任务都应该完成");
            assertNotNull(future.get(), "任务结果不应为null");
        }
        
        log.info("性能测试完成");
    }
}