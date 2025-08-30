package com.example.treaconcurrent.model;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import java.util.Map;
import java.util.HashMap;

/**
 * 任务请求数据传输对象
 * 用于接收任务提交请求的参数
 * 
 * @author SOLO Coding
 * @since 2024-01-20
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequest {
    
    /**
     * 任务名称（必填）
     */
    @NotBlank(message = "任务名称不能为空")
    private String taskName;
    
    /**
     * 任务描述
     */
    private String description;
    
    /**
     * 任务类型（必填）
     * 如：COMPUTATION（计算任务）、IO（IO任务）、SCHEDULED（定时任务）等
     */
    @NotBlank(message = "任务类型不能为空")
    private String taskType;
    
    /**
     * 任务优先级（1-10，数字越大优先级越高）
     */
    @Min(value = 1, message = "任务优先级最小值为1")
    @Max(value = 10, message = "任务优先级最大值为10")
    @Builder.Default
    private Integer priority = 5;
    
    /**
     * 任务参数
     * 存储任务执行所需的各种参数
     */
    @Builder.Default
    private Map<String, Object> parameters = new HashMap<>();
    
    /**
     * 延迟执行时间（毫秒）
     * 0表示立即执行，大于0表示延迟指定时间后执行
     */
    @Min(value = 0, message = "延迟时间不能为负数")
    @Builder.Default
    private Long delayMs = 0L;
    
    /**
     * 超时时间（毫秒）
     * 0表示不设置超时，大于0表示任务执行超过指定时间后自动取消
     */
    @Min(value = 0, message = "超时时间不能为负数")
    @Builder.Default
    private Long timeoutMs = 0L;
    
    /**
     * 是否允许重试
     */
    @Builder.Default
    private Boolean retryEnabled = false;
    
    /**
     * 最大重试次数
     */
    @Min(value = 0, message = "重试次数不能为负数")
    @Builder.Default
    private Integer maxRetries = 0;
    
    /**
     * 重试间隔时间（毫秒）
     */
    @Min(value = 0, message = "重试间隔不能为负数")
    @Builder.Default
    private Long retryIntervalMs = 1000L;
    
    /**
     * 任务标签
     * 用于任务分类和查询
     */
    private String tags;
    
    /**
     * 回调URL
     * 任务完成后的回调地址
     */
    private String callbackUrl;
    
    /**
     * 添加任务参数
     * 
     * @param key 参数名
     * @param value 参数值
     * @return 当前对象，支持链式调用
     */
    public TaskRequest addParameter(String key, Object value) {
        if (this.parameters == null) {
            this.parameters = new HashMap<>();
        }
        this.parameters.put(key, value);
        return this;
    }
    
    /**
     * 获取指定参数值
     * 
     * @param key 参数名
     * @return 参数值
     */
    public Object getParameter(String key) {
        return this.parameters != null ? this.parameters.get(key) : null;
    }
    
    /**
     * 获取指定参数值（带默认值）
     * 
     * @param key 参数名
     * @param defaultValue 默认值
     * @param <T> 参数类型
     * @return 参数值或默认值
     */
    @SuppressWarnings("unchecked")
    public <T> T getParameter(String key, T defaultValue) {
        Object value = getParameter(key);
        return value != null ? (T) value : defaultValue;
    }
    
    /**
     * 获取字符串类型参数
     * 
     * @param key 参数名
     * @return 字符串参数值
     */
    public String getStringParameter(String key) {
        Object value = getParameter(key);
        return value != null ? value.toString() : null;
    }
    
    /**
     * 获取整数类型参数
     * 
     * @param key 参数名
     * @return 整数参数值
     */
    public Integer getIntParameter(String key) {
        Object value = getParameter(key);
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        } else if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    
    /**
     * 获取长整数类型参数
     * 
     * @param key 参数名
     * @return 长整数参数值
     */
    public Long getLongParameter(String key) {
        Object value = getParameter(key);
        if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof Number) {
            return ((Number) value).longValue();
        } else if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    
    /**
     * 判断是否为立即执行任务
     * 
     * @return true表示立即执行
     */
    public boolean isImmediateExecution() {
        return delayMs == null || delayMs <= 0;
    }
    
    /**
     * 判断是否设置了超时时间
     * 
     * @return true表示设置了超时
     */
    public boolean hasTimeout() {
        return timeoutMs != null && timeoutMs > 0;
    }
    
    /**
     * 判断是否启用重试
     * 
     * @return true表示启用重试
     */
    public boolean isRetryEnabled() {
        return retryEnabled != null && retryEnabled && maxRetries != null && maxRetries > 0;
    }
    
    /**
     * 验证请求参数的有效性
     * 
     * @return 验证结果描述，null表示验证通过
     */
    public String validate() {
        if (taskName == null || taskName.trim().isEmpty()) {
            return "任务名称不能为空";
        }
        
        if (taskType == null || taskType.trim().isEmpty()) {
            return "任务类型不能为空";
        }
        
        if (priority != null && (priority < 1 || priority > 10)) {
            return "任务优先级必须在1-10之间";
        }
        
        if (delayMs != null && delayMs < 0) {
            return "延迟时间不能为负数";
        }
        
        if (timeoutMs != null && timeoutMs < 0) {
            return "超时时间不能为负数";
        }
        
        if (maxRetries != null && maxRetries < 0) {
            return "重试次数不能为负数";
        }
        
        if (retryIntervalMs != null && retryIntervalMs < 0) {
            return "重试间隔不能为负数";
        }
        
        return null; // 验证通过
    }
    
    @Override
    public String toString() {
        return String.format("TaskRequest{taskName='%s', taskType='%s', priority=%d, delayMs=%d}", 
                taskName, taskType, priority, delayMs);
    }
}