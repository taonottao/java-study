package com.example.treaconcurrent.model;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.concurrent.Future;

/**
 * 任务信息数据模型
 * 封装任务的基本信息和执行状态
 * 
 * @author SOLO Coding
 * @since 2024-01-20
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskInfo {
    
    /**
     * 任务唯一标识
     */
    private String taskId;
    
    /**
     * 任务名称
     */
    private String taskName;
    
    /**
     * 任务描述
     */
    private String description;
    
    /**
     * 任务状态
     */
    private TaskStatus status;
    
    /**
     * 任务创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 任务开始执行时间
     */
    private LocalDateTime startTime;
    
    /**
     * 任务完成时间
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
     * 任务执行结果
     */
    private Object result;
    
    /**
     * 异常信息（如果任务执行失败）
     */
    private String errorMessage;
    
    /**
     * 任务优先级（1-10，数字越大优先级越高）
     */
    private Integer priority;
    
    /**
     * Future对象，用于任务控制
     */
    private transient Future<?> future;
    
    /**
     * 任务类型标识
     */
    private String taskType;
    
    /**
     * 任务参数（JSON格式）
     */
    private String parameters;
    
    /**
     * 计算任务执行耗时
     * 如果任务尚未完成，返回当前已执行时间
     * 
     * @return 执行耗时（毫秒）
     */
    public Long calculateDuration() {
        if (startTime == null) {
            return 0L;
        }
        
        LocalDateTime endTimeToUse = endTime != null ? endTime : LocalDateTime.now();
        return java.time.Duration.between(startTime, endTimeToUse).toMillis();
    }
    
    /**
     * 判断任务是否已完成
     * 
     * @return true表示任务已完成（成功或失败）
     */
    public boolean isCompleted() {
        return status != null && status.isTerminal();
    }
    
    /**
     * 判断任务是否成功
     * 
     * @return true表示任务成功完成
     */
    public boolean isSuccess() {
        return status != null && status.isSuccess();
    }
    
    /**
     * 判断任务是否失败
     * 
     * @return true表示任务执行失败
     */
    public boolean isFailed() {
        return status != null && status.isFailure();
    }
    
    /**
     * 更新任务状态为开始执行
     */
    public void markAsStarted() {
        this.status = TaskStatus.RUNNING;
        this.startTime = LocalDateTime.now();
        this.threadName = Thread.currentThread().getName();
    }
    
    /**
     * 更新任务状态为完成
     * 
     * @param result 任务执行结果
     */
    public void markAsCompleted(Object result) {
        this.status = TaskStatus.COMPLETED;
        this.endTime = LocalDateTime.now();
        this.duration = calculateDuration();
        this.result = result;
    }
    
    /**
     * 更新任务状态为失败
     * 
     * @param errorMessage 错误信息
     */
    public void markAsFailed(String errorMessage) {
        this.status = TaskStatus.FAILED;
        this.endTime = LocalDateTime.now();
        this.duration = calculateDuration();
        this.errorMessage = errorMessage;
    }
    
    /**
     * 更新任务状态为取消
     */
    public void markAsCancelled() {
        this.status = TaskStatus.CANCELLED;
        this.endTime = LocalDateTime.now();
        this.duration = calculateDuration();
    }
    
    /**
     * 获取任务执行进度描述
     * 
     * @return 进度描述字符串
     */
    public String getProgressDescription() {
        if (status == null) {
            return "未知状态";
        }
        
        switch (status) {
            case PENDING:
                return "等待执行";
            case RUNNING:
                return String.format("执行中（已运行%dms）", calculateDuration());
            case COMPLETED:
                return String.format("执行完成（耗时%dms）", duration);
            case FAILED:
                return String.format("执行失败（耗时%dms）: %s", duration, errorMessage);
            case CANCELLED:
                return String.format("已取消（耗时%dms）", duration);
            default:
                return status.getName();
        }
    }
    
    @Override
    public String toString() {
        return String.format("TaskInfo{taskId='%s', taskName='%s', status=%s, duration=%dms}", 
                taskId, taskName, status != null ? status.getName() : "未知", calculateDuration());
    }
}