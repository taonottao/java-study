package com.trae.study.collections.concurrent;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ConcurrentLinkedQueue 演示
 *
 * 展示无界、基于非阻塞 CAS 的队列在并发入队/出队下的行为与吞吐。
 */
public class ConcurrentLinkedQueueDemo {

    /** 并发生产-消费模拟结果 DTO */
    public static class ConcurrencyStatsDTO {
        private final int producers;
        private final int consumers;
        private final int produced;
        private final int consumed;
        private final long timeMs;
        public ConcurrencyStatsDTO(int producers, int consumers, int produced, int consumed, long timeMs) {
            this.producers = producers; this.consumers = consumers; this.produced = produced; this.consumed = consumed; this.timeMs = timeMs;
        }
        public int getProducers() { return producers; }
        public int getConsumers() { return consumers; }
        public int getProduced() { return produced; }
        public int getConsumed() { return consumed; }
        public long getTimeMs() { return timeMs; }
    }

    /** 基本 FIFO 行为演示 */
    public List<Integer> demonstrateFifo() {
        Queue<Integer> q = new ConcurrentLinkedQueue<>();
        q.offer(1); q.offer(2); q.offer(3);
        List<Integer> out = new ArrayList<>();
        out.add(q.poll());
        out.add(q.poll());
        out.add(q.poll());
        return out;
    }

    /** 并发生产消费：p 个生产者、c 个消费者，总任务 total */
    public ConcurrencyStatsDTO simulateProducersConsumers(int producers, int consumers, int total) {
        ConcurrentLinkedQueue<Integer> q = new ConcurrentLinkedQueue<>();
        ExecutorService pool = Executors.newFixedThreadPool(producers + consumers);
        AtomicInteger produced = new AtomicInteger();
        AtomicInteger consumed = new AtomicInteger();
        long start = System.currentTimeMillis();
        CountDownLatch latch = new CountDownLatch(producers + consumers);
        int per = total / producers;
        for (int p = 0; p < producers; p++) {
            pool.submit(() -> {
                for (int i = 0; i < per; i++) { q.offer(i); produced.incrementAndGet(); }
                latch.countDown();
            });
        }
        for (int c = 0; c < consumers; c++) {
            pool.submit(() -> {
                int x;
                while ((x = q.poll() != null ? 1 : 0) == 1) consumed.incrementAndGet();
                latch.countDown();
            });
        }
        try { latch.await(5, TimeUnit.SECONDS); } catch (InterruptedException ignored) {}
        pool.shutdownNow();
        long time = System.currentTimeMillis() - start;
        return new ConcurrencyStatsDTO(producers, consumers, produced.get(), consumed.get(), time);
    }
}