# LinkedHashSet 源码与用法进阶

> 关联代码：
> - src/main/java/com/trae/study/collections/set/LinkedHashSetDemo.java
> - src/test/java/com/trae/study/collections/set/LinkedHashSetDemoTest.java
> - 配图：docs/notes/collections/svg/linkedhashset.svg

## 1. 核心结构
- 继承 HashSet，底层为 LinkedHashMap
- 维护双向链表以记录插入顺序或访问顺序（accessOrder=true）

## 2. 典型用法
- 保序去重：保持插入顺序
- LRU 策略：配合 accessOrder=true + removeEldestEntry 实现简单 LRU

## 3. 关键点
- 时间复杂度接近 HashSet，同时具备顺序特性
- 迭代顺序可控：插入序或访问序

## 4. 小结
在需要“有序去重”的场景下，LinkedHashSet 是 HashSet 的优选替代方案，几乎不牺牲性能。