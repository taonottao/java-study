# LRU缓存实现

## 概述

本项目实现了两个版本的 LRU（Least Recently Used）缓存：
1. **基础版**：基于 `LinkedHashMap` 的简单实现
2. **高级版**：支持 TTL、命中率统计、并发安全的完整实现

## 基础版实现

### 实现要点

#### 1. LinkedHashMap 的访问顺序特性

```java
// 构造函数中启用访问顺序
public LRUCache(int capacity) {
    super(16, 0.75f, true); // accessOrder = true
}
```

- `accessOrder = true`：按访问顺序排序，最近访问的元素移到链表尾部
- `accessOrder = false`：按插入顺序排序（默认）

#### 2. 自动淘汰机制

```java
@Override
protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
    return size() > capacity; // 超过容量时自动移除最老的元素
}
```

### 优势

1. **实现简单**：利用 `LinkedHashMap` 内置特性，代码简洁
2. **性能良好**：O(1) 时间复杂度的 get/put 操作
3. **内存效率**：双向链表 + 哈希表的组合，空间利用率高

## 高级版实现

### 核心特性

#### 1. TTL过期策略

```java
// 支持设置过期时间
cache.put("key1", "value1", 5000); // 5秒后过期
cache.put("key2", "value2"); // 永不过期

// 自动过期检查
if (node.isExpired()) {
    cache.remove(key);
    stats.expiredCount++;
    return null;
}
```

#### 2. 命中率统计

```java
public static class CacheStats {
    private long hitCount = 0;      // 命中次数
    private long missCount = 0;     // 未命中次数
    private long putCount = 0;      // 写入次数
    private long evictionCount = 0; // 淘汰次数
    private long expiredCount = 0;  // 过期清理次数
    
    public double getHitRate() {
        long totalRequests = hitCount + missCount;
        return totalRequests == 0 ? 0.0 : (double) hitCount / totalRequests;
    }
}
```

#### 3. 并发安全支持

```java
// 可选择是否启用线程安全
AdvancedLRUCache<String, String> cache = new AdvancedLRUCache<>(100, true);

// 使用读写锁保证并发安全
private final ReentrantReadWriteLock lock;

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
```

#### 4. 懒加载支持

```java
// 支持懒加载模式
String value = cache.getOrLoad("user:123", userId -> {
    return userService.getUserById(userId); // 从数据库加载
}, 300000); // 5分钟TTL
```

### 高级功能

1. **过期清理**：手动或自动清理过期缓存项
2. **统计监控**：详细的缓存使用统计信息
3. **线程安全**：可选的并发安全支持
4. **容量控制**：严格的容量限制和LRU淘汰
5. **性能监控**：命中率、淘汰率等关键指标

## 性能对比

| 特性 | 基础版 | 高级版 |
|------|--------|--------|
| 实现复杂度 | 简单 | 中等 |
| 内存开销 | 低 | 中等 |
| 并发安全 | 否 | 可选 |
| TTL支持 | 否 | 是 |
| 统计信息 | 否 | 是 |
| 适用场景 | 简单缓存 | 生产环境 |

## 示例代码

- 基础实现：`src/main/java/com/study/cache/LRUCache.java`
- 高级实现：`src/main/java/com/study/cache/AdvancedLRUCache.java`
- 基础测试：`src/test/java/com/study/cache/LRUCacheTest.java`
- 高级测试：`src/test/java/com/study/cache/AdvancedLRUCacheTest.java`

## 使用示例

### 基础版使用

```java
LRUCache<String, String> cache = new LRUCache<>(3);
cache.put("key1", "value1");
cache.put("key2", "value2");
cache.put("key3", "value3");

// 访问key1，使其变为最近使用
String value = cache.get("key1");

// 添加新元素，key2将被淘汰（最久未使用）
cache.put("key4", "value4");
```

### 高级版使用

```java
// 创建线程安全的缓存
AdvancedLRUCache<String, User> cache = new AdvancedLRUCache<>(1000, true);

// 添加带TTL的缓存项
cache.put("user:123", user, 300000); // 5分钟过期

// 懒加载模式
User user = cache.getOrLoad("user:456", userId -> {
    return userService.loadUser(userId);
}, 300000);

// 获取统计信息
AdvancedLRUCache.CacheStats stats = cache.getStats();
System.out.println("命中率: " + stats.getHitRate() * 100 + "%");
System.out.println(cache.getSummary());

// 手动清理过期项
cache.cleanupExpired();
```

## 扩展思考

### 1. 不同LRU实现方式对比

| 实现方式 | 优点 | 缺点 | 适用场景 |
|----------|------|------|----------|
| LinkedHashMap | 实现简单，性能好 | 功能有限，非线程安全 | 简单应用 |
| 手动双向链表+HashMap | 功能完整，可定制 | 实现复杂，容易出错 | 高性能要求 |
| ConcurrentHashMap+LRU | 并发性能好 | 实现复杂 | 高并发场景 |

### 2. 生产环境考虑因素

1. **内存管理**：避免内存泄漏，合理设置容量
2. **监控告警**：命中率过低、频繁淘汰等异常情况
3. **配置管理**：支持动态调整缓存参数
4. **序列化**：分布式环境下的缓存同步
5. **降级策略**：缓存失效时的备用方案

### 3. 性能优化建议

1. **批量操作**：减少锁竞争
2. **分段锁**：提高并发度
3. **异步清理**：后台线程清理过期项
4. **预热机制**：启动时预加载热点数据
5. **容量规划**：根据业务特点合理设置容量