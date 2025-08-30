package com.example.benchmark;

import cn.hutool.core.collection.CollUtil;
import com.example.model.BenchmarkResultDTO;
import com.example.util.StopWatchUtil;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 使用 Hutool StopWatch 对 ArrayList 与 LinkedList 的 add 操作进行粗略对比。
 * 注意：结果仅做数量级参考。
 */
class ListAddBenchmarkStopWatchTest {

    @Test
    void compareArrayListAndLinkedListAdd() {
        final int n = 100_000;

        long arrayListMs = StopWatchUtil.runAndMeasureMillis(() -> {
            ArrayList<Integer> list = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                list.add(i);
            }
        });

        long linkedListMs = StopWatchUtil.runAndMeasureMillis(() -> {
            LinkedList<Integer> list = new LinkedList<>();
            for (int i = 0; i < n; i++) {
                list.add(i);
            }
        });

        BenchmarkResultDTO arrayDto = BenchmarkResultDTO.builder()
                .benchmarkName("ArrayList-add")
                .sampleSize(n)
                .elapsedMs(arrayListMs)
                .notes("尾部追加")
                .build();

        BenchmarkResultDTO linkedDto = BenchmarkResultDTO.builder()
                .benchmarkName("LinkedList-add")
                .sampleSize(n)
                .elapsedMs(linkedListMs)
                .notes("尾部追加")
                .build();

        // 断言仅为确保测试执行：至少有一个结果 >= 0
        assertTrue(arrayDto.getElapsedMs() >= 0 && linkedDto.getElapsedMs() >= 0);

        System.out.printf("StopWatch benchmark: %s ms=%d, %s ms=%d%n",
                arrayDto.getBenchmarkName(), arrayDto.getElapsedMs(),
                linkedDto.getBenchmarkName(), linkedDto.getElapsedMs());
    }
}