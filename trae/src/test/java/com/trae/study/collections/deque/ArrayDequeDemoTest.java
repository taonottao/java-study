package com.trae.study.collections.deque;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ArrayDeque 演示测试")
class ArrayDequeDemoTest {

    @Test
    @DisplayName("基本双端操作")
    void testBasicOps() {
        ArrayDequeDemo demo = new ArrayDequeDemo();
        List<Integer> r = demo.demonstrateBasicDequeOps();
        assertEquals(Arrays.asList(0,3,1,2), r);
    }

    @Test
    @DisplayName("作为栈使用")
    void testAsStack() {
        ArrayDequeDemo demo = new ArrayDequeDemo();
        List<Integer> r = demo.demonstrateAsStack(Arrays.asList(1,2,3,4));
        assertEquals(Arrays.asList(4,3,2,1), r);
    }

    @Test
    @DisplayName("作为队列使用")
    void testAsQueue() {
        ArrayDequeDemo demo = new ArrayDequeDemo();
        List<Integer> r = demo.demonstrateAsQueue(Arrays.asList(1,2,3,4));
        assertEquals(Arrays.asList(1,2,3,4), r);
    }

    @Test
    @DisplayName("与 LinkedList 微基准对比")
    void testBenchmark() {
        ArrayDequeDemo demo = new ArrayDequeDemo();
        ArrayDequeDemo.DequeBenchmarkDTO m = demo.benchmarkSimple(10000);
        assertTrue(m.getArrayDequeNs() >= 0);
        assertTrue(m.getLinkedListNs() >= 0);
    }
}