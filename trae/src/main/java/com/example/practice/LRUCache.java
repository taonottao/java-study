package com.example.practice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 基于 LinkedHashMap 的 LRU 缓存实现。
 * - 访问顺序（accessOrder=true）
 * - 超过容量自动淘汰最久未使用的条目
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    /** 最大容量 */
    private final int maxCapacity;

    /**
     * @param maxCapacity 最大容量（>0）
     */
    public LRUCache(int maxCapacity) {
        super(Math.max(16, (int) (maxCapacity / 0.75f) + 1), 0.75f, true);
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("maxCapacity must be > 0");
        }
        this.maxCapacity = maxCapacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxCapacity;
    }
}