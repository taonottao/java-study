package com.example.treaconcurrent.async;

import com.example.treaconcurrent.model.TaskRequest;
import com.example.treaconcurrent.model.TaskResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 异步任务服务接口
 * 定义各种异步任务处理方法
 * 
 * @author 系统
 * @since 1.0
 */
public interface AsyncTaskService {

    /**
     * 异步处理单个任务
     * 
     * @param request 任务请求
     * @return 异步任务结果
     */
    CompletableFuture<TaskResult> processTaskAsync(TaskRequest request);

    /**
     * 异步批量处理任务
     * 
     * @param requests 任务请求列表
     * @return 异步任务结果列表
     */
    CompletableFuture<List<TaskResult>> processBatchTasksAsync(List<TaskRequest> requests);

    /**
     * 异步计算斐波那契数列
     * 
     * @param n 计算到第n项
     * @return 异步计算结果
     */
    CompletableFuture<Long> calculateFibonacciAsync(int n);

    /**
     * 异步模拟网络请求
     * 
     * @param url 请求URL
     * @param timeoutMs 超时时间（毫秒）
     * @return 异步请求结果
     */
    CompletableFuture<String> simulateNetworkRequestAsync(String url, int timeoutMs);

    /**
     * 异步文件处理
     * 
     * @param fileName 文件名
     * @param content 文件内容
     * @return 异步处理结果
     */
    CompletableFuture<String> processFileAsync(String fileName, String content);

    /**
     * 异步数据库查询模拟
     * 
     * @param query 查询语句
     * @return 异步查询结果
     */
    CompletableFuture<List<String>> simulateDatabaseQueryAsync(String query);

    /**
     * 异步邮件发送模拟
     * 
     * @param to 收件人
     * @param subject 邮件主题
     * @param content 邮件内容
     * @return 异步发送结果
     */
    CompletableFuture<Boolean> sendEmailAsync(String to, String subject, String content);

    /**
     * 异步缓存操作
     * 
     * @param key 缓存键
     * @param value 缓存值
     * @param expireSeconds 过期时间（秒）
     * @return 异步操作结果
     */
    CompletableFuture<Boolean> setCacheAsync(String key, String value, int expireSeconds);

    /**
     * 异步获取缓存
     * 
     * @param key 缓存键
     * @return 异步获取结果
     */
    CompletableFuture<String> getCacheAsync(String key);

    /**
     * 异步组合任务：先查询数据，再处理，最后保存
     * 
     * @param id 数据ID
     * @return 异步组合任务结果
     */
    CompletableFuture<String> compositeTaskAsync(String id);
}