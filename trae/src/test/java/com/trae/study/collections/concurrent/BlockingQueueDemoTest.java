package com.trae.study.collections.concurrent;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BlockingQueue 演示测试")
class BlockingQueueDemoTest {

    @Test
    @DisplayName("ArrayBlockingQueue 生产消费")
    void testArrayBlockingQueue() {
        BlockingQueueDemo demo = new BlockingQueueDemo();
        var stats = demo.simulate("array", 256, 4, 4, 2000);
        assertEquals("array", stats.getQueueType());
        assertEquals(4, stats.getProducers());
        assertEquals(4, stats.getConsumers());
        assertTrue(stats.getProduced() > 0);
        assertTrue(stats.getConsumed() >= 0);
        assertTrue(stats.getDurationMs() >= 0);
    }

    @Test
    @DisplayName("LinkedBlockingQueue 生产消费")
    void testLinkedBlockingQueue() {
        BlockingQueueDemo demo = new BlockingQueueDemo();
        var stats = demo.simulate("linked", 256, 4, 4, 2000);
        assertEquals("linked", stats.getQueueType());
        assertTrue(stats.getProduced() > 0);
        assertTrue(stats.getConsumed() >= 0);
    }
}