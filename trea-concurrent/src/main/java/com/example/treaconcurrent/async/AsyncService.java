package com.example.treaconcurrent.async;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 异步服务接口
 * 提供各种异步业务操作的接口定义
 * 
 * @author 系统
 * @since 1.0
 */
public interface AsyncService {

    /**
     * 异步获取用户信息
     * 
     * @param userId 用户ID
     * @return 用户信息的CompletableFuture
     */
    CompletableFuture<String> getUserInfoAsync(String userId);

    /**
     * 异步处理订单
     * 
     * @param orderId 订单ID
     * @return 处理结果的CompletableFuture
     */
    CompletableFuture<String> processOrderAsync(String orderId);

    /**
     * 异步发送邮件
     * 
     * @param email 邮箱地址
     * @param subject 邮件主题
     * @param content 邮件内容
     * @return 发送结果的CompletableFuture
     */
    CompletableFuture<Boolean> sendEmailAsync(String email, String subject, String content);

    /**
     * 异步批量处理数据
     * 
     * @param dataList 数据列表
     * @return 处理结果列表的CompletableFuture
     */
    CompletableFuture<List<String>> batchProcessDataAsync(List<String> dataList);

    /**
     * 异步计算复杂任务
     * 
     * @param input 输入参数
     * @return 计算结果的CompletableFuture
     */
    CompletableFuture<Integer> complexCalculationAsync(Integer input);

    /**
     * 异步文件上传
     * 
     * @param fileName 文件名
     * @param fileSize 文件大小
     * @return 上传结果的CompletableFuture
     */
    CompletableFuture<String> uploadFileAsync(String fileName, Long fileSize);

    /**
     * 异步数据同步
     * 
     * @param sourceId 源数据ID
     * @param targetId 目标数据ID
     * @return 同步结果的CompletableFuture
     */
    CompletableFuture<Boolean> syncDataAsync(String sourceId, String targetId);

    /**
     * 异步生成报告
     * 
     * @param reportType 报告类型
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 报告内容的CompletableFuture
     */
    CompletableFuture<String> generateReportAsync(String reportType, String startDate, String endDate);
}