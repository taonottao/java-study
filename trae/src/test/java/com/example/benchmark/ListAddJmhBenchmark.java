package com.example.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 使用 JMH 对 ArrayList 与 LinkedList 的尾部 add 进行微基准测试。
 * 注意：JMH 适用于更严谨的性能对比，避免常见基准陷阱。
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 300, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
@State(Scope.Thread)
public class ListAddJmhBenchmark {

    @Param({"1024"})
    public int capacity;

    private List<Integer> arrayList;
    private List<Integer> linkedList;

    @Setup(Level.Invocation)
    public void setup() {
        arrayList = new ArrayList<>(capacity);
        linkedList = new LinkedList<>();
    }

    /**
     * 基准：向 ArrayList 尾部追加 N 个元素。
     */
    @Benchmark
    public void arrayListAddBatch(Blackhole bh) {
        for (int i = 0; i < capacity; i++) {
            arrayList.add(i);
        }
        bh.consume(arrayList.size());
    }

    /**
     * 基准：向 LinkedList 尾部追加 N 个元素。
     */
    @Benchmark
    public void linkedListAddBatch(Blackhole bh) {
        for (int i = 0; i < capacity; i++) {
            linkedList.add(i);
        }
        bh.consume(linkedList.size());
    }

    /**
     * 可选：通过 main 方法在 IDE 中快速运行该基准。
     * 也可以使用 Maven 命令或 JMH 插件运行。
     */
    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(ListAddJmhBenchmark.class.getSimpleName())
                .detectJvmArgs()
                .build();
        new Runner(opt).run();
    }
}