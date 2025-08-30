package com.trae.study.collections.concurrent;

import com.trae.study.dto.BenchmarkResultDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.NavigableMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ConcurrentSkipListMapDemo 单元测试
 * 验证 ConcurrentSkipListMap 的有序性、并发特性、范围查询等功能
 * 
 * @author trae
 * @since 2024
 */
@DisplayName("ConcurrentSkipListMap 演示测试")
class ConcurrentSkipListMapDemoTest {
    
    private ConcurrentSkipListMapDemo demo;
    
    @BeforeEach
    void setUp() {
        demo = new ConcurrentSkipListMapDemo();
    }
    
    @Test
    @DisplayName("测试基本有序特性")
    void testBasicOrderedFeatures() {
        assertDoesNotThrow(() -> {
            demo.demonstrateBasicOrderedFeatures();
        });
    }
    
    @Test
    @DisplayName("测试ConcurrentSkipListMap的有序性")
    void testOrderedInsertion() {
        ConcurrentSkipListMap<Integer, String> map = new ConcurrentSkipListMap<>();
        
        // 随机插入数据
        int[] randomNumbers = {15, 3, 9, 1, 12, 7, 20, 5, 18, 11};
        for (int num : randomNumbers) {
            map.put(num, "value_" + num);
        }
        
        // 验证有序性
        List<Integer> keys = new ArrayList<>(map.keySet());
        assertEquals(randomNumbers.length, keys.size());
        
        // 检查是否按升序排列
        for (int i = 1; i < keys.size(); i++) {
            assertTrue(keys.get(i - 1) < keys.get(i), 
                "键应该按升序排列: " + keys.get(i - 1) + " < " + keys.get(i));
        }
    }
    
    @Test
    @DisplayName("测试自定义比较器")
    void testCustomComparator() {
        // 降序比较器
        ConcurrentSkipListMap<Integer, String> descendingMap = 
            new ConcurrentSkipListMap<>(Collections.reverseOrder());
        
        int[] numbers = {5, 2, 8, 1, 9, 3};
        for (int num : numbers) {
            descendingMap.put(num, "value_" + num);
        }
        
        List<Integer> keys = new ArrayList<>(descendingMap.keySet());
        
        // 验证降序排列
        for (int i = 1; i < keys.size(); i++) {
            assertTrue(keys.get(i - 1) > keys.get(i), 
                "键应该按降序排列: " + keys.get(i - 1) + " > " + keys.get(i));
        }
    }
    
    @Test
    @DisplayName("测试Student类的比较")
    void testStudentComparison() {
        ConcurrentSkipListMapDemo.Student student1 = 
            new ConcurrentSkipListMapDemo.Student(1, "Alice", 95.5, System.currentTimeMillis());
        ConcurrentSkipListMapDemo.Student student2 = 
            new ConcurrentSkipListMapDemo.Student(2, "Bob", 87.0, System.currentTimeMillis());
        ConcurrentSkipListMapDemo.Student student3 = 
            new ConcurrentSkipListMapDemo.Student(3, "Charlie", 95.5, System.currentTimeMillis());
        
        // 测试比较逻辑：分数降序，ID升序
        assertTrue(student1.compareTo(student2) < 0, "Alice分数更高，应该排在前面");
        assertTrue(student1.compareTo(student3) < 0, "分数相同时，ID小的排在前面");
        assertTrue(student2.compareTo(student1) > 0, "Bob分数更低，应该排在后面");
        
        // 测试在Map中的排序
        ConcurrentSkipListMap<ConcurrentSkipListMapDemo.Student, String> studentMap = 
            new ConcurrentSkipListMap<>();
        
        studentMap.put(student2, "grade_B");
        studentMap.put(student1, "grade_A");
        studentMap.put(student3, "grade_A");
        
        List<ConcurrentSkipListMapDemo.Student> sortedStudents = 
            new ArrayList<>(studentMap.keySet());
        
        assertEquals(student1, sortedStudents.get(0), "Alice应该排第一");
        assertEquals(student3, sortedStudents.get(1), "Charlie应该排第二");
        assertEquals(student2, sortedStudents.get(2), "Bob应该排第三");
    }
    
    @Test
    @DisplayName("测试范围查询功能")
    void testRangeQueries() {
        assertDoesNotThrow(() -> {
            demo.demonstrateRangeQueries();
        });
    }
    
    @Test
    @DisplayName("测试subMap范围查询")
    void testSubMapQuery() {
        ConcurrentSkipListMap<Integer, String> map = new ConcurrentSkipListMap<>();
        
        // 填充测试数据
        for (int i = 1; i <= 100; i++) {
            map.put(i, "value_" + i);
        }
        
        // 测试范围查询
        NavigableMap<Integer, String> subMap = map.subMap(20, true, 30, true);
        assertEquals(11, subMap.size(), "范围[20,30]应该包含11个元素");
        
        // 验证范围内的所有键
        for (Integer key : subMap.keySet()) {
            assertTrue(key >= 20 && key <= 30, "键应该在范围[20,30]内: " + key);
        }
        
        // 测试不包含边界的范围查询
        NavigableMap<Integer, String> exclusiveSubMap = map.subMap(20, false, 30, false);
        assertEquals(9, exclusiveSubMap.size(), "范围(20,30)应该包含9个元素");
        
        for (Integer key : exclusiveSubMap.keySet()) {
            assertTrue(key > 20 && key < 30, "键应该在范围(20,30)内: " + key);
        }
    }
    
    @Test
    @DisplayName("测试边界查询方法")
    void testBoundaryQueries() {
        ConcurrentSkipListMap<Integer, String> map = new ConcurrentSkipListMap<>();
        
        // 填充测试数据
        int[] values = {10, 20, 30, 40, 50};
        for (int value : values) {
            map.put(value, "value_" + value);
        }
        
        // 测试边界查询
        assertEquals(Integer.valueOf(10), map.firstKey(), "最小键应该是10");
        assertEquals(Integer.valueOf(50), map.lastKey(), "最大键应该是50");
        
        // 测试查找最接近的元素
        Integer target = 25;
        assertEquals(Integer.valueOf(20), map.lowerKey(target), "lowerKey(25)应该是20");
        assertEquals(Integer.valueOf(20), map.floorKey(target), "floorKey(25)应该是20");
        assertEquals(Integer.valueOf(30), map.ceilingKey(target), "ceilingKey(25)应该是30");
        assertEquals(Integer.valueOf(30), map.higherKey(target), "higherKey(25)应该是30");
        
        // 测试存在的键
        target = 30;
        assertEquals(Integer.valueOf(20), map.lowerKey(target), "lowerKey(30)应该是20");
        assertEquals(Integer.valueOf(30), map.floorKey(target), "floorKey(30)应该是30");
        assertEquals(Integer.valueOf(30), map.ceilingKey(target), "ceilingKey(30)应该是30");
        assertEquals(Integer.valueOf(40), map.higherKey(target), "higherKey(30)应该是40");
    }
    
    @Test
    @DisplayName("测试headMap和tailMap")
    void testHeadMapAndTailMap() {
        ConcurrentSkipListMap<Integer, String> map = new ConcurrentSkipListMap<>();
        
        for (int i = 1; i <= 50; i++) {
            map.put(i, "value_" + i);
        }
        
        // 测试headMap
        NavigableMap<Integer, String> headMap = map.headMap(25, false);
        assertEquals(24, headMap.size(), "headMap(25, false)应该包含24个元素");
        
        for (Integer key : headMap.keySet()) {
            assertTrue(key < 25, "headMap中的键应该小于25: " + key);
        }
        
        // 测试tailMap
        NavigableMap<Integer, String> tailMap = map.tailMap(25, true);
        assertEquals(26, tailMap.size(), "tailMap(25, true)应该包含26个元素");
        
        for (Integer key : tailMap.keySet()) {
            assertTrue(key >= 25, "tailMap中的键应该大于等于25: " + key);
        }
    }
    
    @Test
    @DisplayName("测试并发特性")
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void testConcurrentFeatures() {
        assertDoesNotThrow(() -> {
            demo.demonstrateConcurrentFeatures();
        });
    }
    
    @Test
    @DisplayName("测试并发插入的线程安全性")
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testConcurrentInsertionSafety() throws InterruptedException {
        ConcurrentSkipListMap<Integer, String> map = new ConcurrentSkipListMap<>();
        
        int threadCount = 4;
        int operationsPerThread = 1000;
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        // 启动多个线程并发插入
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            new Thread(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        int key = threadId * operationsPerThread + j;
                        map.put(key, "thread_" + threadId + "_value_" + j);
                    }
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        
        // 等待所有线程完成
        assertTrue(latch.await(10, TimeUnit.SECONDS), "所有线程应该在10秒内完成");
        
        // 验证结果
        assertEquals(threadCount * operationsPerThread, map.size(), 
            "应该插入" + (threadCount * operationsPerThread) + "个元素");
        
        // 验证有序性
        List<Integer> keys = new ArrayList<>(map.keySet());
        for (int i = 1; i < keys.size(); i++) {
            assertTrue(keys.get(i - 1) < keys.get(i), 
                "并发插入后仍应保持有序性");
        }
    }
    
    @Test
    @DisplayName("测试并发读写操作")
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    void testConcurrentReadWrite() throws InterruptedException {
        ConcurrentSkipListMap<Integer, String> map = new ConcurrentSkipListMap<>();
        
        // 预填充一些数据
        for (int i = 0; i < 1000; i++) {
            map.put(i, "initial_value_" + i);
        }
        
        int readerCount = 3;
        int writerCount = 2;
        int operationsPerThread = 500;
        CountDownLatch latch = new CountDownLatch(readerCount + writerCount);
        
        // 启动读线程
        for (int i = 0; i < readerCount; i++) {
            new Thread(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        int key = j % 1000;
                        String value = map.get(key);
                        assertNotNull(value, "读取的值不应该为null");
                        
                        // 偶尔进行范围查询
                        if (j % 50 == 0) {
                            NavigableMap<Integer, String> subMap = 
                                map.subMap(key, true, key + 50, true);
                            assertTrue(subMap.size() >= 0, "范围查询结果应该有效");
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
                        int key = 1000 + writerId * operationsPerThread + j;
                        map.put(key, "writer_" + writerId + "_value_" + j);
                    }
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        
        // 等待所有线程完成
        assertTrue(latch.await(15, TimeUnit.SECONDS), "所有线程应该在15秒内完成");
        
        // 验证最终状态
        assertTrue(map.size() >= 1000, "Map大小应该至少包含初始数据");
        
        // 验证有序性
        List<Integer> keys = new ArrayList<>(map.keySet());
        for (int i = 1; i < keys.size(); i++) {
            assertTrue(keys.get(i - 1) < keys.get(i), 
                "并发读写后仍应保持有序性");
        }
    }
    
    @Test
    @DisplayName("测试RangeQueryResult类")
    void testRangeQueryResult() {
        List<String> sampleResults = List.of("result1", "result2", "result3");
        ConcurrentSkipListMapDemo.RangeQueryResult result = 
            new ConcurrentSkipListMapDemo.RangeQueryResult(
                "测试查询", 10, 1000000L, sampleResults);
        
        assertEquals("测试查询", result.getQueryType());
        assertEquals(10, result.getResultCount());
        assertEquals(1000000L, result.getExecutionTimeNs());
        assertEquals(sampleResults, result.getSampleResults());
    }
    
    @Test
    @DisplayName("测试与其他Map实现的对比")
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void testComparison() {
        assertDoesNotThrow(() -> {
            demo.demonstrateComparison();
        });
    }
    
    @Test
    @DisplayName("测试性能基准测试")
    @Timeout(value = 60, unit = TimeUnit.SECONDS)
    void testBenchmark() {
        BenchmarkResultDTO result = demo.benchmarkConcurrentSkipListMap();
        
        assertNotNull(result, "基准测试结果不应该为null");
        assertEquals("ConcurrentSkipListMap 性能基准测试", result.getTestName());
        assertTrue(result.getExecutionTimeMs() > 0, "执行时间应该大于0");
        assertNotNull(result.getAdditionalMetrics(), "性能指标不应该为null");
        assertFalse(result.getAdditionalMetrics().isEmpty(), "性能指标不应该为空");
        
        // 验证关键性能指标存在
        Map<String, Object> metrics = result.getAdditionalMetrics();
        assertTrue(metrics.containsKey("插入操作平均时间(ns)"), "应该包含插入操作时间");
        assertTrue(metrics.containsKey("查询操作平均时间(ns)"), "应该包含查询操作时间");
        assertTrue(metrics.containsKey("删除操作平均时间(ns)"), "应该包含删除操作时间");
    }
    
    @Test
    @DisplayName("测试综合演示")
    @Timeout(value = 120, unit = TimeUnit.SECONDS)
    void testRunAllDemonstrations() {
        assertDoesNotThrow(() -> {
            demo.runAllDemonstrations();
        });
    }
    
    @Test
    @DisplayName("测试空Map的边界条件")
    void testEmptyMapBoundaryConditions() {
        ConcurrentSkipListMap<Integer, String> emptyMap = new ConcurrentSkipListMap<>();
        
        assertTrue(emptyMap.isEmpty(), "新创建的Map应该为空");
        assertEquals(0, emptyMap.size(), "空Map的大小应该为0");
        
        // 测试空Map的边界查询
        assertThrows(Exception.class, () -> emptyMap.firstKey(), 
            "空Map调用firstKey应该抛出异常");
        assertThrows(Exception.class, () -> emptyMap.lastKey(), 
            "空Map调用lastKey应该抛出异常");
        
        // 测试空Map的范围查询
        NavigableMap<Integer, String> subMap = emptyMap.subMap(1, true, 10, true);
        assertTrue(subMap.isEmpty(), "空Map的范围查询结果应该为空");
    }
    
    @Test
    @DisplayName("测试单元素Map的操作")
    void testSingleElementMap() {
        ConcurrentSkipListMap<Integer, String> singleMap = new ConcurrentSkipListMap<>();
        singleMap.put(42, "single_value");
        
        assertEquals(1, singleMap.size(), "单元素Map大小应该为1");
        assertEquals(Integer.valueOf(42), singleMap.firstKey(), "firstKey应该是42");
        assertEquals(Integer.valueOf(42), singleMap.lastKey(), "lastKey应该是42");
        
        // 测试边界查询
        assertNull(singleMap.lowerKey(42), "lowerKey(42)应该为null");
        assertEquals(Integer.valueOf(42), singleMap.floorKey(42), "floorKey(42)应该是42");
        assertEquals(Integer.valueOf(42), singleMap.ceilingKey(42), "ceilingKey(42)应该是42");
        assertNull(singleMap.higherKey(42), "higherKey(42)应该为null");
        
        // 测试范围查询
        NavigableMap<Integer, String> subMap = singleMap.subMap(40, true, 45, true);
        assertEquals(1, subMap.size(), "范围查询应该包含单个元素");
        assertTrue(subMap.containsKey(42), "范围查询结果应该包含键42");
    }
    
    @Test
    @DisplayName("测试大量数据的性能特性")
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void testLargeDataPerformance() {
        ConcurrentSkipListMap<Integer, String> largeMap = new ConcurrentSkipListMap<>();
        
        int dataSize = 50000;
        
        // 测量插入时间
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < dataSize; i++) {
            largeMap.put(i, "large_value_" + i);
        }
        long insertTime = System.currentTimeMillis() - startTime;
        
        assertEquals(dataSize, largeMap.size(), "应该插入所有元素");
        assertTrue(insertTime < 10000, "插入" + dataSize + "个元素应该在10秒内完成");
        
        // 测量查询时间
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            String value = largeMap.get(i * (dataSize / 1000));
            assertNotNull(value, "查询的值不应该为null");
        }
        long queryTime = System.currentTimeMillis() - startTime;
        
        assertTrue(queryTime < 1000, "1000次查询应该在1秒内完成");
        
        // 测量范围查询时间
        startTime = System.currentTimeMillis();
        NavigableMap<Integer, String> subMap = largeMap.subMap(10000, true, 20000, true);
        int subMapSize = subMap.size();
        long rangeQueryTime = System.currentTimeMillis() - startTime;
        
        assertEquals(10001, subMapSize, "范围查询应该返回正确数量的元素");
        assertTrue(rangeQueryTime < 1000, "范围查询应该在1秒内完成");
    }
    
    @Test
    @DisplayName("测试null值处理")
    void testNullValueHandling() {
        ConcurrentSkipListMap<Integer, String> map = new ConcurrentSkipListMap<>();
        
        // ConcurrentSkipListMap不允许null键
        assertThrows(NullPointerException.class, () -> {
            map.put(null, "value");
        }, "插入null键应该抛出NullPointerException");
        
        // 但允许null值
        assertDoesNotThrow(() -> {
            map.put(1, null);
        }, "插入null值应该被允许");
        
        assertNull(map.get(1), "应该能够存储和检索null值");
        assertTrue(map.containsKey(1), "应该包含键1");
        assertTrue(map.containsValue(null), "应该包含null值");
    }
    
    @Test
    @DisplayName("测试迭代器的一致性")
    void testIteratorConsistency() {
        ConcurrentSkipListMap<Integer, String> map = new ConcurrentSkipListMap<>();
        
        // 填充数据
        for (int i = 0; i < 100; i++) {
            map.put(i, "value_" + i);
        }
        
        // 测试键的迭代器
        List<Integer> keys = new ArrayList<>();
        for (Integer key : map.keySet()) {
            keys.add(key);
        }
        
        assertEquals(100, keys.size(), "应该迭代所有键");
        
        // 验证有序性
        for (int i = 1; i < keys.size(); i++) {
            assertTrue(keys.get(i - 1) < keys.get(i), 
                "迭代的键应该保持有序");
        }
        
        // 测试值的迭代器
        List<String> values = new ArrayList<>();
        for (String value : map.values()) {
            values.add(value);
        }
        
        assertEquals(100, values.size(), "应该迭代所有值");
        
        // 测试条目的迭代器
        List<Map.Entry<Integer, String>> entries = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            entries.add(entry);
        }
        
        assertEquals(100, entries.size(), "应该迭代所有条目");
        
        // 验证条目的有序性
        for (int i = 1; i < entries.size(); i++) {
            assertTrue(entries.get(i - 1).getKey() < entries.get(i).getKey(), 
                "迭代的条目应该按键有序");
        }
    }
}