package com.example.practice;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * LRUCache 基础行为测试
 */
class LRUCacheTest {

    @Test
    void testEvictionOrder() {
        LRUCache<Integer, String> cache = new LRUCache<>(3);
        cache.put(1, "A");
        cache.put(2, "B");
        cache.put(3, "C");
        // 访问 key=1，使其成为最近使用
        cache.get(1);
        // 插入新元素触发淘汰：应淘汰最久未使用的 key=2
        cache.put(4, "D");

        assertTrue(cache.containsKey(1));
        assertFalse(cache.containsKey(2));
        assertTrue(cache.containsKey(3));
        assertTrue(cache.containsKey(4));
    }

    @Test
    void testCapacityMustPositive() {
        assertThrows(IllegalArgumentException.class, () -> new LRUCache<>(0));
    }
}