package com.trae.study.collections.concurrent;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.*;

/**
 * BlockingQueue 演示：以 ArrayBlockingQueue/LinkedBlockingQueue 为例
 *
 * 展示阻塞生产者-消费者模型、背压、限流与超时获取等特性。
 */
public class BlockingQueueDemo {

    /** 任务 DTO，包含入队时间用于统计 */
    public static class JobDTO {
        private final String id;
        private final ZonedDateTime enqueueTime;
        public JobDTO(String id) {
            this.id = id;
            this.enqueueTime = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
        }
        public String getId() { return id; }
        public ZonedDateTime getEnqueueTime() { return enqueueTime; }
    }

    /** 运行统计 DTO */
    public static class RunStatsDTO {
        private final String queueType;
        private final int producers;
        private final int consumers;
        private final int produced;
        private final int consumed;
        private final long durationMs;
        public RunStatsDTO(String queueType, int producers, int consumers, int produced, int consumed, long durationMs) {
            this.queueType = queueType; this.producers = producers; this.consumers = consumers; this.produced = produced; this.consumed = consumed; this.durationMs = durationMs;
        }
        public String getQueueType() { return queueType; }
        public int getProducers() { return producers; }
        public int getConsumers() { return consumers; }
        public int getProduced() { return produced; }
        public int getConsumed() { return consumed; }
        public long getDurationMs() { return durationMs; }
    }

    /** 使用指定 BlockingQueue 进行生产消费模拟 */
    public RunStatsDTO simulate(String type, int capacity, int producers, int consumers, int totalJobs) {
        BlockingQueue<JobDTO> queue = "array".equalsIgnoreCase(type)
            ? new ArrayBlockingQueue<>(capacity)
            : new LinkedBlockingQueue<>(capacity);
        ExecutorService pool = Executors.newFixedThreadPool(producers + consumers);
        CountDownLatch latch = new CountDownLatch(producers + consumers);
        int per = totalJobs / producers;
        long start = System.currentTimeMillis();
        for (int i = 0; i < producers; i++) {
            int idx = i;
            pool.submit(() -> {
                try {
                    for (int j = 0; j < per; j++) {
                        queue.put(new JobDTO("P" + idx + "-" + j)); // 满时阻塞，体现背压
                    }
                } catch (InterruptedException ignored) {} finally { latch.countDown(); }
            });
        }
        for (int i = 0; i < consumers; i++) {
            pool.submit(() -> {
                try {
                    while (true) {
                        JobDTO job = queue.poll(200, TimeUnit.MILLISECONDS); // 超时返回 null，避免永久阻塞
                        if (job == null && latch.getCount() <= consumers) break;
                    }
                } catch (InterruptedException ignored) {} finally { latch.countDown(); }
            });
        }
        try { latch.await(10, TimeUnit.SECONDS); } catch (InterruptedException ignored) {}
        pool.shutdownNow();
        long time = System.currentTimeMillis() - start;
        int produced = per * producers;
        int consumed = totalJobs - queue.size();
        return new RunStatsDTO(type, producers, consumers, produced, consumed, time);
    }
}