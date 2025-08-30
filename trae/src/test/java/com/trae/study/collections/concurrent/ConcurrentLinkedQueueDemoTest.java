package com.trae.study.collections.concurrent;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ConcurrentLinkedQueue 演示测试")
class ConcurrentLinkedQueueDemoTest {

    @Test
    @DisplayName("FIFO 行为")
    void testFifo() {
        ConcurrentLinkedQueueDemo demo = new ConcurrentLinkedQueueDemo();
        var out = demo.demonstrateFifo();
        assertEquals(java.util.Arrays.asList(1,2,3), out);
    }

    @Test
    @DisplayName("并发生产消费模拟")
    void testSimulate() {
        ConcurrentLinkedQueueDemo demo = new ConcurrentLinkedQueueDemo();
        var stats = demo.simulateProducersConsumers(4, 4, 4000);
        assertEquals(4, stats.getProducers());
        assertEquals(4, stats.getConsumers());
        assertTrue(stats.getProduced() > 0);
        assertTrue(stats.getConsumed() >= 0);
        assertTrue(stats.getTimeMs() >= 0);
    }
}