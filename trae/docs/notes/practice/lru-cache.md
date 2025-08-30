# 实战：基于 LinkedHashMap 的 LRU 缓存

- 关键点：LinkedHashMap(accessOrder=true) + 覆写 removeEldestEntry 实现容量上限淘汰。
- 优势：实现简单、线程不安全（必要时需外部同步）。
- 扩展方向：增加统计信息（命中率）、过期策略（TTL）、并发封装（使用 ConcurrentHashMap + 双向链表或 Segment + LinkedHashMap）。
- 示例：src/main/java/com/example/practice/LRUCache.java；测试：src/test/java/com/example/practice/LRUCacheTest.java。