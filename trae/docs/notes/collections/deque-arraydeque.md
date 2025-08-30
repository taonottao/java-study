# ArrayDeque：高性能双端队列（Deque）

> 关联代码：
> - src/main/java/com/trae/study/collections/deque/ArrayDequeDemo.java
> - src/test/java/com/trae/study/collections/deque/ArrayDequeDemoTest.java
> - 配图：docs/notes/collections/svg/arraydeque.svg

## 1. 核心特性
- 基于环形数组实现，支持双端 O(1) 均摊的入队/出队
- 可用作栈（push/pop/peek）或队列（offer/poll/peek）
- 不允许存储 null（避免方法语义与返回值冲突）

## 2. 与 LinkedList 作为 Deque 的对比
- ArrayDeque 在大多数场景下更快且更节省内存
- LinkedList 节点分配和指针带来额外开销，适合频繁在中间插入/删除的 List 场景

## 3. 典型用法
- 双端操作：addFirst/addLast/removeFirst/removeLast
- 栈用法：push/pop 实际是对头部的 addFirst/removeFirst 封装
- 队列用法：offer/poll 对尾部与头部操作

## 4. 注意事项
- 迭代期间不要结构性修改（fail-fast 原则同 List）
- 初始容量会按 2 的幂次扩容，尽量估计合理容量降低扩容次数

## 5. 小结
- 作为 Deque 的默认选择，除非有特殊需求（如中间插入/删除或需要并发队列）