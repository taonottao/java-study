# HashSet 源码与用法进阶

> 关联代码：
> - src/main/java/com/trae/study/collections/set/HashSetDemo.java
> - src/test/java/com/trae/study/collections/set/HashSetDemoTest.java
> - 配图：docs/notes/collections/svg/hashset.svg

## 1. HashSet 的核心结构
- 基于 HashMap 实现，所有 value 共享一个哨兵对象 PRESENT
- 时间复杂度：平均 O(1)，最坏 O(N)
- 去重规则：由 equals/hashCode 决定；hash 冲突时通过链表/树化（JDK8+）解决

## 2. 典型用法
- 去重集合：快速判重、成员测试
- 批量构造：new HashSet<>(list) 或流式收集 Collectors.toSet()
- 自定义实体：需正确实现 equals/hashCode，建议使用不可变 key 字段

## 3. 关键细节
- 负载因子与扩容：默认 0.75；扩容重新散列
- 树化阈值：链表长度超过 8 且容量足够时树化为红黑树
- fail-fast：结构性修改时并发迭代会抛出 ConcurrentModificationException

## 4. 常见坑
- 忽略 equals/hashCode 导致“看似重复却未去重”
- 可变对象作为 key：放入后修改会找不到元素
- 自定义 hashCode 质量差导致严重碰撞，性能退化

## 5. 小结
HashSet 适合“快速判重”的大多数场景。关键在于：高质量 hash、稳定 equals/hashCode、一致性与不可变设计。