package com.example.treaconcurrent.model;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * 任务结果数据传输对象
 * 封装任务执行完成后的结果信息
 * 
 * @author SOLO Coding
 * @since 2024-01-20
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskResult {
    
    /**
     * 任务唯一标识
     */
    private String taskId;
    
    /**
     * 任务名称
     */
    private String taskName;
    
    /**
     * 任务类型
     */
    private String taskType;
    
    /**
     * 任务执行状态
     */
    private TaskStatus status;
    
    /**
     * 任务执行结果数据
     */
    private Object data;
    
    /**
     * 任务开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 任务结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 任务执行耗时（毫秒）
     */
    private Long duration;
    
    /**
     * 执行线程名称
     */
    private String threadName;
    
    /**
     * 错误信息（任务失败时）
     */
    private String errorMessage;
    
    /**
     * 错误堆栈信息（任务失败时）
     */
    private String stackTrace;
    
    /**
     * 任务执行的详细信息
     */
    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();
    
    /**
     * 重试次数
     */
    @Builder.Default
    private Integer retryCount = 0;
    
    /**
     * 任务优先级
     */
    private Integer priority;
    
    /**
     * 任务标签
     */
    private String tags;
    
    /**
     * 创建成功的任务结果
     * 
     * @param taskId 任务ID
     * @param taskName 任务名称
     * @param taskType 任务类型
     * @param data 结果数据
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param threadName 线程名称
     * @return 任务结果对象
     */
    public static TaskResult success(String taskId, String taskName, String taskType, 
                                   Object data, LocalDateTime startTime, LocalDateTime endTime, String threadName) {
        return TaskResult.builder()
                .taskId(taskId)
                .taskName(taskName)
                .taskType(taskType)
                .status(TaskStatus.COMPLETED)
                .data(data)
                .startTime(startTime)
                .endTime(endTime)
                .duration(calculateDuration(startTime, endTime))
                .threadName(threadName)
                .build();
    }
    
    /**
     * 创建失败的任务结果
     * 
     * @param taskId 任务ID
     * @param taskName 任务名称
     * @param taskType 任务类型
     * @param errorMessage 错误信息
     * @param stackTrace 堆栈信息
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param threadName 线程名称
     * @return 任务结果对象
     */
    public static TaskResult failure(String taskId, String taskName, String taskType, 
                                   String errorMessage, String stackTrace, 
                                   LocalDateTime startTime, LocalDateTime endTime, String threadName) {
        return TaskResult.builder()
                .taskId(taskId)
                .taskName(taskName)
                .taskType(taskType)
                .status(TaskStatus.FAILED)
                .errorMessage(errorMessage)
                .stackTrace(stackTrace)
                .startTime(startTime)
                .endTime(endTime)
                .duration(calculateDuration(startTime, endTime))
                .threadName(threadName)
                .build();
    }
    
    /**
     * 创建取消的任务结果
     * 
     * @param taskId 任务ID
     * @param taskName 任务名称
     * @param taskType 任务类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param threadName 线程名称
     * @return 任务结果对象
     */
    public static TaskResult cancelled(String taskId, String taskName, String taskType, 
                                     LocalDateTime startTime, LocalDateTime endTime, String threadName) {
        return TaskResult.builder()
                .taskId(taskId)
                .taskName(taskName)
                .taskType(taskType)
                .status(TaskStatus.CANCELLED)
                .startTime(startTime)
                .endTime(endTime)
                .duration(calculateDuration(startTime, endTime))
                .threadName(threadName)
                .build();
    }
    
    /**
     * 计算执行耗时
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 耗时（毫秒）
     */
    private static Long calculateDuration(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            return 0L;
        }
        return java.time.Duration.between(startTime, endTime).toMillis();
    }
    
    /**
     * 判断任务是否成功
     * 
     * @return true表示任务成功完成
     */
    public boolean isSuccess() {
        return status == TaskStatus.COMPLETED;
    }
    
    /**
     * 判断任务是否失败
     * 
     * @return true表示任务执行失败
     */
    public boolean isFailure() {
        return status == TaskStatus.FAILED;
    }
    
    /**
     * 判断任务是否被取消
     * 
     * @return true表示任务被取消
     */
    public boolean isCancelled() {
        return status == TaskStatus.CANCELLED;
    }
    
    /**
     * 添加元数据信息
     * 
     * @param key 键
     * @param value 值
     * @return 当前对象，支持链式调用
     */
    public TaskResult addMetadata(String key, Object value) {
        if (this.metadata == null) {
            this.metadata = new HashMap<>();
        }
        this.metadata.put(key, value);
        return this;
    }
    
    /**
     * 获取元数据信息
     * 
     * @param key 键
     * @return 值
     */
    public Object getMetadata(String key) {
        return this.metadata != null ? this.metadata.get(key) : null;
    }
    
    /**
     * 获取元数据信息（带默认值）
     * 
     * @param key 键
     * @param defaultValue 默认值
     * @param <T> 值类型
     * @return 值或默认值
     */
    @SuppressWarnings("unchecked")
    public <T> T getMetadata(String key, T defaultValue) {
        Object value = getMetadata(key);
        return value != null ? (T) value : defaultValue;
    }
    
    /**
     * 获取结果数据（指定类型）
     * 
     * @param clazz 目标类型
     * @param <T> 类型参数
     * @return 转换后的结果数据
     */
    @SuppressWarnings("unchecked")
    public <T> T getData(Class<T> clazz) {
        if (data == null) {
            return null;
        }
        
        if (clazz.isInstance(data)) {
            return (T) data;
        }
        
        // 简单的类型转换
        if (clazz == String.class) {
            return (T) data.toString();
        }
        
        return null;
    }
    
    /**
     * 获取执行效率描述
     * 
     * @return 效率描述字符串
     */
    public String getPerformanceDescription() {
        if (duration == null || duration <= 0) {
            return "执行时间未知";
        }
        
        if (duration < 1000) {
            return String.format("执行耗时: %dms (快速)", duration);
        } else if (duration < 5000) {
            return String.format("执行耗时: %dms (正常)", duration);
        } else if (duration < 30000) {
            return String.format("执行耗时: %dms (较慢)", duration);
        } else {
            return String.format("执行耗时: %dms (很慢)", duration);
        }
    }
    
    /**
     * 获取任务执行摘要
     * 
     * @return 执行摘要字符串
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append(String.format("任务[%s]执行%s", taskName, status.getName()));
        
        if (duration != null) {
            summary.append(String.format("，耗时%dms", duration));
        }
        
        if (threadName != null) {
            summary.append(String.format("，执行线程: %s", threadName));
        }
        
        if (retryCount != null && retryCount > 0) {
            summary.append(String.format("，重试%d次", retryCount));
        }
        
        if (isFailure() && errorMessage != null) {
            summary.append(String.format("，错误: %s", errorMessage));
        }
        
        return summary.toString();
    }
    
    @Override
    public String toString() {
        return String.format("TaskResult{taskId='%s', taskName='%s', status=%s, duration=%dms, success=%s}", 
                taskId, taskName, status != null ? status.getName() : "未知", 
                duration != null ? duration : 0, isSuccess());
    }
}