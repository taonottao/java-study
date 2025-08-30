package com.trae.study.collections.concurrent;

import cn.hutool.core.util.RandomUtil;
import com.trae.study.dto.BenchmarkResultDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * ConcurrentSkipListMap 源码级演示
 * 深入分析 ConcurrentSkipListMap 的跳表结构、有序性、并发特性等核心机制
 * 
 * @author trae
 * @since 2024
 */
@Slf4j
public class ConcurrentSkipListMapDemo {
    
    /**
     * 测试数据类
     */
    @Data
    @AllArgsConstructor
    public static class Student implements Comparable<Student> {
        private Integer id;
        private String name;
        private Double score;
        private Long timestamp;
        
        @Override
        public int compareTo(Student other) {
            // 按分数降序，分数相同按ID升序
            int scoreCompare = Double.compare(other.score, this.score);
            return scoreCompare != 0 ? scoreCompare : Integer.compare(this.id, other.id);
        }
        
        @Override
        public String toString() {
            return String.format("Student{id=%d, name='%s', score=%.1f}", id, name, score);
        }
    }
    
    /**
     * 范围查询结果
     */
    @Data
    @AllArgsConstructor
    public static class RangeQueryResult {
        private String queryType;
        private int resultCount;
        private long executionTimeNs;
        private List<String> sampleResults;
    }
    
    /**
     * 演示 ConcurrentSkipListMap 的基本有序特性
     */
    public void demonstrateBasicOrderedFeatures() {
        log.info("=== ConcurrentSkipListMap 基本有序特性演示 ===");
        
        // 1. 基本插入和有序性
        log.info("\n1. 基本插入和有序性");
        ConcurrentSkipListMap<Integer, String> orderedMap = new ConcurrentSkipListMap<>();
        
        // 随机插入数据
        int[] randomNumbers = {15, 3, 9, 1, 12, 7, 20, 5, 18, 11};
        for (int num : randomNumbers) {
            orderedMap.put(num, "value_" + num);
            log.info("插入: {} -> {}", num, "value_" + num);
        }
        
        log.info("\n插入完成后的有序遍历:");
        orderedMap.forEach((key, value) -> {
            log.info("键: {}, 值: {}", key, value);
        });
        
        // 2. 自定义比较器
        log.info("\n2. 自定义比较器（降序）");
        ConcurrentSkipListMap<Integer, String> descendingMap = 
            new ConcurrentSkipListMap<>(Collections.reverseOrder());
        
        for (int num : randomNumbers) {
            descendingMap.put(num, "desc_value_" + num);
        }
        
        log.info("降序遍历:");
        descendingMap.forEach((key, value) -> {
            log.info("键: {}, 值: {}", key, value);
        });
        
        // 3. 复杂对象排序
        log.info("\n3. 复杂对象排序（学生成绩）");
        demonstrateComplexObjectOrdering();
    }
    
    /**
     * 演示复杂对象的排序
     */
    private void demonstrateComplexObjectOrdering() {
        ConcurrentSkipListMap<Student, String> studentMap = new ConcurrentSkipListMap<>();
        
        // 创建测试学生数据
        List<Student> students = Arrays.asList(
            new Student(1, "Alice", 95.5, System.currentTimeMillis()),
            new Student(2, "Bob", 87.0, System.currentTimeMillis()),
            new Student(3, "Charlie", 95.5, System.currentTimeMillis()), // 同分数
            new Student(4, "David", 92.0, System.currentTimeMillis()),
            new Student(5, "Eve", 89.5, System.currentTimeMillis())
        );
        
        // 随机顺序插入
        Collections.shuffle(students);
        log.info("随机插入学生数据:");
        for (Student student : students) {
            studentMap.put(student, "grade_" + student.getId());
            log.info("插入: {}", student);
        }
        
        log.info("\n按成绩排序后的结果（分数降序，ID升序）:");
        AtomicInteger rank = new AtomicInteger(1);
        studentMap.forEach((student, grade) -> {
            log.info("第{}名: {} -> {}", rank.getAndIncrement(), student, grade);
        });
    }
    
    /**
     * 演示范围查询功能
     */
    public void demonstrateRangeQueries() {
        log.info("\n=== 范围查询功能演示 ===");
        
        ConcurrentSkipListMap<Integer, String> map = new ConcurrentSkipListMap<>();
        
        // 填充测试数据
        for (int i = 1; i <= 100; i++) {
            map.put(i, "value_" + i);
        }
        
        log.info("填充了1-100的数据，总数: {}", map.size());
        
        // 1. subMap 范围查询
        log.info("\n1. subMap 范围查询");
        NavigableMap<Integer, String> subMap = map.subMap(20, true, 30, true);
        log.info("范围 [20, 30] 的元素数量: {}", subMap.size());
        log.info("范围内的键: {}", subMap.keySet());
        
        // 2. headMap 和 tailMap
        log.info("\n2. headMap 和 tailMap");
        NavigableMap<Integer, String> headMap = map.headMap(25, false);
        NavigableMap<Integer, String> tailMap = map.tailMap(75, true);
        
        log.info("小于25的元素数量: {}", headMap.size());
        log.info("大于等于75的元素数量: {}", tailMap.size());
        
        // 3. 边界查询
        log.info("\n3. 边界查询");
        demonstrateBoundaryQueries(map);
        
        // 4. 范围查询性能测试
        log.info("\n4. 范围查询性能测试");
        performRangeQueryBenchmark(map);
    }
    
    /**
     * 演示边界查询
     */
    private void demonstrateBoundaryQueries(ConcurrentSkipListMap<Integer, String> map) {
        // 查找边界元素
        log.info("最小键: {}", map.firstKey());
        log.info("最大键: {}", map.lastKey());
        
        // 查找最接近的元素
        Integer target = 25;
        log.info("\n查找最接近 {} 的元素:", target);
        
        Integer lowerKey = map.lowerKey(target);  // < target
        Integer floorKey = map.floorKey(target);  // <= target
        Integer ceilingKey = map.ceilingKey(target); // >= target
        Integer higherKey = map.higherKey(target);   // > target
        
        log.info("lowerKey (< {}): {}", target, lowerKey);
        log.info("floorKey (<= {}): {}", target, floorKey);
        log.info("ceilingKey (>= {}): {}", target, ceilingKey);
        log.info("higherKey (> {}): {}", target, higherKey);
        
        // 测试不存在的键
        Integer nonExistentKey = 150;
        log.info("\n查找不存在的键 {}:", nonExistentKey);
        log.info("floorKey: {}", map.floorKey(nonExistentKey));
        log.info("ceilingKey: {}", map.ceilingKey(nonExistentKey));
    }
    
    /**
     * 范围查询性能基准测试
     */
    private void performRangeQueryBenchmark(ConcurrentSkipListMap<Integer, String> map) {
        List<RangeQueryResult> results = new ArrayList<>();
        
        // 测试不同范围大小的查询性能
        int[] rangeSizes = {10, 50, 100, 500};
        
        for (int rangeSize : rangeSizes) {
            int startKey = RandomUtil.randomInt(1, 100 - rangeSize);
            int endKey = startKey + rangeSize;
            
            long startTime = System.nanoTime();
            NavigableMap<Integer, String> subMap = map.subMap(startKey, true, endKey, true);
            List<String> sampleResults = subMap.values().stream()
                .limit(3)
                .collect(Collectors.toList());
            long endTime = System.nanoTime();
            
            RangeQueryResult result = new RangeQueryResult(
                "范围[" + startKey + ", " + endKey + "]",
                subMap.size(),
                endTime - startTime,
                sampleResults
            );
            
            results.add(result);
            log.info("范围查询 {}: {} 个元素, 耗时: {} ns", 
                result.getQueryType(), result.getResultCount(), result.getExecutionTimeNs());
        }
    }
    
    /**
     * 演示并发特性
     */
    public void demonstrateConcurrentFeatures() {
        log.info("\n=== 并发特性演示 ===");
        
        ConcurrentSkipListMap<Integer, String> concurrentMap = new ConcurrentSkipListMap<>();
        
        // 1. 并发插入测试
        log.info("\n1. 并发插入测试");
        int threadCount = 5;
        int operationsPerThread = 200;
        
        CountDownLatch insertLatch = new CountDownLatch(threadCount);
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            new Thread(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        int key = threadId * operationsPerThread + j;
                        concurrentMap.put(key, "thread_" + threadId + "_value_" + j);
                    }
                } finally {
                    insertLatch.countDown();
                }
            }).start();
        }
        
        try {
            insertLatch.await();
            long insertTime = System.currentTimeMillis() - startTime;
            
            log.info("并发插入完成: {} 个元素, 耗时: {}ms", 
                concurrentMap.size(), insertTime);
            
            // 验证有序性
            List<Integer> keys = new ArrayList<>(concurrentMap.keySet());
            boolean isOrdered = IntStream.range(1, keys.size())
                .allMatch(i -> keys.get(i - 1) < keys.get(i));
            log.info("并发插入后有序性检查: {}", isOrdered ? "通过" : "失败");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("并发插入测试被中断", e);
        }
        
        // 2. 并发读写测试
        log.info("\n2. 并发读写测试");
        demonstrateConcurrentReadWrite(concurrentMap);
        
        // 3. 并发范围查询测试
        log.info("\n3. 并发范围查询测试");
        demonstrateConcurrentRangeQueries(concurrentMap);
    }
    
    /**
     * 演示并发读写
     */
    private void demonstrateConcurrentReadWrite(ConcurrentSkipListMap<Integer, String> map) {
        int readerCount = 3;
        int writerCount = 2;
        int operationsPerThread = 100;
        
        CountDownLatch latch = new CountDownLatch(readerCount + writerCount);
        AtomicInteger readCount = new AtomicInteger(0);
        AtomicInteger writeCount = new AtomicInteger(0);
        
        // 启动读线程
        for (int i = 0; i < readerCount; i++) {
            final int readerId = i;
            new Thread(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        int key = RandomUtil.randomInt(0, map.size());
                        String value = map.get(key);
                        if (value != null) {
                            readCount.incrementAndGet();
                        }
                        
                        // 偶尔进行范围查询
                        if (j % 10 == 0) {
                            int startKey = RandomUtil.randomInt(0, map.size() / 2);
                            int endKey = startKey + 50;
                            NavigableMap<Integer, String> subMap = 
                                map.subMap(startKey, true, endKey, true);
                            readCount.addAndGet(subMap.size());
                        }
                    }
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        
        // 启动写线程
        for (int i = 0; i < writerCount; i++) {
            final int writerId = i;
            new Thread(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        int key = map.size() + writerId * operationsPerThread + j;
                        map.put(key, "concurrent_write_" + writerId + "_" + j);
                        writeCount.incrementAndGet();
                        
                        // 偶尔删除一些元素
                        if (j % 20 == 0 && map.size() > 100) {
                            Integer keyToRemove = map.firstKey();
                            if (keyToRemove != null) {
                                map.remove(keyToRemove);
                            }
                        }
                    }
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        
        try {
            latch.await();
            log.info("并发读写完成: 读操作 {} 次, 写操作 {} 次, 最终大小: {}", 
                readCount.get(), writeCount.get(), map.size());
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("并发读写测试被中断", e);
        }
    }
    
    /**
     * 演示并发范围查询
     */
    private void demonstrateConcurrentRangeQueries(ConcurrentSkipListMap<Integer, String> map) {
        int queryThreadCount = 4;
        int queriesPerThread = 50;
        
        CountDownLatch queryLatch = new CountDownLatch(queryThreadCount);
        LongAdder totalQueryTime = new LongAdder();
        AtomicInteger totalResults = new AtomicInteger(0);
        
        for (int i = 0; i < queryThreadCount; i++) {
            final int threadId = i;
            new Thread(() -> {
                try {
                    for (int j = 0; j < queriesPerThread; j++) {
                        int mapSize = map.size();
                        if (mapSize > 100) {
                            int startKey = RandomUtil.randomInt(0, mapSize / 2);
                            int endKey = startKey + RandomUtil.randomInt(10, 100);
                            
                            long queryStart = System.nanoTime();
                            NavigableMap<Integer, String> subMap = 
                                map.subMap(startKey, true, endKey, true);
                            int resultCount = subMap.size();
                            long queryEnd = System.nanoTime();
                            
                            totalQueryTime.add(queryEnd - queryStart);
                            totalResults.addAndGet(resultCount);
                        }
                    }
                } finally {
                    queryLatch.countDown();
                }
            }).start();
        }
        
        try {
            queryLatch.await();
            long avgQueryTime = totalQueryTime.sum() / (queryThreadCount * queriesPerThread);
            log.info("并发范围查询完成: 平均查询时间 {} ns, 总结果数 {}", 
                avgQueryTime, totalResults.get());
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("并发范围查询测试被中断", e);
        }
    }
    
    /**
     * 演示与其他Map实现的对比
     */
    public void demonstrateComparison() {
        log.info("\n=== 与其他Map实现对比 ===");
        
        int dataSize = 10000;
        
        // 1. 插入性能对比
        log.info("\n1. 插入性能对比（{} 个元素）", dataSize);
        
        Map<String, Long> insertTimes = new HashMap<>();
        
        // ConcurrentSkipListMap
        long startTime = System.currentTimeMillis();
        ConcurrentSkipListMap<Integer, String> skipListMap = new ConcurrentSkipListMap<>();
        for (int i = 0; i < dataSize; i++) {
            skipListMap.put(RandomUtil.randomInt(0, dataSize * 2), "value_" + i);
        }
        insertTimes.put("ConcurrentSkipListMap", System.currentTimeMillis() - startTime);
        
        // TreeMap (synchronized)
        startTime = System.currentTimeMillis();
        Map<Integer, String> treeMap = Collections.synchronizedMap(new TreeMap<>());
        for (int i = 0; i < dataSize; i++) {
            treeMap.put(RandomUtil.randomInt(0, dataSize * 2), "value_" + i);
        }
        insertTimes.put("Synchronized TreeMap", System.currentTimeMillis() - startTime);
        
        // ConcurrentHashMap
        startTime = System.currentTimeMillis();
        ConcurrentHashMap<Integer, String> hashMap = new ConcurrentHashMap<>();
        for (int i = 0; i < dataSize; i++) {
            hashMap.put(RandomUtil.randomInt(0, dataSize * 2), "value_" + i);
        }
        insertTimes.put("ConcurrentHashMap", System.currentTimeMillis() - startTime);
        
        insertTimes.forEach((mapType, time) -> {
            log.info("{}: {} ms", mapType, time);
        });
        
        // 2. 查询性能对比
        log.info("\n2. 随机查询性能对比");
        compareRandomQueryPerformance(skipListMap, treeMap, hashMap);
        
        // 3. 范围查询对比（只有有序Map支持）
        log.info("\n3. 范围查询性能对比");
        compareRangeQueryPerformance(skipListMap, treeMap);
        
        // 4. 内存使用对比
        log.info("\n4. 内存使用分析");
        analyzeMemoryUsage(skipListMap, treeMap, hashMap);
    }
    
    /**
     * 比较随机查询性能
     */
    private void compareRandomQueryPerformance(ConcurrentSkipListMap<Integer, String> skipListMap,
                                             Map<Integer, String> treeMap,
                                             ConcurrentHashMap<Integer, String> hashMap) {
        int queryCount = 1000;
        List<Integer> queryKeys = skipListMap.keySet().stream()
            .limit(queryCount)
            .collect(Collectors.toList());
        
        // ConcurrentSkipListMap 查询
        long startTime = System.nanoTime();
        for (Integer key : queryKeys) {
            skipListMap.get(key);
        }
        long skipListTime = System.nanoTime() - startTime;
        
        // TreeMap 查询
        startTime = System.nanoTime();
        synchronized (treeMap) {
            for (Integer key : queryKeys) {
                treeMap.get(key);
            }
        }
        long treeMapTime = System.nanoTime() - startTime;
        
        // ConcurrentHashMap 查询
        startTime = System.nanoTime();
        for (Integer key : queryKeys) {
            hashMap.get(key);
        }
        long hashMapTime = System.nanoTime() - startTime;
        
        log.info("ConcurrentSkipListMap: {} ns", skipListTime);
        log.info("Synchronized TreeMap: {} ns", treeMapTime);
        log.info("ConcurrentHashMap: {} ns", hashMapTime);
    }
    
    /**
     * 比较范围查询性能
     */
    private void compareRangeQueryPerformance(ConcurrentSkipListMap<Integer, String> skipListMap,
                                            Map<Integer, String> treeMap) {
        if (skipListMap.isEmpty()) return;
        
        Integer firstKey = skipListMap.firstKey();
        Integer lastKey = skipListMap.lastKey();
        Integer midKey = firstKey + (lastKey - firstKey) / 2;
        
        // ConcurrentSkipListMap 范围查询
        long startTime = System.nanoTime();
        NavigableMap<Integer, String> skipListSubMap = 
            skipListMap.subMap(firstKey, true, midKey, true);
        int skipListResults = skipListSubMap.size();
        long skipListTime = System.nanoTime() - startTime;
        
        // TreeMap 范围查询
        startTime = System.nanoTime();
        NavigableMap<Integer, String> treeSubMap;
        synchronized (treeMap) {
            if (treeMap instanceof NavigableMap) {
                treeSubMap = ((NavigableMap<Integer, String>) treeMap)
                    .subMap(firstKey, true, midKey, true);
            } else {
                // 如果不是NavigableMap，手动过滤
                treeSubMap = new TreeMap<>();
                for (Map.Entry<Integer, String> entry : treeMap.entrySet()) {
                    if (entry.getKey() >= firstKey && entry.getKey() <= midKey) {
                        treeSubMap.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
        int treeMapResults = treeSubMap.size();
        long treeMapTime = System.nanoTime() - startTime;
        
        log.info("范围查询 [{}, {}]:", firstKey, midKey);
        log.info("ConcurrentSkipListMap: {} 个结果, {} ns", skipListResults, skipListTime);
        log.info("TreeMap: {} 个结果, {} ns", treeMapResults, treeMapTime);
    }
    
    /**
     * 分析内存使用
     */
    private void analyzeMemoryUsage(ConcurrentSkipListMap<Integer, String> skipListMap,
                                  Map<Integer, String> treeMap,
                                  ConcurrentHashMap<Integer, String> hashMap) {
        Runtime runtime = Runtime.getRuntime();
        
        log.info("各Map实现的大小:");
        log.info("ConcurrentSkipListMap: {} 个元素", skipListMap.size());
        log.info("TreeMap: {} 个元素", treeMap.size());
        log.info("ConcurrentHashMap: {} 个元素", hashMap.size());
        
        // 简单的内存使用估算（实际内存使用会受到很多因素影响）
        log.info("\n内存使用特点:");
        log.info("ConcurrentSkipListMap: 跳表结构，额外的索引层级，内存开销较大但支持高效范围查询");
        log.info("TreeMap: 红黑树结构，平衡的内存使用，支持范围查询");
        log.info("ConcurrentHashMap: 哈希表结构，内存效率高，但不支持范围查询");
    }
    
    /**
     * 性能基准测试
     */
    public BenchmarkResultDTO benchmarkConcurrentSkipListMap() {
        log.info("\n=== ConcurrentSkipListMap 性能基准测试 ===");
        
        long startTime = System.currentTimeMillis();
        Map<String, Object> metrics = new HashMap<>();
        
        // 1. 基本操作性能
        log.info("\n1. 基本操作性能测试");
        measureBasicOperations(metrics);
        
        // 2. 并发性能测试
        log.info("\n2. 并发性能测试");
        measureConcurrentPerformance(metrics);
        
        // 3. 范围查询性能
        log.info("\n3. 范围查询性能测试");
        measureRangeQueryPerformance(metrics);
        
        // 4. 扩展性测试
        log.info("\n4. 扩展性测试");
        measureScalability(metrics);
        
        long totalTime = System.currentTimeMillis() - startTime;
        metrics.put("总测试时间(ms)", totalTime);
        
        return new BenchmarkResultDTO(
            "ConcurrentSkipListMap 性能基准测试",
            totalTime,
            metrics
        );
    }
    
    /**
     * 测量基本操作性能
     */
    private void measureBasicOperations(Map<String, Object> metrics) {
        ConcurrentSkipListMap<Integer, String> map = new ConcurrentSkipListMap<>();
        int operationCount = 10000;
        
        // 插入性能
        long startTime = System.nanoTime();
        for (int i = 0; i < operationCount; i++) {
            map.put(i, "value_" + i);
        }
        long insertTime = System.nanoTime() - startTime;
        
        // 查询性能
        startTime = System.nanoTime();
        for (int i = 0; i < operationCount; i++) {
            map.get(i);
        }
        long queryTime = System.nanoTime() - startTime;
        
        // 删除性能
        startTime = System.nanoTime();
        for (int i = 0; i < operationCount / 2; i++) {
            map.remove(i);
        }
        long deleteTime = System.nanoTime() - startTime;
        
        metrics.put("插入操作平均时间(ns)", insertTime / operationCount);
        metrics.put("查询操作平均时间(ns)", queryTime / operationCount);
        metrics.put("删除操作平均时间(ns)", deleteTime / (operationCount / 2));
        
        log.info("插入 {} 个元素平均耗时: {} ns", operationCount, insertTime / operationCount);
        log.info("查询 {} 个元素平均耗时: {} ns", operationCount, queryTime / operationCount);
        log.info("删除 {} 个元素平均耗时: {} ns", operationCount / 2, deleteTime / (operationCount / 2));
    }
    
    /**
     * 测量并发性能
     */
    private void measureConcurrentPerformance(Map<String, Object> metrics) {
        int[] threadCounts = {1, 2, 4, 8};
        int operationsPerThread = 5000;
        
        for (int threadCount : threadCounts) {
            ConcurrentSkipListMap<Integer, String> map = new ConcurrentSkipListMap<>();
            CountDownLatch latch = new CountDownLatch(threadCount);
            
            long startTime = System.currentTimeMillis();
            
            for (int i = 0; i < threadCount; i++) {
                final int threadId = i;
                new Thread(() -> {
                    try {
                        for (int j = 0; j < operationsPerThread; j++) {
                            int key = threadId * operationsPerThread + j;
                            map.put(key, "value_" + key);
                            map.get(key);
                        }
                    } finally {
                        latch.countDown();
                    }
                }).start();
            }
            
            try {
                latch.await();
                long endTime = System.currentTimeMillis();
                long totalTime = endTime - startTime;
                long totalOperations = (long) threadCount * operationsPerThread * 2; // put + get
                long operationsPerSecond = totalOperations * 1000 / totalTime;
                
                metrics.put(threadCount + "线程操作/秒", operationsPerSecond);
                log.info("{} 线程并发性能: {} 操作/秒", threadCount, operationsPerSecond);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * 测量范围查询性能
     */
    private void measureRangeQueryPerformance(Map<String, Object> metrics) {
        ConcurrentSkipListMap<Integer, String> map = new ConcurrentSkipListMap<>();
        
        // 填充数据
        int dataSize = 50000;
        for (int i = 0; i < dataSize; i++) {
            map.put(i, "value_" + i);
        }
        
        // 测试不同范围大小的查询
        int[] rangeSizes = {100, 1000, 5000, 10000};
        
        for (int rangeSize : rangeSizes) {
            int queryCount = 100;
            long totalTime = 0;
            
            for (int i = 0; i < queryCount; i++) {
                int startKey = RandomUtil.randomInt(0, dataSize - rangeSize);
                int endKey = startKey + rangeSize;
                
                long startTime = System.nanoTime();
                NavigableMap<Integer, String> subMap = map.subMap(startKey, true, endKey, true);
                int resultCount = subMap.size(); // 触发实际查询
                long endTime = System.nanoTime();
                
                totalTime += (endTime - startTime);
            }
            
            long avgTime = totalTime / queryCount;
            metrics.put("范围查询" + rangeSize + "平均时间(ns)", avgTime);
            log.info("范围大小 {} 的查询平均耗时: {} ns", rangeSize, avgTime);
        }
    }
    
    /**
     * 测量扩展性
     */
    private void measureScalability(Map<String, Object> metrics) {
        int[] dataSizes = {1000, 10000, 50000, 100000};
        
        for (int dataSize : dataSizes) {
            ConcurrentSkipListMap<Integer, String> map = new ConcurrentSkipListMap<>();
            
            // 测量插入时间
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < dataSize; i++) {
                map.put(i, "value_" + i);
            }
            long insertTime = System.currentTimeMillis() - startTime;
            
            // 测量查询时间
            startTime = System.nanoTime();
            for (int i = 0; i < Math.min(1000, dataSize); i++) {
                map.get(RandomUtil.randomInt(0, dataSize));
            }
            long queryTime = System.nanoTime() - startTime;
            
            metrics.put(dataSize + "元素插入时间(ms)", insertTime);
            metrics.put(dataSize + "元素查询时间(ns)", queryTime);
            
            log.info("数据规模 {}: 插入耗时 {} ms, 查询耗时 {} ns", 
                dataSize, insertTime, queryTime);
        }
    }
    
    /**
     * 运行所有演示
     */
    public void runAllDemonstrations() {
        log.info("开始 ConcurrentSkipListMap 综合演示");
        
        demonstrateBasicOrderedFeatures();
        demonstrateRangeQueries();
        demonstrateConcurrentFeatures();
        demonstrateComparison();
        
        BenchmarkResultDTO benchmark = benchmarkConcurrentSkipListMap();
        log.info("\n性能基准测试完成: {}", benchmark.getTestName());
        log.info("总耗时: {}ms", benchmark.getExecutionTimeMs());
        
        log.info("\nConcurrentSkipListMap 演示完成");
    }
}