package com.trae.study.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 基准测试结果 DTO
 *
 * 说明：
 * - 统一工程内 Demo 的性能结果返回结构，避免直接使用 Map 作为返回类型。
 * - 兼容不同使用方式：既支持 Builder，也支持部分类中使用的有参构造方法。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BenchmarkResultDTO {
    /** 测试名称，例如："Stream API 性能对比" */
    private String testName;
    /** 描述信息，例如：测试场景或方法说明 */
    private String description;
    /** 执行耗时（毫秒） */
    private long executionTimeMs;
    /** 额外指标（键为指标名，值为指标值），例如：各阶段耗时、数据量等 */
    private Map<String, Object> additionalMetrics;

    /**
     * 兼容已有代码的三参构造：有的地方未提供 description
     * @param testName 测试名称
     * @param executionTimeMs 执行耗时（毫秒）
     * @param additionalMetrics 额外指标
     */
    public BenchmarkResultDTO(String testName, long executionTimeMs, Map<String, Object> additionalMetrics) {
        this.testName = testName;
        this.executionTimeMs = executionTimeMs;
        this.additionalMetrics = additionalMetrics;
    }
}