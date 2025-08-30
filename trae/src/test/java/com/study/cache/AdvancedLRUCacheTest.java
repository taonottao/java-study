package com.study.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 高级版LRU缓存测试类
 * 测试功能：
 * 1. 基础LRU功能
 * 2. 命中率统计
 * 3. TTL过期策略
 * 4. 并发安全性
 * 5. 缓存统计信息
 */
@DisplayName("高级版LRU缓存测试")
class AdvancedLRUCacheTest {
    
    private AdvancedLRUCache<String, String> cache;
    private AdvancedLRUCache<String, String> threadSafeCache;
    
    @BeforeEach
    void setUp() {
        cache = new AdvancedLRUCache<>(3, false);
        threadSafeCache = new AdvancedLRUCache<>(3, true);
    }
    
    @Test
    @DisplayName("测试基础LRU淘汰功能")
    void testBasicLRUEviction() {
        // 添加3个元素，达到容量上限
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.put("key3", "value3");
        assertEquals(3, cache.size());
        
        // 添加第4个元素，应该淘汰最久未使用的key1
        cache.put("key4", "value4");
        assertEquals(3, cache.size());
        assertNull(cache.get("key1")); // key1被淘汰
        assertNotNull(cache.get("key2"));
        assertNotNull(cache.get("key3"));
        assertNotNull(cache.get("key4"));
        
        // 验证统计信息
        AdvancedLRUCache.CacheStats stats = cache.getStats();
        assertEquals(1, stats.getEvictionCount()); // 1次淘汰
        assertEquals(4, stats.getPutCount()); // 4次写入
    }
    
    @Test
    @DisplayName("测试访问顺序影响LRU")
    void testAccessOrderAffectsLRU() {
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.put("key3", "value3");
        
        // 访问key1，使其变为最近使用
        cache.get("key1");
        
        // 添加新元素，应该淘汰key2（最久未使用）
        cache.put("key4", "value4");
        
        assertNotNull(cache.get("key1")); // key1被访问过，不会被淘汰
        assertNull(cache.get("key2")); // key2被淘汰
        assertNotNull(cache.get("key3"));
        assertNotNull(cache.get("key4"));
    }
    
    @Test
    @DisplayName("测试命中率统计")
    void testHitRateStatistics() {
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        
        // 执行一些get操作
        cache.get("key1"); // 命中
        cache.get("key2"); // 命中
        cache.get("key3"); // 未命中
        cache.get("key1"); // 命中
        cache.get("key4"); // 未命中
        
        AdvancedLRUCache.CacheStats stats = cache.getStats();
        assertEquals(3, stats.getHitCount()); // 3次命中
        assertEquals(2, stats.getMissCount()); // 2次未命中
        assertEquals(5, stats.getTotalRequests()); // 总共5次请求
        assertEquals(0.6, stats.getHitRate(), 0.01); // 命中率60%
    }
    
    @Test
    @DisplayName("测试TTL过期功能")
    void testTTLExpiration() throws InterruptedException {
        // 添加一个100ms后过期的缓存项
        cache.put("key1", "value1", 100);
        cache.put("key2", "value2"); // 永不过期
        
        // 立即访问，应该能获取到
        assertNotNull(cache.get("key1"));
        assertNotNull(cache.get("key2"));
        
        // 等待过期
        Thread.sleep(150);
        
        // key1应该过期，key2仍然存在
        assertNull(cache.get("key1")); // 过期返回null
        assertNotNull(cache.get("key2")); // 永不过期
        
        // 验证过期统计
        AdvancedLRUCache.CacheStats stats = cache.getStats();
        assertEquals(1, stats.getExpiredCount()); // 1个过期项被清理
    }
    
    @Test
    @DisplayName("测试手动清理过期缓存")
    void testManualExpiredCleanup() throws InterruptedException {
        // 添加多个短TTL的缓存项
        cache.put("key1", "value1", 50);
        cache.put("key2", "value2", 50);
        cache.put("key3", "value3"); // 永不过期
        
        assertEquals(3, cache.size());
        
        // 等待过期
        Thread.sleep(100);
        
        // 手动清理过期项
        cache.cleanupExpired();
        
        assertEquals(1, cache.size()); // 只剩下永不过期的key3
        assertNull(cache.get("key1"));
        assertNull(cache.get("key2"));
        assertNotNull(cache.get("key3"));
    }
    
    @Test
    @DisplayName("测试getOrLoad功能")
    void testGetOrLoad() {
        AtomicInteger loadCount = new AtomicInteger(0);
        
        // 第一次调用，缓存中没有，会触发加载
        String result1 = cache.getOrLoad("key1", key -> {
            loadCount.incrementAndGet();
            return "loaded_" + key;
        }, -1);
        
        assertEquals("loaded_key1", result1);
        assertEquals(1, loadCount.get()); // 加载了1次
        
        // 第二次调用，缓存中有，不会触发加载
        String result2 = cache.getOrLoad("key1", key -> {
            loadCount.incrementAndGet();
            return "loaded_again_" + key;
        }, -1);
        
        assertEquals("loaded_key1", result2); // 返回缓存的值
        assertEquals(1, loadCount.get()); // 仍然只加载了1次
    }
    
    @Test
    @DisplayName("测试并发安全性")
    void testConcurrentSafety() throws InterruptedException {
        int threadCount = 10;
        int operationsPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        
        // 启动多个线程并发操作缓存
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        String key = "key_" + (j % 10); // 使用有限的key集合
                        String value = "value_" + threadId + "_" + j;
                        
                        // 随机执行put或get操作
                        if (j % 2 == 0) {
                            threadSafeCache.put(key, value);
                        } else {
                            threadSafeCache.get(key);
                        }
                        successCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // 等待所有线程完成
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        executor.shutdown();
        
        // 验证所有操作都成功完成
        assertEquals(threadCount * operationsPerThread, successCount.get());
        
        // 验证缓存状态正常
        assertTrue(threadSafeCache.size() <= 3); // 不超过容量
        assertNotNull(threadSafeCache.getStats()); // 统计信息正常
    }
    
    @Test
    @DisplayName("测试缓存更新功能")
    void testCacheUpdate() {
        cache.put("key1", "value1");
        assertEquals("value1", cache.get("key1"));
        
        // 更新同一个key的值
        cache.put("key1", "updated_value1");
        assertEquals("updated_value1", cache.get("key1"));
        assertEquals(1, cache.size()); // 大小不变
        
        AdvancedLRUCache.CacheStats stats = cache.getStats();
        assertEquals(2, stats.getPutCount()); // 2次put操作
    }
    
    @Test
    @DisplayName("测试remove功能")
    void testRemove() {
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        assertEquals(2, cache.size());
        
        // 移除存在的key
        String removedValue = cache.remove("key1");
        assertEquals("value1", removedValue);
        assertEquals(1, cache.size());
        assertNull(cache.get("key1"));
        
        // 移除不存在的key
        String notExist = cache.remove("key3");
        assertNull(notExist);
        assertEquals(1, cache.size());
    }
    
    @Test
    @DisplayName("测试clear功能")
    void testClear() {
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.put("key3", "value3");
        assertEquals(3, cache.size());
        
        cache.clear();
        assertEquals(0, cache.size());
        assertTrue(cache.isEmpty());
        assertNull(cache.get("key1"));
        assertNull(cache.get("key2"));
        assertNull(cache.get("key3"));
    }
    
    @Test
    @DisplayName("测试缓存摘要信息")
    void testCacheSummary() {
        cache.put("key1", "value1");
        cache.get("key1"); // 命中
        cache.get("key2"); // 未命中
        
        String summary = cache.getSummary();
        assertNotNull(summary);
        assertTrue(summary.contains("容量=3"));
        assertTrue(summary.contains("当前大小=1"));
        assertTrue(summary.contains("命中率="));
        assertTrue(summary.contains("总请求=2"));
    }
    
    @Test
    @DisplayName("测试非法容量参数")
    void testInvalidCapacity() {
        assertThrows(IllegalArgumentException.class, () -> {
            new AdvancedLRUCache<String, String>(0, false);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            new AdvancedLRUCache<String, String>(-1, false);
        });
    }
}