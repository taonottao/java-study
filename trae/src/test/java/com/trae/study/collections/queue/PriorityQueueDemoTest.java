package com.trae.study.collections.queue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PriorityQueue 演示测试")
class PriorityQueueDemoTest {

    @Test
    @DisplayName("基本最小堆行为")
    void testBasicBehavior() {
        PriorityQueueDemo demo = new PriorityQueueDemo();
        List<PriorityQueueDemo.Task> order = demo.demonstrateBasicBehavior();
        int prev = Integer.MIN_VALUE;
        for (PriorityQueueDemo.Task t : order) {
            assertTrue(t.getPriority() >= prev);
            prev = t.getPriority();
        }
    }

    @Test
    @DisplayName("自定义比较器")
    void testCustomComparator() {
        PriorityQueueDemo demo = new PriorityQueueDemo();
        List<String> inputs = Arrays.asList("bbb", "a", "cc", "dd", "eee", "bb");
        List<String> order = demo.demonstrateCustomComparator(inputs);
        List<String> expected = inputs.stream().sorted(Comparator
                .comparingInt(String::length).thenComparing(Comparator.naturalOrder()))
            .collect(Collectors.toList());
        assertEquals(expected, order);
    }

    @Test
    @DisplayName("TopK 最大元素")
    void testTopKMax() {
        PriorityQueueDemo demo = new PriorityQueueDemo();
        int[] a = {5,1,6,2,9,7,3,8,4};
        PriorityQueueDemo.TopKResultDTO r = demo.topKMax(a, 3);
        assertEquals(Arrays.asList(9,8,7), r.getTopK());
        assertEquals("min-heap O(n log k)", r.getAlgorithm());
        assertTrue(r.getTimeNs() >= 0);
    }

    @Test
    @DisplayName("合并k个有序列表")
    void testMergeKSortedLists() {
        PriorityQueueDemo demo = new PriorityQueueDemo();
        List<List<Integer>> lists = Arrays.asList(
            Arrays.asList(1,4,7),
            Arrays.asList(2,5,8),
            Arrays.asList(3,6,9)
        );
        PriorityQueueDemo.MergeResultDTO r = demo.mergeKSortedLists(lists);
        assertEquals(3, r.getInputLists());
        assertEquals(9, r.getTotalCount());
        assertEquals(Arrays.asList(1,2,3,4,5,6,7,8,9), r.getMerged());
        assertTrue(r.getTimeNs() >= 0);
    }
}