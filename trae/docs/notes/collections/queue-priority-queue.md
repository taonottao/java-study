# PriorityQueue 源码与用法进阶

> 关联代码：
> - src/main/java/com/trae/study/collections/queue/PriorityQueueDemo.java
> - src/test/java/com/trae/study/collections/queue/PriorityQueueDemoTest.java
> - 配图：docs/notes/collections/svg/priorityqueue.svg

## 1. 核心结构
- 基于二叉堆（数组）实现，默认最小堆，支持自定义 Comparator
- 复杂度：offer/poll O(logN)，peek O(1)，遍历无序

## 2. 典型用法
- Top-K 问题：固定大小堆
- 定时/调度：按优先级出队
- 流式合并：多路归并的候选池

## 3. 关键点与注意事项
- 非线程安全，迭代 fail-fast
- 不保证元素稳定性（相对顺序可能改变）
- 仅连续 poll 才能得到有序序列

## 4. 小结
当需要“按优先级近似实时地取出最小/最大元素”时，PriorityQueue 是首选。Comparator 的设计决定了队列的行为与语义。