package com.trae.study.collections.concurrent;

import com.trae.study.dto.BenchmarkResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ConcurrentHashMapDemo 单元测试
 * 验证 ConcurrentHashMap 的并发特性、CAS 操作、性能等功能
 * 
 * @author trae
 * @since 2024
 */
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ConcurrentHashMapDemoTest {
    
    private ConcurrentHashMapDemo demo;
    
    @BeforeEach
    void setUp() {
        demo = new ConcurrentHashMapDemo();
        log.info("初始化 ConcurrentHashMapDemo 测试");
    }
    
    @Test
    @Order(1)
    @DisplayName("测试基本并发特性")
    void testBasicConcurrency() {
        log.info("=== 测试基本并发特性 ===");
        
        assertDoesNotThrow(() -> {
            demo.demonstrateBasicConcurrency();
        }, "基本并发特性演示不应抛出异常");
        
        // 验证并发安全性
        ConcurrentHashMap<String, Integer> testMap = new ConcurrentHashMap<>();
        int threadCount = 5;
        int operationsPerThread = 1000;
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            new Thread(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        String key = "test_key_" + (threadId * operationsPerThread + j);
                        testMap.put(key, j);
                        Integer value = testMap.get(key);
                        if (value != null && value.equals(j)) {
                            successCount.incrementAndGet();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        
        assertDoesNotThrow(() -> latch.await(), "并发操作等待不应被中断");
        
        int expectedOperations = threadCount * operationsPerThread;
        assertEquals(expectedOperations, successCount.get(), 
            "所有并发操作都应该成功");
        
        log.info("并发安全性验证通过: {}/{} 操作成功", 
            successCount.get(), expectedOperations);
    }
    
    @Test
    @Order(2)
    @DisplayName("测试内部结构分析")
    void testInternalStructure() {
        log.info("=== 测试内部结构分析 ===");
        
        assertDoesNotThrow(() -> {
            demo.demonstrateInternalStructure();
        }, "内部结构演示不应抛出异常");
        
        // 验证内部结构的基本特性
        ConcurrentHashMap<String, String> testMap = new ConcurrentHashMap<>();
        
        // 测试初始状态
        assertTrue(testMap.isEmpty(), "新创建的 ConcurrentHashMap 应该为空");
        assertEquals(0, testMap.size(), "初始大小应该为 0");
        
        // 测试添加元素后的状态
        for (int i = 0; i < 10; i++) {
            testMap.put("key" + i, "value" + i);
        }
        
        assertEquals(10, testMap.size(), "添加 10 个元素后大小应该为 10");
        assertFalse(testMap.isEmpty(), "添加元素后不应该为空");
        
        // 验证所有元素都能正确检索
        for (int i = 0; i < 10; i++) {
            String key = "key" + i;
            assertTrue(testMap.containsKey(key), "应该包含键: " + key);
            assertEquals("value" + i, testMap.get(key), "值应该匹配");
        }
        
        log.info("内部结构分析验证通过");
    }
    
    @Test
    @Order(3)
    @DisplayName("测试CAS操作和原子性")
    void testCASOperations() {
        log.info("=== 测试CAS操作和原子性 ===");
        
        assertDoesNotThrow(() -> {
            demo.demonstrateCASOperations();
        }, "CAS操作演示不应抛出异常");
        
        // 验证 compute 方法的原子性
        ConcurrentHashMap<String, AtomicInteger> counterMap = new ConcurrentHashMap<>();
        String testKey = "atomic_test";
        int threadCount = 10;
        int incrementsPerThread = 100;
        
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                try {
                    for (int j = 0; j < incrementsPerThread; j++) {
                        counterMap.compute(testKey, (key, value) -> {
                            if (value == null) {
                                return new AtomicInteger(1);
                            } else {
                                value.incrementAndGet();
                                return value;
                            }
                        });
                    }
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        
        assertDoesNotThrow(() -> latch.await(), "CAS操作等待不应被中断");
        
        AtomicInteger finalCounter = counterMap.get(testKey);
        assertNotNull(finalCounter, "计数器不应为null");
        
        int expectedCount = threadCount * incrementsPerThread;
        assertEquals(expectedCount, finalCounter.get(), 
            "原子操作计数应该准确");
        
        log.info("CAS操作原子性验证通过: 期望={}, 实际={}", 
            expectedCount, finalCounter.get());
    }
    
    @Test
    @Order(4)
    @DisplayName("测试putIfAbsent原子性")
    void testPutIfAbsentAtomicity() {
        log.info("=== 测试putIfAbsent原子性 ===");
        
        ConcurrentHashMap<String, String> testMap = new ConcurrentHashMap<>();
        String testKey = "unique_key";
        int threadCount = 10;
        
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicReference<String> winner = new AtomicReference<>();
        AtomicInteger successCount = new AtomicInteger(0);
        
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            new Thread(() -> {
                try {
                    String value = "thread_" + threadId;
                    String existing = testMap.putIfAbsent(testKey, value);
                    
                    if (existing == null) {
                        // 成功插入
                        winner.set(value);
                        successCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        
        assertDoesNotThrow(() -> latch.await(), "putIfAbsent测试等待不应被中断");
        
        // 验证只有一个线程成功插入
        assertEquals(1, successCount.get(), "只应该有一个线程成功插入");
        assertNotNull(winner.get(), "应该有获胜者");
        assertEquals(winner.get(), testMap.get(testKey), "最终值应该与获胜者一致");
        
        log.info("putIfAbsent原子性验证通过: 获胜者={}", winner.get());
    }
    
    @Test
    @Order(5)
    @DisplayName("测试树化演示")
    void testTreeification() {
        log.info("=== 测试树化演示 ===");
        
        assertDoesNotThrow(() -> {
            demo.demonstrateTreeification();
        }, "树化演示不应抛出异常");
        
        // 验证哈希冲突处理
        ConcurrentHashMap<ConcurrentHashMapDemo.CollidingKey, String> collisionMap = 
            new ConcurrentHashMap<>();
        
        // 创建多个具有相同哈希值的键
        int collisionCount = 15;
        for (int i = 0; i < collisionCount; i++) {
            ConcurrentHashMapDemo.CollidingKey key = 
                new ConcurrentHashMapDemo.CollidingKey("collision_" + i, 42);
            collisionMap.put(key, "value_" + i);
        }
        
        assertEquals(collisionCount, collisionMap.size(), 
            "所有冲突键都应该被正确存储");
        
        // 验证所有键都能正确检索
        for (int i = 0; i < collisionCount; i++) {
            ConcurrentHashMapDemo.CollidingKey key = 
                new ConcurrentHashMapDemo.CollidingKey("collision_" + i, 42);
            assertTrue(collisionMap.containsKey(key), 
                "应该能找到冲突键: " + key.getName());
            assertEquals("value_" + i, collisionMap.get(key), 
                "冲突键的值应该正确");
        }
        
        log.info("哈希冲突处理验证通过: {} 个冲突键", collisionCount);
    }
    
    @Test
    @Order(6)
    @DisplayName("测试CollidingKey类")
    void testCollidingKey() {
        log.info("=== 测试CollidingKey类 ===");
        
        ConcurrentHashMapDemo.CollidingKey key1 = 
            new ConcurrentHashMapDemo.CollidingKey("test1", 100);
        ConcurrentHashMapDemo.CollidingKey key2 = 
            new ConcurrentHashMapDemo.CollidingKey("test1", 100);
        ConcurrentHashMapDemo.CollidingKey key3 = 
            new ConcurrentHashMapDemo.CollidingKey("test2", 100);
        
        // 测试哈希值
        assertEquals(100, key1.hashCode(), "哈希值应该是固定的");
        assertEquals(100, key2.hashCode(), "相同参数的键应该有相同哈希值");
        assertEquals(100, key3.hashCode(), "不同名称但相同哈希参数应该有相同哈希值");
        
        // 测试相等性
        assertEquals(key1, key2, "相同名称的键应该相等");
        assertNotEquals(key1, key3, "不同名称的键不应该相等");
        
        // 测试toString
        String str = key1.toString();
        assertTrue(str.contains("test1"), "toString应该包含名称");
        assertTrue(str.contains("100"), "toString应该包含哈希值");
        
        log.info("CollidingKey类验证通过");
    }
    
    @Test
    @Order(7)
    @DisplayName("测试性能基准")
    @Execution(ExecutionMode.SAME_THREAD)
    void testBenchmark() {
        log.info("=== 测试性能基准 ===");
        
        BenchmarkResultDTO result = assertDoesNotThrow(() -> {
            return demo.benchmarkConcurrentHashMap();
        }, "性能基准测试不应抛出异常");
        
        assertNotNull(result, "基准测试结果不应为null");
        assertNotNull(result.getTestName(), "测试名称不应为null");
        assertTrue(result.getExecutionTimeMs() > 0, "执行时间应该大于0");
        assertNotNull(result.getAdditionalMetrics(), "指标不应为null");
        assertFalse(result.getAdditionalMetrics().isEmpty(), "指标不应为空");
        
        // 验证关键指标存在
        assertTrue(result.getAdditionalMetrics().containsKey("单线程操作/秒"), 
            "应该包含单线程性能指标");
        assertTrue(result.getAdditionalMetrics().containsKey("总测试时间(ms)"), 
            "应该包含总测试时间");
        
        log.info("性能基准测试验证通过: {}", result.getTestName());
        log.info("执行时间: {}ms", result.getExecutionTimeMs());
        log.info("指标数量: {}", result.getAdditionalMetrics().size());
    }
    
    @Test
    @Order(8)
    @DisplayName("测试并发修改安全性")
    void testConcurrentModificationSafety() {
        log.info("=== 测试并发修改安全性 ===");
        
        ConcurrentHashMap<String, Integer> testMap = new ConcurrentHashMap<>();
        
        // 预填充一些数据
        for (int i = 0; i < 100; i++) {
            testMap.put("key" + i, i);
        }
        
        int threadCount = 5;
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger exceptionCount = new AtomicInteger(0);
        
        // 启动多个线程同时修改和遍历
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            new Thread(() -> {
                try {
                    for (int j = 0; j < 50; j++) {
                        // 修改操作
                        testMap.put("thread" + threadId + "_key" + j, j);
                        testMap.remove("key" + (j % 50));
                        
                        // 遍历操作（不应该抛出 ConcurrentModificationException）
                        try {
                            for (String key : testMap.keySet()) {
                                testMap.get(key);
                            }
                        } catch (Exception e) {
                            exceptionCount.incrementAndGet();
                            log.warn("遍历时发生异常: {}", e.getMessage());
                        }
                    }
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        
        assertDoesNotThrow(() -> latch.await(), "并发修改测试等待不应被中断");
        
        // ConcurrentHashMap 不应该抛出 ConcurrentModificationException
        assertEquals(0, exceptionCount.get(), 
            "ConcurrentHashMap 在并发修改时不应抛出异常");
        
        log.info("并发修改安全性验证通过: 无异常发生");
    }
    
    @Test
    @Order(9)
    @DisplayName("测试综合演示")
    void testRunAllDemonstrations() {
        log.info("=== 测试综合演示 ===");
        
        assertDoesNotThrow(() -> {
            demo.runAllDemonstrations();
        }, "综合演示不应抛出异常");
        
        log.info("综合演示验证通过");
    }
    
    @Test
    @Order(10)
    @DisplayName("测试边界条件")
    void testBoundaryConditions() {
        log.info("=== 测试边界条件 ===");
        
        ConcurrentHashMap<String, String> testMap = new ConcurrentHashMap<>();
        
        // 测试空map操作
        assertNull(testMap.get("nonexistent"), "空map中获取不存在的键应返回null");
        assertFalse(testMap.containsKey("nonexistent"), "空map不应包含任何键");
        assertEquals(0, testMap.size(), "空map大小应为0");
        
        // 测试null值处理
        assertThrows(NullPointerException.class, () -> {
            testMap.put(null, "value");
        }, "ConcurrentHashMap不应接受null键");
        
        assertThrows(NullPointerException.class, () -> {
            testMap.put("key", null);
        }, "ConcurrentHashMap不应接受null值");
        
        // 测试大量数据
        int largeSize = 10000;
        for (int i = 0; i < largeSize; i++) {
            testMap.put("large_key_" + i, "large_value_" + i);
        }
        
        assertEquals(largeSize, testMap.size(), "大量数据插入后大小应正确");
        
        // 验证随机访问
        for (int i = 0; i < 100; i++) {
            int randomIndex = (int) (Math.random() * largeSize);
            String key = "large_key_" + randomIndex;
            assertTrue(testMap.containsKey(key), "应该包含随机键: " + key);
        }
        
        log.info("边界条件测试验证通过");
    }
    
    @Test
    @Order(11)
    @DisplayName("测试内存和性能特性")
    void testMemoryAndPerformanceCharacteristics() {
        log.info("=== 测试内存和性能特性 ===");
        
        // 测试不同大小的map性能
        int[] sizes = {100, 1000, 5000};
        
        for (int size : sizes) {
            ConcurrentHashMap<String, Integer> testMap = new ConcurrentHashMap<>();
            
            // 测量插入性能
            long startTime = System.nanoTime();
            for (int i = 0; i < size; i++) {
                testMap.put("perf_key_" + i, i);
            }
            long insertTime = System.nanoTime() - startTime;
            
            // 测量查找性能
            startTime = System.nanoTime();
            for (int i = 0; i < size; i++) {
                testMap.get("perf_key_" + i);
            }
            long lookupTime = System.nanoTime() - startTime;
            
            assertEquals(size, testMap.size(), "插入后大小应正确");
            
            log.info("大小 {}: 插入耗时 {} ns, 查找耗时 {} ns", 
                size, insertTime, lookupTime);
            
            // 基本性能断言（这些值可能因环境而异）
            assertTrue(insertTime > 0, "插入时间应大于0");
            assertTrue(lookupTime > 0, "查找时间应大于0");
        }
        
        log.info("内存和性能特性测试验证通过");
    }
    
    @AfterEach
    void tearDown() {
        log.info("ConcurrentHashMapDemo 测试完成");
    }
}