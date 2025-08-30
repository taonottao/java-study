package com.example.treaconcurrent.model;

/**
 * 任务状态枚举
 * 定义任务在执行过程中的各种状态
 * 
 * @author SOLO Coding
 * @since 2024-01-20
 */
public enum TaskStatus {
    
    /**
     * 待执行 - 任务已提交但尚未开始执行
     */
    PENDING("待执行", "任务已提交到队列，等待执行"),
    
    /**
     * 执行中 - 任务正在执行
     */
    RUNNING("执行中", "任务正在被线程执行"),
    
    /**
     * 已完成 - 任务成功执行完成
     */
    COMPLETED("已完成", "任务已成功执行完成"),
    
    /**
     * 执行失败 - 任务执行过程中发生异常
     */
    FAILED("执行失败", "任务执行过程中发生异常或错误"),
    
    /**
     * 已取消 - 任务被手动取消
     */
    CANCELLED("已取消", "任务被用户或系统取消执行");
    
    /**
     * 状态名称
     */
    private final String name;
    
    /**
     * 状态描述
     */
    private final String description;
    
    /**
     * 构造函数
     * 
     * @param name 状态名称
     * @param description 状态描述
     */
    TaskStatus(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    /**
     * 获取状态名称
     * 
     * @return 状态名称
     */
    public String getName() {
        return name;
    }
    
    /**
     * 获取状态描述
     * 
     * @return 状态描述
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 判断是否为终态（已完成、失败或取消）
     * 
     * @return true表示任务已结束，false表示任务仍在进行中
     */
    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED;
    }
    
    /**
     * 判断是否为成功状态
     * 
     * @return true表示任务成功完成
     */
    public boolean isSuccess() {
        return this == COMPLETED;
    }
    
    /**
     * 判断是否为失败状态
     * 
     * @return true表示任务执行失败
     */
    public boolean isFailure() {
        return this == FAILED;
    }
    
    /**
     * 判断是否可以取消
     * 只有待执行和执行中的任务可以被取消
     * 
     * @return true表示任务可以被取消
     */
    public boolean isCancellable() {
        return this == PENDING || this == RUNNING;
    }
    
    @Override
    public String toString() {
        return String.format("%s(%s)", name, description);
    }
}