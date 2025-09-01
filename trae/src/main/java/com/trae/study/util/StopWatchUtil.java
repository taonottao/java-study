package com.trae.study.util;

import cn.hutool.core.date.StopWatch;
import com.trae.study.dto.BenchmarkResultDTO;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.Map;

/**
 * 轻量计时工具封装：基于 Hutool StopWatch。
 * 用于与 JMH 形成对照（小规模、功能性验证时可使用）。
 */
public final class StopWatchUtil {

    private StopWatchUtil() {}

    /**
     * 运行一个任务并返回毫秒耗时（无返回值任务）。
     * @param task 可执行任务
     * @return 耗时毫秒
     */
    public static long runAndMeasureMillis(Runnable task) {
        StopWatch sw = new StopWatch();
        sw.start();
        try {
            task.run();
        } finally {
            sw.stop();
        }
        return sw.getTotal(TimeUnit.MILLISECONDS);
    }

    /**
     * 计时有返回值的任务，并返回耗时（毫秒）。
     * 与现有 Demo 中的 measureTask("描述", supplier) 调用保持一致。
     * @param taskName 任务名称，仅用于对齐签名；实际计时逻辑忽略该参数
     * @param supplier 提供需要执行的任务
     * @param <T> 任务返回类型
     * @return 耗时毫秒
     */
    public static <T> long measureTask(String taskName, Supplier<T> supplier) {
        StopWatch sw = new StopWatch();
        sw.start();
        try {
            supplier.get();
        } finally {
            sw.stop();
        }
        return sw.getTotal(TimeUnit.MILLISECONDS);
    }

    /**
     * 计时有返回值的任务，并返回耗时（毫秒）。
     * 用于支持 OptionalDemo 中的 measureTime 方法调用。
     * @param supplier 提供需要执行的任务
     * @param <T> 任务返回类型
     * @return 耗时毫秒
     */
    public static <T> long measureTime(Supplier<T> supplier) {
        StopWatch sw = new StopWatch();
        sw.start();
        try {
            supplier.get();
        } finally {
            sw.stop();
        }
        return sw.getTotal(TimeUnit.MILLISECONDS);
    }

    /**
     * 执行基准测试并返回结果 DTO
     * @param testName 测试名称
     * @param description 测试描述
     * @param testExecution 测试执行逻辑，返回额外的指标数据
     * @return 基准测试结果 DTO
     */
    public static BenchmarkResultDTO execute(String testName, String description, Supplier<Map<String, Object>> testExecution) {
        StopWatch sw = new StopWatch();
        sw.start();
        Map<String, Object> additionalMetrics;
        try {
            additionalMetrics = testExecution.get();
        } finally {
            sw.stop();
        }
        long executionTime = sw.getTotal(TimeUnit.MILLISECONDS);
        
        return new BenchmarkResultDTO(testName, description, executionTime, additionalMetrics);
    }
}