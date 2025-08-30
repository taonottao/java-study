package com.study.cache;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 高级版LRU缓存实现
 * 支持功能：
 * 1. 命中率统计
 * 2. TTL过期策略
 * 3. 可选并发支持
 * 4. 缓存统计信息
 * 
 * @param <K> 键类型
 * @param <V> 值类型
 */
@Slf4j
public class AdvancedLRUCache<K, V> {
    
    /**
     * 缓存节点，包含值、过期时间、访问时间等信息
     */
    @Data
    private static class CacheNode<V> {
        private V value;
        private long expireTime; // 过期时间戳，-1表示永不过期
        private long accessTime; // 最后访问时间
        private CacheNode<V> prev;
        private CacheNode<V> next;
        
        public CacheNode(V value, long ttlMs) {
            this.value = value;
            this.accessTime = System.currentTimeMillis();
            this.expireTime = ttlMs > 0 ? this.accessTime + ttlMs : -1;
        }
        
        /**
         * 检查节点是否已过期
         */
        public boolean isExpired() {
            return expireTime > 0 && System.currentTimeMillis() > expireTime;
        }
        
        /**
         * 更新访问时间
         */
        public void updateAccessTime() {
            this.accessTime = System.currentTimeMillis();
        }
    }
    
    /**
     * 缓存统计信息
     */
    @Data
    public static class CacheStats {
        private long hitCount = 0;      // 命中次数
        private long missCount = 0;     // 未命中次数
        private long putCount = 0;      // 写入次数
        private long evictionCount = 0; // 淘汰次数
        private long expiredCount = 0;  // 过期清理次数
        
        /**
         * 计算命中率
         */
        public double getHitRate() {
            long totalRequests = hitCount + missCount;
            return totalRequests == 0 ? 0.0 : (double) hitCount / totalRequests;
        }
        
        /**
         * 获取总请求数
         */
        public long getTotalRequests() {
            return hitCount + missCount;
        }
    }
    
    private final int capacity;
    private final boolean threadSafe;
    private final ConcurrentHashMap<K, CacheNode<V>> cache;
    private final CacheStats stats;
    
    // 双向链表头尾节点
    private final CacheNode<V> head;
    private final CacheNode<V> tail;
    
    // 读写锁，用于并发控制
    private final ReentrantReadWriteLock lock;
    
    /**
     * 构造函数
     * 
     * @param capacity 缓存容量
     * @param threadSafe 是否线程安全
     */
    public AdvancedLRUCache(int capacity, boolean threadSafe) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("缓存容量必须大于0");
        }
        
        this.capacity = capacity;
        this.threadSafe = threadSafe;
        this.cache = new ConcurrentHashMap<>(capacity);
        this.stats = new CacheStats();
        this.lock = threadSafe ? new ReentrantReadWriteLock() : null;
        
        // 初始化双向链表的头尾哨兵节点
        this.head = new CacheNode<>(null, -1);
        this.tail = new CacheNode<>(null, -1);
        this.head.next = this.tail;
        this.tail.prev = this.head;
        log.info("创建AdvancedLRUCache，容量：{}，线程安全：{}", capacity, threadSafe);
    }
    
    /**
     * 获取缓存值
     * 
     * @param key 键
     * @return 值，如果不存在或已过期则返回null
     */
    public V get(K key) {
        if (threadSafe) {
            lock.readLock().lock();
            try {
                return doGet(key);
            } finally {
                lock.readLock().unlock();
            }
        } else {
            return doGet(key);
        }
    }
    
    private V doGet(K key) {
        CacheNode<V> node = cache.get(key);
        
        if (node == null) {
            stats.missCount++;
            return null;
        }
        
        // 检查是否过期
        if (node.isExpired()) {
            // 过期则移除
            cache.remove(key);
            removeFromList(node);
            stats.missCount++;
            stats.expiredCount++;
            log.debug("缓存键 {} 已过期，自动清理", key);
            return null;
        }
        
        // 命中，更新访问时间并移动到链表头部
        node.updateAccessTime();
        moveToHead(node);
        stats.hitCount++;
        
        return node.value;
    }
    
    /**
     * 放入缓存
     * 
     * @param key 键
     * @param value 值
     * @param ttlMs TTL时间（毫秒），-1表示永不过期
     */
    public void put(K key, V value, long ttlMs) {
        if (threadSafe) {
            lock.writeLock().lock();
            try {
                doPut(key, value, ttlMs);
            } finally {
                lock.writeLock().unlock();
            }
        } else {
            doPut(key, value, ttlMs);
        }
    }
    
    /**
     * 放入缓存（永不过期）
     */
    public void put(K key, V value) {
        put(key, value, -1);
    }
    
    private void doPut(K key, V value, long ttlMs) {
        CacheNode<V> existingNode = cache.get(key);
        
        if (existingNode != null) {
            // 更新现有节点
            existingNode.value = value;
            existingNode.expireTime = ttlMs > 0 ? System.currentTimeMillis() + ttlMs : -1;
            existingNode.updateAccessTime();
            moveToHead(existingNode);
        } else {
            // 创建新节点
            CacheNode<V> newNode = new CacheNode<>(value, ttlMs);
            
            // 检查容量
            if (cache.size() >= capacity) {
                // 移除最久未使用的节点
                CacheNode<V> tailNode = tail.prev;
                if (tailNode != head) {
                    cache.entrySet().removeIf(entry -> entry.getValue() == tailNode);
                    removeFromList(tailNode);
                    stats.evictionCount++;
                }
            }
            
            cache.put(key, newNode);
            addToHead(newNode);
        }
        
        stats.putCount++;
    }
    
    /**
     * 获取缓存值，如果不存在则通过loader加载
     * 
     * @param key 键
     * @param loader 加载函数
     * @param ttlMs TTL时间（毫秒）
     * @return 值
     */
    public V getOrLoad(K key, Function<K, V> loader, long ttlMs) {
        V value = get(key);
        if (value != null) {
            return value;
        }
        
        // 加载数据
        V loadedValue = loader.apply(key);
        if (loadedValue != null) {
            put(key, loadedValue, ttlMs);
        }
        
        return loadedValue;
    }
    
    /**
     * 移除缓存
     */
    public V remove(K key) {
        if (threadSafe) {
            lock.writeLock().lock();
            try {
                return doRemove(key);
            } finally {
                lock.writeLock().unlock();
            }
        } else {
            return doRemove(key);
        }
    }
    
    private V doRemove(K key) {
        CacheNode<V> node = cache.remove(key);
        if (node != null) {
            removeFromList(node);
            return node.value;
        }
        return null;
    }
    
    /**
     * 清理过期缓存
     */
    public void cleanupExpired() {
        if (threadSafe) {
            lock.writeLock().lock();
            try {
                doCleanupExpired();
            } finally {
                lock.writeLock().unlock();
            }
        } else {
            doCleanupExpired();
        }
    }
    
    private void doCleanupExpired() {
        long expiredCount = 0;
        
        // 先收集过期的键，然后移除
        var expiredKeys = cache.entrySet().stream()
            .filter(entry -> entry.getValue().isExpired())
            .map(entry -> {
                removeFromList(entry.getValue());
                return entry.getKey();
            })
            .collect(Collectors.toList());
        
        // 移除过期的缓存项
        for (K key : expiredKeys) {
            cache.remove(key);
            expiredCount++;
        }
        
        stats.expiredCount += expiredCount;
        if (expiredCount > 0) {
            log.debug("清理了 {} 个过期缓存项", expiredCount);
        }
    }
    
    /**
     * 清空缓存
     */
    public void clear() {
        if (threadSafe) {
            lock.writeLock().lock();
            try {
                doClear();
            } finally {
                lock.writeLock().unlock();
            }
        } else {
            doClear();
        }
    }
    
    private void doClear() {
        cache.clear();
        head.next = tail;
        tail.prev = head;
    }
    
    /**
     * 获取缓存大小
     */
    public int size() {
        return cache.size();
    }
    
    /**
     * 检查是否为空
     */
    public boolean isEmpty() {
        return cache.isEmpty();
    }
    
    /**
     * 获取缓存统计信息
     */
    public CacheStats getStats() {
        return stats;
    }
    
    /**
     * 将节点添加到链表头部
     */
    private void addToHead(CacheNode<V> node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }
    
    /**
     * 从链表中移除节点
     */
    private void removeFromList(CacheNode<V> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
    
    /**
     * 将节点移动到链表头部
     */
    private void moveToHead(CacheNode<V> node) {
        removeFromList(node);
        addToHead(node);
    }
    
    /**
     * 获取缓存信息摘要
     */
    public String getSummary() {
        return String.format(
            "AdvancedLRUCache[容量=%d, 当前大小=%d, 命中率=%.2f%%, 总请求=%d, 命中=%d, 未命中=%d, 写入=%d, 淘汰=%d, 过期清理=%d]",
            capacity, size(), stats.getHitRate() * 100, stats.getTotalRequests(),
            stats.hitCount, stats.missCount, stats.putCount, stats.evictionCount, stats.expiredCount
        );
    }
}