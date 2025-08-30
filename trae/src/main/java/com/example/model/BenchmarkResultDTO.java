package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO: 基准测试结果封装
 * - 用于对比不同实现或不同参数下的性能表现
 * - 不使用 Map，使用字段语义更清晰
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BenchmarkResultDTO {
    /** 基准名称，例如："ArrayList-add" */
    private String benchmarkName;
    /** 样本大小或规模，例如：100_000 */
    private int sampleSize;
    /** 耗时（毫秒） */
    private long elapsedMs;
    /** 备注，例如实现或参数描述 */
    private String notes;
}