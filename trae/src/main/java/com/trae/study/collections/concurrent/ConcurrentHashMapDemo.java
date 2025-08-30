package com.trae.study.collections.concurrent;

import cn.hutool.core.util.ReflectUtil;
import com.trae.study.dto.BenchmarkResultDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * ConcurrentHashMap 源码级演示
 * 深入分析 ConcurrentHashMap 的并发机制、CAS 操作、树化过程等核心特性
 * 
 * @author trae
 * @since 2024
 */
@Slf4j
public class ConcurrentHashMapDemo {
    
    /**
     * 测试数据类
     */
    @Data
    @AllArgsConstructor
    public static class TestData {
        private String key;
        private Integer value;
        private Long timestamp;
    }
    
    /**
     * 并发统计结果
     */
    @Data
    @AllArgsConstructor
    public static class ConcurrencyStats {
        private int threadCount;
        private int operationsPerThread;
        private long totalTimeMs;
        private long operationsPerSecond;
        private int finalSize;
        private boolean hasDataRace;
    }
    
    /**
     * 演示 ConcurrentHashMap 的基本并发特性
     */
    public void demonstrateBasicConcurrency() {
        log.info("=== ConcurrentHashMap 基本并发特性演示 ===");
        
        ConcurrentHashMap<String, Integer> concurrentMap = new ConcurrentHashMap<>();
        HashMap<String, Integer> normalMap = new HashMap<>();
        
        // 1. 基本线程安全性对比
        log.info("\n1. 线程安全性对比");
        
        int threadCount = 10;
        int operationsPerThread = 1000;
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        // ConcurrentHashMap 并发测试
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            new Thread(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        String key = "key_" + (threadId * operationsPerThread + j);
                        concurrentMap.put(key, j);
                        concurrentMap.get(key);
                        if (j % 100 == 0) {
                            concurrentMap.remove(key);
                        }
                    }
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        
        try {
            latch.await();
            long concurrentTime = System.currentTimeMillis() - startTime;
            log.info("ConcurrentHashMap 并发操作完成，耗时: {}ms，最终大小: {}", 
                concurrentTime, concurrentMap.size());
            
            // 验证数据一致性
            boolean isConsistent = concurrentMap.values().stream()
                .allMatch(value -> value >= 0 && value < operationsPerThread);
            log.info("数据一致性检查: {}", isConsistent ? "通过" : "失败");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("并发测试被中断", e);
        }
    }
    
    /**
     * 演示 ConcurrentHashMap 的内部结构
     */
    public void demonstrateInternalStructure() {
        log.info("\n=== ConcurrentHashMap 内部结构演示 ===");
        
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        
        // 1. 初始状态分析
        log.info("\n1. 初始状态分析");
        analyzeInternalStructure(map, "初始状态");
        
        // 2. 添加少量元素
        log.info("\n2. 添加少量元素后的结构");
        for (int i = 0; i < 8; i++) {
            map.put("key" + i, "value" + i);
        }
        analyzeInternalStructure(map, "8个元素");
        
        // 3. 触发扩容
        log.info("\n3. 触发扩容后的结构");
        for (int i = 8; i < 20; i++) {
            map.put("key" + i, "value" + i);
        }
        analyzeInternalStructure(map, "20个元素");
        
        // 4. 制造哈希冲突
        log.info("\n4. 制造哈希冲突");
        demonstrateHashCollision(map);
    }
    
    /**
     * 分析 ConcurrentHashMap 的内部结构
     */
    private void analyzeInternalStructure(ConcurrentHashMap<String, String> map, String phase) {
        try {
            // 通过反射获取内部字段
            Field tableField = ConcurrentHashMap.class.getDeclaredField("table");
            tableField.setAccessible(true);
            Object[] table = (Object[]) tableField.get(map);
            
            Field sizectlField = ConcurrentHashMap.class.getDeclaredField("sizeCtl");
            sizectlField.setAccessible(true);
            int sizeCtl = sizectlField.getInt(map);
            
            log.info("{} - 表大小: {}, sizeCtl: {}, 元素数量: {}", 
                phase, 
                table != null ? table.length : 0, 
                sizeCtl, 
                map.size());
            
            if (table != null) {
                // 分析桶的使用情况
                int usedBuckets = 0;
                int chainLength = 0;
                for (Object node : table) {
                    if (node != null) {
                        usedBuckets++;
                        // 计算链表长度（简化版）
                        chainLength++;
                    }
                }
                
                double loadFactor = (double) map.size() / table.length;
                log.info("使用的桶数: {}/{}, 负载因子: {:.2f}", 
                    usedBuckets, table.length, loadFactor);
            }
            
        } catch (Exception e) {
            log.warn("无法分析内部结构: {}", e.getMessage());
        }
    }
    
    /**
     * 演示哈希冲突处理
     */
    private void demonstrateHashCollision(ConcurrentHashMap<String, String> map) {
        // 创建具有相同哈希值的键（简化演示）
        List<CollidingKey> collidingKeys = Arrays.asList(
            new CollidingKey("collision1", 100),
            new CollidingKey("collision2", 100),
            new CollidingKey("collision3", 100),
            new CollidingKey("collision4", 100)
        );
        
        ConcurrentHashMap<CollidingKey, String> collisionMap = new ConcurrentHashMap<>();
        
        log.info("添加哈希冲突的键:");
        for (CollidingKey key : collidingKeys) {
            collisionMap.put(key, "value_" + key.getName());
            log.info("添加键: {}, 哈希值: {}", key.getName(), key.hashCode());
        }
        
        log.info("冲突处理后的映射大小: {}", collisionMap.size());
        
        // 验证所有键都能正确检索
        boolean allFound = collidingKeys.stream()
            .allMatch(key -> collisionMap.containsKey(key));
        log.info("所有冲突键都能正确检索: {}", allFound);
    }
    
    /**
     * 演示 CAS 操作和原子性
     */
    public void demonstrateCASOperations() {
        log.info("\n=== CAS 操作和原子性演示 ===");
        
        ConcurrentHashMap<String, AtomicInteger> counterMap = new ConcurrentHashMap<>();
        
        // 1. 原子性更新操作
        log.info("\n1. 原子性更新操作");
        String counterKey = "counter";
        
        // 使用 compute 方法进行原子更新
        for (int i = 0; i < 5; i++) {
            AtomicInteger result = counterMap.compute(counterKey, (key, value) -> {
                if (value == null) {
                    log.info("首次创建计数器");
                    return new AtomicInteger(1);
                } else {
                    int newValue = value.incrementAndGet();
                    log.info("计数器递增到: {}", newValue);
                    return value;
                }
            });
        }
        
        // 2. 并发计数测试
        log.info("\n2. 并发计数测试");
        demonstrateConcurrentCounting();
        
        // 3. putIfAbsent 原子操作
        log.info("\n3. putIfAbsent 原子操作");
        ConcurrentHashMap<String, String> atomicMap = new ConcurrentHashMap<>();
        
        // 模拟多线程同时尝试插入相同键
        ExecutorService executor = Executors.newFixedThreadPool(5);
        CountDownLatch latch = new CountDownLatch(5);
        
        for (int i = 0; i < 5; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    String result = atomicMap.putIfAbsent("unique_key", "thread_" + threadId);
                    if (result == null) {
                        log.info("线程 {} 成功插入值", threadId);
                    } else {
                        log.info("线程 {} 发现已存在值: {}", threadId, result);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        try {
            latch.await();
            log.info("最终值: {}", atomicMap.get("unique_key"));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }
    }
    
    /**
     * 并发计数演示
     */
    private void demonstrateConcurrentCounting() {
        ConcurrentHashMap<String, LongAdder> counters = new ConcurrentHashMap<>();
        int threadCount = 10;
        int incrementsPerThread = 1000;
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threadCount; i++) {
            final String counterName = "counter_" + (i % 3); // 3个计数器
            executor.submit(() -> {
                try {
                    for (int j = 0; j < incrementsPerThread; j++) {
                        counters.computeIfAbsent(counterName, k -> new LongAdder())
                            .increment();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        try {
            latch.await();
            long endTime = System.currentTimeMillis();
            
            log.info("并发计数完成，耗时: {}ms", endTime - startTime);
            counters.forEach((name, counter) -> {
                log.info("计数器 {}: {}", name, counter.sum());
            });
            
            // 验证总计数
            long totalCount = counters.values().stream()
                .mapToLong(LongAdder::sum)
                .sum();
            long expectedCount = (long) threadCount * incrementsPerThread;
            log.info("总计数: {}, 期望: {}, 正确性: {}", 
                totalCount, expectedCount, totalCount == expectedCount);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }
    }
    
    /**
     * 演示树化和退化过程
     */
    public void demonstrateTreeification() {
        log.info("\n=== 树化和退化过程演示 ===");
        
        // 注意：实际的树化阈值是8，但需要表大小>=64
        // 这里演示概念，实际触发条件较复杂
        
        ConcurrentHashMap<CollidingKey, String> map = new ConcurrentHashMap<>();
        
        log.info("\n1. 创建大量哈希冲突以触发树化");
        
        // 创建足够多的冲突键
        List<CollidingKey> keys = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            keys.add(new CollidingKey("tree_key_" + i, 42)); // 相同哈希值
        }
        
        // 分批添加并观察结构变化
        for (int batch = 1; batch <= 4; batch++) {
            int endIndex = batch * 5;
            for (int i = (batch - 1) * 5; i < endIndex && i < keys.size(); i++) {
                map.put(keys.get(i), "value_" + i);
            }
            
            log.info("批次 {}: 添加了 {} 个冲突元素，当前大小: {}", 
                batch, endIndex - (batch - 1) * 5, map.size());
            
            // 测试检索性能
            long startTime = System.nanoTime();
            for (CollidingKey key : keys.subList(0, Math.min(endIndex, keys.size()))) {
                map.get(key);
            }
            long endTime = System.nanoTime();
            
            log.info("检索 {} 个元素耗时: {} ns", 
                Math.min(endIndex, keys.size()), endTime - startTime);
        }
        
        // 验证所有元素都能正确检索
        boolean allFound = keys.stream().allMatch(map::containsKey);
        log.info("所有冲突元素都能正确检索: {}", allFound);
    }
    
    /**
     * 性能基准测试
     */
    public BenchmarkResultDTO benchmarkConcurrentHashMap() {
        log.info("\n=== ConcurrentHashMap 性能基准测试 ===");
        
        long startTime = System.currentTimeMillis();
        Map<String, Object> metrics = new HashMap<>();
        
        // 1. 单线程性能测试
        log.info("\n1. 单线程性能测试");
        ConcurrencyStats singleThreadStats = performConcurrencyTest(1, 100000);
        metrics.put("单线程操作/秒", singleThreadStats.getOperationsPerSecond());
        log.info("单线程性能: {} 操作/秒", singleThreadStats.getOperationsPerSecond());
        
        // 2. 多线程性能测试
        log.info("\n2. 多线程性能测试");
        int[] threadCounts = {2, 4, 8, 16};
        for (int threadCount : threadCounts) {
            ConcurrencyStats stats = performConcurrencyTest(threadCount, 50000);
            metrics.put(threadCount + "线程操作/秒", stats.getOperationsPerSecond());
            log.info("{} 线程性能: {} 操作/秒，数据竞争: {}", 
                threadCount, stats.getOperationsPerSecond(), stats.isHasDataRace());
        }
        
        // 3. 与 HashMap + synchronized 对比
        log.info("\n3. 与同步 HashMap 性能对比");
        long concurrentMapTime = measureConcurrentMapPerformance();
        long synchronizedMapTime = measureSynchronizedMapPerformance();
        
        metrics.put("ConcurrentHashMap耗时(ms)", concurrentMapTime);
        metrics.put("Synchronized HashMap耗时(ms)", synchronizedMapTime);
        metrics.put("性能提升倍数", (double) synchronizedMapTime / concurrentMapTime);
        
        log.info("ConcurrentHashMap 耗时: {}ms", concurrentMapTime);
        log.info("Synchronized HashMap 耗时: {}ms", synchronizedMapTime);
        log.info("性能提升: {:.2f}x", (double) synchronizedMapTime / concurrentMapTime);
        
        // 4. 内存使用分析
        log.info("\n4. 内存使用分析");
        analyzeMemoryUsage(metrics);
        
        long totalTime = System.currentTimeMillis() - startTime;
        metrics.put("总测试时间(ms)", totalTime);
        
        return new BenchmarkResultDTO(
            "ConcurrentHashMap 性能基准测试",
            totalTime,
            metrics
        );
    }
    
    /**
     * 执行并发性能测试
     */
    private ConcurrencyStats performConcurrencyTest(int threadCount, int operationsPerThread) {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            new Thread(() -> {
                try {
                    Random random = new Random(threadId);
                    for (int j = 0; j < operationsPerThread; j++) {
                        String key = "key_" + random.nextInt(operationsPerThread / 2);
                        
                        // 混合操作：put, get, remove
                        switch (j % 10) {
                            case 0:
                            case 1:
                            case 2:
                                map.remove(key);
                                break;
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                                map.get(key);
                                break;
                            default:
                                map.put(key, j);
                        }
                    }
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        long totalOperations = (long) threadCount * operationsPerThread;
        long operationsPerSecond = totalOperations * 1000 / totalTime;
        
        // 简单的数据竞争检测（检查是否有异常的数据）
        boolean hasDataRace = map.values().stream()
            .anyMatch(value -> value < 0 || value >= operationsPerThread);
        
        return new ConcurrencyStats(
            threadCount,
            operationsPerThread,
            totalTime,
            operationsPerSecond,
            map.size(),
            hasDataRace
        );
    }
    
    /**
     * 测量 ConcurrentHashMap 性能
     */
    private long measureConcurrentMapPerformance() {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        int threadCount = 8;
        int operationsPerThread = 10000;
        
        return measureMapPerformance(map, threadCount, operationsPerThread, false);
    }
    
    /**
     * 测量同步 HashMap 性能
     */
    private long measureSynchronizedMapPerformance() {
        Map<String, Integer> map = Collections.synchronizedMap(new HashMap<>());
        int threadCount = 8;
        int operationsPerThread = 10000;
        
        return measureMapPerformance(map, threadCount, operationsPerThread, true);
    }
    
    /**
     * 通用的 Map 性能测量方法
     */
    private long measureMapPerformance(Map<String, Integer> map, int threadCount, 
                                     int operationsPerThread, boolean needSync) {
        CountDownLatch latch = new CountDownLatch(threadCount);
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            new Thread(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        String key = "perf_key_" + (threadId * operationsPerThread + j);
                        
                        if (needSync) {
                            synchronized (map) {
                                map.put(key, j);
                                map.get(key);
                            }
                        } else {
                            map.put(key, j);
                            map.get(key);
                        }
                    }
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return System.currentTimeMillis() - startTime;
    }
    
    /**
     * 分析内存使用情况
     */
    private void analyzeMemoryUsage(Map<String, Object> metrics) {
        Runtime runtime = Runtime.getRuntime();
        
        // 创建不同大小的 ConcurrentHashMap 并测量内存
        int[] sizes = {1000, 10000, 100000};
        
        for (int size : sizes) {
            // 强制垃圾回收
            System.gc();
            long beforeMemory = runtime.totalMemory() - runtime.freeMemory();
            
            // 创建并填充 map
            ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
            for (int i = 0; i < size; i++) {
                map.put("memory_key_" + i, "memory_value_" + i);
            }
            
            // 再次测量内存
            long afterMemory = runtime.totalMemory() - runtime.freeMemory();
            long memoryUsed = afterMemory - beforeMemory;
            
            double memoryPerEntry = (double) memoryUsed / size;
            metrics.put(size + "个元素内存使用(bytes)", memoryUsed);
            metrics.put(size + "个元素平均内存/条目(bytes)", memoryPerEntry);
            
            log.info("{} 个元素内存使用: {} bytes, 平均每个条目: {:.2f} bytes", 
                size, memoryUsed, memoryPerEntry);
        }
    }
    
    /**
     * 运行所有演示
     */
    public void runAllDemonstrations() {
        log.info("开始 ConcurrentHashMap 综合演示");
        
        demonstrateBasicConcurrency();
        demonstrateInternalStructure();
        demonstrateCASOperations();
        demonstrateTreeification();
        
        BenchmarkResultDTO benchmark = benchmarkConcurrentHashMap();
        log.info("\n性能基准测试完成: {}", benchmark.getTestName());
        log.info("总耗时: {}ms", benchmark.getExecutionTimeMs());
        
        log.info("\nConcurrentHashMap 演示完成");
    }
    
    /**
     * 用于制造哈希冲突的测试类
     */
    @Data
    @AllArgsConstructor
    public static class CollidingKey {
        private String name;
        private int fixedHashCode;
        
        @Override
        public int hashCode() {
            return fixedHashCode; // 固定哈希值，制造冲突
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            CollidingKey that = (CollidingKey) obj;
            return Objects.equals(name, that.name);
        }
        
        @Override
        public String toString() {
            return "CollidingKey{name='" + name + "', hash=" + fixedHashCode + "}";
        }
    }
}